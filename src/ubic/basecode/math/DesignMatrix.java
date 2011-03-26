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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Represents the A matrix in regression problems posed as Ax=b. The starting point is a matrix of sample information,
 * where the rows are sample names and the columns are the names of factors or covariates. Intercept and interaction
 * terms can be added.
 * 
 * @author paul
 * @version $Id$
 */
public class DesignMatrix {

    private static Log log = LogFactory.getLog( DesignMatrix.class );

    /**
     * @param vec
     * @return
     */
    private static Set<String> levels( Collection<String> vec ) {
        Set<String> result = new LinkedHashSet<String>();
        result.addAll( vec );
        return result;
    }

    private DoubleMatrix<String, String> matrix;

    private List<Integer> assign = new ArrayList<Integer>();

    /**
     * Store which terms show up in which columns of the design
     */
    private Map<String, List<Integer>> terms = new LinkedHashMap<String, List<Integer>>();

    /**
     * @param factor in form of Doubles or Strings. Any other types will yield errors.
     * @param start
     */
    public DesignMatrix( Object[] factor, int start, String factorName ) {
        matrix = this.buildDesign( 1, Arrays.asList( factor ), null, start, factorName );
    }

    /**
     * @param sampleInfo in form of Doubles or Strings. Any other types will yield errors.
     */
    public DesignMatrix( ObjectMatrix<String, String, Object> sampleInfo ) {
        this( sampleInfo, true );
    }

    /**
     * @param sampleInfo in form of Doubles or Strings. Any other types will yield errors.
     * @param intercept
     */
    public DesignMatrix( ObjectMatrix<String, String, Object> sampleInfo, boolean intercept ) {
        matrix = this.designMatrix( sampleInfo, intercept );
        if ( sampleInfo.getRowNames().size() == matrix.rows() ) matrix.setRowNames( sampleInfo.getRowNames() );
    }

    /**
     * Append additional factors/covariates to this
     * 
     * @param sampleInfo
     */
    public void add( ObjectMatrix<String, String, Object> sampleInfo ) {
        /*
         * FIXME this does not properly maintain the "assign"
         */
        this.matrix = this.designMatrix( sampleInfo, this.matrix );
    }

    /**
     * @param terms
     */
    public void addInteraction( String... interactionTerms ) {

        /*
         * Figure out which columns of the data we need to look at.
         * 
         * If the factor is in >1 columns - we have to add two or more columns
         */
        int columnAddedNumber = 0;
        Collection<String> doneTerms = new HashSet<String>();
        for ( String t1 : interactionTerms ) {
            if ( doneTerms.contains( t1 ) ) continue;
            List<Integer> cols1 = terms.get( t1 );
            for ( String t2 : interactionTerms ) {
                if ( t1.equals( t2 ) ) continue;
                doneTerms.add( t2 );
                List<Integer> cols2 = terms.get( t2 );

                for ( int i = 0; i < cols1.size(); i++ ) {
                    double[] col1i = this.matrix.getColumn( cols1.get( i ) );
                    double[] col2i = this.matrix.getColumn( cols2.get( i ) );

                    Double[] prod = new Double[col1i.length];

                    this.matrix = this.copyWithSpace( this.matrix, this.matrix.columns() + 1 );
                    for ( int k = 0; k < col1i.length; k++ ) {
                        prod[k] = col1i[k] * col2i[k];
                        matrix.set( k, this.matrix.columns() - 1, prod[k] );
                    }
                    String termName = t1 + ":" + t2;
                    matrix.addColumnName( termName + "_" + columnAddedNumber++ );
                    if ( !this.terms.containsKey( termName ) ) {
                        this.terms.put( termName, new ArrayList<Integer>() );
                    }
                    terms.get( termName ).add( this.matrix.columns() - 1 );
                }
            }
        }

        /*
         * Compute the product of the values.
         */

        /*
         * Add them to the matrix.
         */
        // this.matrix.addColumnName( );
    }

    /**
     * @return
     */
    public List<Integer> getAssign() {
        return assign;
    }

    public DoubleMatrix2D getDoubleMatrix() {
        return new DenseDoubleMatrix2D( matrix.asDoubles() );
    }

    public DoubleMatrix<String, String> getMatrix() {
        return matrix;
    }

    public List<String> getTerms() {
        List<String> result = new ArrayList<String>();
        result.addAll( terms.keySet() );
        return result;
    }

    @Override
    public String toString() {
        return this.matrix.toString();
    }

    /**
     * @param vec
     * @param inputDesign
     * @return
     */
    private DoubleMatrix<String, String> addContinuousCovariate( List<?> vec, DoubleMatrix<String, String> inputDesign ) {
        DoubleMatrix<String, String> tmp;
        /*
         * CONTINUOUS COVARIATE
         */
        log.debug( "Treating factor as continuous covariate" );
        if ( inputDesign != null ) {
            /*
             * copy it into a new one.
             */
            assert vec.size() == inputDesign.rows();
            int numberofColumns = inputDesign.columns() + 1;
            tmp = copyWithSpace( inputDesign, numberofColumns );
        } else {
            tmp = new DenseDoubleMatrix<String, String>( vec.size(), 1 );
            tmp.assign( 0.0 );
        }
        int startcol = 0;
        if ( inputDesign != null ) {
            startcol = inputDesign.columns();
        }
        for ( int i = startcol; i < tmp.columns(); i++ ) {
            for ( int j = 0; j < tmp.rows(); j++ ) {
                tmp.set( j, i, ( Double ) vec.get( j ) );
            }
        }
        return tmp;
    }

    /**
     * @param which column of the input matrix are we working on.
     * @param vec of doubles or strings.
     * @param inputDesign
     * @param start 1 or 2. Set to 1 to get a column for each level (must not have an intercept in the model); Set to 2
     *        to get a column for all but the last (Redundant) level.
     * @param factorName String to associate with the factor
     * @return
     */
    private DoubleMatrix<String, String> buildDesign( int columnNum, List<?> vec,
            DoubleMatrix<String, String> inputDesign, int start, String factorName ) {
        if ( !terms.containsKey( factorName ) ) {
            terms.put( factorName, new ArrayList<Integer>() );
        }
        DoubleMatrix<String, String> tmp = null;
        if ( vec.get( 0 ) instanceof Double ) {
            tmp = addContinuousCovariate( vec, inputDesign );
            this.assign.add( columnNum );
            terms.get( factorName ).add( columnNum );
            tmp.addColumnName( factorName );
        } else {
            /*
             * CATEGORICAL COVARIATE
             */
            Set<String> levels = levels( ( Collection<String> ) vec );

            if ( inputDesign != null ) {
                /*
                 * copy it into a new one.
                 */
                assert vec.size() == inputDesign.rows();
                int numberofColumns = inputDesign.columns() + levels.size() - start + 1;
                tmp = copyWithSpace( inputDesign, numberofColumns );

            } else {
                tmp = new DenseDoubleMatrix<String, String>( vec.size(), levels.size() - start + 1 );
                tmp.assign( 0.0 );
            }

            List<String> levelList = new ArrayList<String>();
            levelList.addAll( levels );
            int startcol = 0;
            if ( inputDesign != null ) {
                startcol = inputDesign.columns();
            }
            for ( int i = startcol; i < tmp.columns(); i++ ) {
                for ( int j = 0; j < tmp.rows(); j++ ) {
                    tmp.set( j, i, vec.get( j ).equals( levelList.get( i - startcol + ( start - 1 ) ) ) ? 1.0 : 0.0 );
                }
                this.assign.add( columnNum ); // all of these columns use this factor.

                terms.get( factorName ).add( columnNum );
                tmp.getColNames().add( factorName + "_" + i );
            }
        }
        return tmp;
    }

    private DoubleMatrix<String, String> copyWithSpace( DoubleMatrix<String, String> inputDesign, int numberofColumns ) {
        DoubleMatrix<String, String> tmp;
        tmp = new DenseDoubleMatrix<String, String>( inputDesign.rows(), numberofColumns );
        tmp.assign( 0.0 );

        for ( int i = 0; i < inputDesign.rows(); i++ ) {
            for ( int j = 0; j < inputDesign.columns(); j++ ) {
                if ( i == 0 ) tmp.getColNames().add( inputDesign.getColName( j ) );
                tmp.set( i, j, inputDesign.get( i, j ) );
            }
        }

        if ( !inputDesign.getRowNames().isEmpty() ) tmp.setRowNames( inputDesign.getRowNames() );
        return tmp;
    }

    /**
     * @param sampleInfo
     * @param design
     * @return
     */
    private DoubleMatrix<String, String> designMatrix( ObjectMatrix<?, String, Object> sampleInfo,
            DoubleMatrix<String, String> design ) {
        for ( int i = 0; i < sampleInfo.columns(); i++ ) {
            Object[] f = sampleInfo.getColumn( i );
            List<Object> fL = Arrays.asList( f );
            design = buildDesign( i + 1, fL, design, 2, sampleInfo.getColName( i ) );
        }
        return design;
    }

    /**
     * Build a "standard" design matrix from a matrix of sample information.
     * 
     * @param sampleInfo
     * @param intercept if true, an intercept term is included.
     * @return
     */
    private DoubleMatrix<String, String> designMatrix( ObjectMatrix<String, String, Object> sampleInfo,
            boolean intercept ) {
        DoubleMatrix<String, String> tmp = null;
        if ( intercept ) {
            tmp = new DenseDoubleMatrix<String, String>( sampleInfo.rows(), 1 );
            tmp.addColumnName( "Intercept" );
            tmp.assign( 1.0 );
            this.assign.add( 0 );
            this.terms.put( "Intercept", new ArrayList<Integer>() );
            this.terms.get( "Intercept" ).add( 0 );
        }
        return designMatrix( sampleInfo, tmp );
    }

}
