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
package ubic.basecode.util.r.type;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.math.MathException;
import org.apache.commons.math.distribution.FDistribution;
import org.apache.commons.math.distribution.FDistributionImpl;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;

/**
 * Represents the results of a linear model analysis from R. Both the summary.lm and anova objects are represented.
 * 
 * @author paul
 * @version $Id$
 */
public class LinearModelSummary implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String INTERCEPT_COEFFICIENT_NAME_IN_R = "(Intercept)";

    private Double adjRSquared = Double.NaN;

    private GenericAnovaResult anovaResult;

    private DoubleMatrix<String, String> coefficients = null;

    private List<String> factorNames = null;

    private Map<String, List<String>> factorValueNames = new HashMap<String, List<String>>();

    private String formula = null;

    private Double[] residuals = null;

    private Double rSquared = Double.NaN;

    private Map<String, Collection<String>> term2CoefficientNames = new HashMap<String, Collection<String>>();

    private Double fStat;

    private Integer numeratorDof;

    private Integer denominatorDof;

    // private DoubleMatrix<String, String> covariance;

    /**
     * Construct an empty summary. Use for failed tests.
     */
    public LinearModelSummary() {
    }

    /**
     * Construct from the result of evaluation of an R call.
     * 
     * @param summaryLm
     * @param anova
     * @param factorNames as referred to in the model. Used as keys to keep track of coefficients etc.
     */
    public LinearModelSummary( REXP summaryLm, REXP anova, String[] factorNames ) {
        try {
            basicSetup( summaryLm );

            this.factorNames = Arrays.asList( factorNames );

            if ( anova != null ) {
                this.anovaResult = new GenericAnovaResult( anova );
            }

            setupCoefficientNames( factorNames );

        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /**
     * @param summaryLm
     * @param factorNames used as keys to keep track of coefficients etc.
     */
    public LinearModelSummary( REXP summaryLm, String[] factorNames ) {
        this( summaryLm, null, factorNames );
    }

    /**
     * @return the adjRSquared
     */
    public Double getAdjRSquared() {
        return adjRSquared;
    }

    /**
     * @return the coefficients. Row names are the contrasts, for example for a model with one factor "f" with two
     *         levels "a" and "b": {"(Intercept)", "fb"}. columns are always {"Estimate" ,"Std. Error", "t value",
     *         "Pr(>|t|)"}
     */
    public DoubleMatrix<String, String> getCoefficients() {
        return coefficients;
    }

    public Map<String, Double> getContrastCoefficients( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<String, Double>();
        for ( String term : terms ) {
            results.put( term, coefficients.getByKeys( term, "Estimate" ) );
        }

        return results;
    }

    public Map<String, Double> getContrastCoefficientStderr( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<String, Double>();
        for ( String term : terms ) {
            results.put( term, coefficients.getByKeys( term, "Std. Error" ) );
        }

        return results;
    }

    /**
     * @param factorName
     * @return Map of pvalues for the given factor. For continuous factors or factors with only one level, there will be
     *         just one value. For factors with N>2 levels, there will be N-1 values.
     */
    public Map<String, Double> getContrastPValues( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<String, Double>();
        for ( String term : terms ) {
            results.put( term, coefficients.getByKeys( term, "Pr(>|t|)" ) );
        }

        return results;
    }

    /**
     * @param factorName
     * @return Map of T statistics for the given factor. For continuous factors or factors with only one level, there
     *         will be just one value. For factors with N>2 levels, there will be N-1 values.
     */
    public Map<String, Double> getContrastTStats( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        Map<String, Double> results = new HashMap<String, Double>();
        for ( String term : terms ) {
            results.put( term, coefficients.getByKeys( term, "t value" ) );
        }

        return results;

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
     * @see ubic.basecode.util.r.type.GenericAnovaResult#getInteractionEffectP(java.lang.String)
     */
    public Double getInteractionEffectP( String... fnames ) {
        if ( anovaResult == null ) return Double.NaN;
        return anovaResult.getInteractionEffectP( fnames );
    }

    /**
     * @return
     */
    public Double getInterceptCoeff() {
        if ( coefficients != null ) {
            if ( coefficients.hasRow( INTERCEPT_COEFFICIENT_NAME_IN_R ) ) {
                return coefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME_IN_R, "Estimate" );
            } else if ( coefficients.rows() == 1 ) {
                /*
                 * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
                 * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
                 * prepends the x).
                 */
                assert coefficients.getRowName( 0 ).equals( "x1" );
                return coefficients.getByKeys( coefficients.getRowName( 0 ), "Estimate" );
            }
        }

        return Double.NaN;
    }

    /**
     * @return
     */
    public Double getInterceptP() {
        if ( coefficients != null ) {
            if ( coefficients.hasRow( INTERCEPT_COEFFICIENT_NAME_IN_R ) ) {
                return coefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME_IN_R, "Pr(>|t|)" );
            } else if ( coefficients.rows() == 1 ) {
                /*
                 * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
                 * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
                 * prepends the x).
                 */
                assert coefficients.getRowName( 0 ).equals( "x1" );
                return coefficients.getByKeys( coefficients.getRowName( 0 ), "Pr(>|t|)" );
            }
        }
        return Double.NaN;
    }

    /**
     * @return
     */
    public Double getInterceptT() {
        if ( coefficients != null ) {
            if ( coefficients.hasRow( INTERCEPT_COEFFICIENT_NAME_IN_R ) ) {
                return coefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME_IN_R, "t value" );
            } else if ( coefficients.rows() == 1 ) {
                /*
                 * This is a bit of a kludge. When we use lm.fit instead of lm, we end up with a somewhat screwy
                 * coefficent matrix in the case of one-sample ttest, and R put in x1 (I think it starts as 1 and it
                 * prepends the x).
                 */
                assert coefficients.getRowName( 0 ).equals( "x1" );
                return coefficients.getByKeys( coefficients.getRowName( 0 ), "t value" );
            }
        }

        return Double.NaN;
    }

    /**
     * @return
     * @see ubic.basecode.util.r.type.GenericAnovaResult#getMainEffectFactorNames()
     */
    public Collection<String> getMainEffectFactorNames() {
        if ( anovaResult == null ) return null;
        return anovaResult.getMainEffectFactorNames();
    }

    /**
     * @param factorName
     * @return overall p-value for the given factor
     * @see ubic.basecode.util.r.type.GenericAnovaResult#getMainEffectP(java.lang.String)
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
        FDistribution f = new FDistributionImpl( numeratorDof, denominatorDof );
        try {
            return 1.0 - f.cumulativeProbability( this.getF() );
        } catch ( MathException e ) {
            return Double.NaN;
        }
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

    /**
     * @return
     * @see ubic.basecode.util.r.type.GenericAnovaResult#hasInteractions()
     */
    public boolean hasInteractions() {
        if ( anovaResult == null ) return false;
        return anovaResult.hasInteractions();
    }

    public boolean isBaseline( String factorValueName ) {
        return !coefficients.hasRow( factorValueName );
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return coefficients.toString();
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

        Double[] ff = ArrayUtils.toObject( ( ( REXP ) li.get( "fstatistic" ) ).asDoubles() );
        this.fStat = ff[0];
        this.numeratorDof = ff[1].intValue();
        this.denominatorDof = ff[2].intValue();
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
        coefficients = DoubleMatrixFactory.dense( coef );
        coefficients.setRowNames( Arrays.asList( itemnames ) );
        coefficients.setColumnNames( Arrays.asList( colNames ) );
    }

    /**
     * @param factorNames
     */
    private void setupCoefficientNames( String[] factorNames ) {

        for ( String string : factorNames ) {
            term2CoefficientNames.put( string, new HashSet<String>() );
        }

        assert this.coefficients != null;

        List<String> coefRowNames = coefficients.getRowNames();

        for ( String coefNameFromR : coefRowNames ) {

            if ( coefNameFromR.equals( INTERCEPT_COEFFICIENT_NAME_IN_R ) ) {
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
