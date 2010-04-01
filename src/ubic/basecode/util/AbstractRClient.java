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
package ubic.basecode.util;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPDouble;
import org.rosuda.REngine.REXPFactor;
import org.rosuda.REngine.REXPGenericVector;
import org.rosuda.REngine.REXPInteger;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPMismatchException;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.util.r.type.HTest;

/**
 * Base class for RClients
 * 
 * @author Paul
 * @version $Id$
 */
public abstract class AbstractRClient implements RClient {

    private static Log log = LogFactory.getLog( AbstractRClient.class.getName() );

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

    /**
     * @param ob
     * @return
     */
    public static String variableIdentityNumber( Object ob ) {
        return Integer.toString( Math.abs( ob.hashCode() ) ) + RandomStringUtils.randomAlphabetic( 6 );
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#assignFactor(java.util.List)
     */
    public String assignFactor( List<String> strings ) {
        String variableName = "factor." + variableIdentityNumber( strings );
        Object[] array = strings.toArray();
        String[] sa = new String[array.length];
        for ( int i = 0; i < array.length; i++ ) {
            sa[i] = array[i].toString();
        }

        String l = assignStringList( strings );
        this.voidEval( variableName + "<-factor(" + l + ")" );
        return variableName;
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#assignMatrix(double[][])
     */
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
     * @see ubic.basecode.util.RClient#assignMatrix(ubic.basecode.dataStructure.matrix.DoubleMatrixNamed)
     */
    public String assignMatrix( DoubleMatrix<?, ?> matrix ) {
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

        if ( matrix.hasColNames() && matrix.hasRowNames() ) assignRowAndColumnNames( matrix, matrixVarName );
        return matrixVarName;
    }

    /**
     * @param matrix
     * @param matrixVarName
     * @return
     */
    protected <R, C> void assignRowAndColumnNames( DoubleMatrix<R, C> matrix, String matrixVarName ) {

        String rowNameVar = assignStringList( matrix.getRowNames() );
        String colNameVar = assignStringList( matrix.getColNames() );

        String dimcmd = "dimnames(" + matrixVarName + ")<-list(" + rowNameVar + ", " + colNameVar + ")";
        this.voidEval( dimcmd );
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#assignStringList(java.util.List)
     */
    @SuppressWarnings("unchecked")
    public String assignStringList( List strings ) {
        String variableName = "stringList." + variableIdentityNumber( strings );

        Object[] array = strings.toArray();
        String[] sa = new String[array.length];
        for ( int i = 0; i < array.length; i++ ) {
            sa[i] = array[i].toString();
        }

        assign( variableName, sa );
        return variableName;
    }

    protected abstract REXP eval( String command );

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
                } else {

                    log.debug( rexp.getClass().getName() );
                    result = new ArrayList<Object>( rexp.asList().values() );
                }
            } else {
                throw new UnsupportedOperationException( "Don't know how to deal with vector type of "
                        + rexp.getClass().getName() );
            }

            return result;
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /* (non-Javadoc)
     * @see ubic.basecode.util.RClient#listEvalWithLogging(java.lang.Class, java.lang.String)
     */
    public List<?> listEvalWithLogging( Class<?> listEntryType, String command ) { 
        RLoggingThread rLoggingThread = null;
        List<?> result = null;
        try {
            rLoggingThread = RLoggingThreadFactory.createRLoggingThread();
            result = this.listEval( listEntryType, command );
        } catch ( Exception e ) {
            throw new RuntimeException( "Problems executing R command " + command.toString(), e );
        } finally {
            if ( rLoggingThread != null ) {
                log.debug( "Shutting down logging thread." );
                rLoggingThread.done();
            }
        }
        return result;

    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#loadLibrary(java.lang.String)
     */
    public boolean loadLibrary( String libraryName ) {
        List<String> libraries = stringListEval( "installed.packages()[,1]" );
        if ( libraries.contains( libraryName ) ) {
            voidEval( "library(" + libraryName + ")" );
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#remove(java.lang.String)
     */
    public void remove( String variableName ) {
        this.voidEval( "rm(" + variableName + ")" );
    }

}
