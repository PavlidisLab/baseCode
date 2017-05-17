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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.math3.distribution.FDistribution;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;

/**
 * Represents the results of a linear model analysis from R. Both the summary.lm and anova objects are represented.
 * 
 * FIXME make this have the capabilities of the "fit" object, rather than just the summary.
 * 
 * @author paul
 */
public class LinearModelSummary implements Serializable {

    public static final String INTERCEPT_COEFFICIENT_NAME = "(Intercept)";

    private static final long serialVersionUID = 1L;

    private Double adjRSquared = Double.NaN;

    private GenericAnovaResult anovaResult;

    private Double[] coefficients = null;

    /**
     * 
     */
    private DoubleMatrix<String, String> contrastCoefficients = null;

    private Integer denominatorDof = null;

    /**
     * AKA Qty
     */
    private Double[] effects = null;

    private List<String> factorNames = null;

    private Map<String, List<String>> factorValueNames = new HashMap<>();

    private String formula = null;

    private Double fStat = Double.NaN;

    /**
     * Name for this summary e.g. probe identifier
     */
    private String key = null;

    private Integer numeratorDof = null;

    /**
     * Only if ebayes has been applied
     */
    private Double priorDof = null;

    private Double[] residuals = null;

    private Double rSquared = Double.NaN;

    /**
     * True = ebayes has been applied
     */
    private boolean shrunken = false;

    private Double sigma = null;

    /**
     * Unscaled standard deviations for the coefficient estimators in same order as coefficients. The standard errors
     * are given by stdev.unscaled * sigma (a la limma)
     */
    private Double[] stdevUnscaled;

    private Map<String, Collection<String>> term2CoefficientNames = new HashMap<>();

    /**
     * Construct from the result of evaluation of an R call. should be deprecated because we're not using R integration
     * 
     * @param summaryLm
     * @param anova
     * @param factorNames as referred to in the model. Used as keys to keep track of coefficients etc.
     * 
     */
    public LinearModelSummary( REXP summaryLm, REXP anova, String[] factorNames ) {
        try {
            basicSetup( summaryLm );

            this.factorNames = Arrays.asList( factorNames );

            if ( anova != null ) {
                this.anovaResult = new GenericAnovaResult( anova );
            }

            setupCoefficientNames();

        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /**
     * Construct an empty summary. Use for model fits that fail due to 0 degrees of freedom, etc.
     * 
     * @param key identifier
     */
    public LinearModelSummary( String key ) {
        this();
        this.key = key;
    }

    /**
     * @param key optional identifier
     * @param coefficients
     * @param residuals
     * @param terms
     * @param contrastCoefficients
     * @param effects AKA Qty
     * @param rsquared
     * @param adjRsquared
     * @param fstat
     * @param ndof
     * @param ddof
     * @param anovaResult
     * @param sigma
     * @param isShrunken
     */
    public LinearModelSummary( String k, Double[] coefficients, Double[] residuals, List<String> terms,
            DoubleMatrix<String, String> contrastCoefficients, Double[] effects, Double[] stdevUnscaled,
            double rsquared,
            double adjRsquared,
            double fstat, Integer ndof,
            Integer ddof, GenericAnovaResult anovaResult, double sigma, boolean isShrunken ) {

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

        if ( anovaResult != null ) {
            if ( anovaResult.getKey() == null ) {
                anovaResult.setKey( key );
            } else {
                if ( !anovaResult.getKey().equals( key ) ) {
                    throw new IllegalArgumentException( "Keys of ANOVA and holding LM must match" );
                }
            }
        }
        this.setupCoefficientNames();
    }

    /**
     * Construct an empty summary. Use for model fits that fail due to 0 residual degrees of freedom, etc.
     */
    protected LinearModelSummary() {
    }

    /**
     * @return the adjRSquared
     */
    public Double getAdjRSquared() {
        return adjRSquared;
    }

    /**
     * @return may be null if ANOVA was not run.
     */
    public GenericAnovaResult getAnova() {
        return this.anovaResult;
    }

    public Double[] getCoefficients() {
        return coefficients;
    }

    /**
     * @return The contrast coefficients and associated statistics for all tested contrasts.
     *         <p>
     *         Row names are the contrasts, for example for a model with one
     *         factor "f" with two
     *         levels "a" and "b": {"(Intercept)", "fb"}. columns are always {"Estimate" ,"Std. Error", "t value",
     *         "Pr(>|t|)"}
     * 
     */
    public DoubleMatrix<String, String> getContrastCoefficients() {
        return contrastCoefficients;
    }

    /**
     * 
     * @param factorName
     * @return
     */
    public Map<String, Double> getContrastCoefficients( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<>();
        for ( String term : terms ) {
            results.put( term, contrastCoefficients.getByKeys( term, "Estimate" ) );
        }

        return results;
    }

    /**
     * For the requested factor, return the standard errors associated with the contrast coefficient estimates.
     * 
     * 
     * @param factorName
     * @return
     */
    public Map<String, Double> getContrastCoefficientStderr( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<>();
        for ( String term : terms ) {
            results.put( term, contrastCoefficients.getByKeys( term, "Std. Error" ) );
        }

        return results;
    }

    /**
     * @param factorName
     * @return Map of pvalues for the given factor. For continuous factors or factors with only one level, there will be
     *         just one value. For factors with N>2 levels, there will be N-1 values, one for each contrast
     *         (since we compute treatment contrasts to the baseline)
     */
    public Map<String, Double> getContrastPValues( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<>();
        for ( String term : terms ) {
            results.put( term, contrastCoefficients.getByKeys( term, "Pr(>|t|)" ) );
        }

        return results;
    }

    /**
     * @param factorName
     * @return Map of T statistics for the given factor. For continuous factors or factors with only one level, there
     *         will be just one value. For factors with N>2 levels, there will be N-1 values, one for each contrast
     *         (since we compute treatment contrasts to the baseline)
     */
    public Map<String, Double> getContrastTStats( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<>();
        for ( String term : terms ) {
            results.put( term, contrastCoefficients.getByKeys( term, "t value" ) );
        }

        return results;

    }

    /**
     * @return
     */
    public Double[] getEffects() {
        return this.effects;
    }

    /**
     * @return F statistic for overall model fit.
     */
    public Double getF() {
        return this.fStat;
    }

    /**
     * @return the factorNames
     */
    public List<String> getFactorNames() {
        return factorNames;
    }

    /**
     * Return the factor names in the order they are stored here. Pvalues and T statistics for this factor are in the
     * same order, but the 'baseline' must be accounted for.
     * 
     * @param factorName
     * @return
     */
    public List<String> getFactorValueNames( String factorName ) {
        return factorValueNames.get( factorName );
    }

    /**
     * @return the formula
     */
    public String getFormula() {
        return formula;
    }

    /**
     * @param fnames names of the factors
     * @return
     * @see ubic.basecode.math.linearmodels.GenericAnovaResult#getInteractionEffectP(java.lang.String)
     */
    public Double getInteractionEffectP( String... fnames ) {
        if ( anovaResult == null ) return Double.NaN;
        return anovaResult.getInteractionEffectP( fnames );
    }

    /**
     * @return
     */
    public Double getInterceptCoeff() {
        if ( contrastCoefficients != null ) {
            if ( contrastCoefficients.hasRow( INTERCEPT_COEFFICIENT_NAME ) ) {
                return contrastCoefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME, "Estimate" );
            } else if ( contrastCoefficients.rows() == 1 ) {
                /*
                 * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
                 * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
                 * prepends the x).
                 */
                assert contrastCoefficients.getRowName( 0 ).equals( "x1" );
                return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "Estimate" );
            }
        }

        return Double.NaN;
    }

    /**
     * @return
     */
    public Double getInterceptP() {
        if ( contrastCoefficients != null ) {
            if ( contrastCoefficients.hasRow( INTERCEPT_COEFFICIENT_NAME ) ) {
                return contrastCoefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME, "Pr(>|t|)" );
            } else if ( contrastCoefficients.rows() == 1 ) {
                /*
                 * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
                 * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
                 * prepends the x).
                 */
                assert contrastCoefficients.getRowName( 0 ).equals( "x1" );
                return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "Pr(>|t|)" );
            }
        }
        return Double.NaN;
    }

    /**
     * @return
     */
    public Double getInterceptT() {
        if ( contrastCoefficients != null ) {
            if ( contrastCoefficients.hasRow( INTERCEPT_COEFFICIENT_NAME ) ) {
                return contrastCoefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME, "t value" );
            } else if ( contrastCoefficients.rows() == 1 ) {
                /*
                 * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
                 * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
                 * prepends the x).
                 */
                assert contrastCoefficients.getRowName( 0 ).equals( "x1" );
                return contrastCoefficients.getByKeys( contrastCoefficients.getRowName( 0 ), "t value" );
            }
        }

        return Double.NaN;
    }

    public String getKey() {
        return key;
    }

    /**
     * @return
     * @see ubic.basecode.math.linearmodels.GenericAnovaResult#getMainEffectFactorNames()
     */
    public Collection<String> getMainEffectFactorNames() {
        if ( anovaResult == null ) return null;
        return anovaResult.getMainEffectFactorNames();
    }

    /**
     * @param factorName
     * @return overall p-value for the given factor
     * @see ubic.basecode.math.linearmodels.GenericAnovaResult#getMainEffectP(java.lang.String)
     */
    public Double getMainEffectP( String factorName ) {
        if ( anovaResult == null ) return Double.NaN;
        return anovaResult.getMainEffectP( factorName );
    }

    /**
     * @return
     */
    public Integer getNumeratorDof() {
        return this.numeratorDof;
    }

    /**
     * Overall p value for F stat of model fit (upper tail probability)
     * 
     * @return value or NaN if it can't be computed for some reason
     */
    public Double getP() {
        if ( numeratorDof == null || denominatorDof == null || numeratorDof == 0 || denominatorDof == 0 )
            return Double.NaN;

        FDistribution f = new FDistribution( numeratorDof, denominatorDof );

        return 1.0 - f.cumulativeProbability( this.getF() );

    }

    /**
     * @return the priorDof
     */
    public Double getPriorDof() {
        return priorDof;
    }

    /**
     * @return
     */
    public Integer getResidualDof() {
        return this.denominatorDof;
    }

    /**
     * @return the residuals
     */
    public Double[] getResiduals() {
        return residuals;
    }

    /**
     * @return the rSquared
     */
    public Double getRSquared() {
        return rSquared;
    }

    public Double getSigma() {
        return sigma;
    }

    public Double[] getStdevUnscaled() {
        return stdevUnscaled;
    }

    /**
     * @return
     * @see ubic.basecode.math.linearmodels.GenericAnovaResult#hasInteractions()
     */
    public boolean hasInteractions() {
        if ( anovaResult == null ) return false;
        return anovaResult.hasInteractions();
    }

    public boolean isBaseline( String factorValueName ) {
        return !contrastCoefficients.hasRow( factorValueName );
    }

    /**
     * Whether this is the result of emprical bayes shrinkage of variance estimates
     * 
     * @return
     */
    public boolean isShrunken() {
        return shrunken;
    }

    /**
     * @param genericAnovaResult
     */
    public void setAnova( GenericAnovaResult genericAnovaResult ) {
        this.anovaResult = genericAnovaResult;
    }

    public void setKey( String key ) {
        this.key = key;
    }

    /**
     * @param priorDof the priorDof to set
     */
    public void setPriorDof( Double priorDof ) {
        this.priorDof = priorDof;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder buf = new StringBuilder();
        if ( StringUtils.isNotBlank( this.key ) ) {
            buf.append( this.key + "\n" );
        }
        buf.append( "F=" + String.format( "%.2f", this.fStat ) + " Rsquare=" + String.format( "%.2f", this.rSquared )
                + "\n" );

        buf.append( "Residuals:\n" );
        if ( residuals != null ) {
            for ( Double d : residuals ) {
                buf.append( String.format( "%.2f ", d ) );
            }
        } else {
            buf.append( "Residuals are null" );
        }

        buf.append( "\n\nCoefficients:\n" + contrastCoefficients + "\n" );
        if ( this.anovaResult != null ) {
            buf.append( this.anovaResult.toString() + "\n" );
        }

        return buf.toString();
    }

    /**
     * @param summaryLm
     * @return
     * @throws REXPMismatchException
     */
    private RList basicSetup( REXP summaryLm ) throws REXPMismatchException {
        RList li = summaryLm.asList();

        extractCoefficients( li );

        this.residuals = ArrayUtils.toObject( ( ( REXP ) li.get( "residuals" ) ).asDoubles() );

        this.rSquared = ( ( REXP ) li.get( "r.squared" ) ).asDouble();

        this.adjRSquared = ( ( REXP ) li.get( "adj.r.squared" ) ).asDouble();

        REXP fstats = ( REXP ) li.get( "fstatistic" ); // for overall model fit.
        if ( fstats != null ) {
            Double[] ff = ArrayUtils.toObject( fstats.asDoubles() );
            this.fStat = ff[0];
            this.numeratorDof = ff[1].intValue();
            this.denominatorDof = ff[2].intValue();
        }
        // this.residualDof = ArrayUtils.toObject( ( ( REXP ) li.get( "df" ) ).asIntegers() )[1];

        // li.get( "cov.unscaled" );
        // this.doF = ArrayUtils.toObject( ( ( REXP ) li.get( "df" ) ).asIntegers() );

        return li;
    }

    /**
     * @param li
     * @throws REXPMismatchException
     */
    private void extractCoefficients( RList li ) throws REXPMismatchException {
        REXPDouble coraw = ( REXPDouble ) li.get( "coefficients" );

        RList dimnames = coraw.getAttribute( "dimnames" ).asList();
        String[] itemnames = ( ( REXP ) dimnames.get( 0 ) ).asStrings();
        String[] colNames = ( ( REXP ) dimnames.get( 1 ) ).asStrings();

        double[][] coef = coraw.asDoubleMatrix();
        contrastCoefficients = DoubleMatrixFactory.dense( coef );
        contrastCoefficients.setRowNames( Arrays.asList( itemnames ) );
        contrastCoefficients.setColumnNames( Arrays.asList( colNames ) );
    }

    /**
     */
    private void setupCoefficientNames() {

        for ( String string : factorNames ) {
            term2CoefficientNames.put( string, new HashSet<String>() );
        }

        assert this.contrastCoefficients != null;

        List<String> coefRowNames = contrastCoefficients.getRowNames();

        for ( String coefNameFromR : coefRowNames ) {

            if ( coefNameFromR.equals( INTERCEPT_COEFFICIENT_NAME ) ) {
                continue; // ?
            } else if ( coefNameFromR.contains( ":" ) ) {
                /*
                 * We're counting on the interaction terms names ending up like this: f1001fv1005:f1002fv1006 (see
                 * LinearModelAnalyzer in Gemma, which sets up the factor and factorvalue names in the way that
                 * generates this). Risky, and it won't work for continuous factors. But R makes kind of a mess from the
                 * interactions. If we assume there is only one interaction term we can work it out by the presence of
                 * ":".
                 */
                String cleanedInterationTermName = coefNameFromR.replaceAll( "fv_[0-9]+(?=(:|$))", "" );

                for ( String factorName : factorNames ) {
                    if ( !factorName.contains( ":" ) ) continue;

                    if ( factorName.equals( cleanedInterationTermName ) ) {
                        assert term2CoefficientNames.containsKey( factorName );
                        term2CoefficientNames.get( factorName ).add( coefNameFromR );
                    }

                }

            } else {

                for ( String factorName : factorNames ) {
                    if ( coefNameFromR.startsWith( factorName ) ) {
                        assert term2CoefficientNames.containsKey( factorName );
                        term2CoefficientNames.get( factorName ).add( coefNameFromR );

                    }
                }
            }
        }
    }

}
