/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import ubic.basecode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;

/**
 * @author pavlidis
 * @version $Id$
 */
public class RServeClient implements RClient {

    final static Log log = LogFactory.getLog( RServeClient.class.getName() );

    static Process serverProcess;

    private static final int MAX_TRIES = 10;

    private final static String os = System.getProperty( "os.name" ).toLowerCase();

    private static final boolean QUIET = true;

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

    private static RConnection connection = null;

    protected RServeClient() {
        connect();
    }

    protected RServeClient( boolean startServer ) {
        if ( startServer ) {
            this.startServer();
        }
        connect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, double[])
     */
    public void assign( String argName, double[] arg ) {
        checkConnection();

        try {
            connection.assign( argName, arg );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, int[])
     */
    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, int[])
     */
    public void assign( String arg0, int[] arg1 ) {
        checkConnection();
        try {
            connection.assign( arg0, arg1 );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, java.lang.String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String)
     */
    public void assign( String sym, String ct ) {
        try {
            connection.assign( sym, ct );
        } catch ( RserveException e ) {
            throw new RuntimeException( "Assignment failed: " + sym + " value " + ct, e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String[])
     */
    public void assign( String argName, String[] array ) {
        try {
            connection.assign( argName, array );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }
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
        double[] unrolledMatrix = RServeClient.unrollMatrix( matrix );
        if ( rows == 0 || cols == 0 ) throw new IllegalArgumentException( "Empty matrix?" );

        this.voidEval( matrixVarName + "_rows<-" + rows );
        this.voidEval( matrixVarName + "_cols<-" + cols );
        this.assign( "U" + matrixVarName, unrolledMatrix );
        this.voidEval( matrixVarName + "<-matrix(" + "U" + matrixVarName + ", nrow=" + matrixVarName + "_rows<-, ncol="
                + matrixVarName + "_cols, byrow=TRUE)" );
        this.voidEval( "rm(U" + matrixVarName + ")" ); // maybe this saves memory...

        return matrixVarName;
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
        String unrolledMatrixVar = "U" + matrixVarName;
        this.assign( unrolledMatrixVar, unrolledMatrix );
        this.voidEval( matrixVarName + "<-matrix(" + unrolledMatrixVar + ", nrow=" + rows + ", ncol=" + cols
                + ", byrow=TRUE)" );
        REXP dimexp = this.eval( "dim(" + matrixVarName + ")" );

        try {
            assert dimexp.asIntegers()[0] == rows;
            assert dimexp.asIntegers()[1] == cols;
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

        this.voidEval( "rm(U" + matrixVarName + ")" ); // maybe this saves memory...

        assignRowAndColumnNames( matrix, matrixVarName );
        return matrixVarName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#assignStringList(java.util.List)
     */
    @SuppressWarnings("unchecked")
    public String assignStringList( List<String> strings ) {
        String variableName = "stringList." + variableIdentityNumber( strings );
        String[] sar = new String[strings.size()];
        strings.toArray( sar );
        this.assign( variableName, sar );
        return variableName;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#booleanDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
    public boolean booleanDoubleArrayEval( String command, String argName, double[] arg ) {
        checkConnection();
        this.assign( argName, arg );
        REXP x = this.eval( command );
        if ( x.isLogical() ) {
            try {
                REXPLogical b = new REXPLogical( new boolean[1], new REXPList( x.asList() ) );
                return b.isTrue()[0];
            } catch ( REXPMismatchException e ) {
                throw new RuntimeException( e );
            }
        }
        return false;
    }

    /**
     * 
     *
     */
    public boolean connect() {
        return connect( false );
    }

    public void disconnect() {
        if ( connection != null && connection.isConnected() ) connection.close();
        connection = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#doubleArrayDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
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
    public double[] doubleArrayEval( String command ) {
        try {
            return this.eval( command ).asDoubles();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
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
     *      java.lang.String, double[])
     */
    public double doubleTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2, double[] arg2 ) {
        checkConnection();
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
     * @see ubic.basecode.util.RClient#eval(java.lang.String)
     */
    private REXP eval( String command ) {
        log.debug( "eval: " + command );
        checkConnection();
        try {
            return connection.eval( command );
        } catch ( RserveException e ) {
            log.error( "Error excecuting " + command, e );
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#finalize()
     */
    public void finalize() {
        this.disconnect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#getLastError()
     */
    public String getLastError() {
        return connection.getLastError();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#intArrayEval(java.lang.String)
     */
    public int[] intArrayEval( String command ) {
        try {
            return eval( command ).asIntegers();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * 
     */
    public boolean isConnected() {
        if ( connection != null && connection.isConnected() ) return true;
        return false;
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
        try {
            log.debug( "Retrieving " + variableName );

            // REXP clr = this.eval( "class(" + variableName + ")" );
            // log.info( clr.asString() );

            // note that for some reason, asDoubleMatrix is returning a 1-d array. So I do this.
            REXP r = this.eval( "data.frame(t(" + variableName + "))" );
            if ( r == null ) throw new IllegalArgumentException( variableName + " not found in R context" );

            RList dataframe = r.asList();
            int numrows = dataframe.size();
            double[][] results = new double[numrows][];
            int i = 0;
            for ( Iterator it = dataframe.iterator(); it.hasNext(); ) {
                REXP next = ( REXP ) it.next();
                double[] row = next.asDoubles();
                results[i] = row;
                i++;
            }

            DoubleMatrixNamed resultObject = DoubleMatrix2DNamedFactory.dense( results );

            retrieveRowAndColumnNames( variableName, resultObject );
            return resultObject;
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( "Failed to get back matrix for variable " + variableName, e );
        }

    }

    /**
     * Starts the RServe server, unless one is already running.
     */
    public void startServer() {

        try {
            boolean connected = connect( QUIET );
            if ( connected ) {
                log.info( "RServer is already running" );
                return;
            }
            log.error( "Server is running but can't connect?" ); // shouldn't hit this, but just in case.
        } catch ( RuntimeException e1 ) {
            // okay, not running
        }

        try {

            String rserveExecutable = findRserveCommand();

            log.info( "Starting Rserve with command " + rserveExecutable );
            serverProcess = Runtime.getRuntime().exec( rserveExecutable );

            GenericStreamConsumer gscErr = new GenericStreamConsumer( serverProcess.getErrorStream() );
            GenericStreamConsumer gscIn = new GenericStreamConsumer( serverProcess.getInputStream() );
            gscErr.start();
            gscIn.start();

            waitForServerStart();

        } catch ( IOException e ) {
            log.error( "Could not start Rserver", e );
        } catch ( ConfigurationException e ) {
            log.error( "Could not connect to RServe: server executable is not configured", e );
            throw new RuntimeException( e );
        } catch ( NoSuchElementException e ) {
            log.error( "Could not connect to RServe, make sure you configure"
                    + " 'rserve.start.command' in your build.properties", e );
            throw new RuntimeException( e );
        }
    }

    /**
     * Stop the server, if it was started by this.
     */
    public void stopServer() {
        if ( serverProcess != null ) {
            log.info( "Stopping server by killing it." );
            serverProcess.destroy();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#stringEval(java.lang.String)
     */
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
     * @see org.rosuda.JRclient.Rconnection#voidEval(java.lang.String)
     */
    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#voidEval(java.lang.String)
     */
    public void voidEval( String command ) {
        if ( command == null ) throw new IllegalArgumentException( "Null command" );
        this.checkConnection();
        try {
            log.debug( "voidEval: " + command );
            connection.voidEval( command );
        } catch ( RserveException e ) {
            log.error( "R failure with command " + command, e );
            throw new RuntimeException( e );
        } catch ( Exception e ) {
            log.error( "R failure with command " + command, e );
            throw new RuntimeException( e );
        }
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
     * 
     */
    private void checkConnection() {
        if ( !this.isConnected() ) throw new RuntimeException( "Not connected" );
    }

    /**
     * @param beQuiet
     */
    private boolean connect( boolean beQuiet ) {
        if ( connection != null && connection.isConnected() ) {
            return true;
        }
        try {
            connection = new RConnection();
        } catch ( RserveException e ) {
            if ( !beQuiet ) {
                log.error( "Could not connect to RServe: " + e.getMessage() );
            }
            return false;
        }
        return true;
    }

    /**
     * @return
     * @throws ConfigurationException
     */
    private String findRserveCommand() throws ConfigurationException {
        URL userSpecificConfigFileLocation = ConfigurationUtils.locate( "local.properties" );

        Configuration userConfig = null;
        if ( userSpecificConfigFileLocation != null ) {
            userConfig = new PropertiesConfiguration( userSpecificConfigFileLocation );
        }
        String rserveExecutable = userConfig.getString( "rserve.start.command" );
        if ( rserveExecutable == null || rserveExecutable.length() == 0 ) {
            log.info( "Rserve command not configured, trying fallbacks" );
            if ( os.startsWith( "windows" ) ) {
                rserveExecutable = System.getenv( "R_HOME" ) + java.io.File.separator + "Rserve.exe";
            } else {
                rserveExecutable = "R CMD Rserve";
            }
        }
        return rserveExecutable;
    }

    /**
     * @param variableName
     * @param resultObject
     * @throws REXPMismatchException
     */
    private void retrieveRowAndColumnNames( String variableName, DoubleMatrixNamed resultObject )
            throws REXPMismatchException {
        // getting the row names.
        RList rownamesREXP = this.eval( "dimnames(" + variableName + ")[1][[1]]" ).asList();
        List<String> rowNames = new ArrayList<String>();
        for ( Iterator it = rownamesREXP.iterator(); it.hasNext(); ) {
            rowNames.add( ( ( REXP ) it.next() ).asString() );
        }
        resultObject.setRowNames( rowNames );

        RList colnamesREXP = this.eval( "dimnames(" + variableName + ")[2][[1]]" ).asList();
        List<String> colNames = new ArrayList<String>();
        for ( Iterator it = colnamesREXP.iterator(); it.hasNext(); ) {
            colNames.add( ( ( REXP ) it.next() ).asString() );
        }
        resultObject.setColumnNames( colNames );
    }

    /**
     * Wait until we can get a connection to the server - if there is a better way to monitor when the server is up, we
     * should us it.
     */
    private void waitForServerStart() {
        try {
            int exitValue = Integer.MIN_VALUE;
            boolean waiting = true;
            int tries = 0;
            while ( waiting ) {
                try {
                    // exitValue = serverProcess.exitValue();
                    boolean connected = connect( QUIET );
                    if ( connected ) {
                        waiting = false;
                        return;
                    } else {
                        // not running, keep trying
                        tries++;
                        Thread.sleep( 2000 );
                    }
                } catch ( IllegalThreadStateException e ) {
                    log.warn( "Rserve process is dead." );
                    return;
                }
                if ( tries > MAX_TRIES ) {
                    log.warn( "Could not get a connection to R server: timed out after " + MAX_TRIES + " attempts." );
                    waiting = false;
                }
            }

            log.error( "Could not get a connection to the server: " + exitValue );
        } catch ( IllegalThreadStateException e ) {
            log.info( "Rserver seems to have started" );
        } catch ( InterruptedException e ) {
            ;
        }
    }

}
