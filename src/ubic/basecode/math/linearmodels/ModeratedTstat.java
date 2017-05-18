/*
 * The baseCode project
 * 
 * Copyright (c) 2017 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package ubic.basecode.math.linearmodels;

import java.util.List;

import org.apache.commons.math3.special.Gamma;

import cern.colt.matrix.DoubleMatrix1D;
import cern.colt.matrix.impl.DenseDoubleMatrix1D;
import cern.jet.math.Functions;
import ubic.basecode.math.SpecFunc;

/**
 * Implements methods described in
 * <p>
 * Smyth, G. K. (2004). Linear models and empirical Bayes methods for assessing differential expression in microarray
 * experiments. Statistical Applications in Genetics and Molecular Biology Volume 3, Issue 1, Article 3.
 * <p>
 * Important: does not handle missing values in the original data.
 * 
 * @author paul
 */
public class ModeratedTstat {

    /**
     * Does essentially the same thing as limma::ebayes. However: does not handle missing values; it assumes a single
     * value of residual degrees of freedom.
     * 
     * @param fit which will be modified
     */
    public static void ebayes( LeastSquaresFit fit ) {

        List<LinearModelSummary> summaries = fit.summarize();
        DoubleMatrix1D sigmas = new DenseDoubleMatrix1D( new double[summaries.size()] );
        int i = 0;
        Integer dof = 0;
        for ( LinearModelSummary lms : summaries ) {
            sigmas.set( i, lms.getSigma() );
            // Double[] stdevUnscaled = lms.getStdevUnscaled();
            Integer residualDof = lms.getResidualDof();
            dof = residualDof;
            i++;
        }

        //  need to handle a vector of dofs, really (missing values). or at least check it's constant
        squeezeVar( sigmas.copy().assign( Functions.square ), dof, fit );
    }

    /*
     * Return the scale and df2
     * TODO deal with missing/bad values.
     * TODO support covariate?
     */
    protected static double[] fitFDist( DoubleMatrix1D x, double df1 ) {

        // stay away from zero valuess
        x = x.copy().assign( Functions.max( 1e-5 ) );
        // z <- log(x)
        // e <- z-digamma(df1/2)+log(df1/2)
        DoubleMatrix1D e = x.copy().assign( Functions.log ).assign( Functions.minus( Gamma.digamma( df1 / 2.0 ) ) )
                .assign( Functions.plus( Math.log( df1 / 2.0 ) ) );

        int nok = x.size(); // number of oks - need to implement checks
        //  emean <- mean(e)
        double emean = e.copy().aggregate( Functions.plus, Functions.identity ) / nok;
        // evar <- sum((e-emean)^2)/(nok-1L)
        double evar = e.copy().assign( Functions.minus( emean ) ).aggregate( Functions.plus, Functions.square )
                / ( nok - 1 );

        evar = evar - Gamma.trigamma( df1 / 2.0 );

        double df2;
        double s20;
        if ( evar > 0 ) {
            // df2 <- 2*trigammaInverse(evar)
            df2 = 2 * SpecFunc.trigammaInverse( evar );

            // s20 <- exp(emean+digamma(df2/2)-log(df2/2)) 
            s20 = Math.exp( emean + Gamma.digamma( df2 / 2.0 ) - Math.log( df2 / 2.0 ) );

        } else {
            df2 = Double.POSITIVE_INFINITY;
            // s20 <- mean(x)
            s20 = x.copy().aggregate( Functions.plus, Functions.identity ) / x.size();
        }

        return new double[] { s20, df2 };
    }

    /**
     * Ignoring robust and covariate for now
     * 
     * @param var initial values of estimated residual variance = sigma^2 = rssq/rdof; this will be moderated
     * @param df
     * @param fit will be updated with new info; call fit.summarize() to get updated pvalues etc.
     * @return varPost for testing mostly
     */
    protected static DoubleMatrix1D squeezeVar( DoubleMatrix1D var, double df, LeastSquaresFit fit ) {

        double[] ffit = fitFDist( var, df );
        double dfPrior = ffit[1];

        DoubleMatrix1D varPost = squeezeVar( var, df, ffit );

        if ( fit != null )
            fit.ebayesUpdate( dfPrior, ffit[0], varPost );

        return varPost;
    }

    /**
     * @param var
     * @param df should be a vector of dfs but not sure if necessary.
     * @param fit
     * @return
     */
    private static DoubleMatrix1D squeezeVar( DoubleMatrix1D var, double df, double[] fit ) {
        double varPrior = fit[0];
        double dfPrior = fit[1];

        if ( Double.isFinite( df ) ) {

            return var.copy().assign( Functions.mult( df ) )
                    .assign( Functions.plus( dfPrior * varPrior ) ).assign( Functions.div( df + dfPrior ) );

        }
        throw new IllegalStateException( "not implemented case of infinite dof" );
    }

}
