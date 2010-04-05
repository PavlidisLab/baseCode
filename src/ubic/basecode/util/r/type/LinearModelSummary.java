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

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.RList;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;

/**
 * Represents a summary.lm object from R.
 * 
 * @author paul
 * @version $Id$
 */
public class LinearModelSummary {

    /**
     * 
     */
    private static final String INTERCEPT_COEFFICIENT_NAME_IN_R = "(Intercept)";

    private Double adjRSquared = Double.NaN;

    private DoubleMatrix<String, String> coefficients = null;

    private Integer df = null;

    private String formula = null;

    private Double[] residuals = null;

    /**
     * @return the factorNames
     */
    public List<String> getFactorNames() {
        return factorNames;
    }

    private Double rSquared = Double.NaN;

    private List<String> factorNames = null;

    /**
     * Construct an empty summary. Use for failed tests.
     */
    public LinearModelSummary() {
    }

    /**
     * Construct from the result of evaluatio of an R call where design matrix is defined explicitly like 'summary(lm( x
     * ~ designMatrix))'.
     * 
     * @param summaryLm
     * @param factorNames
     */
    public LinearModelSummary( REXP summaryLm, String[] factorNames ) {
        try {
            basicSetup( summaryLm );

            this.factorNames = Arrays.asList( factorNames );
            setupFactorNames( factorNames );

        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /**
     * Construct from the result of evaluation of an R call where factors are listed separately like 'summary(lm( x ~ f
     * + g + h))'
     * 
     * @param summaryLm
     */
    public LinearModelSummary( REXP summaryLm ) {
        try {
            RList li = basicSetup( summaryLm );

            // factor name setup
            String[] termLabels = ( ( REXP ) li.get( "terms" ) ).getAttribute( "term.labels" ).asStrings();
            setupFactorNames( termLabels );

        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    private void setupFactorNames( String[] termLabels ) {
        LinkedList<String> v = new LinkedList<String>();
        this.factorNames = Arrays.asList( termLabels );

        if ( coefficients.rows() < this.factorNames.size() + 1 ) { // one more to allow for the Intercept.
            /*
             * Missing...?
             */
            List<String> coefRowNames = coefficients.getRowNames();
            for ( String coefNameFromR : coefRowNames ) {
                if ( coefNameFromR.equals( INTERCEPT_COEFFICIENT_NAME_IN_R ) ) {
                    continue;
                }
                boolean matched = false;
                for ( String factorName : factorNames ) {
                    if ( coefNameFromR.startsWith( factorName ) ) {
                        if ( matched ) {
                            throw new IllegalStateException( "Ambiguous factor name in lm result: " + coefNameFromR );
                        }
                        v.add( factorName );
                        matched = true;
                    }
                }
                if ( !matched ) {
                    throw new IllegalStateException( "Unrecognized factor name in lm result: " + coefNameFromR );
                }
            }

        } else {
            v.addAll( factorNames );
        }

        v.add( 0, INTERCEPT_COEFFICIENT_NAME_IN_R );
        assert v.size() == coefficients.rows();
        coefficients.setRowNames( v );

        assert coefficients.getRowNames().get( 0 ).equals( INTERCEPT_COEFFICIENT_NAME_IN_R );
    }

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

        this.df = ( ( REXP ) li.get( "df" ) ).asInteger();
        return li;
    }

    /**
     * @return the adjRSquared
     */
    public Double getAdjRSquared() {
        return adjRSquared;
    }

    /**
     * @return the coefficients
     */
    public DoubleMatrix<String, String> getCoefficients() {
        return coefficients;
    }

    /**
     * @return the df
     */
    public Integer getDf() {
        return df;
    }

    /**
     * @return the formula
     */
    public String getFormula() {
        return formula;
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

    public Double getP( String factorName ) {
        if ( coefficients != null && coefficients.hasRow( factorName ) )
            return coefficients.getByKeys( factorName, "Pr(>|t|)" );
        return Double.NaN;
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

    public Double getT( String factorName ) {
        if ( coefficients != null && coefficients.hasRow( factorName ) )
            return coefficients.getByKeys( factorName, "t value" );
        return Double.NaN;
    }

}
