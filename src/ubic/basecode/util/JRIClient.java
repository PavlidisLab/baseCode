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
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.REXPMismatchException;

import ubic.basecode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;

/**
 * @author paul
 * @version $Id$
 */
public class JRIClient implements RClient {

    private static Log log = LogFactory.getLog( JRIClient.class.getName() );

//    public static void main( String[] args ) {
//        // just making sure we have the right version of everything
//        if ( !Rengine.versionCheck() ) {
//            System.err.println( "** Version mismatch - Java files don't match library version." );
//            System.exit( 1 );
//        }
//        System.out.println( "Creating Rengine (with arguments)" );
//        // 1) we pass the arguments from the command line
//        // 2) we won't use the main loop at first, we'll start it later
//        // (that's the "false" as second argument)
//        // 3) the callbacks are implemented by the TextConsole class above
//        Rengine re = new Rengine( args, false, new TextConsole() );
//        System.out.println( "Rengine created, waiting for R" );
//        // the engine creates R is a new thread, so we should wait until it's ready
//        if ( !re.waitForR() ) {
//            System.out.println( "Cannot load R" );
//            return;
//        }
//
//        REXP x;
//        re.eval( "data(iris)", false );
//        System.out.println( x = re.eval( "iris" ) );
//        re.end();
//
//    }

    Rengine connection;

    // static {
    // connection = new Rengine( new String[] {}, false, new TextConsole() );
    // }

    public JRIClient() {

        if ( connection == null ) {
            log.info( "Loading JRI library, looking in " + System.getProperty( "java.library.path" ) );
            try {
                System.loadLibrary( "jri" );
            } catch ( UnsatisfiedLinkError e ) {
                throw new RuntimeException( "No jri library, looked in: " + System.getProperty( "java.library.path" ) );
            }
            connection = new Rengine( new String[] { "--no-save" }, false, null );
            if ( !connection.waitForR() ) {
                throw new UnsatisfiedLinkError( "Cannot load R" );
            }
        }
        /*
         * TODO capture the RConsoleOutputStream.
         */
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, double[])
     */
    public void assign( String argName, double[] arg ) {
        connection.assign( argName, arg );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, int[])
     */
    public void assign( String arg0, int[] arg1 ) {
        connection.assign( arg0, arg1 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String[])
     */
    public void assign( String argName, String[] array ) {
        connection.assign( argName, array );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, org.rosuda.REngine.REXP)
     */
    public void assign( String arg0, REXP arg1 ) {
        connection.assign( arg0, arg1 );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String)
     */
    public void assign( String sym, String ct ) {
        connection.assign( sym, ct );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignMatrix(ubic.basecode.dataStructure.matrix.DoubleMatrixNamed)
     */
    public String assignMatrix( DoubleMatrixNamed matrix ) {
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

        assignRowAndColumnNames( matrix, matrixVarName );
        return matrixVarName;
    }

    /**
     * @param matrix
     * @param matrixVarName
     * @return
     */
    private void assignRowAndColumnNames( DoubleMatrixNamed matrix, String matrixVarName ) {

        String rowNameVar = assignStringList( matrix.getRowNames() );
        String colNameVar = assignStringList( matrix.getColNames() );

        String dimcmd = "dimnames(" + matrixVarName + ")<-list(" + rowNameVar + ", " + colNameVar + ")";
        this.voidEval( dimcmd );
    }

    /**
     * @param ob
     * @return
     */
    public static String variableIdentityNumber( Object ob ) {
        return Integer.toString( Math.abs( ob.hashCode() ) );
    }

    /**
     * Copy a matrix into an array, so that rows are represented consecutively in the array. (RServe has no interface
     * for passing a 2-d array).
     * 
     * @param matrix
     * @return array representation of the matrix.
     */
    private static double[] unrollMatrix( DoubleMatrixNamed matrix ) {
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
                unrolledMatrix[k] = matrix.getQuick( i, j );
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

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignMatrix(double[][])
     */
    public String assignMatrix( double[][] matrix ) {
        String matrixVarName = "Matrix_" + variableIdentityNumber( matrix );
        log.debug( "Assigning matrix with variable name " + matrixVarName );
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] unrolledMatrix = unrollMatrix( matrix );
        if ( rows == 0 || cols == 0 ) throw new IllegalArgumentException( "Empty matrix?" );

        this.voidEval( matrixVarName + "_rows<-" + rows );
        this.voidEval( matrixVarName + "_cols<-" + cols );
        this.assign( "U" + matrixVarName, unrolledMatrix );
        this.voidEval( matrixVarName + "<-matrix(" + "U" + matrixVarName + ", nrow=rows, ncol=cols, byrow=TRUE)" );
        this.voidEval( "rm(U" + matrixVarName + ")" ); // maybe this saves memory...

        return matrixVarName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignStringList(java.util.List)
     */
    public String assignStringList( List<String> strings ) {
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
    public boolean booleanDoubleArrayEval( String command, String argName, double[] arg ) {
        this.assign( argName, arg );
        REXP x = this.eval( command );
        return x.asBool().isTRUE();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleArrayDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
    public double[] doubleArrayDoubleArrayEval( String command, String argName, double[] arg ) {
        this.assign( argName, arg );
        RList l = this.eval( command ).asList();
        return l.at( argName ).asDoubleArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleArrayEval(java.lang.String)
     */
    public double[] doubleArrayEval( String command ) {
        return this.eval( command ).asDoubleArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleArrayTwoDoubleArrayEval(java.lang.String, java.lang.String, double[],
     *      java.lang.String, double[])
     */
    public double[] doubleArrayTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2,
            double[] arg2 ) {
        this.assign( argName, arg );
        this.assign( argName2, arg2 );

        return this.eval( command ).asDoubleArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleTwoDoubleArrayEval(java.lang.String, java.lang.String, double[],
     *      java.lang.String, double[])
     */
    public double doubleTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2, double[] arg2 ) {
        this.assign( argName, arg );
        this.assign( argName2, arg2 );
        REXP x = this.eval( command );

        return x.asDouble();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#eval(java.lang.String)
     */
    private REXP eval( String command ) {
        return connection.eval( command );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#getLastError()
     */
    public String getLastError() {
        return "Sorry, no information";
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#remove(java.lang.String)
     */
    public void remove( String variableName ) {
        this.voidEval( "rm(" + variableName + ")" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#retrieveMatrix(java.lang.String)
     */
    public DoubleMatrixNamed retrieveMatrix( String variableName ) {
        log.debug( "Retrieving " + variableName );
        REXP r = this.eval( variableName );
        if ( r == null ) throw new IllegalArgumentException( variableName + " not found in R context" );

        double[][] results = r.asDoubleMatrix();

        if ( results == null ) throw new RuntimeException( "Failed to get back matrix for variable " + variableName );

        DoubleMatrixNamed resultObject = DoubleMatrix2DNamedFactory.dense( results );

        retrieveRowAndColumnNames( variableName, resultObject );
        return resultObject;

    }

    /**
     * @param variableName
     * @param resultObject
     * @throws REXPMismatchException
     */
    private void retrieveRowAndColumnNames( String variableName, DoubleMatrixNamed resultObject ) {
        // getting the row names.
        List rowNamesREXP = this.eval( "dimnames(" + variableName + ")[1][[1]]" ).asVector();

        if ( rowNamesREXP != null ) {
            log.debug( "Got row names" );
            List rowNames = new ArrayList();
            for ( Iterator iter = rowNamesREXP.iterator(); iter.hasNext(); ) {
                REXP element = ( REXP ) iter.next();
                String rowName = element.asString();
                rowNames.add( rowName );
            }
            resultObject.setRowNames( rowNames );
        }

        // Getting the column names.
        List colNamesREXP = this.eval( "dimnames(" + variableName + ")[2][[1]]" ).asVector();
        if ( colNamesREXP != null ) {
            log.debug( "Got column names" );
            List colNames = new ArrayList();
            for ( Iterator iter = colNamesREXP.iterator(); iter.hasNext(); ) {
                REXP element = ( REXP ) iter.next();
                String rowName = element.asString();
                colNames.add( rowName );
            }
            resultObject.setColumnNames( colNames );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#voidEval(java.lang.String)
     */
    public void voidEval( String command ) {
        connection.eval( command );
    }

    public boolean isConnected() {
        return connection.isAlive();
    }

    public void disconnect() {
        connection.end();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#stringEval(java.lang.String)
     */
    public String stringEval( String command ) {
        return this.eval( command ).asString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#intArrayEval(java.lang.String)
     */
    public int[] intArrayEval( String command ) {
        return this.eval( command ).asIntArray();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#loadLibrary(java.lang.String)
     */
    public boolean loadLibrary( String libraryName ) {
        REXP eval = eval( "library(" + libraryName + ")" );
        return true;
    }

}
