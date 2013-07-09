/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.functors.StringValueTransformer;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPFactor;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.RList;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrix;
import ubic.basecode.dataStructure.matrix.ObjectMatrixImpl;
import ubic.basecode.util.r.type.HTest;
import ubic.basecode.util.r.type.LinearModelSummary;
import ubic.basecode.util.r.type.OneWayAnovaResult;
import ubic.basecode.util.r.type.TwoWayAnovaResult;

/**
 * Base class for RClients
 * 
 * @author Paul
 * @version $Id$
 */
public abstract class AbstractRClient implements RClient {

    private static Log log = LogFactory.getLog( AbstractRClient.class.getName() );

    /**
     * @param ob
     * @return
     */
    public static String variableIdentityNumber( Object ob ) {
        return Integer.toString( Math.abs( ob.hashCode() + 1 ) ) + RandomStringUtils.randomAlphabetic( 6 );
    }

    /**
     * Copy a matrix into an array, so that rows are represented consecutively in the array. (RServe has no interface
     * for passing a 2-d array).
     * 
     * @param matrix
     * @return
     */
    private static double[] unrollMatrix( double[][] matrix ) {
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] unrolledMatrix = new double[rows * cols];

        int k = 0;
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < cols; j++ ) {
                unrolledMatrix[k] = matrix[i][j];
                k++;
            }
        }
        return unrolledMatrix;
    }

    /**
     * Copy a matrix into an array, so that rows are represented consecutively in the array. (RServe has no interface
     * for passing a 2-d array).
     * 
     * @param matrix
     * @return array representation of the matrix.
     */
    private static double[] unrollMatrix( DoubleMatrix<?, ?> matrix ) {
        // unroll the matrix into an array Unfortunately this makes a
        // copy of the data...and R will probably make yet
        // another copy. If there was a way to get the raw element array from the DoubleMatrixNamed, that would
        // be better.
        int rows = matrix.rows();
        int cols = matrix.columns();
        double[] unrolledMatrix = new double[rows * cols];

        int k = 0;
        for ( int i = 0; i < rows; i++ ) {
            for ( int j = 0; j < cols; j++ ) {
                unrolledMatrix[k] = matrix.get( i, j );
                k++;
            }
        }
        return unrolledMatrix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignFactor(java.util.List)
     */
    @Override
    public String assignFactor( List<String> strings ) {
        String variableName = "f." + variableIdentityNumber( strings );
        return assignFactor( variableName, strings );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignFactor(java.lang.String, java.util.List)
     */
    @Override
    public String assignFactor( String factorName, List<String> list ) {
        String l = assignStringList( list );
        this.voidEval( factorName + "<-factor(" + l + ")" );
        return factorName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignMatrix(double[][])
     */
    @Override
    public String assignMatrix( double[][] matrix ) {
        String matrixVarName = "Matrix_" + variableIdentityNumber( matrix );
        log.debug( "Assigning matrix with variable name " + matrixVarName );
        int rows = matrix.length;
        int cols = matrix[0].length;
        if ( rows == 0 || cols == 0 ) throw new IllegalArgumentException( "Empty matrix?" );
        double[] unrolledMatrix = unrollMatrix( matrix );
        this.assign( "U" + matrixVarName, unrolledMatrix ); // temporary
        this.voidEval( matrixVarName + "<-matrix(" + "U" + matrixVarName + ", nrow=" + rows + " , ncol=" + cols
                + ", byrow=TRUE)" );
        this.voidEval( "rm(U" + matrixVarName + ")" ); // maybe this saves memory...

        return matrixVarName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignMatrix(ubic.basecode.dataStructure.matrix.DoubleMatrixNamed)
     */
    @Override
    public String assignMatrix( DoubleMatrix<?, ?> matrix ) {
        return assignMatrix( matrix, StringValueTransformer.getInstance() );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignMatrix(ubic.basecode.dataStructure.matrix.DoubleMatrix,
     * org.apache.commons.collections.Transformer)
     */
    @Override
    public String assignMatrix( DoubleMatrix<?, ?> matrix, Transformer rowNameExtractor ) {
        String matrixVarName = "Matrix_" + variableIdentityNumber( matrix );
        log.debug( "Assigning matrix with variable name " + matrixVarName );
        int rows = matrix.rows();
        int cols = matrix.columns();
        if ( rows == 0 || cols == 0 ) throw new IllegalArgumentException( "Empty matrix?" );
        double[] unrolledMatrix = unrollMatrix( matrix );
        assert ( unrolledMatrix != null );
        this.assign( "U" + matrixVarName, unrolledMatrix );
        this.voidEval( matrixVarName + "<-matrix(" + "U" + matrixVarName + ", nrow=" + rows + ", ncol=" + cols
                + ", byrow=TRUE)" );
        this.voidEval( "rm(U" + matrixVarName + ")" ); // maybe this saves memory...

        if ( matrix.hasColNames() && matrix.hasRowNames() )
            assignRowAndColumnNames( matrix, matrixVarName, rowNameExtractor );
        return matrixVarName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignStringList(java.util.List)
     */
    @Override
    public String assignStringList( List<?> strings ) {
        String variableName = "stringList." + variableIdentityNumber( strings );

        Object[] array = strings.toArray();
        String[] sa = new String[array.length];
        for ( int i = 0; i < array.length; i++ ) {
            sa[i] = array[i].toString();
        }

        assign( variableName, sa );
        return variableName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#booleanDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
    @Override
    public boolean booleanDoubleArrayEval( String command, String argName, double[] arg ) {
        this.assign( argName, arg );
        REXP x = this.eval( command );
        if ( x.isLogical() ) {
            try {
                REXPLogical b = new REXPLogical( new boolean[1], new REXPList( x.asList() ) );
                return b.isTRUE()[0];
            } catch ( REXPMismatchException e ) {
                throw new RuntimeException( e );
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#dataFrame(ubic.basecode.dataStructure.matrix.ObjectMatrix)
     */
    @Override
    public String dataFrame( ObjectMatrix<String, String, Object> matrix ) {

        /*
         * Extract columns, convert
         */

        List<String> colNames = matrix.getColNames();
        List<String> rowNames = matrix.getRowNames();

        assert colNames.size() == matrix.columns();

        String colV = assignStringList( colNames );
        String rowV = assignStringList( rowNames );

        List<String> terms = new ArrayList<String>();
        for ( int j = 0; j < colNames.size(); j++ ) {

            Object[] column;

            Object v = matrix.getEntry( 0, j );

            if ( v instanceof Number ) {
                column = new Double[matrix.rows()];
            } else if ( v instanceof Boolean ) {
                column = new String[matrix.rows()];
            } else if ( v instanceof String ) {
                column = new String[matrix.rows()];
            } else {
                throw new IllegalArgumentException( "Sorry, can't make data frame from values of type "
                        + v.getClass().getName() );
            }

            for ( int i = 0; i < matrix.rows(); i++ ) {

                Object value = matrix.getEntry( i, j );

                if ( matrix.isMissing( i, j ) ) {
                    column[i] = null;
                } else if ( value instanceof Number ) {
                    column[i] = ( ( Number ) value ).doubleValue();
                } else if ( value instanceof Boolean ) {
                    column[i] = ( ( Boolean ) value ) ? "T" : "F";
                } else if ( value instanceof String ) {
                    column[i] = value;
                }
            }

            if ( v instanceof Number ) {
                assign( colNames.get( j ), ArrayUtils.toPrimitive( ( Double[] ) column ) );
                terms.add( colNames.get( j ) );
            } else if ( v instanceof Boolean ) {
                assignFactor( colNames.get( j ), Arrays.asList( ( String[] ) column ) );
                terms.add( colNames.get( j ) );
            } else if ( v instanceof String ) {
                assignFactor( colNames.get( j ), Arrays.asList( ( String[] ) column ) );
                terms.add( colNames.get( j ) );
            }

        }

        String varName = "df." + variableIdentityNumber( matrix );

        String command = varName + "<-data.frame(" + StringUtils.join( terms, "," ) + ", row.names=" + rowV + " )";
        log.debug( command );
        eval( command );
        eval( "names(" + varName + ")<-" + colV );

        return varName;

    }

    @Override
    public ObjectMatrix<String, String, Object> dataFrameEval( String command ) {

        REXP df = eval( command );

        try {

            RList dfl = df.asList();

            if ( !df.getAttribute( "class" ).asString().equals( "data.frame" ) ) {
                throw new IllegalArgumentException( "Command did not return a dataframe" );
            }

            String[] rowNames = df.getAttribute( "row.names" ).asStrings();
            String[] colNames = df.getAttribute( "names" ).asStrings();

            assert dfl.size() == colNames.length;

            ObjectMatrix<String, String, Object> result = new ObjectMatrixImpl<String, String, Object>(
                    rowNames.length, colNames.length );

            result.setRowNames( Arrays.asList( rowNames ) );
            result.setColumnNames( Arrays.asList( colNames ) );

            for ( int i = 0; i < dfl.size(); i++ ) {
                REXP column = ( REXP ) dfl.get( i );

                if ( column.isNumeric() ) {
                    double[] asDoubles = column.asDoubles();

                    for ( int j = 0; j < rowNames.length; j++ ) {
                        result.set( j, i, asDoubles[j] );
                    }

                } else {
                    String[] asStrings = column.asStrings();

                    for ( int j = 0; j < rowNames.length; j++ ) {
                        result.set( j, i, asStrings[j] );
                    }

                }

            }

            return result;
        } catch ( REXPMismatchException e ) {

            throw new RuntimeException( e );

        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleArrayDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
    @Override
    public double[] doubleArrayDoubleArrayEval( String command, String argName, double[] arg ) {
        try {
            this.assign( argName, arg );
            RList l = this.eval( command ).asList();
            return l.at( argName ).asDoubles();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleArrayEval(java.lang.String)
     */
    @Override
    public double[] doubleArrayEval( String command ) {
        REXP r = this.eval( command );
        if ( r == null ) {
            return null;
        }

        if ( !r.isNumeric() ) {
            throw new RuntimeException( "Command did not return numbers: " + command + ", result was: " + r );
        }

        try {
            return r.asDoubles();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleArrayTwoDoubleArrayEval(java.lang.String, java.lang.String, double[],
     * java.lang.String, double[])
     */
    @Override
    public double[] doubleArrayTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2,
            double[] arg2 ) {
        this.assign( argName, arg );
        this.assign( argName2, arg2 );
        try {
            return this.eval( command ).asDoubles();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleTwoDoubleArrayEval(java.lang.String, java.lang.String, double[],
     * java.lang.String, double[])
     */
    @Override
    public double doubleTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2, double[] arg2 ) {
        this.assign( argName, arg );
        this.assign( argName2, arg2 );
        REXP x = this.eval( command );
        try {
            return x.asDouble();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#intArrayEval(java.lang.String)
     */
    @Override
    public int[] intArrayEval( String command ) {
        try {
            return eval( command ).asIntegers();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#linearModel(double[], java.util.List)
     */
    @Override
    @SuppressWarnings({ "unchecked" })
    public LinearModelSummary linearModel( double[] data, Map<String, List<?>> factors ) {

        String datName = RandomStringUtils.randomAlphabetic( 10 );
        assign( datName, data );

        for ( String factorName : factors.keySet() ) {
            List<?> list = factors.get( factorName );
            if ( list.iterator().next() instanceof String ) {
                assignFactor( factorName, ( List<String> ) list );
            } else {
                // treat is as a numeric covariate
                List<Double> d = new ArrayList<Double>();
                for ( Object object : list ) {
                    d.add( ( Double ) object );
                }

                assign( factorName, ArrayUtils.toPrimitive( d.toArray( new Double[] {} ) ) );
            }
        }

        String modelDeclaration = datName + " ~ " + StringUtils.join( factors.keySet(), "+" );

        String lmName = RandomStringUtils.randomAlphabetic( 10 );
        String command = lmName + "<-lm(" + modelDeclaration + ", na.action=na.exclude)";
        log.debug( command );
        voidEval( command );

        REXP lmsum = eval( "summary(" + lmName + ")" );
        REXP anova = eval( "anova(" + lmName + ")" );

        return new LinearModelSummary( lmsum, anova, factors.keySet().toArray( new String[] {} ) );

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#linearModel(double[], ubic.basecode.dataStructure.matrix.ObjectMatrix)
     */
    @Override
    public LinearModelSummary linearModel( double[] data, ObjectMatrix<String, String, Object> d ) {

        String datName = RandomStringUtils.randomAlphabetic( 10 );
        assign( datName, data );

        String df = dataFrame( d );

        String varNames = StringUtils.join( d.getColNames(), "+" );

        String lmName = RandomStringUtils.randomAlphabetic( 10 );
        String command = lmName + "<-lm(" + datName + " ~ " + varNames + ", data=" + df + ", na.action=na.exclude)";
        voidEval( command );

        REXP lmsum = eval( "summary(" + lmName + ")" );
        REXP anova = eval( "anova(" + lmName + ")" );

        return new LinearModelSummary( lmsum, anova, d.getColNames().toArray( new String[] {} ) );

    }

    /**
     * FIXME only partly implemented, possibly not going to stay.
     */
    @Override
    public List<?> listEval( Class<?> listEntryType, String command ) {

        REXP rexp = this.eval( command );

        List<Object> result = new ArrayList<Object>();
        try {
            if ( !rexp.isVector() ) {
                throw new IllegalArgumentException( "Command did not return some kind of vector" );
            }

            if ( rexp instanceof REXPInteger ) {
                log.debug( "integer" );
                double[][] asDoubleMatrix = rexp.asDoubleMatrix();
                for ( double[] ds : asDoubleMatrix ) {
                    result.add( ds );
                }

                if ( rexp instanceof REXPFactor ) {
                    log.info( "factor" );
                    // not sure what to do...
                }
            } else if ( rexp instanceof REXPGenericVector ) {
                log.debug( "generic" );
                REXPGenericVector v = ( REXPGenericVector ) rexp;
                List<?> tmp = new ArrayList<Object>( v.asList().values() );

                if ( tmp.size() == 0 ) return tmp;

                for ( Object t : tmp ) {
                    String clazz = ( ( REXP ) t ).getAttribute( "class" ).asString();
                    /*
                     * FIXME!!!!
                     */
                    if ( clazz.equals( "htest" ) ) {
                        try {
                            result.add( new HTest( ( ( REXP ) t ).asList() ) );
                        } catch ( REXPMismatchException e ) {
                            result.add( new HTest() );
                        }
                    } else if ( clazz.equals( "lm" ) ) {
                        throw new UnsupportedOperationException();
                    } else {
                        result.add( new HTest() ); // e.g. failed result or something we don't know about yet
                    }
                    /*
                     * todo: support lm objects, anova objects others? pair.htest?
                     */
                }

            } else if ( rexp instanceof REXPDouble ) {
                log.debug( "double" );
                double[][] asDoubleMatrix = rexp.asDoubleMatrix();
                for ( double[] ds : asDoubleMatrix ) {
                    result.add( ds );
                }

            } else if ( rexp instanceof REXPList ) {
                log.debug( "list" );
                if ( rexp.isPairList() ) {
                    // log.info( "pairlist" ); always true for REXPList.
                }

                if ( rexp.isLanguage() ) {
                    throw new UnsupportedOperationException( "Don't know how to deal with vector type of "
                            + rexp.getClass().getName() );
                }
                log.debug( rexp.getClass().getName() );
                result = new ArrayList<Object>( rexp.asList().values() );
            } else {
                throw new UnsupportedOperationException( "Don't know how to deal with vector type of "
                        + rexp.getClass().getName() );
            }

            return result;
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#loadLibrary(java.lang.String)
     */
    @Override
    public boolean loadLibrary( String libraryName ) {
        try {
            List<String> libraries = stringListEval( "installed.packages()[,1]" );
            if ( !libraries.contains( libraryName ) ) {
                return false;
            }
        } catch ( Exception e ) {
            /*
             * This can happen with a 'Error in gzfile()', no idea why and it seems harmless if uninformative.
             */
            log.warn( "Was unable to check for installed library before attempting to load it." );
        }

        try {
            voidEval( "library(" + libraryName + ")" );
            return true;
        } catch ( Exception e ) {
            return false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#oneWayAnova(double[], java.util.List)
     */
    @Override
    public OneWayAnovaResult oneWayAnova( double[] data, List<String> factor ) {
        String f = assignFactor( factor );
        StringBuffer command = new StringBuffer();

        assign( "foo", data );

        String modelDeclaration;

        modelDeclaration = "foo  ~ " + f;

        command.append( "anova(aov(" + modelDeclaration + "))" );

        REXP eval = eval( command.toString() );

        return new OneWayAnovaResult( eval );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#oneWayAnovaEval(java.lang.String)
     */
    @Override
    public Map<String, OneWayAnovaResult> oneWayAnovaEval( String command ) {
        REXP rawResult = this.eval( command );

        if ( rawResult == null ) {
            return null;
        }

        Map<String, OneWayAnovaResult> result = new HashMap<String, OneWayAnovaResult>();
        try {
            RList mainList = rawResult.asList();
            if ( mainList == null ) {
                return null;
            }

            log.debug( mainList.size() + " results." );

            for ( int i = 0; i < mainList.size(); i++ ) {

                REXP anovaTable = mainList.at( i );

                String elementIdentifier = mainList.keyAt( i );

                assert elementIdentifier != null;

                if ( log.isDebugEnabled() ) log.debug( "Key: " + elementIdentifier );

                if ( !anovaTable.isList() || !anovaTable.hasAttribute( "row.names" ) ) {
                    log.debug( "No anovaresult for " + elementIdentifier );
                    result.put( elementIdentifier, new OneWayAnovaResult() );
                    continue;
                }

                result.put( elementIdentifier, new OneWayAnovaResult( anovaTable ) );

            }
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

        return result;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#remove(java.lang.String)
     */
    @Override
    public void remove( String variableName ) {
        this.voidEval( "rm(" + variableName + ")" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#rowApplyLinearModel(java.lang.String, java.lang.String, java.lang.String[])
     */
    @Override
    public Map<String, LinearModelSummary> rowApplyLinearModel( String dataMatrixVarName, String modelFormula,
            String[] factorNames ) {

        String lmres = "lmlist." + RandomStringUtils.randomAlphanumeric( 10 );

        log.info( "Starting model fitting ..." );

        loadScript( this.getClass().getResourceAsStream( "/ubic/basecode/util/r/linearModels.R" ) );

        String command = lmres + "<-rowlm(" + modelFormula + ", data.frame(" + dataMatrixVarName + ") )";

        log.debug( command );
        this.voidEval( command );

        log.info( "Model fits complete, summarizing ..." );
        REXP rawLmSummaries = this.eval( "lapply(" + lmres + ", function(x){ try(summary(x), silent=T)})" );

        if ( rawLmSummaries == null ) {
            log.warn( "No results from apply(... lm)" );
            return null;
        }

        log.info( "Summaries done, doing hypothesis tests ..." );
        /* this tends to be the slow part. */
        REXP rawAnova = this.eval( "lapply(" + lmres + ", function(x){ try(anova(x), silent=T)})" );

        Map<String, LinearModelSummary> result = new HashMap<String, LinearModelSummary>();
        try {
            log.info( "Processing the results ..." );

            RList rawLmList = rawLmSummaries.asList();
            if ( rawLmList == null ) {
                log.warn( "Raw lm summary results were null" );
                return null;
            }

            RList rawAnovaList = rawAnova.asList();
            if ( rawAnovaList == null ) {
                return null;
            }

            log.debug( rawLmList.size() + " results." );

            assert rawLmList.size() == rawAnovaList.size();

            for ( int i = 0; i < rawLmList.size(); i++ ) {

                REXP lmSummary = rawLmList.at( i );
                REXP anova = rawAnovaList.at( i );

                String elementIdentifier = rawLmList.keyAt( i );

                assert elementIdentifier != null;
                assert elementIdentifier.equals( rawAnovaList.keyAt( i ) );

                if ( log.isDebugEnabled() ) log.debug( "Key: " + elementIdentifier );

                if ( !lmSummary.isList() || !lmSummary.getAttribute( "class" ).asString().equals( "summary.lm" ) ) {
                    log.debug( "No lm for " + elementIdentifier );
                    result.put( elementIdentifier, new LinearModelSummary( elementIdentifier ) );
                } else {
                    LinearModelSummary linearModelSummary = new LinearModelSummary( lmSummary, anova, factorNames );
                    result.put( elementIdentifier, linearModelSummary );
                }

            }
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#stringEval(java.lang.String)
     */
    @Override
    public String stringEval( String command ) {
        try {
            return this.eval( command ).asString();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.r.RClient#stringListEval(java.lang.String)
     */
    @Override
    public List<String> stringListEval( String command ) {
        try {
            REXP eval = this.eval( command );

            RList v;
            List<String> results = new ArrayList<String>();
            if ( eval instanceof REXPString ) {
                String[] strs = ( ( REXPString ) eval ).asStrings();
                for ( String string : strs ) {
                    results.add( string );
                }
            } else {
                v = eval.asList();
                for ( Iterator<?> it = v.iterator(); it.hasNext(); ) {
                    results.add( ( ( REXPString ) it.next() ).asString() );
                }
            }

            return results;
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.r.RClient#twoWayAnova(double[], java.util.List, java.util.List, boolean)
     */
    @Override
    public TwoWayAnovaResult twoWayAnova( double[] data, List<String> factor1, List<String> factor2,
            boolean includeInteraction ) {

        String factorA = assignFactor( factor1 );
        String factorB = assignFactor( factor2 );
        StringBuffer command = new StringBuffer();

        assign( "foo", data );

        String modelDeclaration;

        if ( includeInteraction ) {
            modelDeclaration = "foo  ~ " + factorA + "*" + factorB;
        } else {
            modelDeclaration = "foo  ~ " + factorA + "+" + factorB;
        }

        command.append( "anova(aov(" + modelDeclaration + "))" );

        REXP eval = eval( command.toString() );

        return new TwoWayAnovaResult( eval );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#twoWayAnovaEval(java.lang.String, boolean)
     */
    @Override
    public Map<String, TwoWayAnovaResult> twoWayAnovaEval( String command, boolean withInteractions ) {
        REXP rawResult = this.eval( command );

        if ( rawResult == null ) {
            return null;
        }

        Map<String, TwoWayAnovaResult> result = new HashMap<String, TwoWayAnovaResult>();
        try {
            RList mainList = rawResult.asList();
            if ( mainList == null ) {
                return null;
            }

            log.debug( mainList.size() + " results." );

            for ( int i = 0; i < mainList.size(); i++ ) {

                REXP anovaTable = mainList.at( i );

                String elementIdentifier = mainList.keyAt( i );

                assert elementIdentifier != null;

                if ( log.isDebugEnabled() ) log.debug( "Key: " + elementIdentifier );

                if ( !anovaTable.isList() || !anovaTable.hasAttribute( "row.names" ) ) {
                    log.debug( "No anovaresult for " + elementIdentifier );
                    result.put( elementIdentifier, new TwoWayAnovaResult() );
                    continue;
                }

                result.put( elementIdentifier, new TwoWayAnovaResult( anovaTable ) );

            }
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

        return result;

    }

    /**
     * There is a pretty annoying limitation of this. The file must contain only one statement. You can get around this
     * by using c(x<-1,x<-2). See testScript.R
     * 
     * @param is
     */
    protected void loadScript( InputStream is ) {
        try {

            BufferedReader reader = new BufferedReader( new InputStreamReader( is ) );
            String line = null;
            StringBuilder buf = new StringBuilder();
            while ( ( line = reader.readLine() ) != null ) {
                if ( line.startsWith( "#" ) || StringUtils.isBlank( line ) ) {
                    continue;
                }
                buf.append( StringUtils.trim( line ) + "\n" );
            }
            is.close();
            log.debug( buf.toString() );
            this.voidEval( buf.toString() );

        } catch ( IOException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param matrix
     * @param matrixVarName
     * @param rowNameExtractor e.g. you could use StringValueTransformer.getInstance()
     * @return
     */
    private void assignRowAndColumnNames( DoubleMatrix<?, ?> matrix, String matrixVarName, Transformer rowNameExtractor ) {

        List<Object> rown = new ArrayList<Object>();
        for ( Object o : matrix.getRowNames() ) {
            rown.add( rowNameExtractor.transform( o ) );
        }

        String rowNameVar = assignStringList( rown );
        String colNameVar = assignStringList( matrix.getColNames() );

        String dimcmd = "dimnames(" + matrixVarName + ")<-list(" + rowNameVar + ", " + colNameVar + ")";
        this.voidEval( dimcmd );
    }

}
