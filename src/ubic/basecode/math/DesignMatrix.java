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

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.StringMatrix;
import ubic.basecode.util.r.type.LinearModelSummary;

import cern.colt.matrix.DoubleMatrix2D;
import cern.colt.matrix.impl.DenseDoubleMatrix2D;

/**
 * Represents the A matrix in regression problems posed as Ax=b. The starting point is a matrix of sample information,
 * where the rows are sample names and the columns are the names of factors or covariates. Intercept and interaction
 * terms can be added.
 * <p>
 * Baseline levels are initially determined by the order in which factor levels appear. You can re-level using the
 * setBaseline method.
 * 
 * @author paul
 * @version $Id$
 */
public class DesignMatrix {

    private static Log log = LogFactory.getLog( DesignMatrix.class );

    private DoubleMatrix<String, String> matrix;

    /**
     * 
     */
    private List<Integer> assign = new ArrayList<Integer>();

    /**
     * Store which terms show up in which columns of the design
     */
    private Map<String, List<Integer>> terms = new LinkedHashMap<String, List<Integer>>();

    private boolean hasIntercept = false;

    /**
     * Saved version of the original factors provided.
     */
    private Map<String, List<Object>> valuesForFactors = new LinkedHashMap<String, List<Object>>();

    /**
     * Only applied for categorical factors.
     */
    private Map<String, List<String>> levelsForFactors = new LinkedHashMap<String, List<String>>();

    private Set<String[]> interactions = new LinkedHashSet<String[]>();

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
    public DesignMatrix( ObjectMatrix<String, String, ? extends Object> sampleInfo ) {
        this( sampleInfo, true );
    }

    /**
     * @param sampleInfo in form of Doubles or Strings. Any other types will yield errors.
     * @param intercept
     */
    public DesignMatrix( ObjectMatrix<String, String, ?> sampleInfo, boolean intercept ) {
        matrix = this.designMatrix( sampleInfo, intercept );
        this.hasIntercept = intercept;
        if ( sampleInfo.getRowNames().size() == matrix.rows() ) matrix.setRowNames( sampleInfo.getRowNames() );

    }

    public DesignMatrix( StringMatrix<String, String> sampleInfo ) {
        this( sampleInfo, true );
    }

    /**
     * Append additional factors/covariates to this
     * 
     * @param sampleInfo
     */
    public void add( ObjectMatrix<String, String, Object> sampleInfo ) {
        this.matrix = this.designMatrix( sampleInfo, this.matrix );
    }

    /**
     * Add interaction term; only works if there exactly two factors so this can figure out which interaction to add.
     * For more control use the other method.
     */
    public void addInteraction() {
        if ( this.terms.size() != 2 && !hasIntercept ) {
            throw new IllegalArgumentException( "You must specify which two terms" );
        }

        if ( this.terms.size() == 2 && hasIntercept || this.terms.size() < 2 ) {
            throw new IllegalArgumentException( "You need at least two terms" );
        }

        if ( this.terms.size() > 3 ) {
            throw new IllegalArgumentException( "You must specify which two terms, there are " + this.terms.size()
                    + " terms: " + StringUtils.join( this.terms.keySet(), "," ) );
        }

        List<String> iterms = new ArrayList<String>();
        for ( String t : terms.keySet() ) {
            if ( t.equals( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME ) ) {
                continue;
            }
            iterms.add( t );
        }

        this.addInteraction( iterms.toArray( new String[] {} ) );
    }

    /**
     * @param interactionTerms
     */
    public void addInteraction( String... interactionTerms ) {

        /*
         * Figure out which columns of the data we need to look at.
         * 
         * If the factor is in >1 columns - we have to add two or more columns
         */
        Collection<String> doneTerms = new HashSet<String>();

        // the column where the interaction "goes"
        int interactionIndex = terms.size();

        for ( String t1 : interactionTerms ) {
            if ( doneTerms.contains( t1 ) ) continue;
            List<Integer> cols1 = terms.get( t1 );

            for ( int i = 0; i < cols1.size(); i++ ) {
                double[] col1i = this.matrix.getColumn( cols1.get( i ) );
                for ( String t2 : interactionTerms ) {
                    if ( t1.equals( t2 ) ) continue;
                    doneTerms.add( t2 );
                    List<Integer> cols2 = terms.get( t2 );

                    for ( int j = 0; j < cols2.size(); j++ ) {
                        double[] col2i = this.matrix.getColumn( cols2.get( j ) );

                        Double[] prod = new Double[col1i.length];

                        this.matrix = this.copyWithSpace( this.matrix, this.matrix.columns() + 1 );
                        String termName = null;
                        for ( int k = 0; k < col1i.length; k++ ) {
                            prod[k] = col1i[k] * col2i[k];
                            if ( prod[k] != 0 && StringUtils.isBlank( termName ) ) {
                                String if1 = this.valuesForFactors.get( t1 ).get( k ).toString();
                                String if2 = this.valuesForFactors.get( t2 ).get( k ).toString();
                                termName = t1 + if1 + ":" + t2 + if2;
                            }
                            matrix.set( k, this.matrix.columns() - 1, prod[k] );
                        }

                        matrix.addColumnName( termName );
                        if ( !this.terms.containsKey( termName ) ) {
                            this.terms.put( termName, new ArrayList<Integer>() );
                        }
                        terms.get( termName ).add( this.matrix.columns() - 1 );
                        assign.add( interactionIndex );
                    }
                }
            }
        }

        this.interactions.add( interactionTerms );

    }

    /**
     * @return
     */
    public List<Integer> getAssign() {
        return assign;
    }

    public String getBaseline( String factorName ) {
        return this.levelsForFactors.get( factorName ).toString();
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

    public boolean hasIntercept() {
        return this.hasIntercept;
    }

    /**
     * @param factorName
     * @param baselineFactorValue
     */
    public void setBaseline( String factorName, String baselineFactorValue ) {
        if ( !this.levelsForFactors.containsKey( factorName ) ) {
            throw new IllegalArgumentException( "No factor known by name " + factorName );
        }
        List<String> oldValues = this.levelsForFactors.get( factorName );
        int index = oldValues.indexOf( baselineFactorValue );
        if ( index < 0 ) {
            throw new IllegalArgumentException( baselineFactorValue + " is not a level of the factor " + factorName );
        }

        if ( index == 0 ) return;

        /*
         * Put the given level in the desired location; move the others along.
         */
        List<String> releveled = new ArrayList<String>();
        releveled.add( oldValues.get( index ) );
        for ( int i = 0; i < oldValues.size(); i++ ) {
            if ( i == index ) continue;
            releveled.add( oldValues.get( i ) );
        }
        this.levelsForFactors.put( factorName, releveled );

        /*
         * Recompute the design.
         */
        this.rebuild();
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
     * @param rows
     * @return
     */
    private DoubleMatrix<String, String> addIntercept( int rows ) {
        DoubleMatrix<String, String> tmp;
        tmp = new DenseDoubleMatrix<String, String>( rows, 1 );
        tmp.addColumnName( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME );
        tmp.assign( 1.0 );
        this.assign.add( 0 );
        this.terms.put( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME, new ArrayList<Integer>() );
        this.terms.get( LinearModelSummary.INTERCEPT_COEFFICIENT_NAME ).add( 0 );
        return tmp;
    }

    /**
     * @param which column of the input matrix are we working on.
     * @param factorValues of doubles or strings.
     * @param inputDesign
     * @param start 1 or 2. Set to 1 to get a column for each level (must not have an intercept in the model); Set to 2
     *        to get a column for all but the last (Redundant) level.
     * @param factorName String to associate with the factor
     * @return
     */
    private DoubleMatrix<String, String> buildDesign( int columnNum, List<?> factorValues,
            DoubleMatrix<String, String> inputDesign, int start, String factorName ) {
        if ( !terms.containsKey( factorName ) ) {
            terms.put( factorName, new ArrayList<Integer>() );
        }
        DoubleMatrix<String, String> tmp = null;
        if ( factorValues.get( 0 ) instanceof Double ) {
            tmp = addContinuousCovariate( factorValues, inputDesign );
            this.assign.add( columnNum );
            terms.get( factorName ).add( columnNum );
            tmp.addColumnName( factorName );
        } else {
            /*
             * CATEGORICAL COVARIATE
             */
            List<String> levels;
            if ( this.levelsForFactors.containsKey( factorName ) ) {
                levels = this.levelsForFactors.get( factorName );
            } else {
                levels = levels( factorName, ( List<String> ) factorValues );
            }

            if ( inputDesign != null ) {
                /*
                 * copy it into a new one.
                 */
                assert factorValues.size() == inputDesign.rows();
                int numberofColumns = inputDesign.columns() + levels.size() - start + 1;
                tmp = copyWithSpace( inputDesign, numberofColumns );

            } else {
                tmp = new DenseDoubleMatrix<String, String>( factorValues.size(), levels.size() - start + 1 );
                tmp.assign( 0.0 );
            }

            List<String> levelList = new ArrayList<String>();
            levelList.addAll( levels );
            int startcol = 0;
            if ( inputDesign != null ) {
                startcol = inputDesign.columns();
            }
            for ( int i = startcol; i < tmp.columns(); i++ ) {

                String contrastingValue = null;
                for ( int j = 0; j < tmp.rows(); j++ ) {
                    boolean isBaseline = !factorValues.get( j ).equals( levelList.get( i - startcol + ( start - 1 ) ) );

                    if ( !isBaseline ) {
                        contrastingValue = ( String ) factorValues.get( j );
                    }

                    tmp.set( j, i, isBaseline ? 0.0 : 1.0 );
                }

                assert contrastingValue != null;

                this.assign.add( columnNum ); // all of these columns use this factor.

                terms.get( factorName ).add( i );
                tmp.addColumnName( factorName + contrastingValue );
            }
        }
        return tmp;
    }

    /**
     * Add extra empty columns to a matrix, implemented by copying.
     * 
     * @param inputDesign
     * @param numberofColumns how many to add.
     * @return
     */
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
     * Build a "standard" design matrix from a matrix of sample information.
     * 
     * @param sampleInfo
     * @param intercept if true, an intercept term is included.
     * @return
     */
    private DoubleMatrix<String, String> designMatrix( ObjectMatrix<String, String, ?> sampleInfo, boolean intercept ) {
        DoubleMatrix<String, String> tmp = null;
        if ( intercept ) {
            int rows = sampleInfo.rows();
            tmp = addIntercept( rows );
        }
        return designMatrix( sampleInfo, tmp );
    }

    /**
     * @param sampleInfo
     * @param design
     * @return
     */
    private DoubleMatrix<String, String> designMatrix( ObjectMatrix<String, String, ?> sampleInfo,
            DoubleMatrix<String, String> design ) {
        for ( int i = 0; i < sampleInfo.columns(); i++ ) {
            Object[] factorValuesAr = sampleInfo.getColumn( i );
            List<Object> factorValues = Arrays.asList( factorValuesAr );
            design = buildDesign( i + 1, factorValues, design, 2, sampleInfo.getColName( i ) );
            this.valuesForFactors.put( sampleInfo.getColName( i ), factorValues );
        }
        return design;
    }

    /**
     * @param vec
     * @return
     */
    private List<String> levels( String factorName, List<String> vec ) {
        return this.levels( factorName, vec.toArray( new String[] {} ) );
    }

    private List<String> levels( String factorName, String[] vec ) {
        Set<String> flevs = new LinkedHashSet<String>();
        for ( String v : vec ) {
            flevs.add( v );
        }
        List<String> result = new ArrayList<String>();
        for ( String fl : flevs ) {
            result.add( fl );
        }
        this.levelsForFactors.put( factorName, result );
        return result;
    }

    /**
     * Refresh the design matrix, for example after releveling.
     */
    protected void rebuild() {
        this.matrix = null;
        this.assign.clear();
        this.terms.clear();

        if ( this.hasIntercept ) {
            int nrows = valuesForFactors.get( valuesForFactors.keySet().iterator().next() ).size();
            matrix = addIntercept( nrows );
        }

        int i = 0;
        for ( String factorName : valuesForFactors.keySet() ) {
            List<Object> factorValues = valuesForFactors.get( factorName );
            this.valuesForFactors.put( factorName, factorValues );

            if ( factorValues.get( 0 ) instanceof String && !this.levelsForFactors.containsKey( factorName ) ) {
                this.levels( factorName, factorValues.toArray( new String[] {} ) );
            }

            matrix = buildDesign( i + 1, factorValues, matrix, 2, factorName );

            i++;
        }

        if ( !this.interactions.isEmpty() ) {
            List<String[]> redoInteractionTerms = new ArrayList<String[]>();
            for ( String[] interactionTerms : interactions ) {
                redoInteractionTerms.add( interactionTerms );
            }
            this.interactions.clear();
            for ( String[] t : redoInteractionTerms ) {
                this.addInteraction( t );
            }
        }
    }

}
