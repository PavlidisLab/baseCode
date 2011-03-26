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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.apache.commons.math.distribution.TDistribution;
import org.apache.commons.math.distribution.TDistributionImpl;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;
import ubic.basecode.dataStructure.matrix.MatrixUtil;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.util.r.type.AnovaEffect;
import ubic.basecode.util.r.type.GenericAnovaResult;
import ubic.basecode.util.r.type.LinearModelSummary;
import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.colt.matrix.linalg.QRDecomposition;
import cern.jet.math.Functions;
import cern.jet.stat.Descriptive;

/**
 * For performing "bulk" linear model fits. This allows very rapid fitting to large data sets.
 * 
 * @author paul
 * @version $Id$
 */
public class LeastSquaresFit {

    static Algebra solver = new Algebra();
    private DoubleMatrix2D coefficients = null;
    private boolean hasMissing = false;
    private DoubleMatrix2D fitted;
    private DoubleMatrix2D residuals;

    private QRDecomposition qr;
    private DoubleMatrix2D A;
    private DoubleMatrix2D b;
    private int residualDof; // FIXME this really should be a vector, to deal with missingness.
    private List<Integer> assign;
    private List<String> terms;
    private DesignMatrix designMatrix;
    private List<String> rowNames;
    private List<QRDecomposition> qrs = new ArrayList<QRDecomposition>();
    private List<Integer> residualDofs = new ArrayList<Integer>();

    /**
     * @param A Design matrix, which will be used directly in least squares regression
     * @param b Data matrix
     */
    public LeastSquaresFit( DoubleMatrix2D A, DoubleMatrix2D b ) {
        this.A = A;
        this.b = b;
        lsf();
    }

    /**
     * @param sample information that will be converted to a design matrix; intercept term is added.
     * @param data Data matrix
     */
    public LeastSquaresFit( ObjectMatrix<String, String, Object> sampleInfo, DenseDoubleMatrix2D data ) {
        this.designMatrix = new DesignMatrix( sampleInfo, true );

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

        DoubleMatrix1D ones = new DenseDoubleMatrix1D( residuals.columns() );
        ones.assign( 1.0 );
        DoubleMatrix1D residualSumsOfSquares = solver.mult( residuals.copy().assign( Functions.square ), ones );

        DoubleMatrix2D q = this.qr.getQ();

        /*
         * Effects
         */
        DoubleMatrix2D comp = solver.mult( this.b, q );

        comp.assign( Functions.square );

        /*
         * Add up the ssr for the columns within each factor.
         */
        Set<Integer> facs = new TreeSet<Integer>();
        facs.addAll( assign );

        DoubleMatrix2D ssq = new DenseDoubleMatrix2D( comp.rows(), facs.size() + 1 );
        DoubleMatrix2D dof = new DenseDoubleMatrix2D( comp.rows(), facs.size() + 1 );
        ssq.assign( 0.0 );
        dof.assign( 0.0 );
        for ( int i = 0; i < ssq.rows(); i++ ) {
            ssq.set( i, facs.size(), residualSumsOfSquares.get( i ) );
            dof.set( i, facs.size(), residualDof );
        }

        boolean hasIntercept = false;
        for ( Integer col : assign ) {
            for ( int i = 0; i < comp.rows(); i++ ) {

                DoubleMatrix1D r = comp.viewRow( i );

                if ( col == 0 ) {
                    /*
                     * Intercept
                     */
                    hasIntercept = true;

                    ssq.set( i, col, r.get( 0 ) );

                } else {
                    /*
                     * These always start from 1, if there is an intercept we'll actually be at col 1, otherwise we are
                     * actually starting from zero.
                     */
                    int c = hasIntercept ? col : col - 1;

                    ssq.set( i, col, ssq.get( i, col ) + r.get( c ) );

                }
                dof.set( i, col, dof.get( i, col ) + 1 );
            }
        }

        DoubleMatrix2D fStats = ssq.copy().assign( dof, Functions.div );

        // one value for each term in the model
        DoubleMatrix1D denominator = residualSumsOfSquares.copy().assign( Functions.div( residualDof ) );

        DoubleMatrix2D pvalues = fStats.like();

        computeStats( dof, fStats, denominator, pvalues );

        return summarizeAnova( ssq, dof, fStats, pvalues );
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
        for ( int i = 0; i < fStats.rows(); i++ ) {
            for ( int j = 0; j < fStats.columns(); j++ ) {
                FDistribution pf = new FDistributionImpl( dof.get( i, j ), residualDof );

                if ( j == fStats.columns() - 1 ) {
                    // don't fill in f & p values for the residual...
                    pvalues.set( i, j, Double.NaN );
                    fStats.set( i, j, Double.NaN );
                    continue;
                }

                fStats.set( i, j, fStats.get( i, j ) / denominator.get( i ) );
                try {
                    pvalues.set( i, j, 1.0 - pf.cumulativeProbability( fStats.get( i, j ) ) );
                } catch ( MathException e ) {
                    pvalues.set( i, j, Double.NaN );
                }
            }
        }
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

    public DoubleMatrix2D getCoefficients() {
        return coefficients;
    }

    public DoubleMatrix2D getFitted() {
        return fitted;
    }

    public DoubleMatrix2D getResiduals() {
        return residuals;
    }

    public boolean isHasMissing() {
        return hasMissing;
    }

    public void setHasMissing( boolean hasMissing ) {
        this.hasMissing = hasMissing;
    }

    private void lsf() {

        this.residualDof = b.columns() - A.columns();

        if ( residualDof <= 0 ) {
            throw new IllegalArgumentException( "No residual degrees of freedom to fit the model" );
        }

        for ( int i = 0; i < b.rows(); i++ ) {
            for ( int j = 0; j < b.columns(); j++ ) {
                if ( Double.isNaN( b.get( i, j ) ) ) {
                    this.hasMissing = true;
                    break;
                }
            }
        }

        if ( this.hasMissing ) {

            double[][] rawResult = new double[b.rows()][];
            for ( int i = 0; i < b.rows(); i++ ) {
                DoubleMatrix1D row = b.viewRow( i );
                DoubleMatrix1D withoutMissing = ordinaryLeastSquaresWithMissing( row, A );
                rawResult[i] = withoutMissing.toArray();
            }
            this.coefficients = solver.transpose( new DenseDoubleMatrix2D( rawResult ) );
        } else {
            qr = new QRDecomposition( A );
            this.coefficients = qr.solve( solver.transpose( b ) );
        }

        assert this.coefficients.rows() == A.columns();

        this.fitted = solver.transpose( solver.mult( A, coefficients ) );

        if ( this.hasMissing ) {
            MatrixUtil.maskMissing( b, fitted );
        }

        // FIXME if there are missing values, this won't be right.
        this.residuals = b.copy().assign( fitted, Functions.minus );
    }

    /**
     * @return summaries. ANOVA will not be computed.
     */
    public List<LinearModelSummary> summarize() {
        return this.summarize( false );
    }

    /**
     * @param anova
     * @return
     */
    public Map<String, LinearModelSummary> summarizeByKeys( boolean anova ) {
        List<LinearModelSummary> summaries = this.summarize( anova );
        Map<String, LinearModelSummary> result = new HashMap<String, LinearModelSummary>();
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
     * Note: does not populate the ANOVA.
     * 
     * @param i
     * @return
     */
    protected LinearModelSummary summarize( int i ) {
        boolean hasInterceptTerm = this.terms.contains( "Intercept" );
        String key = null;
        if ( this.rowNames != null ) {
            key = this.rowNames.get( i );
        }

        QRDecomposition qrd = null;
        if ( this.qrs.isEmpty() ) {
            qrd = this.qr;
        } else {
            qrd = this.qrs.get( i );
        }

        int rdf;
        if ( this.residualDofs.isEmpty() ) {
            rdf = this.residualDof;
        } else {
            rdf = this.residualDofs.get( i );
        }

        int p = this.coefficients.rows();
        int n = qrd.getQ().rows();

        assert rdf == n - p;

        DoubleMatrix1D r = MatrixUtil.removeMissing( this.residuals.viewRow( i ) );
        DoubleMatrix1D f = MatrixUtil.removeMissing( fitted.viewRow( i ) );
        DoubleMatrix1D est = MatrixUtil.removeMissing( coefficients.viewColumn( i ) );

        double mss;
        if ( hasInterceptTerm ) {
            mss = f.copy().assign( Functions.minus( Descriptive.mean( new DoubleArrayList( f.toArray() ) ) ) ).assign(
                    Functions.square ).aggregate( Functions.plus, Functions.identity );
        } else {
            mss = f.copy().assign( Functions.square ).aggregate( Functions.plus, Functions.identity );
        }

        double rss = r.copy().assign( Functions.square ).aggregate( Functions.plus, Functions.identity );
        double resvar = rss / rdf;

        DoubleMatrix2D R = solver.inverse( solver.mult( solver.transpose( qrd.getR() ), qrd.getR() ) );

        DoubleMatrix<String, String> c = DoubleMatrixFactory.dense( coefficients.rows(), 4 );
        c.setRowNames( this.designMatrix.getMatrix().getColNames() );
        c.setColumnNames( Arrays.asList( new String[] { "Estimate", "Std. Error", "t value", "Pr(>|t|)" } ) );

        DoubleMatrix1D se = MatrixUtil.diagonal( R ).assign( Functions.mult( resvar ) ).assign( Functions.sqrt );
        DoubleMatrix1D tval = est.copy().assign( se, Functions.div );
        TDistribution tdist = new TDistributionImpl( rdf );
        for ( int ti = 0; ti < tval.size(); ti++ ) {
            c.set( ti, 2, tval.get( ti ) );
            c.set( ti, 0, est.get( ti ) );
            c.set( ti, 1, se.get( ti ) );
            try {
                double pval = 2.0 * ( 1.0 - tdist.cumulativeProbability( Math.abs( tval.get( ti ) ) ) );
                c.set( ti, 3, pval );
            } catch ( MathException e ) {
                c.set( ti, 3, Double.NaN );
            }
        }

        double rsquared = 0.0;
        double adjRsquared = 0.0;
        double fstatistic = 0.0;
        int numdf = 0;
        int dendf = 0;
        // is there anything besides the intercept???
        if ( terms.size() > 1 || !terms.contains( "Intercept" ) ) {
            int dfint = hasInterceptTerm ? 1 : 0;
            rsquared = mss / ( mss + rss );
            adjRsquared = 1 - ( 1 - rsquared * ( ( n - dfint ) / rdf ) );
            fstatistic = mss / ( p - dfint ) / resvar;

            numdf = p - dfint;
            dendf = rdf;

        } else {
            rsquared = 0.0;
            adjRsquared = 0.0;
        }

        if ( Double.isNaN( fstatistic ) ) {
            System.err.println( "Nan" );
        }

        LinearModelSummary lms = new LinearModelSummary( key, ArrayUtils.toObject( this.residuals.viewRow( i )
                .toArray() ), c, rsquared, adjRsquared, fstatistic, numdf, dendf, null );
        return lms;
    }

    /**
     * @param y
     * @param des
     * @return
     */
    private DoubleMatrix1D ordinaryLeastSquaresWithMissing( DoubleMatrix1D y, DoubleMatrix2D des ) {
        List<Double> r = new ArrayList<Double>( y.size() );
        int size = y.size();

        int countNonMissing = 0;
        for ( int i = 0; i < size; i++ ) {
            if ( !Double.isNaN( y.getQuick( i ) ) ) {
                countNonMissing++;
            }
        }

        if ( countNonMissing < 3 ) {
            /*
             * FIXME return nothing.
             */
        }

        double[][] rawDesignWithoutMissing = new double[countNonMissing][];
        int index = 0;
        boolean missing = false;
        for ( int i = 0; i < size; i++ ) {
            double yi = y.getQuick( i );
            if ( Double.isNaN( yi ) ) {
                missing = true;
                continue;
            }
            r.add( yi );
            rawDesignWithoutMissing[index++] = des.viewRow( i ).toArray();
        }
        double[] ypa = ArrayUtils.toPrimitive( r.toArray( new Double[] {} ) );
        DenseDoubleMatrix2D yp = new DenseDoubleMatrix2D( new double[][] { ypa } );

        DoubleMatrix2D designWithoutMissing = new DenseDoubleMatrix2D( rawDesignWithoutMissing );

        QRDecomposition rqr = null;
        if ( missing ) {
            rqr = new QRDecomposition( designWithoutMissing );
        } else {
            if ( this.qr == null ) {
                qr = new QRDecomposition( des );
            }
            rqr = qr;
        }
        this.qrs.add( rqr );
        this.residualDofs.add( yp.size() - designWithoutMissing.columns() );
        DoubleMatrix2D x = rqr.solve( solver.transpose( yp ) );
        return x.viewColumn( 0 );

    }

}
