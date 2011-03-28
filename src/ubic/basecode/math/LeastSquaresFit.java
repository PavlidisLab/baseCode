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

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import cern.colt.matrix.linalg.SingularValueDecomposition;
import cern.jet.math.Functions;
import cern.jet.stat.Descriptive;

/**
 * For performing "bulk" linear model fits. Allows rapid fitting to large data sets.
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
    private int residualDof;

    /*
     * Lists which factors (terms) are associated with which columns of the design matrix; 0 indicates the intercept.
     */
    private List<Integer> assign;

    /*
     * Names of the factors (terms)
     */
    private List<String> terms;

    private DesignMatrix designMatrix;

    /*
     * Optional, but useful
     */
    private List<String> rowNames;

    /*
     * Used if we have missing values so QR might be different for each row (possible optimization to consider: only
     * store set of variants that are actually used)
     */
    private List<QRDecomposition> qrs = new ArrayList<QRDecomposition>();

    /*
     * Used if we have missing value so RDOF might be different for each row (we can actually get away without this)
     */
    private List<Integer> residualDofs = new ArrayList<Integer>();

    private boolean hasIntercept = true;
    private List<List<Integer>> assigns = new ArrayList<List<Integer>>();

    private static Log log = LogFactory.getLog( LeastSquaresFit.class );

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
        assert hasInterceptTerm == this.hasIntercept;
        lsf();
    }

    /**
     * Stripped-down interface for simple use. ANOVA not possible (use the other constructors)
     * 
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

        /*
         * FIXME should do this.cleanDesign
         */

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

        /*
         * FIXME should do this.cleanDesign
         */

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

        /*
         * FIXME should do this.cleanDesign
         */

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

        /*
         * FIXME should do this.cleanDesign
         */

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
        DoubleMatrix1D residualSumsOfSquares = MatrixUtil.multWithMissing( residuals.copy().assign( Functions.square ),
                ones );

        DoubleMatrix2D q = this.qr.getQ();

        DoubleMatrix2D effects = null;
        if ( this.hasMissing ) {
            effects = new DenseDoubleMatrix2D( this.b.rows(), q.columns() );
            effects.assign( Double.NaN );
            for ( int i = 0; i < this.b.rows(); i++ ) {
                QRDecomposition qrd = this.qrs.get( i );
                if ( qrd == null ) {
                    // means we did not get a fit
                    for ( int j = 0; j < effects.columns(); j++ ) {
                        effects.set( i, j, Double.NaN );
                    }
                    continue;
                }

                DoubleMatrix2D qrow = qrd.getQ();
                DoubleMatrix1D brow = b.viewRow( i );

                DoubleMatrix1D crow = MatrixUtil.multWithMissing( MatrixUtil.removeMissing( brow ), qrow );

                for ( int j = 0; j < crow.size(); j++ ) {
                    effects.set( i, j, crow.get( j ) );
                }
            }

        } else {
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
                 * Accumulate the sum.
                 */
                if ( !Double.isNaN( valueToAdd ) ) {
                    /*
                     * Is this always true?
                     */
                    ssq.set( i, col, ssq.get( i, col ) + valueToAdd );
                    dof.set( i, col, dof.get( i, col ) + 1 );
                } else {
                    log.warn( "Missing value in effects for row " + i
                            + ( this.rowNames == null ? "" : " (row=" + this.rowNames.get( i ) + ")" ) );
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

    public DoubleMatrix2D getResiduals() {
        return residuals;
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
     * @param anova
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
     * @param dof
     * @param fStats
     * @param denominator
     * @param pvalues
     */
    private void computeStats( DoubleMatrix2D dof, DoubleMatrix2D fStats, DoubleMatrix1D denominator,
            DoubleMatrix2D pvalues ) {
        pvalues.assign( Double.NaN );
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

                fStats.set( i, j, fStats.get( i, j ) / denominator.get( i ) );
                try {
                    FDistribution pf = new FDistributionImpl( ndof, rdof );
                    pvalues.set( i, j, 1.0 - pf.cumulativeProbability( fStats.get( i, j ) ) );
                } catch ( MathException e ) {
                    pvalues.set( i, j, Double.NaN );
                }

            }
        }
    }

    /**
     * Internal function that does the hard work.
     */
    private void lsf() {

        this.residualDof = b.columns() - A.columns();

        if ( residualDof <= 0 ) {
            throw new IllegalArgumentException( "No residual degrees of freedom to fit the model" );
        }

        checkForMissingValues();

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
            qr = new QRDecomposition( A );
            this.coefficients = qr.solve( solver.transpose( b ) );
        }

        assert this.coefficients.rows() == A.columns();

        this.fitted = solver.transpose( MatrixUtil.multWithMissing( A, coefficients ) );

        if ( this.hasMissing ) {
            MatrixUtil.maskMissing( b, fitted );
        }

        this.residuals = b.copy().assign( fitted, Functions.minus );
    }

    /**
     * 
     */
    private void checkForMissingValues() {
        for ( int i = 0; i < b.rows(); i++ ) {
            for ( int j = 0; j < b.columns(); j++ ) {
                if ( Double.isNaN( b.get( i, j ) ) ) {
                    this.hasMissing = true;
                    break;
                }
            }
        }
    }

    /**
     * Has side effect of filling in this.qrs and this.residualDofs.
     * 
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
             * return nothing.
             */
            DoubleMatrix1D re = new DenseDoubleMatrix1D( des.columns() );
            re.assign( Double.NaN );
            log.debug( "Not enough non-missing values" );
            this.qrs.add( null );
            this.residualDofs.add( countNonMissing - des.columns() );
            this.assigns.add( new ArrayList<Integer>() );
            return re;
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

        boolean mustReturn = false;
        List<Integer> droppedColumns = new ArrayList<Integer>();
        designWithoutMissing = this.cleanDesign( designWithoutMissing, yp.size(), droppedColumns );

        if ( designWithoutMissing.columns() == 0 ) {
            mustReturn = true;
        }

        if ( mustReturn ) {
            DoubleMatrix1D re = new DenseDoubleMatrix1D( des.columns() );
            re.assign( Double.NaN );
            this.qrs.add( null );
            this.residualDofs.add( countNonMissing - des.columns() );
            this.assigns.add( new ArrayList<Integer>() );
            return re;
        }

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
        int rdof = yp.size() - designWithoutMissing.columns();
        this.residualDofs.add( rdof );

        DoubleMatrix2D x = rqr.solve( solver.transpose( yp ) );

        /*
         * Put NaNs in for missing coefficients that were dropped from our estimation.
         */
        if ( designWithoutMissing.columns() < des.columns() ) {
            DoubleMatrix1D col = x.viewColumn( 0 ).copy();
            DoubleMatrix1D result = new DenseDoubleMatrix1D( des.columns() );
            result.assign( Double.NaN );
            int k = 0;
            List<Integer> assignForRow = new ArrayList<Integer>();
            for ( int i = 0; i < des.columns(); i++ ) {
                if ( droppedColumns.contains( i ) ) {
                    result.set( i, Double.NaN ); // not necessary
                    continue;
                }
                assignForRow.add( assign.get( i ) );
                result.set( i, col.get( k ) );
                k++;
            }
            assigns.add( assignForRow );
            return result;
        }
        assigns.add( assign );
        return x.viewColumn( 0 );

    }

    /**
     * Drop, and track non-pivotal columns, starting (generally) from the right side. We first remove constant columns;
     * then identify deficient rank.
     * <p>
     * FIXME slow?
     * 
     * @param design
     * @param ypsize
     * @param droppedColumns
     * @return
     */
    private DoubleMatrix2D cleanDesign( final DoubleMatrix2D design, int ypsize, List<Integer> droppedColumns ) {

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
                droppedColumns.add( j );
            }
        }

        DoubleMatrix2D returnValue = MatrixUtil.dropColumns( design, droppedColumns );

        SingularValueDecomposition svd = new SingularValueDecomposition( design );
        int rank = svd.rank();
        while ( rank > 0 && rank < returnValue.columns() ) {
            droppedColumns.add( returnValue.columns() - 1 );
            returnValue = returnValue.viewPart( 0, 0, returnValue.rows(), returnValue.columns() - 1 );
            log.info( "Dropping column " + ( returnValue.columns() - 1 ) );
            if ( returnValue.columns() == 0 ) {
                rank = 0;
                break;
            }
            svd = new SingularValueDecomposition( returnValue );
            rank = svd.rank();

            if ( rank == returnValue.columns() ) {
                break;
            }
        }

        return returnValue;
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

        QRDecomposition qrd = null;
        if ( this.qrs.isEmpty() ) {
            qrd = this.qr; // no missing values, so it's global
        } else {
            qrd = this.qrs.get( i ); // row-specific
        }

        if ( qrd == null ) {
            log.warn( "QR was null for item " + i );
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

        DoubleMatrix1D r = MatrixUtil.removeMissing( this.residuals.viewRow( i ) );
        DoubleMatrix1D f = MatrixUtil.removeMissing( fitted.viewRow( i ) );
        DoubleMatrix1D est = MatrixUtil.removeMissing( coefficients.viewColumn( i ) );

        int p = est.size();
        int n = qrd.getQ().rows();
        assert rdf == n - p;

        double mss;
        if ( hasIntercept ) {
            mss = f.copy().assign( Functions.minus( Descriptive.mean( new DoubleArrayList( f.toArray() ) ) ) ).assign(
                    Functions.square ).aggregate( Functions.plus, Functions.identity );
        } else {
            mss = f.copy().assign( Functions.square ).aggregate( Functions.plus, Functions.identity );
        }

        double rss = r.copy().assign( Functions.square ).aggregate( Functions.plus, Functions.identity );
        double resvar = rss / rdf;

        /*
         * If any of the diagnonal values of R are zero, we can't go on. R is upper triangular, so a zero on the
         * diagonal means the R'R will be singular; however, this should have been fixed during the model setup. Note
         * that Colt QR gives us R that is "full rank" but it's not really.
         */
        DoubleMatrix2D qrdR = qrd.getR();

        DoubleMatrix2D mult = solver.mult( solver.transpose( qrdR ), qrdR );

        // this will fail if we have non-pivotal columns in the R matrix.
        DoubleMatrix2D R = solver.inverse( mult );

        DoubleMatrix<String, String> coeffMat = DoubleMatrixFactory.dense( qrdR.columns(), 4 );
        coeffMat.assign( Double.NaN );

        coeffMat.setColumnNames( Arrays.asList( new String[] { "Estimate", "Std. Error", "t value", "Pr(>|t|)" } ) );

        DoubleMatrix1D se = MatrixUtil.diagonal( R ).assign( Functions.mult( resvar ) ).assign( Functions.sqrt );
        DoubleMatrix1D tval = est.copy().assign( se, Functions.div );
        TDistribution tdist = new TDistributionImpl( rdf );
        for ( int ti = 0; ti < tval.size(); ti++ ) {
            coeffMat.set( ti, 0, est.get( ti ) );
            coeffMat.set( ti, 1, se.get( ti ) );
            coeffMat.set( ti, 2, tval.get( ti ) );
            String dmcolname = this.designMatrix.getMatrix().getColNames().get( ti );
            /* FIXME the contrast should be stored in there. */
            coeffMat.addRowName( dmcolname );
            try {
                double pval = 2.0 * ( 1.0 - tdist.cumulativeProbability( Math.abs( tval.get( ti ) ) ) );
                coeffMat.set( ti, 3, pval );
            } catch ( MathException e ) {
                coeffMat.set( ti, 3, Double.NaN );
            }
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
            adjRsquared = 1 - ( 1 - rsquared * ( ( n - dfint ) / rdf ) );
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

}
