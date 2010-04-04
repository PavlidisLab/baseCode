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

    private Double adjRSquared = null;

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

    private Double rSquared = null;

    private List<String> factorNames = null;

    /**
     * Construct an empty summary. Use for failed tests.
     */
    public LinearModelSummary() {
    }

    /**
     * Construct from the result of evaluation of an R call like 'summary(lm(...))'.
     * 
     * @param summaryLm
     */
    public LinearModelSummary( REXP summaryLm ) {

        try {
            RList li = summaryLm.asList();

            REXPDouble coraw = ( REXPDouble ) li.get( "coefficients" );

            RList dimnames = coraw.getAttribute( "dimnames" ).asList();
            String[] itemnames = ( ( REXP ) dimnames.get( 0 ) ).asStrings();
            String[] colNames = ( ( REXP ) dimnames.get( 1 ) ).asStrings();

            double[][] coef = coraw.asDoubleMatrix();
            coefficients = DoubleMatrixFactory.dense( coef );
            coefficients.setRowNames( Arrays.asList( itemnames ) );
            coefficients.setColumnNames( Arrays.asList( colNames ) );

            String[] termLabels = ( ( REXP ) li.get( "terms" ) ).getAttribute( "term.labels" ).asStrings();
            LinkedList<String> v = new LinkedList<String>();
            this.factorNames = Arrays.asList( termLabels );
            v.addAll( factorNames );
            v.add( 0, "(Intercept)" );
            coefficients.setRowNames( v );
            assert coefficients.getRowNames().get( 0 ).equals( "(Intercept)" );

            this.residuals = ArrayUtils.toObject( ( ( REXP ) li.get( "residuals" ) ).asDoubles() );

            this.rSquared = ( ( REXP ) li.get( "r.squared" ) ).asDouble();

            this.adjRSquared = ( ( REXP ) li.get( "adj.r.squared" ) ).asDouble();

            this.df = ( ( REXP ) li.get( "df" ) ).asInteger();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

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
        if ( coefficients.hasRow( "(Intercept)" ) ) return coefficients.getByKeys( "(Intercept)", "Pr(>|t|)" );
        return null;
    }

    public Double getInterceptT() {
        if ( coefficients.hasRow( "(Intercept)" ) ) return coefficients.getByKeys( "(Intercept)", "t value" );
        return null;
    }

    public Double getP( String factorName ) {
        if ( coefficients.hasRow( factorName ) ) return coefficients.getByKeys( factorName, "Pr(>|t|)" );
        return null;
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
        if ( coefficients.hasRow( factorName ) ) return coefficients.getByKeys( factorName, "t value" );
        return null;
    }

}
