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
package ubic.basecode.math.linearmodels;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.FDistribution;
import org.jspecify.annotations.Nullable;
import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;

import java.util.*;

/**
 * Represents the results of a linear model analysis.
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
    private GenericAnovaResult anovaResult;
    private final double[] coefficients;
    private final DoubleMatrix<String, String> contrastCoefficients;
    private final double denominatorDof;
    /**
     * AKA Qty
     */
    private final double[] effects;
    private final List<String> factorNames;
    private final double fStat;
    private final double numeratorDof;
    /**
     * Only if ebayes has been applied
     */
    private final double priorDof;
    private final double[] residuals;
    private final double rSquared;
    /**
     * True = ebayes has been applied
     */
    private final boolean shrunken;
    private final double sigma;
    private final double[] stdevUnscaled;
    private final Map<String, Collection<String>> term2CoefficientNames;

    @Nullable
    private Double overallPValue = null;

    /**
     * Construct an empty summary. Use for model fits that fail due to 0 degrees of freedom, etc.
     */
    public LinearModelSummaryImpl( String key ) {
        this.key = key;
        this.stdevUnscaled = new double[0];
        this.adjRSquared = Double.NaN;
        this.coefficients = new double[0];
        this.effects = new double[0];
        this.sigma = Double.NaN;
        this.priorDof = Double.NaN;
        this.numeratorDof = Double.NaN;
        this.fStat = Double.NaN;
        this.shrunken = false;
        this.rSquared = Double.NaN;
        this.residuals = new double[0];
        this.factorNames = Collections.emptyList();
        this.denominatorDof = Double.NaN;
        this.contrastCoefficients = new DenseDoubleMatrix<>( new double[0][0] );
        this.term2CoefficientNames = Collections.emptyMap();
    }

    public LinearModelSummaryImpl( String k, double[] coefficients, double[] residuals, List<String> terms,
        DoubleMatrix<String, String> contrastCoefficients, double[] effects, double[] stdevUnscaled, double rsquared,
        double adjRsquared, double fstat, double ndof, double ddof, @Nullable GenericAnovaResult anovaResult,
        double sigma, boolean isShrunken, double priorDof ) {
        if ( anovaResult != null && !anovaResult.getKey().equals( k ) ) {
            throw new IllegalArgumentException( "Keys of ANOVA and holding LM must match" );
        }
        this.residuals = residuals;
        this.coefficients = coefficients;
        this.contrastCoefficients = contrastCoefficients;
        this.rSquared = rsquared;
        this.adjRSquared = adjRsquared;
        this.fStat = fstat;
        this.numeratorDof = ndof;
        this.denominatorDof = ddof;
        this.key = k;
        this.anovaResult = anovaResult;
        this.effects = effects;
        this.stdevUnscaled = stdevUnscaled;
        this.factorNames = terms;
        this.sigma = sigma;
        this.shrunken = isShrunken;
        this.priorDof = priorDof;
        this.term2CoefficientNames = LinearModelSummaryUtils.createTerm2CoefficientNames( factorNames, contrastCoefficients );
    }

    @Override
    public double getAdjRSquared() {
        return adjRSquared;
    }

    @Nullable
    @Override
    public GenericAnovaResult getAnova() {
        return this.anovaResult;
    }

    @Override
    public double[] getCoefficients() {
        return coefficients;
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
        if ( terms == null ) {
            throw new IllegalArgumentException( "Unknown factor " + factorName + "." );
        }
        Map<String, Double> results = new HashMap<>();
        for ( String term : terms ) {
            results.put( term, contrastCoefficients.getByKeys( term, attributeName ) );
        }
        return results;
    }

    @Override
    public double[] getEffects() {
        return this.effects;
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
        if ( contrastCoefficients.hasRow( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME ) ) {
            return contrastCoefficients.getByKeys( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME, "Estimate" );
        } else if ( contrastCoefficients.rows() == 1 ) {
            /*
             * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
             * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
             * prepends the x).
             */
            assert "x1".equals( contrastCoefficients.getRowName( 0 ) );
            return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "Estimate" );
        } else {
            throw new IllegalArgumentException( "The model does not have an intercept." );
        }
    }

    @Override
    public double getInterceptPValue() {
        if ( contrastCoefficients.hasRow( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME ) ) {
            return contrastCoefficients.getByKeys( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME, "Pr(>|t|)" );
        } else if ( contrastCoefficients.rows() == 1 ) {
            /*
             * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
             * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
             * prepends the x).
             */
            assert "x1".equals( contrastCoefficients.getRowName( 0 ) );
            return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "Pr(>|t|)" );
        } else {
            throw new IllegalArgumentException( "The model does not have an intercept." );
        }
    }

    @Override
    public double getInterceptTStat() {
        if ( contrastCoefficients.hasRow( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME ) ) {
            return contrastCoefficients.getByKeys( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME, "t value" );
        } else if ( contrastCoefficients.rows() == 1 ) {
            /*
             * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
             * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
             * prepends the x).
             */
            assert "x1".equals( contrastCoefficients.getRowName( 0 ) );
            return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "t value" );
        } else {
            throw new IllegalArgumentException( "The model does not have an intercept." );
        }
    }

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
        return priorDof;
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
        return sigma;
    }

    @Override
    public double[] getStdevUnscaled() {
        return stdevUnscaled;
    }

    @Override
    public boolean isBaseline( String factorValueName ) {
        return !contrastCoefficients.hasRow( factorValueName );
    }

    @Override
    public boolean isShrunken() {
        return shrunken;
    }

    public void setAnova( GenericAnovaResult genericAnovaResult ) {
        this.anovaResult = genericAnovaResult;
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
