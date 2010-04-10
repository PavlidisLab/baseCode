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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.ArrayUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;

/**
 * Represents the results of a linear model analysis from R. Both the summary.lm and anova objects are represented.
 * <p>
 * 
 * @author paul
 * @version $Id$
 */
public class LinearModelSummary implements Serializable {

    private static final String INTERCEPT_COEFFICIENT_NAME_IN_R = "(Intercept)";

    private Double adjRSquared = Double.NaN;

    private GenericAnovaResult anovaResult;

    private DoubleMatrix<String, String> coefficients = null;

    private List<String> factorNames = null;

    private Map<String, List<String>> factorValueNames = new HashMap<String, List<String>>();

    private String formula = null;

    private Double[] residuals = null;

    private Double rSquared = Double.NaN;

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

    /**
     * @return the factorNames
     */
    public List<String> getFactorNames() {
        return factorNames;
    }

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

    public Double getInterceptP() {
        if ( coefficients != null && coefficients.hasRow( INTERCEPT_COEFFICIENT_NAME_IN_R ) )
            return coefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME_IN_R, "Pr(>|t|)" );
        return Double.NaN;
    }

    public Double getInterceptT() {
        if ( coefficients != null && coefficients.hasRow( INTERCEPT_COEFFICIENT_NAME_IN_R ) )
            return coefficients.getByKeys( INTERCEPT_COEFFICIENT_NAME_IN_R, "t value" );
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
     * @return
     * @see ubic.basecode.util.r.type.GenericAnovaResult#getMainEffectP(java.lang.String)
     */
    public Double getMainEffectP( String factorName ) {
        if ( anovaResult == null ) return Double.NaN;
        return anovaResult.getMainEffectP( factorName );
    }

    /**
     * @param factorName
     * @return array of T statistics for the given factor. For continuous factors or factors with only one level, there
     *         will be just one value. For factors with N>2 levels, there will be N-1 values.
     */
    public Double[] getMainEffectT( String factorName ) {
        Collection<String> terms = term2CoefficientNames.get( factorName );

        if ( terms == null ) return null;

        List<Double> ts = new ArrayList<Double>();
        for ( String term : terms ) {
            ts.add( coefficients.getByKeys( term, "t value" ) );
        }

        return ts.toArray( new Double[] {} );

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

    /**
     * @param summaryLm
     * @return
     * @throws REXPMismatchException
     */
    private RList basicSetup( REXP summaryLm ) throws REXPMismatchException {
        RList li = summaryLm.asList();

        REXPDouble coraw = ( REXPDouble ) li.get( "coefficients" );

        RList dimnames = coraw.getAttribute( "dimnames" ).asList();
        String[] itemnames = ( ( REXP ) dimnames.get( 0 ) ).asStrings();
        String[] colNames = ( ( REXP ) dimnames.get( 1 ) ).asStrings();

        double[][] coef = coraw.asDoubleMatrix();
        coefficients = DoubleMatrixFactory.dense( coef );
        coefficients.setRowNames( Arrays.asList( itemnames ) );
        coefficients.setColumnNames( Arrays.asList( colNames ) );

        this.residuals = ArrayUtils.toObject( ( ( REXP ) li.get( "residuals" ) ).asDoubles() );

        this.rSquared = ( ( REXP ) li.get( "r.squared" ) ).asDouble();

        this.adjRSquared = ( ( REXP ) li.get( "adj.r.squared" ) ).asDouble();

        return li;
    }

    private Map<String, Collection<String>> term2CoefficientNames = new HashMap<String, Collection<String>>();

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
                // String[] interactionTermNames = coefNameFromR.split( ":" );
                // currently not using.
                continue;
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

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return coefficients.toString();
    }

}
