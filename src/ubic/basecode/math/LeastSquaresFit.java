/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.math;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import no.uib.cipr.matrix.DenseMatrix;
import no.uib.cipr.matrix.Matrices;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;
import org.netlib.lapack.LAPACK;
import org.netlib.util.intW;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;
import ubic.basecode.dataStructure.matrix.MatrixUtil;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.util.r.type.AnovaEffect;
import ubic.basecode.util.r.type.GenericAnovaResult;
import ubic.basecode.util.r.type.LinearModelSummary;
import cern.colt.function.IntIntDoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.math.Functions;
import cern.jet.stat.Descriptive;

/**
 * For performing "bulk" linear model fits. Allows rapid fitting to large data sets.
 * 
 * @author paul
 * @version $Id$
 */
public class LeastSquaresFit {

    /*
     * Making this static causes problems with memory leaks/garbage that is not efficiently collected.
     */
    // private static Algebra solver = new Algebra();

    private static Log log = LogFactory.getLog( LeastSquaresFit.class );

    /**
     * The (raw) design matrix
     */
    private DoubleMatrix2D A;

    /*
     * Lists which factors (terms) are associated with which columns of the design matrix; 0 indicates the intercept.
     */
    private List<Integer> assign = new ArrayList<Integer>();

    private List<List<Integer>> assigns = new ArrayList<List<Integer>>();

    /**
     * Independent variables
     */
    private DoubleMatrix2D b;

    /**
     * Model fix coefficients (the x in Ax=b)
     */
    private DoubleMatrix2D coefficients = null;

    private DesignMatrix designMatrix;

    /**
     * Fitted values
     */
    private DoubleMatrix2D fitted;

    private boolean hasIntercept = true;

    /**
     * 
     */
    private boolean hasMissing = false;

    private boolean hasWarned = false;

    /**
     * QR decomposition of the design matrix
     */
    private QRDecompositionPivoting qr;

    /*
     * Used if we have missing values so QR might be different for each row (possible optimization to consider: only
     * store set of variants that are actually used)
     */
    private List<QRDecompositionPivoting> qrs = new ArrayList<QRDecompositionPivoting>();

    private int residualDof;

    /*
     * Used if we have missing value so RDOF might be different for each row (we can actually get away without this)
     */
    private List<Integer> residualDofs = new ArrayList<Integer>();

    /**
     * Residuals of the fit
     */
    private DoubleMatrix2D residuals;
    /*
     * Optional, but useful
     */
    private List<String> rowNames;

    /*
     * Names of the factors (terms)
     */
    private List<String> terms;

    // private List<Integer[]> pivotIndicesList = new ArrayList<Integer[]>();

    /*
     * For weighted regression
     */
    private DoubleMatrix2D weights = null;

    /**
     * Preferred interface if you want control over how the design is set up.
     * 
     * @param designMatrix
     * @param data
     */
    public LeastSquaresFit( DesignMatrix designMatrix, DoubleMatrix<String, String> data ) {
        this.designMatrix = designMatrix;
        DoubleMatrix2D X = designMatrix.getDoubleMatrix();
        this.assign = designMatrix.getAssign();
        this.terms = designMatrix.getTerms();
        this.A = X;
        this.rowNames = data.getRowNames();
        this.b = new DenseDoubleMatrix2D( data.asArray() );
        boolean hasInterceptTerm = this.terms.contains( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME );
        this.hasIntercept = designMatrix.hasIntercept();
        assert hasInterceptTerm == this.hasIntercept : diagnosis( null );
        lsf();
    }

    /**
     * Weighted least squares fit between two matrices
     * 
     * @param designMatrix
     * @param data
     * @param weights to be used in modifying the influence of the observations in data.
     */
    public LeastSquaresFit( DesignMatrix designMatrix, DoubleMatrix<String, String> data, final DoubleMatrix2D weights ) {
        this.designMatrix = designMatrix;
        DoubleMatrix2D X = designMatrix.getDoubleMatrix();
        this.assign = designMatrix.getAssign();
        this.terms = designMatrix.getTerms();
        this.A = X;
        this.rowNames = data.getRowNames();
        this.b = new DenseDoubleMatrix2D( data.asArray() );
        boolean hasInterceptTerm = this.terms.contains( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME );
        this.hasIntercept = designMatrix.hasIntercept();
        assert hasInterceptTerm == this.hasIntercept : diagnosis( null );
        this.weights = weights;
        wlsf();
    }

    /**
     * Preferred interface for weighted least squares fit between two matrices
     * 
     * @param designMatrix
     * @param data
     * @param weights to be used in modifying the influence of the observations in vectorB.
     */
    public LeastSquaresFit( DesignMatrix designMatrix, DoubleMatrix2D b, final DoubleMatrix2D weights ) {

        this.designMatrix = designMatrix;
        DoubleMatrix2D X = designMatrix.getDoubleMatrix();
        this.assign = designMatrix.getAssign();
        this.terms = designMatrix.getTerms();
        this.A = X;
        this.b = b;
        boolean hasInterceptTerm = this.terms.contains( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME );
        this.hasIntercept = designMatrix.hasIntercept();
        assert hasInterceptTerm == this.hasIntercept : diagnosis( null );

        this.weights = weights;

        wlsf();

    }

    /**
     * Least squares fit between two vectors. Always adds an intercept!
     * 
     * @param vectorA Design
     * @param vectorB Data
     */
    public LeastSquaresFit( DoubleMatrix1D vectorA, DoubleMatrix1D vectorB ) {
        assert vectorA.size() == vectorB.size();

        this.A = new DenseDoubleMatrix2D( vectorA.size(), 2 );
        this.b = new DenseDoubleMatrix2D( 1, vectorB.size() );

        for ( int i = 0; i < vectorA.size(); i++ ) {
            A.set( i, 0, 1 );
            A.set( i, 1, vectorA.get( i ) );
            b.set( 0, i, vectorB.get( i ) );
        }

        lsf();
    }

    /**
     * Least squares fit between two vectors. Always adds an intercept!
     * 
     * @param vectorA Design
     * @param vectorB Data
     * @param weights to be used in modifying the influence of the observations in vectorB.
     */
    public LeastSquaresFit( DoubleMatrix1D vectorA, DoubleMatrix1D vectorB, final DoubleMatrix1D weights ) {

        assert vectorA.size() == vectorB.size();
        assert vectorA.size() == weights.size();

        this.A = new DenseDoubleMatrix2D( vectorA.size(), 2 );
        this.b = new DenseDoubleMatrix2D( 1, vectorB.size() );
        this.weights = new DenseDoubleMatrix2D( 1, weights.size() );

        for ( int i = 0; i < vectorA.size(); i++ ) {
            double ws = Math.sqrt( weights.get( i ) );
            A.set( i, 0, ws );
            A.set( i, 1, vectorA.get( i ) * ws );
            b.set( 0, i, vectorB.get( i ) * ws );
            this.weights.set( 0, i, weights.get( i ) );
        }

        lsf();

        residuals = residuals.forEachNonZero( new IntIntDoubleFunction() {
            @Override
            public double apply( int row, int col, double nonZeroValue ) {
                return nonZeroValue / Math.sqrt( weights.get( col ) );
            }
        } );

        DoubleMatrix1D f2 = vectorB.copy().assign( residuals.viewRow( 0 ), Functions.minus );
        this.fitted.viewRow( 0 ).assign( f2 );
    }

    /**
     * Stripped-down interface for simple use. ANOVA not possible (use the other constructors)
     * 
     * @param A Design matrix, which will be used directly in least squares regression
     * @param b Data matrix, containing data in rows.
     */
    public LeastSquaresFit( DoubleMatrix2D A, DoubleMatrix2D b ) {
        this.A = A;
        this.b = b;
        lsf();
    }

    /**
     * Weighted least squares fit between two matrices
     * 
     * @param vectorA Design
     * @param vectorB Data
     * @param weights to be used in modifying the influence of the observations in vectorB.
     */
    public LeastSquaresFit( DoubleMatrix2D A, DoubleMatrix2D b, final DoubleMatrix2D weights ) {

        assert A.rows() == b.columns();
        assert b.columns() == weights.columns();
        assert b.rows() == weights.rows();

        this.A = A;
        this.b = b;
        this.weights = weights;

        wlsf();

    }

    /**
     * @param sample information that will be converted to a design matrix; intercept term is added.
     * @param data Data matrix
     */
    public LeastSquaresFit( ObjectMatrix<String, String, Object> sampleInfo, DenseDoubleMatrix2D data ) {

        this.designMatrix = new DesignMatrix( sampleInfo, true );

        this.hasIntercept = true;
        DoubleMatrix2D X = designMatrix.getDoubleMatrix();
        this.assign = designMatrix.getAssign();
        this.terms = designMatrix.getTerms();
        this.A = X;
        this.b = data;
        lsf();
    }

    /**
     * @param sampleInfo
     * @param data
     * @param interactions add interaction term (two-way only is supported)
     */
    public LeastSquaresFit( ObjectMatrix<String, String, Object> sampleInfo, DenseDoubleMatrix2D data,
            boolean interactions ) {
        this.designMatrix = new DesignMatrix( sampleInfo, true );

        if ( interactions ) {
            addInteraction();
        }

        DoubleMatrix2D X = designMatrix.getDoubleMatrix();
        this.assign = designMatrix.getAssign();
        this.terms = designMatrix.getTerms();
        this.A = X;
        this.b = data;
        lsf();
    }

    /**
     * NamedMatrix allows easier handling of the results.
     * 
     * @param sample information that will be converted to a design matrix; intercept term is added.
     * @param b Data matrix
     */
    public LeastSquaresFit( ObjectMatrix<String, String, Object> design, DoubleMatrix<String, String> b ) {
        this.designMatrix = new DesignMatrix( design, true );

        DoubleMatrix2D X = designMatrix.getDoubleMatrix();
        this.assign = designMatrix.getAssign();
        this.terms = designMatrix.getTerms();
        this.A = X;
        this.b = new DenseDoubleMatrix2D( b.asArray() );
        this.rowNames = b.getRowNames();
        lsf();
    }

    /**
     * NamedMatrix allows easier handling of the results.
     * 
     * @param sample information that will be converted to a design matrix; intercept term is added.
     * @param data Data matrix
     */
    public LeastSquaresFit( ObjectMatrix<String, String, Object> design, DoubleMatrix<String, String> data,
            boolean interactions ) {
        this.designMatrix = new DesignMatrix( design, true );

        if ( interactions ) {
            addInteraction();
        }

        DoubleMatrix2D X = designMatrix.getDoubleMatrix();
        this.assign = designMatrix.getAssign();
        this.terms = designMatrix.getTerms();
        this.A = X;
        this.b = new DenseDoubleMatrix2D( data.asArray() );
        lsf();
    }

    /**
     * Compute ANOVA based on the model fit
     * 
     * @return
     */
    public List<GenericAnovaResult> anova() {
        Algebra solver = new Algebra();
        DoubleMatrix1D ones = new DenseDoubleMatrix1D( residuals.columns() );
        ones.assign( 1.0 );
        DoubleMatrix1D residualSumsOfSquares = MatrixUtil.multWithMissing( residuals.copy().assign( Functions.square ),
                ones );

        DoubleMatrix2D effects = null;
        if ( this.hasMissing ) {
            effects = new DenseDoubleMatrix2D( this.b.rows(), this.A.columns() );
            effects.assign( Double.NaN );
            for ( int i = 0; i < this.b.rows(); i++ ) {
                // if ( this.rowNames.get( i ).equals( "probe_60" ) ) {
                // log.info( "MARV" );
                // }
                QRDecompositionPivoting qrd = this.qrs.get( i );
                if ( qrd == null ) {
                    // means we did not get a fit
                    for ( int j = 0; j < effects.columns(); j++ ) {
                        effects.set( i, j, Double.NaN );
                    }
                    continue;
                }
                DoubleMatrix1D brow = b.viewRow( i );
                DoubleMatrix1D browWithoutMissing = MatrixUtil.removeMissing( brow );
                DoubleMatrix2D qrow = qrd.getQ().viewPart( 0, 0, browWithoutMissing.size(), qrd.getRank() );

                // will never happen.
                // if ( qrow.rows() != browWithoutMissing.size() ) {
                // for ( int j = 0; j < effects.columns(); j++ ) {
                // effects.set( i, j, Double.NaN );
                // }
                // continue;
                // }
                DoubleMatrix1D crow = MatrixUtil.multWithMissing( browWithoutMissing, qrow );

                for ( int j = 0; j < crow.size(); j++ ) {
                    effects.set( i, j, crow.get( j ) );
                }
            }

        } else {
            assert this.qr != null : " QR was null. \n" + this.diagnosis( qr );
            DoubleMatrix2D q = this.qr.getQ();
            effects = solver.mult( this.b, q );
        }

        effects.assign( Functions.square );

        /*
         * Add up the ssr for the columns within each factor.
         */
        Set<Integer> facs = new TreeSet<Integer>();
        facs.addAll( assign );

        DoubleMatrix2D ssq = new DenseDoubleMatrix2D( effects.rows(), facs.size() + 1 );
        DoubleMatrix2D dof = new DenseDoubleMatrix2D( effects.rows(), facs.size() + 1 );
        dof.assign( 0.0 );
        ssq.assign( 0.0 );
        List<Integer> assignToUse = assign;

        for ( int i = 0; i < ssq.rows(); i++ ) {

            ssq.set( i, facs.size(), residualSumsOfSquares.get( i ) );
            int rdof;
            if ( this.residualDofs.isEmpty() ) {
                rdof = this.residualDof;
            } else {
                rdof = this.residualDofs.get( i );
            }
            /*
             * Store residual DOF in the last column.
             */
            dof.set( i, facs.size(), rdof );

            if ( !assigns.isEmpty() ) {
                assignToUse = assigns.get( i );
            }

            DoubleMatrix1D effectsForRow = effects.viewRow( i );

            if ( assignToUse.size() != effectsForRow.size() ) {
                /*
                 * Effects will have NaNs (FIXME: always at end?)
                 */
                log.debug( "Check me: effects has missing values" );
            }

            for ( int j = 0; j < assignToUse.size(); j++ ) {

                double valueToAdd = effectsForRow.get( j );
                int col = assignToUse.get( j );
                // for ( Integer col : assignToUse ) {
                // double valueToAdd;
                if ( col > 0 && !this.hasIntercept ) {
                    col = col - 1;
                }

                /*
                 * Accumulate the sum. When the data is "constant" you can end up with a tiny but non-zero coefficient,
                 * but it's bogus. See bug 3177.
                 */
                if ( !Double.isNaN( valueToAdd ) && valueToAdd > Constants.SMALL ) {
                    /*
                     * Is this always true?
                     */
                    ssq.set( i, col, ssq.get( i, col ) + valueToAdd );
                    dof.set( i, col, dof.get( i, col ) + 1 );
                } else {
                    // log.warn( "Missing value in effects for row " + i
                    // + ( this.rowNames == null ? "" : " (row=" + this.rowNames.get( i ) + ")" )
                    // + this.diagnosis( null ) );
                }
            }
        }

        // one value for each term in the model
        DoubleMatrix1D denominator;
        if ( this.residualDofs.isEmpty() ) {
            denominator = residualSumsOfSquares.copy().assign( Functions.div( residualDof ) );
        } else {
            denominator = new DenseDoubleMatrix1D( residualSumsOfSquares.size() );
            for ( int i = 0; i < residualSumsOfSquares.size(); i++ ) {
                denominator.set( i, residualSumsOfSquares.get( i ) / residualDofs.get( i ) );
            }
        }

        DoubleMatrix2D fStats = ssq.copy().assign( dof, Functions.div );
        DoubleMatrix2D pvalues = fStats.like();

        computeStats( dof, fStats, denominator, pvalues );

        return summarizeAnova( ssq, dof, fStats, pvalues );
    }

    public DoubleMatrix2D getCoefficients() {
        return coefficients;
    }

    public DoubleMatrix2D getFitted() {
        return fitted;
    }

    public int getResidualDof() {
        return residualDof;
    }

    public DoubleMatrix2D getResiduals() {
        return residuals;
    }

    /**
     * @return externally studentized residuals.
     */
    public DoubleMatrix2D getStudentizedResiduals() {
        int dof = this.residualDof - 1; // MINUS for external studentizing!!

        assert dof > 0;

        if ( this.hasMissing ) {
            throw new UnsupportedOperationException( "Studentizing not supported with missing values" );
        }

        DoubleMatrix2D result = this.residuals.like();

        /*
         * Diagnonal of the hat matrix at i (hi) is the squared norm of the ith row of Q
         */
        DoubleMatrix2D q = qr.getQ();
        DoubleMatrix1D hatdiag = new DenseDoubleMatrix1D( residuals.columns() );
        for ( int j = 0; j < residuals.columns(); j++ ) {
            double hj = q.viewRow( j ).aggregate( Functions.plus, Functions.square );
            if ( 1.0 - hj < Constants.TINY ) {
                hj = 1.0;
            }
            hatdiag.set( j, hj );
        }

        /*
         * Measure sum of squares of residuals / residualDof
         */
        for ( int i = 0; i < residuals.rows(); i++ ) {

            // these are 'internally studentized'
            // double sdhat = Math.sqrt( residuals.viewRow( i ).aggregate( Functions.plus, Functions.square ) / dof );

            DoubleMatrix1D residualRow = residuals.viewRow( i );

            if ( this.weights != null ) {
                // use weighted residuals.
                DoubleMatrix1D w = weights.viewRow( i ).copy().assign( Functions.sqrt );
                residualRow = residualRow.copy().assign( w, Functions.mult );
            }

            double sum = residualRow.aggregate( Functions.plus, Functions.square );

            for ( int j = 0; j < residualRow.size(); j++ ) {

                double hj = hatdiag.get( j );

                // this is how we externalize...
                double sigma;

                if ( hj < 1.0 ) {
                    sigma = Math.sqrt( ( sum - Math.pow( residualRow.get( j ), 2 ) / ( 1.0 - hj ) ) / dof );
                } else {
                    sigma = Math.sqrt( sum / dof );
                }

                double res = residualRow.getQuick( j );
                double studres = res / ( sigma * Math.sqrt( 1.0 - hj ) );

                if ( log.isDebugEnabled() ) log.debug( "sigma=" + sigma + " hj=" + hj + " stres=" + studres );

                result.set( i, j, studres );
            }
        }
        return result;
    }

    public boolean isHasMissing() {
        return hasMissing;
    }

    public void setHasMissing( boolean hasMissing ) {
        this.hasMissing = hasMissing;
    }

    /**
     * @return summaries. ANOVA will not be computed.
     */
    public List<LinearModelSummary> summarize() {
        return this.summarize( false );
    }

    /**
     * @param anova if true, ANOVA will be computed
     * @return
     */
    public List<LinearModelSummary> summarize( boolean anova ) {
        List<LinearModelSummary> lmsresults = new ArrayList<LinearModelSummary>();

        List<GenericAnovaResult> anovas = null;
        if ( anova ) {
            anovas = this.anova();
        }

        for ( int i = 0; i < this.coefficients.columns(); i++ ) {
            LinearModelSummary lms = summarize( i );
            lms.setAnova( anovas != null ? anovas.get( i ) : null );
            lmsresults.add( lms );

        }

        return lmsresults;
    }

    /**
     * @param anova perform ANOVA, otherwise only basic summarization will be done.
     * @return
     */
    public Map<String, LinearModelSummary> summarizeByKeys( boolean anova ) {
        List<LinearModelSummary> summaries = this.summarize( anova );
        Map<String, LinearModelSummary> result = new LinkedHashMap<String, LinearModelSummary>();
        for ( LinearModelSummary lms : summaries ) {
            if ( StringUtils.isBlank( lms.getKey() ) ) {
                /*
                 * Perhaps we should just use an integer.
                 */
                throw new IllegalStateException( "Key must not be blank" );
            }

            if ( result.containsKey( lms.getKey() ) ) {
                throw new IllegalStateException( "Duplicate key " + lms.getKey() );
            }
            result.put( lms.getKey(), lms );
        }
        return result;
    }

    /**
     * Note: does not populate the ANOVA.
     * 
     * @param i
     * @return
     */
    protected LinearModelSummary summarize( int i ) {

        String key = null;
        if ( this.rowNames != null ) {
            key = this.rowNames.get( i );
            if ( key == null ) log.warn( "Key null at " + i );
        }

        QRDecompositionPivoting qrd = null;
        if ( this.qrs.isEmpty() ) {
            qrd = this.qr; // no missing values, so it's global
        } else {
            qrd = this.qrs.get( i ); // row-specific
        }

        if ( qrd == null ) {
            log.debug( "QR was null for item " + i );
            return new LinearModelSummary( key );
        }

        int rdf;
        if ( this.residualDofs.isEmpty() ) {
            rdf = this.residualDof; // no missing values, so it's global
        } else {
            rdf = this.residualDofs.get( i ); // row-specific
        }

        if ( rdf == 0 ) {
            return new LinearModelSummary( key );
        }
        DoubleMatrix1D coef = coefficients.viewColumn( i );
        DoubleMatrix1D r = MatrixUtil.removeMissing( this.residuals.viewRow( i ) );
        DoubleMatrix1D f = MatrixUtil.removeMissing( fitted.viewRow( i ) );
        DoubleMatrix1D est = MatrixUtil.removeMissing( coef );

        if ( est.size() == 0 ) {
            log.warn( "No coefficients estimated for row " + i + this.diagnosis( qrd ) );
            log.info( "Data for this row:\n" + this.b.viewRow( i ) );
            return new LinearModelSummary( key );
        }

        int p = qrd.getRank();
        int n = qrd.getQ().rows();
        assert rdf == n - p : "Rank was not correct, expected " + rdf + " but got Q rows=" + n + ", #Coef=" + p
                + diagnosis( qrd );

        double mss;
        if ( hasIntercept ) {
            mss = f.copy().assign( Functions.minus( Descriptive.mean( new DoubleArrayList( f.toArray() ) ) ) )
                    .assign( Functions.square ).aggregate( Functions.plus, Functions.identity );
        } else {
            mss = f.copy().assign( Functions.square ).aggregate( Functions.plus, Functions.identity );
        }

        double rss = r.copy().assign( Functions.square ).aggregate( Functions.plus, Functions.identity );
        double resvar = rss / rdf;

        /*
         * These next two lines could be computationally expensive; there is no need to compute these over and over in
         * many cases.
         */
        DoubleMatrix2D qrdR = qrd.getR();
        DoubleMatrix2D R = dpotri( qrdR.viewPart( 0, 0, qrd.getRank(), qrd.getRank() ) );

        // matrix to hold the coefficients.
        DoubleMatrix<String, String> coeffMat = DoubleMatrixFactory.dense( coef.size(), 4 );
        coeffMat.assign( Double.NaN );
        coeffMat.setColumnNames( Arrays.asList( new String[] { "Estimate", "Std. Error", "t value", "Pr(>|t|)" } ) );

        DoubleMatrix1D se = MatrixUtil.diagonal( R ).assign( Functions.mult( resvar ) ).assign( Functions.sqrt );

        if ( est.size() != se.size() ) {
            /*
             * This should actually not happen, due to pivoting.
             */
            if ( !hasWarned ) {
                log.warn( "T statistics could not be computed because of missing values (singularity?) " + i
                        + this.diagnosis( qrd ) );
                log.warn( "Data for this row:\n" + this.b.viewRow( i ) );
                log.warn( "Additional warnings suppressed" );
                hasWarned = true;
            }
            return new LinearModelSummary( key, terms, ArrayUtils.toObject( this.residuals.viewRow( i ).toArray() ),
                    coeffMat, 0.0, 0.0, 0.0, 0, 0, null );
        }

        DoubleMatrix1D tval = est.copy().assign( se, Functions.div );
        TDistribution tdist = new TDistributionImpl( rdf );

        int j = 0;
        for ( int ti = 0; ti < coef.size(); ti++ ) {
            double c = coef.get( ti );
            assert this.designMatrix != null;
            List<String> colNames = this.designMatrix.getMatrix().getColNames();

            String dmcolname;
            if ( colNames == null ) {
                dmcolname = "Column_" + ti;
            } else {
                dmcolname = colNames.get( ti );
            }

            /* FIXME the contrast should be stored in there. */
            coeffMat.addRowName( dmcolname );
            if ( Double.isNaN( c ) ) {
                continue;
            }

            coeffMat.set( ti, 0, est.get( j ) );
            coeffMat.set( ti, 1, se.get( j ) );
            coeffMat.set( ti, 2, tval.get( j ) );

            try {
                double pval = 2.0 * ( 1.0 - tdist.cumulativeProbability( Math.abs( tval.get( j ) ) ) );
                coeffMat.set( ti, 3, pval );
            } catch ( MathException e ) {
                coeffMat.set( ti, 3, Double.NaN );
            }
            j++;

        }

        double rsquared = 0.0;
        double adjRsquared = 0.0;
        double fstatistic = 0.0;
        int numdf = 0;
        int dendf = 0;
        // is there anything besides the intercept???
        if ( terms.size() > 1 || !hasIntercept ) {
            int dfint = hasIntercept ? 1 : 0;
            rsquared = mss / ( mss + rss );
            adjRsquared = 1 - ( 1 - rsquared * ( ( n - dfint ) / ( double ) rdf ) );
            fstatistic = mss / ( p - dfint ) / resvar;

            numdf = p - dfint;
            dendf = rdf;

        } else {
            // intercept only, apparently.
            rsquared = 0.0;
            adjRsquared = 0.0;
        }

        LinearModelSummary lms = new LinearModelSummary( key, terms, ArrayUtils.toObject( this.residuals.viewRow( i )
                .toArray() ), coeffMat, rsquared, adjRsquared, fstatistic, numdf, dendf, null );
        return lms;
    }

    private void addInteraction() {
        if ( designMatrix.getTerms().size() == 1 ) {
            throw new IllegalArgumentException( "Need at least two factors for interactions" );
        }
        if ( designMatrix.getTerms().size() != 2 ) {
            throw new UnsupportedOperationException( "Interactions not supported for more than two factors" );
        }
        this.designMatrix.addInteraction( designMatrix.getTerms().get( 0 ), designMatrix.getTerms().get( 1 ) );
    }

    /**
     * 
     */
    private void checkForMissingValues() {
        for ( int i = 0; i < b.rows(); i++ ) {
            for ( int j = 0; j < b.columns(); j++ ) {
                double v = b.get( i, j );
                if ( Double.isNaN( v ) || Double.isInfinite( v ) ) {
                    this.hasMissing = true;
                    break;
                }
            }
        }
    }

    /**
     * Drop, and track, redundant or constant columns. This is only used if we have missing values which would require
     * changing the design depending on what is missing. Otherwise the model is assumed to be clean. Note that this does
     * not check the model for singularity.
     * <p>
     * FIXME Probably slow if we have to run this often; should cache re-used values.
     * 
     * @param design
     * @param ypsize
     * @param droppedColumns
     * @return
     */
    private DoubleMatrix2D cleanDesign( final DoubleMatrix2D design, int ypsize, List<Integer> droppedColumns ) {

        /*
         * Drop constant columns or columns which are the same as another column.
         */
        for ( int j = 0; j < design.columns(); j++ ) {
            if ( j == 0 && this.hasIntercept ) continue;
            double lastValue = Double.NaN;
            boolean constant = true;
            for ( int i = 0; i < design.rows(); i++ ) {
                double thisvalue = design.get( i, j );
                if ( i > 0 && thisvalue != lastValue ) {
                    constant = false;
                    break;
                }
                lastValue = thisvalue;
            }
            if ( constant ) {
                log.debug( "Dropping constant column " + j );
                droppedColumns.add( j );
                continue;
            }

            DoubleMatrix1D col = design.viewColumn( j );

            for ( int p = 0; p < j; p++ ) {
                boolean redundant = true;
                DoubleMatrix1D otherCol = design.viewColumn( p );
                for ( int v = 0; v < col.size(); v++ ) {
                    if ( col.get( v ) != otherCol.get( v ) ) {
                        redundant = false;
                        break;
                    }
                }
                if ( redundant ) {
                    log.debug( "Dropping redundant column " + j );
                    droppedColumns.add( j );
                    break;
                }
            }

        }

        DoubleMatrix2D returnValue = MatrixUtil.dropColumns( design, droppedColumns );

        return returnValue;
    }

    /**
     * @param dof
     * @param fStats
     * @param denominator
     * @param pvalues
     */
    private void computeStats( DoubleMatrix2D dof, DoubleMatrix2D fStats, DoubleMatrix1D denominator,
            DoubleMatrix2D pvalues ) {
        pvalues.assign( Double.NaN );
        int timesWarned = 0;
        for ( int i = 0; i < fStats.rows(); i++ ) {

            int rdof;
            if ( this.residualDofs.isEmpty() ) {
                rdof = residualDof;
            } else {
                rdof = this.residualDofs.get( i );
            }

            for ( int j = 0; j < fStats.columns(); j++ ) {

                double ndof = dof.get( i, j );

                if ( ndof <= 0 || rdof <= 0 ) {
                    pvalues.set( i, j, Double.NaN );
                    fStats.set( i, j, Double.NaN );
                    continue;
                }

                if ( j == fStats.columns() - 1 ) {
                    // don't fill in f & p values for the residual...
                    pvalues.set( i, j, Double.NaN );
                    fStats.set( i, j, Double.NaN );
                    continue;
                }

                /*
                 * Taking ratios of two very small values is not meaningful; happens if the data are ~constant.
                 */
                if ( fStats.get( i, j ) < Constants.SMALLISH && denominator.get( i ) < Constants.SMALLISH ) {
                    pvalues.set( i, j, Double.NaN );
                    fStats.set( i, j, Double.NaN );
                    continue;
                }

                fStats.set( i, j, fStats.get( i, j ) / denominator.get( i ) );
                try {
                    FDistribution pf = new FDistributionImpl( ndof, rdof );
                    pvalues.set( i, j, 1.0 - pf.cumulativeProbability( fStats.get( i, j ) ) );
                } catch ( MathException e ) {
                    if ( timesWarned < 10 ) {
                        log.warn( "Pvalue could not be computed for F=" + fStats.get( i, j ) + "; denominator was="
                                + denominator.get( i ) + "; Error: " + e.getMessage()
                                + " (limited warnings of this type will be given)" );
                        timesWarned++;
                    }
                    pvalues.set( i, j, Double.NaN );
                }

            }
        }
    }

    /**
     * @param qrd
     * @return
     */
    private String diagnosis( QRDecompositionPivoting qrd ) {
        StringBuilder buf = new StringBuilder();
        buf.append( "\n--------\nLM State\n--------\n" );
        buf.append( "hasMissing=" + this.hasMissing + "\n" );
        buf.append( "hasIntercept=" + this.hasIntercept + "\n" );
        buf.append( "Design: " + this.designMatrix + "\n" );
        if ( this.b.rows() < 5 ) {
            buf.append( "Data matrix: " + this.b + "\n" );
        } else {
            buf.append( "Data (first few rows): " + this.b.viewSelection( new int[] { 0, 1, 2, 3, 4 }, null ) + "\n" );

        }
        buf.append( "Current QR:" + qrd + "\n" );
        return buf.toString();
    }

    /**
     * Mimics functionality of chol2inv from R (which just calls LAPACK::dpotri)
     * 
     * @param x upper triangular matrix (from qr)
     * @return symmetric matrix X'X^-1
     */
    private DoubleMatrix2D dpotri( DoubleMatrix2D x ) {

        // this is not numerically stable
        // DoubleMatrix2D mult = solver.mult( solver.transpose( qrdR ), qrdR );
        // DoubleMatrix2D R = solver.inverse( mult );

        DenseMatrix denseMatrix = new DenseMatrix( x.copy().toArray() );
        intW status = new intW( 0 );
        LAPACK.getInstance().dpotri( "U", x.columns(), denseMatrix.getData(), x.columns(), status );
        if ( status.val != 0 ) {
            throw new IllegalStateException( "Could not invert matrix" );
        }

        return new DenseDoubleMatrix2D( Matrices.getArray( denseMatrix ) );
    }

    /**
     * Internal function that does the hard work.
     */
    private void lsf() {

        checkForMissingValues();
        Algebra solver = new Algebra();
        /*
         * Missing values result in the addition of a fair amount of extra code.
         */
        if ( this.hasMissing ) {
            double[][] rawResult = new double[b.rows()][];
            for ( int i = 0; i < b.rows(); i++ ) {
                DoubleMatrix1D row = b.viewRow( i );
                DoubleMatrix1D withoutMissing = ordinaryLeastSquaresWithMissing( row, A );
                if ( withoutMissing == null ) {
                    rawResult[i] = new double[A.columns()];
                } else {
                    rawResult[i] = withoutMissing.toArray();
                }
            }
            this.coefficients = solver.transpose( new DenseDoubleMatrix2D( rawResult ) );

        } else {

            this.qr = new QRDecompositionPivoting( A );
            this.coefficients = qr.solve( solver.transpose( b ) );
            this.residualDof = b.columns() - qr.getRank();
            if ( residualDof <= 0 ) {
                throw new IllegalArgumentException( "No residual degrees of freedom to fit the model" + diagnosis( qr ) );
            }

        }
        assert this.assign.isEmpty() || this.assign.size() == this.coefficients.rows() : assign.size()
                + " != # coefficients " + this.coefficients.rows();
        assert this.coefficients.rows() == A.columns();

        this.fitted = solver.transpose( MatrixUtil.multWithMissing( A, coefficients ) );

        if ( this.hasMissing ) {
            MatrixUtil.maskMissing( b, fitted );
        }

        this.residuals = b.copy().assign( fitted, Functions.minus );
    }

    /**
     * Has side effect of filling in this.qrs and this.residualDofs.
     * 
     * @param y the data to fit (a.k.a. b)
     * @param des the design matrix (a.k.a A)
     * @return the coefficients (a.k.a. x)
     */
    private DoubleMatrix1D ordinaryLeastSquaresWithMissing( DoubleMatrix1D y, DoubleMatrix2D des ) {
        Algebra solver = new Algebra();
        List<Double> ywithoutMissingList = new ArrayList<Double>( y.size() );
        int size = y.size();

        int countNonMissing = 0;
        for ( int i = 0; i < size; i++ ) {
            double v = y.getQuick( i );
            if ( !Double.isNaN( v ) && !Double.isInfinite( v ) ) {
                countNonMissing++;
            }
        }

        if ( countNonMissing < 3 ) {
            /*
             * return nothing.
             */
            DoubleMatrix1D re = new DenseDoubleMatrix1D( des.columns() );
            re.assign( Double.NaN );
            log.debug( "Not enough non-missing values" );
            this.qrs.add( null );
            this.residualDofs.add( countNonMissing - des.columns() );
            this.assigns.add( new ArrayList<Integer>() );
            // this.pivotIndicesList.add( new Integer[] {} );
            return re;
        }

        double[][] rawDesignWithoutMissing = new double[countNonMissing][];
        int index = 0;
        boolean missing = false;
        for ( int i = 0; i < size; i++ ) {
            double yi = y.getQuick( i );
            if ( Double.isNaN( yi ) || Double.isInfinite( yi ) ) {
                missing = true;
                continue;
            }
            ywithoutMissingList.add( yi );
            rawDesignWithoutMissing[index++] = des.viewRow( i ).toArray();
        }
        double[] yWithoutMissing = ArrayUtils.toPrimitive( ywithoutMissingList.toArray( new Double[] {} ) );
        DenseDoubleMatrix2D yWithoutMissingAsMatrix = new DenseDoubleMatrix2D( new double[][] { yWithoutMissing } );

        DoubleMatrix2D designWithoutMissing = new DenseDoubleMatrix2D( rawDesignWithoutMissing );

        boolean mustReturn = false;
        List<Integer> droppedColumns = new ArrayList<Integer>();
        designWithoutMissing = this.cleanDesign( designWithoutMissing, yWithoutMissingAsMatrix.size(), droppedColumns );

        if ( designWithoutMissing.columns() == 0 || designWithoutMissing.columns() > designWithoutMissing.rows() ) {
            mustReturn = true;
        }

        if ( mustReturn ) {
            DoubleMatrix1D re = new DenseDoubleMatrix1D( des.columns() );
            re.assign( Double.NaN );
            this.qrs.add( null );
            this.residualDofs.add( countNonMissing - des.columns() );
            this.assigns.add( new ArrayList<Integer>() );
            // this.pivotIndicesList.add( new Integer[] {} );
            return re;
        }

        QRDecompositionPivoting rqr = null;
        if ( missing ) {
            rqr = new QRDecompositionPivoting( designWithoutMissing );
        } else {
            // in the case of weighted least squares, the Design matrix has different weights
            // for every row observation, so recompute qr everytime
            if ( this.qr == null || !this.A.equals( des ) ) {
                qr = new QRDecompositionPivoting( des );
            }
            rqr = qr;
        }

        this.qrs.add( rqr );

        int pivots = rqr.getRank();
        // this.residualDof = b.columns() - A.columns();
        // this.pivotIndicesList.add( pi );
        // / int rdof = yWithoutMissingAsMatrix.size() - designWithoutMissing.columns();
        int rdof = yWithoutMissingAsMatrix.size() - pivots;
        this.residualDofs.add( rdof );

        DoubleMatrix2D coefs = rqr.solve( solver.transpose( yWithoutMissingAsMatrix ) );

        /*
         * Put NaNs in for missing coefficients that were dropped from our estimation.
         */
        if ( designWithoutMissing.columns() < des.columns() ) {
            DoubleMatrix1D col = coefs.viewColumn( 0 );
            DoubleMatrix1D result = new DenseDoubleMatrix1D( des.columns() );
            result.assign( Double.NaN );
            int k = 0;
            List<Integer> assignForRow = new ArrayList<Integer>();
            for ( int i = 0; i < des.columns(); i++ ) {
                if ( droppedColumns.contains( i ) ) {
                    // leave it as NaN.
                    continue;
                }
                assignForRow.add( this.assign.get( i ) );
                assert k < col.size();
                result.set( i, col.get( k ) );
                k++;
            }
            assigns.add( assignForRow );
            return result;
        }
        assigns.add( this.assign );
        return coefs.viewColumn( 0 );

    }

    /**
     * @param ssq
     * @param dof
     * @param fStats
     * @param pvalues
     * @return
     */
    private List<GenericAnovaResult> summarizeAnova( DoubleMatrix2D ssq, DoubleMatrix2D dof, DoubleMatrix2D fStats,
            DoubleMatrix2D pvalues ) {

        assert ssq != null;
        assert dof != null;
        assert fStats != null;
        assert pvalues != null;

        List<GenericAnovaResult> results = new ArrayList<GenericAnovaResult>();
        for ( int i = 0; i < fStats.rows(); i++ ) {
            Collection<AnovaEffect> efs = new ArrayList<AnovaEffect>();

            /*
             * Don't put in ftest results for the residual thus the -1.
             */
            for ( int j = 0; j < fStats.columns() - 1; j++ ) {
                String effectName = terms.get( j );
                assert effectName != null;
                AnovaEffect ae = new AnovaEffect( effectName, pvalues.get( i, j ), fStats.get( i, j ), ( int ) dof.get(
                        i, j ), ssq.get( i, j ), effectName.contains( ":" ) );
                efs.add( ae );
            }

            /*
             * Add residual
             */
            int residCol = fStats.columns() - 1;
            AnovaEffect ae = new AnovaEffect( "Residual", null, null, ( int ) dof.get( i, residCol ), ssq.get( i,
                    residCol ), false );
            efs.add( ae );

            GenericAnovaResult ao = new GenericAnovaResult( efs );
            if ( this.rowNames != null ) ao.setKey( this.rowNames.get( i ) );
            results.add( ao );
        }
        return results;
    }

    /**
     * Internal function that does the hard work. The weighted version which works like 'lm.wfit()' in R. TODO cleanup,
     * forget about sigma!
     */
    private void wlsf() {

        Algebra solver = new Algebra();

        /*
         * weight A and b: wts <- sqrt(w) A * wts, row * wts
         */
        ArrayList<DoubleMatrix2D> AwList = new ArrayList<DoubleMatrix2D>( b.rows() );
        ArrayList<DoubleMatrix1D> bList = new ArrayList<DoubleMatrix1D>( b.rows() );
        for ( int i = 0; i < b.rows(); i++ ) {
            DoubleMatrix1D wts = this.weights.viewRow( i ).copy().assign( Functions.sqrt );
            DoubleMatrix1D bw = b.viewRow( i ).copy().assign( wts, Functions.mult );
            DoubleMatrix2D Aw = A.copy();
            for ( int j = 0; j < Aw.columns(); j++ ) {
                Aw.viewColumn( j ).assign( wts, Functions.mult );
            }
            AwList.add( Aw );
            bList.add( bw );
        }

        double[][] rawResult = new double[b.rows()][];

        /*
         * Missing values result in the addition of a fair amount of extra code.
         */
        if ( this.hasMissing ) {

            for ( int i = 0; i < b.rows(); i++ ) {
                DoubleMatrix1D bw = bList.get( i );
                DoubleMatrix2D Aw = AwList.get( i );
                DoubleMatrix1D withoutMissing = ordinaryLeastSquaresWithMissing( bw, Aw );
                if ( withoutMissing == null ) {
                    rawResult[i] = new double[A.columns()];
                } else {
                    rawResult[i] = withoutMissing.toArray();
                }
            }

        } else {

            // do QR for each row because A is scaled by different row weights
            // see lm.series() in R
            // FIXME Find a way to speed this up
            for ( int i = 0; i < b.rows(); i++ ) {
                DoubleMatrix1D bw = bList.get( i );
                DoubleMatrix2D Aw = AwList.get( i );
                DoubleMatrix2D bw2D = new DenseDoubleMatrix2D( 1, bw.size() );
                bw2D.viewRow( 0 ).assign( bw );
                this.qr = new QRDecompositionPivoting( Aw );
                this.qrs.add( this.qr );
                rawResult[i] = qr.solve( solver.transpose( bw2D ) ).viewColumn( 0 ).toArray();
                this.residualDof = bw.size() - qr.getRank();
                if ( residualDof <= 0 ) {
                    throw new IllegalArgumentException( "No residual degrees of freedom to fit the model"
                            + diagnosis( qr ) );
                }
            }
        }

        this.coefficients = solver.transpose( new DenseDoubleMatrix2D( rawResult ) );

        assert this.assign.isEmpty() || this.assign.size() == this.coefficients.rows() : assign.size()
                + " != # coefficients " + this.coefficients.rows();
        assert this.coefficients.rows() == A.columns();

        this.fitted = solver.transpose( MatrixUtil.multWithMissing( A, coefficients ) );

        if ( this.hasMissing ) {
            MatrixUtil.maskMissing( b, fitted );
        }

        this.residuals = b.copy().assign( fitted, Functions.minus );
    }
}
