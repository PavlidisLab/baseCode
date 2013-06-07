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

import static cern.jet.math.Functions.chain;
import static cern.jet.math.Functions.div;
import static cern.jet.math.Functions.log2;
import static cern.jet.math.Functions.minus;
import static cern.jet.math.Functions.mult;
import static cern.jet.math.Functions.plus;
import static cern.jet.math.Functions.sqrt;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Ellipse2D;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.math.ArgumentOutsideDomainException;
import org.apache.commons.math.MathException;
import org.apache.commons.math.analysis.interpolation.LinearInterpolator;
import org.apache.commons.math.analysis.interpolation.LoessInterpolator;
import org.apache.commons.math.analysis.polynomials.PolynomialSplineFunction;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import cern.colt.function.IntIntDoubleFunction;
import cern.colt.list.DoubleArrayList;
import cern.colt.list.IntArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;
import cern.colt.matrix.linalg.Algebra;
import cern.jet.stat.Descriptive;

/**
 * Estimate mean-variance relationship and use this to compute weights for least squares fitting. R's limma.voom()
 * Charity Law and Gordon Smyth. Running voom() on data matrices with NaNs are not currently supported.
 * 
 * @author ptan
 * @version $Id$
 */
public class MeanVarianceEstimator {

    /**
     * Default loess span
     */
    public static final double BANDWIDTH = 0.5;

    /**
     * Default number of loess robustness iterations
     */
    public static final int ROBUSTNESS_ITERS = 3;

    /**
     * Normalized variables on log2 scale
     */
    private DoubleMatrix2D E;

    /**
     * Size of each library
     */
    private DoubleMatrix1D libSize;

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

    private static Log log = LogFactory.getLog( MeanVarianceEstimator.class );

    /**
     * Similar implementation of R's stats.approxfun(..., rule = 2) where values outside the interval ['min(x)',
     * 'max(x)'] gets the value at the closest data extreme. Also performs sorting based on xTrain.
     * 
     * @param x the training set of x values
     * @param y the training set of y values
     * @param xInterpolate the set of x values to interpolate
     * @return yInterpolate the interpolated set of y values
     */
    public static double[] approx( double[] x, double[] y, double[] xInterpolate ) {

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
            } catch ( ArgumentOutsideDomainException e ) {
                // this shouldn't happen anymore
                log.error( "Error occured while approximating values", e );
                yInterpolate[i] = Double.NaN;
            }
        }

        return yInterpolate;
    }

    /**
     * Calculates the library size by performing colSums(data). NaN values are omitted from calculations.
     * 
     * @param data raw counts
     * @return total counts for each column
     */
    public static DoubleMatrix1D colSums( DoubleMatrix2D data ) {
        assert data != null;
        DoubleMatrix1D libSize = new DenseDoubleMatrix1D( data.columns() );
        for ( int i = 0; i < libSize.size(); i++ ) {
            libSize.set( i, DescriptiveWithMissing.sum( new DoubleArrayList( data.viewColumn( i ).toArray() ) ) );
        }
        return libSize;
    }

    /**
     * Convert counts to log2 counts-per-million In R: y <- t(log2(t(counts+0.5)/(lib.size+1)*1e6))
     * 
     * @param counts raw counts
     * @param libSize library size
     * @return normalized counts
     */
    public static DoubleMatrix2D countsPerMillion( DoubleMatrix2D counts, DoubleMatrix1D libSize ) {
        assert counts != null;
        assert libSize != null;

        DoubleMatrix2D cpm = counts.copy();

        cpm.assign( plus( 0.5 ) );
        cpm = cpm.viewDice();
        DoubleMatrix1D cpmDenom = libSize.copy().assign( plus( 1 ) );
        for ( int i = 0; i < cpm.columns(); i++ ) {
            cpm.viewColumn( i ).assign( cpmDenom, div );
        }
        cpm.assign( chain( log2, mult( Math.pow( 10, 6 ) ) ) );
        cpm = cpm.viewDice();

        return cpm;
    }

    /**
     * Preferred interface if you want control over how the design is set up. Executes voom() to calculate weights.
     * 
     * @param designMatrix
     * @param data a count matrix
     */
    public MeanVarianceEstimator( DesignMatrix designMatrix, DoubleMatrix<String, String> data ) {

        DoubleMatrix2D b = new DenseDoubleMatrix2D( data.asArray() );
        this.libSize = MeanVarianceEstimator.colSums( b );
        this.E = MeanVarianceEstimator.countsPerMillion( b, libSize );

        mv();
        voom( designMatrix.getDoubleMatrix() );
    }

    /**
     * Executes voom() to calculate weights.
     * 
     * @param designMatrix
     * @param data a count matrix
     */
    public MeanVarianceEstimator( DesignMatrix designMatrix, DoubleMatrix2D data ) {

        this.libSize = MeanVarianceEstimator.colSums( data );
        this.E = MeanVarianceEstimator.countsPerMillion( data, libSize );

        mv();
        voom( designMatrix.getDoubleMatrix() );
    }

    /**
     * Generic method for calculating mean, variance and the loess fit. Calls this.countsPerMillion() to transform the
     * data. voom() is not executed and therefore no weights are calculated.
     * 
     * @param designMatrix
     * @param data a count matrix
     */
    public MeanVarianceEstimator( DoubleMatrix2D data ) {

        this.libSize = MeanVarianceEstimator.colSums( data );
        this.E = MeanVarianceEstimator.countsPerMillion( data, libSize );

        mv();
    }

    /**
     * @return total library size
     */
    public DoubleMatrix1D getLibSize() {
        return this.libSize;
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
     * Writes out the meanVariance scatter plot.
     * 
     * @param outputFilename
     */
    public void plot( String outputFilename ) {

        XYDataset dataset = createPlotDataset();

        JFreeChart chart = ChartFactory.createScatterPlot( "Mean-variance trend", "mean = log2(counts + 0.5)",
                "variance = sqrt(sigma)", dataset, PlotOrientation.VERTICAL, false, false, false );

        // customize look
        final XYPlot plot = chart.getXYPlot();
        plot.setBackgroundPaint( Color.white );
        final XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint( 0, Color.black );
        renderer.setSeriesPaint( 1, Color.red );
        renderer.setSeriesShapesFilled( 0, false );
        renderer.setSeriesShape( 0, new Ellipse2D.Double( 0, 0, 3, 3 ) );
        renderer.setSeriesStroke( 1, new BasicStroke( 4 ) );
        renderer.setSeriesShapesVisible( 0, true );
        renderer.setSeriesLinesVisible( 0, false );
        renderer.setSeriesLinesVisible( 1, true );
        renderer.setSeriesShapesVisible( 1, false );

        plot.setRenderer( 0, renderer );

        try {
            int size = 2 * 200;
            OutputStream os = new FileOutputStream( outputFilename );
            ChartUtilities.writeChartAsPNG( os, chart, size, size );
            os.close();
        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }

    }

    /**
     * Prepares mean-variance data matrix to JFree format
     */
    private XYDataset createPlotDataset() {
        assert meanVariance != null;
        assert meanVariance.columns() == 2;

        // mean and variance of each gene
        XYSeries series = new XYSeries( "Mean-variance" );
        for ( int i = 0; i < meanVariance.rows(); i++ ) {
            series.add( meanVariance.get( i, 0 ), meanVariance.get( i, 1 ) );
        }

        // loess fit trend line
        XYSeries loessSeries = new XYSeries( "Loess" );
        for ( int i = 0; i < loess.rows(); i++ ) {
            loessSeries.add( loess.get( i, 0 ), loess.get( i, 1 ) );
        }

        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries( series );
        dataset.addSeries( loessSeries );

        return dataset;
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
        Map<Double, Double> map = new TreeMap<Double, Double>();
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
        // Note: we start to loose some precision here in comparison with R's loess
        DoubleMatrix2D loessFit = new DenseDoubleMatrix2D( xyChecked.rows(), xyChecked.columns() );
        try {
            // fit a loess curve
            LoessInterpolator loessInterpolator = new LoessInterpolator( MeanVarianceEstimator.BANDWIDTH,
                    MeanVarianceEstimator.ROBUSTNESS_ITERS );

            double[] loessY = loessInterpolator.smooth( xyChecked.viewColumn( 0 ).toArray(), xyChecked.viewColumn( 1 )
                    .toArray() );

            loessFit.viewColumn( 0 ).assign( xyChecked.viewColumn( 0 ) );
            loessFit.viewColumn( 1 ).assign( loessY );
        } catch ( MathException e ) {
            log.error( "Error occured while performing a loess fit", e );
            loessFit = null;
        }

        return loessFit;
    }

    /**
     * Performs row-wise mean (x) and variance (y) and performs a loess fit. Handles missing data.
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

        // fit a loess curve
        this.loess = loessFit( this.meanVariance );
    }

    /**
     * Performs the heavy duty work of calculating the weights. Throws an "IllegalArgumentException" if there are
     * missing values. See Bug 3383.
     * 
     * @param designMatrix
     */
    private void voom( DoubleMatrix2D designMatrix ) {
        assert designMatrix != null;
        assert this.meanVariance != null;
        assert this.E != null;
        assert this.libSize != null;

        Algebra solver = new Algebra();

        DoubleMatrix2D A = designMatrix;
        weights = new DenseDoubleMatrix2D( E.rows(), E.columns() );

        // perform a linear fit to obtain the mean-variance relationship
        // fit3<-lm(t(yCpm) ~ as.matrix(design.matrix[,2]))
        // or gFit <- lmFit(yCpm, design=design.matrix)
        LeastSquaresFit lsf = new LeastSquaresFit( A, E );
        if ( lsf.isHasMissing() ) {
            throw new IllegalArgumentException( "Missing values not supported." );
        }
        // calculate fit$Amean by doing rowSums(CPM) (see limma.getEAWP())
        DoubleMatrix1D Amean = this.meanVariance.viewColumn( 0 );

        // sx <- fit$Amean + mean(log2(lib.size+1)) - log2(1e6)
        DoubleMatrix1D sx = Amean.copy();
        sx.assign( plus( libSize.copy().assign( chain( log2, plus( 1 ) ) ).zSum() / libSize.size() ) );
        sx.assign( minus( Math.log( Math.pow( 10, 6 ) ) / Math.log( 2 ) ) );

        // help("MArrayLM-class")
        // fit$sigma <- sqrt(sum(out$residuals^2)/out$df.residual)
        // sy <- sqrt(fit$sigma)
        int dof = lsf.getResidualDof();
        assert dof != 0; // if you have missing values in the expression matrix, you'll get a 0
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
        sy.assign( chain( sqrt, div( dof ) ) );
        sy.assign( sqrt );

        // only accepts array in strictly increasing order (drop duplicates)
        // so combine sx and sy and sort
        DoubleMatrix2D voomXY = new DenseDoubleMatrix2D( sx.size(), 2 );
        voomXY.viewColumn( 0 ).assign( sx );
        voomXY.viewColumn( 1 ).assign( sy );
        DoubleMatrix2D fit = loessFit( voomXY );

        // quarterroot fitted counts
        DoubleMatrix2D fittedValues = null;
        QRDecompositionPivoting qr = new QRDecompositionPivoting( A );
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
        DoubleMatrix1D libSizePlusOne = libSize.assign( plus( 1 ) );
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
        double[] yInterpolate = MeanVarianceEstimator.approx( fit.viewColumn( 0 ).toArray(), fit.viewColumn( 1 )
                .toArray(), xInterpolate );

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
