/*
 * The baseCode project
 *
 * Copyright (c) 2010 University of British Columbia
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
package ubic.basecode.util.r;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.FDistribution;
import org.jspecify.annotations.Nullable;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;
import ubic.basecode.math.linearmodels.GenericAnovaResult;
import ubic.basecode.math.linearmodels.LinearModelSummary;
import ubic.basecode.math.linearmodels.LinearModelSummaryUtils;

import java.util.*;

/**
 * Represents the results of a linear model analysis from R. Both the summary.lm and anova objects are represented.
 *
 * @author paul
 */
class LinearModelSummaryImpl implements LinearModelSummary {

    private static final long serialVersionUID = 1L;

    /**
     * Name for this summary e.g. probe identifier
     */
    private final String key;
    private final double adjRSquared;
    @Nullable
    private final GenericAnovaResultImpl anovaResult;
    private final DoubleMatrix<String, String> contrastCoefficients;
    private final double denominatorDof;
    private final List<String> factorNames;
    private final double fStat;
    private final double numeratorDof;
    private final double[] residuals;
    private final double rSquared;
    private final Map<String, Collection<String>> term2CoefficientNames;
    // computed on-demand from final fields
    @Nullable
    private Double overallPValue = null;

    /**
     * Construct an empty summary. Use for model fits that fail due to 0 degrees of freedom, etc.
     *
     * @param key identifier
     */
    public LinearModelSummaryImpl( String key ) {
        this.key = key;
        this.factorNames = Collections.emptyList();
        this.contrastCoefficients = new DenseDoubleMatrix<>( new double[0][0] );
        this.anovaResult = null;
        this.adjRSquared = Double.NaN;
        this.fStat = Double.NaN;
        this.numeratorDof = Double.NaN;
        this.denominatorDof = Double.NaN;
        this.residuals = new double[0];
        this.rSquared = Double.NaN;
        this.term2CoefficientNames = Collections.emptyMap();
    }

    /**
     * Construct from the result of evaluation of an R call. should be deprecated because we're not using R integration
     *
     * @param summaryLm
     * @param anova
     * @param factorNames as referred to in the model. Used as keys to keep track of coefficients etc.
     *
     */
    public LinearModelSummaryImpl( String key, REXP summaryLm, @Nullable REXP anova, String[] factorNames ) throws REXPMismatchException {
        this.key = key;

        RList li = summaryLm.asList();

        REXPDouble coraw = ( REXPDouble ) li.get( "coefficients" );

        RList dimnames = coraw.getAttribute( "dimnames" ).asList();
        String[] itemnames = ( ( REXP ) dimnames.get( 0 ) ).asStrings();
        String[] colNames = ( ( REXP ) dimnames.get( 1 ) ).asStrings();

        double[][] coef = coraw.asDoubleMatrix();
        contrastCoefficients = DoubleMatrixFactory.dense( coef );
        contrastCoefficients.setRowNames( Arrays.asList( itemnames ) );
        contrastCoefficients.setColumnNames( Arrays.asList( colNames ) );

        this.residuals = ( ( REXP ) li.get( "residuals" ) ).asDoubles();

        this.rSquared = ( ( REXP ) li.get( "r.squared" ) ).asDouble();

        this.adjRSquared = ( ( REXP ) li.get( "adj.r.squared" ) ).asDouble();

        REXP fstats = ( REXP ) li.get( "fstatistic" ); // for overall model fit.
        if ( fstats != null ) {
            double[] ff = ( fstats.asDoubles() );
            this.fStat = ff[0];
            this.numeratorDof = ff[1];
            this.denominatorDof = ff[2];
        } else {
            this.fStat = Double.NaN;
            this.numeratorDof = Double.NaN;
            this.denominatorDof = Double.NaN;
        }
        // this.residualDof = ArrayUtils.toObject( ( ( REXP ) li.get( "df" ) ).asIntegers() )[1];

        // li.get( "cov.unscaled" );
        // this.doF = ArrayUtils.toObject( ( ( REXP ) li.get( "df" ) ).asIntegers() );

        this.factorNames = Arrays.asList( factorNames );

        if ( anova != null ) {
            this.anovaResult = new GenericAnovaResultImpl( key, anova );
        } else {
            this.anovaResult = null;
        }

        this.term2CoefficientNames = LinearModelSummaryUtils.createTerm2CoefficientNames( this.factorNames, contrastCoefficients );
    }

    /**
     * @return the adjRSquared
     */
    @Override
    public double getAdjRSquared() {
        return adjRSquared;
    }

    /**
     * @return may be null if ANOVA was not run.
     */
    @Nullable
    @Override
    public GenericAnovaResult getAnova() {
        return this.anovaResult;
    }

    @Override
    public double[] getCoefficients() {
        throw new UnsupportedOperationException();
    }

    @Override
    public DoubleMatrix<String, String> getContrastCoefficients() {
        return contrastCoefficients;
    }

    @Override
    public Map<String, Double> getContrastCoefficients( String factorName ) {
        return getContrastAttribute( factorName, "Estimate" );
    }

    @Override
    public Map<String, Double> getContrastCoefficientStderr( String factorName ) {
        return getContrastAttribute( factorName, "Std. Error" );
    }

    @Override
    public Map<String, Double> getContrastPValues( String factorName ) {
        return getContrastAttribute( factorName, "Pr(>|t|)" );
    }

    @Override
    public Map<String, Double> getContrastTStats( String factorName ) {
        return getContrastAttribute( factorName, "t value" );
    }

    private Map<String, Double> getContrastAttribute( String factorName, String attributeName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );
        if ( terms == null ) throw new IllegalArgumentException( "Unknown factor " + factorName + "." );
        Map<String, Double> results = new HashMap<>();
        for ( String term : terms ) {
            results.put( term, contrastCoefficients.getByKeys( term, attributeName ) );
        }
        return results;
    }

    @Override
    public double[] getEffects() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getFStat() {
        return this.fStat;
    }

    @Override
    public List<String> getFactorNames() {
        return factorNames;
    }

    @Override
    public double getInterceptCoefficient() {
        if ( contrastCoefficients.hasRow( INTERCEPT_COEFFICIENT_NAME ) ) {
            return contrastCoefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME, "Estimate" );
        } else if ( contrastCoefficients.rows() == 1 ) {
            /*
             * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
             * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
             * prepends the x).
             */
            assert "x1".equals( contrastCoefficients.getRowName( 0 ) );
            return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "Estimate" );
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double getInterceptPValue() {
        if ( contrastCoefficients.hasRow( INTERCEPT_COEFFICIENT_NAME ) ) {
            return contrastCoefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME, "Pr(>|t|)" );
        } else if ( contrastCoefficients.rows() == 1 ) {
            /*
             * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
             * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
             * prepends the x).
             */
            assert "x1".equals( contrastCoefficients.getRowName( 0 ) );
            return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "Pr(>|t|)" );
        } else {
            return Double.NaN;
        }
    }

    @Override
    public double getInterceptTStat() {
        if ( contrastCoefficients.hasRow( INTERCEPT_COEFFICIENT_NAME ) ) {
            return contrastCoefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME, "t value" );
        } else if ( contrastCoefficients.rows() == 1 ) {
            /*
             * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
             * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
             * prepends the x).
             */
            assert "x1".equals( contrastCoefficients.getRowName( 0 ) );
            return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "t value" );
        } else {
            return Double.NaN;
        }

    }

    @Override
    public String getKey() {
        return key;
    }

    public double getMainEffectPValue( String factorName ) {
        if ( anovaResult == null ) return Double.NaN;
        return anovaResult.getMainEffectPValue( factorName );
    }

    @Override
    public double getNumeratorDof() {
        return this.numeratorDof;
    }

    @Override
    public double getOverallPValue() {
        if ( overallPValue == null ) {
            if ( Double.isNaN( numeratorDof ) || Double.isNaN( denominatorDof ) || numeratorDof == 0 || denominatorDof == 0 ) {
                overallPValue = Double.NaN;
            } else {
                FDistribution f = new FDistribution( numeratorDof, denominatorDof );
                overallPValue = 1.0 - f.cumulativeProbability( this.getFStat() );
            }
        }
        return overallPValue;
    }

    @Override
    public double getPriorDof() {
        throw new UnsupportedOperationException();
    }

    @Override
    public double getResidualsDof() {
        return this.denominatorDof;
    }

    @Override
    public double[] getResiduals() {
        return residuals;
    }

    @Override
    public double getRSquared() {
        return rSquared;
    }

    @Override
    public double getSigma() {
        return Double.NaN;
    }

    @Override
    public double[] getStdevUnscaled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isBaseline( String factorValueName ) {
        return !contrastCoefficients.hasRow( factorValueName );
    }

    @Override
    public boolean isShrunken() {
        return false;
    }

    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if ( StringUtils.isNotBlank( this.key ) ) {
            buf.append( this.key ).append( "\n" );
        }
        buf.append( "F=" ).append( String.format( "%.2f", this.fStat ) ).append( " Rsquare=" ).append( String.format( "%.2f", this.rSquared ) ).append( "\n" );

        buf.append( "Residuals:\n" );
        for ( double d : residuals ) {
            buf.append( String.format( "%.2f ", d ) );
        }

        buf.append( "\n\nCoefficients:\n" ).append( contrastCoefficients ).append( "\n" );
        if ( this.anovaResult != null ) {
            buf.append( this.anovaResult ).append( "\n" );
        }

        return buf.toString();
    }
}
