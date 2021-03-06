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
package ubic.basecode.math.linearmodels;

import static cern.jet.math.Functions.chain;
import static cern.jet.math.Functions.div;
import static cern.jet.math.Functions.log2;
import static cern.jet.math.Functions.minus;
import static cern.jet.math.Functions.mult;
import static cern.jet.math.Functions.plus;
import static cern.jet.math.Functions.sqrt;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math3.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math3.analysis.polynomials.PolynomialSplineFunction;
import org.apache.commons.math3.exception.OutOfRangeException;

import cern.colt.function.IntIntDoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.stat.Descriptive;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.math.DescriptiveWithMissing;
import ubic.basecode.math.linalg.QRDecomposition;

/**
 * Estimate mean-variance relationship and use this to compute weights for least squares fitting. R's limma.voom()
 * Charity Law and Gordon Smyth. See Law et al.
 * {@link http://genomebiology.biomedcentral.com/articles/10.1186/gb-2014-15-2-r29}
 * <p>
 * Running voom() on data matrices with NaNs is not currently supported.
 * 
 * @author ptan
 */
public class MeanVarianceEstimator {

    /**
     * Default loess span (This is the default value used by limma-voom)
     */
    public static final double BANDWIDTH = 0.5;

    /**
     * Default number of loess robustness iterations; 0 is probably fine.
     */
    public static final int ROBUSTNESS_ITERS = 3;

    /**
     * Similar implementation of R's stats.approxfun(..., rule = 2) where values outside the interval ['min(x)',
     * 'max(x)'] gets the value at the closest data extreme. Also performs sorting based on xTrain.
     * 
     * @param x the training set of x values
     * @param y the training set of y values
     * @param xInterpolate the set of x values to interpolate
     * @return yInterpolate the interpolated set of y values
     */
    protected static double[] approx( double[] x, double[] y, double[] xInterpolate ) {

        assert x != null;
        assert y != null;
        assert xInterpolate != null;
        assert x.length == y.length;

        double[] yInterpolate = new double[xInterpolate.length];
        LinearInterpolator linearInterpolator = new LinearInterpolator();

        // make sure that x is strictly increasing
        DoubleMatrix2D matrix = new DenseDoubleMatrix2D( x.length, 2 );
        matrix.viewColumn( 0 ).assign( x );
        matrix.viewColumn( 1 ).assign( y );
        matrix = matrix.viewSorted( 0 );
        double[] sortedX = matrix.viewColumn( 0 ).toArray();
        double[] sortedY = matrix.viewColumn( 1 ).toArray();

        // make sure x is within the domain
        DoubleArrayList xList = new DoubleArrayList( sortedX );
        double x3ListMin = Descriptive.min( xList );
        double x3ListMax = Descriptive.max( xList );
        PolynomialSplineFunction fun = linearInterpolator.interpolate( sortedX, sortedY );
        for ( int i = 0; i < xInterpolate.length; i++ ) {
            try {
                // approx(...,rule=2)
                if ( xInterpolate[i] > x3ListMax ) {
                    yInterpolate[i] = fun.value( x3ListMax );
                } else if ( xInterpolate[i] < x3ListMin ) {
                    yInterpolate[i] = fun.value( x3ListMin );
                } else {
                    yInterpolate[i] = fun.value( xInterpolate[i] );
                }
            } catch ( OutOfRangeException e ) {
                // this shouldn't happen anymore
                yInterpolate[i] = Double.NaN;
            }
        }

        return yInterpolate;
    }

    /**
     * Normalized variables on log2 scale
     */
    private DoubleMatrix2D E;

    /**
     * Size of each library (column).
     */
    private DoubleMatrix1D librarySize;

    /**
     * Loess fit (x, y)
     */
    private DoubleMatrix2D loess;

    /**
     * Matrix that contains the mean and variance of the data. Matrix is sorted by increasing mean. Useful for plotting.
     * mean <- fit$Amean + mean(log2(lib.size+1)) - log2(1e6) variance <- sqrt(fit$sigma)
     */
    private DoubleMatrix2D meanVariance;

    /**
     * inverse variance weights
     */
    private DoubleMatrix2D weights = null;

    /**
     * Preferred interface if you want control over how the design is set up. Executes voom() to calculate weights.
     * 
     * @param designMatrix
     * @param data a normalized count matrix
     * @param librarySize library size (matrix column sum)
     */
    public MeanVarianceEstimator( DesignMatrix designMatrix, DoubleMatrix<String, String> data,
            DoubleMatrix1D librarySize ) {

        DoubleMatrix2D b = new DenseDoubleMatrix2D( data.asArray() );
        this.librarySize = librarySize;
        this.E = b;

        mv();
        voom( designMatrix.getDoubleMatrix() );
    }

    /**
     * Executes voom() to calculate weights.
     * 
     * @param designMatrix
     * @param data a normalized count matrix
     * @param librarySize library size (matrix column sum)
     */
    public MeanVarianceEstimator( DesignMatrix designMatrix, DoubleMatrix2D data, DoubleMatrix1D librarySize ) {

        this.librarySize = librarySize;
        this.E = data;

        mv();
        voom( designMatrix.getDoubleMatrix() );
    }

    /**
     * Generic method for calculating mean, variance and the loess fit. voom() is not executed and therefore no weights
     * are calculated.
     * 
     * @param data a normalized count matrix
     */
    public MeanVarianceEstimator( DoubleMatrix2D data ) {
        this.E = data;

        mv();
    }

    /**
     * @return total library size
     */
    public DoubleMatrix1D getLibrarySize() {
        return this.librarySize;
    }

    /**
     * @return the loess fit of the mean-variance relationship
     */
    public DoubleMatrix2D getLoess() {
        return this.loess;
    }

    /**
     * @return the mean and variance of the normalized data, columns 0 and 1 respectively
     */
    public DoubleMatrix2D getMeanVariance() {
        return this.meanVariance;
    }

    /**
     * @return log2 counts per million. t(log2(t(counts+0.5)/(lib.size+1)*1e6))
     */
    public DoubleMatrix2D getNormalizedValue() {
        return this.E;
    }

    /**
     * @return inverse variance weights or null if the DesignMatrix was not provided.
     */
    public DoubleMatrix2D getWeights() {
        return this.weights;
    }

    /**
     * First ensures that x values are strictly increasing and performs a loess fit afterwards. The loess fit are
     * determined by <code>BANDWIDTH</code> and <code>ROBUSTNESS_ITERS</code>.
     * 
     * @param xy
     * @return loessFit or null if there are less than 3 data points
     */
    private DoubleMatrix2D loessFit( DoubleMatrix2D xy ) {
        assert xy != null;

        DoubleMatrix1D sx = xy.viewColumn( 0 );
        DoubleMatrix1D sy = xy.viewColumn( 1 );
        Map<Double, Double> map = new TreeMap<>();
        for ( int i = 0; i < sx.size(); i++ ) {
            if ( Double.isNaN( sx.get( i ) ) || Double.isInfinite( sx.get( i ) ) || Double.isNaN( sy.get( i ) )
                    || Double.isInfinite( sy.get( i ) ) ) {
                continue;
            }
            map.put( sx.get( i ), sy.get( i ) );
        }
        DoubleMatrix2D xyChecked = new DenseDoubleMatrix2D( map.size(), 2 );
        xyChecked.viewColumn( 0 ).assign( ArrayUtils.toPrimitive( map.keySet().toArray( new Double[0] ) ) );
        xyChecked.viewColumn( 1 ).assign( ArrayUtils.toPrimitive( map.values().toArray( new Double[0] ) ) );

        // in R:
        // loess(c(1:5),c(1:5)^2,f=0.5,iter=3)
        // Note: we start to loose some precision here in comparison with R's loess FIXME why? does it matter?
        DoubleMatrix2D loessFit = new DenseDoubleMatrix2D( xyChecked.rows(), xyChecked.columns() );
        // try {
        // fit a loess curve
        LoessInterpolator loessInterpolator = new LoessInterpolator( MeanVarianceEstimator.BANDWIDTH,
                MeanVarianceEstimator.ROBUSTNESS_ITERS );

        double[] loessY = loessInterpolator.smooth( xyChecked.viewColumn( 0 ).toArray(),
                xyChecked.viewColumn( 1 ).toArray() );

        loessFit.viewColumn( 0 ).assign( xyChecked.viewColumn( 0 ) );
        loessFit.viewColumn( 1 ).assign( loessY );

        return loessFit;
    }

    /**
     * Performs row-wise mean (x) and variance (y) and performs a loess fit. Note this lowess fit is different than the
     * one we use with voom, which is fit to the quarter-root variances (as per Smythe; tends to be more symmetric).
     * Handles missing data.
     * <p>
     * FIXME I'm not sure the lowess fits are useful since we don't use them for analysis, and they are suboptimal being
     * fit to the variance rather than the quarter-root variance.
     */
    private void mv() {
        assert this.E != null;

        // mean-variance
        DoubleMatrix1D Amean = new DenseDoubleMatrix1D( E.rows() );
        DoubleMatrix1D variance = Amean.like();
        for ( int i = 0; i < Amean.size(); i++ ) {
            DoubleArrayList row = new DoubleArrayList( E.viewRow( i ).toArray() );
            double rowMean = DescriptiveWithMissing.mean( row );
            double rowVar = DescriptiveWithMissing.variance( row );
            Amean.set( i, rowMean );
            variance.set( i, rowVar );

        }

        this.meanVariance = new DenseDoubleMatrix2D( E.rows(), 2 );
        this.meanVariance.viewColumn( 0 ).assign( Amean );
        this.meanVariance.viewColumn( 1 ).assign( variance );

        /*
         * fit a loess curve.
         */
        this.loess = loessFit( this.meanVariance );
    }

    /**
     * Performs the heavy duty work of calculating the weights. See Law et al.
     * {@link http://genomebiology.biomedcentral.com/articles/10.1186/gb-2014-15-2-r29}
     * 
     * @param designMatrix
     * @throws IllegalArgumentException if there are missing values.
     */
    private void voom( DoubleMatrix2D designMatrix ) {
        assert designMatrix != null;
        assert this.meanVariance != null;
        assert this.E != null;
        assert this.librarySize != null;

        Algebra solver = new Algebra();

        DoubleMatrix2D A = designMatrix;
        weights = new DenseDoubleMatrix2D( E.rows(), E.columns() );

        // perform a linear fit to obtain the mean-variance relationship
        // fit3<-lm(t(yCpm) ~ as.matrix(design.matrix[,2]))
        // or gFit <- lmFit(yCpm, design=design.matrix)
        LeastSquaresFit lsf = new LeastSquaresFit( A, E );

        // calculate fit$Amean by doing rowSums(CPM) (see limma.getEAWP())
        DoubleMatrix1D Amean = this.meanVariance.viewColumn( 0 );

        // sx <- fit$Amean + mean(log2(lib.size+1)) - log2(1e6)
        DoubleMatrix1D sx = Amean.copy();
        sx.assign( plus( librarySize.copy().assign( chain( log2, plus( 1 ) ) ).zSum() / librarySize.size() ) );
        sx.assign( minus( Math.log( Math.pow( 10, 6 ) ) / Math.log( 2 ) ) );

        // help("MArrayLM-class")
        // fit$sigma <- sqrt(sum(out$residuals^2)/out$df.residual)
        // sy <- sqrt(fit$sigma)
        DoubleMatrix2D residuals = lsf.getResiduals();
        DoubleMatrix1D sy = new DenseDoubleMatrix1D( residuals.rows() );
        for ( int row = 0; row < residuals.rows(); row++ ) {
            double sum = 0;
            for ( int column = 0; column < residuals.columns(); column++ ) {
                Double val = residuals.get( row, column );
                if ( !Double.isNaN( val ) ) {
                    sum += val * val;
                }
            }
            sy.set( row, sum );
        }
        // if you have missing values in the expression matrix
        // you'll get a residual dof of 0
        if ( lsf.isHasMissing() ) {
            // calculate it per row
            List<Integer> dofs = lsf.getResidualDofs();
            assert dofs.size() == sy.size();
            for ( int i = 0; i < sy.size(); i++ ) {
                sy.set( i, Math.sqrt( sy.get( i ) / dofs.get( i ) ) );
            }
        } else {
            int dof = lsf.getResidualDof();
            assert dof != 0;
            sy.assign( chain( sqrt, div( dof ) ) );
        }
        sy.assign( sqrt ); // we're fitting the quarter-root variances.

        // only accepts array in strictly increasing order (drop duplicates)
        // so combine sx and sy and sort
        DoubleMatrix2D voomXY = new DenseDoubleMatrix2D( sx.size(), 2 );
        voomXY.viewColumn( 0 ).assign( sx );
        voomXY.viewColumn( 1 ).assign( sy );
        DoubleMatrix2D fit = loessFit( voomXY );

        // quarterroot fitted counts
        DoubleMatrix2D fittedValues = null;
        QRDecomposition qr = new QRDecomposition( A );
        DoubleMatrix2D coeff = lsf.getCoefficients();

        if ( qr.getRank() < A.columns() ) {
            // j <- fit$pivot[1:fit$rank]
            // fitted.values <- fit$coef[,j,drop=F] %*% t(fit$design[,j,drop=F]);
            IntArrayList pivot = qr.getPivotOrder();

            IntArrayList subindices = ( IntArrayList ) pivot.partFromTo( 0, qr.getRank() - 1 );
            int[] coeffAllCols = new int[coeff.columns()];
            int[] desAllRows = new int[A.rows()];
            for ( int i = 0; i < coeffAllCols.length; i++ ) {
                coeffAllCols[i] = i;
            }
            for ( int i = 0; i < desAllRows.length; i++ ) {
                desAllRows[i] = i;
            }
            DoubleMatrix2D coeffSlice = coeff.viewSelection( subindices.elements(), coeffAllCols );
            DoubleMatrix2D ASlice = A.viewSelection( desAllRows, subindices.elements() );
            fittedValues = solver.mult( coeffSlice.viewDice(), ASlice.viewDice() );
        } else {
            // fitted.values <- fit$coef %*% t(fit$design)
            fittedValues = solver.mult( coeff.viewDice(), A.viewDice() );
        }

        // fitted.cpm <- 2^fitted.values
        // fitted.count <- 1e-6 * t(t(fitted.cpm)*(lib.size+1))
        // fitted.logcount <- log2(fitted.count)
        DoubleMatrix2D fittedCpm = fittedValues.copy().forEachNonZero( new IntIntDoubleFunction() {
            @Override
            public double apply( int row, int column, double third ) {
                return Math.pow( 2, third );
            }
        } );
        DoubleMatrix2D fittedCount = fittedCpm.copy();
        DoubleMatrix1D libSizePlusOne = librarySize.assign( plus( 1 ) );
        for ( int i = 0; i < fittedCount.rows(); i++ ) {
            fittedCount.viewRow( i ).assign( libSizePlusOne, mult );
            fittedCount.viewRow( i ).assign( mult( Math.pow( 10, -6 ) ) );
        }
        DoubleMatrix2D fittedLogCount = fittedCount.copy().assign( log2 );

        // interpolate points using the loess curve
        // f <- approxfun(l, rule=2)
        // apply trend to individual observations
        // w <- 1 / f(fitted.logcount)^4
        // 2D to 1D
        double[] xInterpolate = new double[fittedLogCount.rows() * fittedLogCount.columns()];
        int idx = 0;
        for ( int col = 0; col < fittedLogCount.columns(); col++ ) {
            for ( int row = 0; row < fittedLogCount.rows(); row++ ) {
                xInterpolate[idx] = fittedLogCount.get( row, col );
                idx++;
            }
        }
        assert fit != null;
        double[] yInterpolate = MeanVarianceEstimator.approx( fit.viewColumn( 0 ).toArray(),
                fit.viewColumn( 1 ).toArray(), xInterpolate );

        // 1D to 2D
        idx = 0;
        for ( int col = 0; col < weights.columns(); col++ ) {
            for ( int row = 0; row < weights.rows(); row++ ) {
                weights.set( row, col, ( 1.0 / Math.pow( yInterpolate[idx], 4 ) ) );
                idx++;
            }
        }

    }
}
