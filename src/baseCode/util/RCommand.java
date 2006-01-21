/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.util;

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
import org.rosuda.JRclient.RBool;
import org.rosuda.JRclient.REXP;
import org.rosuda.JRclient.RList;
import org.rosuda.JRclient.RSrvException;
import org.rosuda.JRclient.Rconnection;

import baseCode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import baseCode.dataStructure.matrix.DoubleMatrixNamed;

/**
 * @author pavlidis
 * @version $Id$
 */
public class RCommand {

    private final static Log log = LogFactory.getLog( RCommand.class.getName() );

    private static final int MAX_TRIES = 10;

    private final static String os = System.getProperty( "os.name" ).toLowerCase();

    private static final boolean QUIET = true;

    private static Process serverProcess;

    private Rconnection connection = null;

    /**
     * This class cannot be directly instantiated.
     */
    private RCommand() {
        if ( serverProcess == null ) {
            this.startServer();
        }
        log.info( "Trying to connect...." );
        this.connect();
        log.info( "Connected!" );
    }

    /**
     * @param argName
     * @param arg
     */
    public void assign( String argName, double[] arg ) {
        try {
            connection.assign( argName, arg );
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, int[])
     */
    public void assign( String arg0, int[] arg1 ) {
        checkConnection();
        try {
            connection.assign( arg0, arg1 );
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * 
     */
    private void checkConnection() {
        if ( !this.isConnected() ) throw new RuntimeException( "Not connected" );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, org.rosuda.JRclient.REXP)
     */
    public void assign( String arg0, REXP arg1 ) {
        checkConnection();
        try {
            connection.assign( arg0, arg1 );
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, java.lang.String)
     */
    public void assign( String sym, String ct ) {
        try {
            this.connection.assign( sym, ct );
        } catch ( RSrvException e ) {
            throw new RuntimeException( "Assignment failed: " + sym + " value " + ct, e );
        }
    }

    /**
     * Assign a 2-d matrix.
     * 
     * @param matrix
     * @return the name of the variable by which the R matrix can be referred.
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
     * @param ob
     * @return
     */
    public static String variableIdentityNumber( Object ob ) {
        return Integer.toString( Math.abs( ob.hashCode() ) );
    }

    /**
     * Define a variable corresponding to a character array in the R context, given a List of Strings.
     * 
     * @param strings
     * @return the name of the variable in the R context.
     */
    public String assignStringList( List strings ) {
        String variableName = "stringList." + variableIdentityNumber( strings );

        Object[] stringOA = strings.toArray();
        String[] stringSA = new String[stringOA.length];
        for ( int i = 0; i < stringOA.length; i++ ) {
            stringSA[i] = ( String ) stringOA[i];
        }
        REXP stringRexp = new REXP( stringSA );
        assign( variableName, stringRexp );
        return variableName;
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
     * Assign a 2-d matrix.
     * 
     * @param matrix
     * @return the name of the variable by which the R matrix can be referred.
     */
    public String assignMatrix( double[][] matrix ) {
        String matrixVarName = "Matrix_" + variableIdentityNumber( matrix );
        log.debug( "Assigning matrix with variable name " + matrixVarName );
        int rows = matrix.length;
        int cols = matrix[0].length;
        double[] unrolledMatrix = RCommand.unrollMatrix( matrix );
        if ( rows == 0 || cols == 0 ) throw new IllegalArgumentException( "Empty matrix?" );

        this.voidEval( matrixVarName + "_rows<-" + rows );
        this.voidEval( matrixVarName + "_cols<-" + cols );
        this.assign( "U" + matrixVarName, unrolledMatrix );
        this.voidEval( matrixVarName + "<-matrix(" + "U" + matrixVarName + ", nrow=rows, ncol=cols, byrow=TRUE)" );
        this.voidEval( "rm(U" + matrixVarName + ")" ); // maybe this saves memory...

        return matrixVarName;
    }

    /**
     * Run a command that takes a double array as an argument and returns a boolean.
     * 
     * @param command
     * @param argName
     * @param arg
     * @return
     */
    public boolean booleanDoubleArrayEval( String command, String argName, double[] arg ) {
        checkConnection();
        this.assign( argName, arg );
        REXP x = this.eval( command );
        RBool b = x.asBool();
        return b.isTRUE();
    }

    /**
     * 
     *
     */
    public void connect() {
        connect( false );
    }

    /**
     * 
     */
    public void disconnect() {
        if ( connection != null && connection.isConnected() ) connection.close();
        connection = null;
    }

    /**
     * Run a command that has a single double array parameter, and returns a double array.
     * 
     * @param command
     * @param argName
     * @param arg
     * @return
     */
    public double[] doubleArrayDoubleArrayEval( String command, String argName, double[] arg ) {
        this.assign( argName, arg );
        RList l = this.eval( command ).asList();
        return ( double[] ) l.at( argName ).getContent();
    }

    /**
     * Run a command that returns a double array with no arguments.
     * 
     * @param command
     * @return
     */
    public double[] doubleArrayEval( String command ) {
        return ( double[] ) this.eval( command ).getContent();
    }

    /**
     * Run a command that takes two double array arguments and returns a double array.
     * 
     * @param command
     * @param argName
     * @param arg
     * @param argName2
     * @param arg2
     * @return
     */
    public double[] doubleArrayTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2,
            double[] arg2 ) {
        this.assign( argName, arg );
        this.assign( argName2, arg2 );
        return ( double[] ) this.eval( command ).getContent();
    }

    /**
     * Run a command that takes two double arrays as arguments and returns a double value.
     * 
     * @param command
     * @param argName
     * @param arg
     * @param argName2
     * @param arg2
     * @return
     */
    public double doubleTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2, double[] arg2 ) {
        checkConnection();
        this.assign( argName, arg );
        this.assign( argName2, arg2 );
        REXP x = this.eval( command );
        return x.asDouble();
    }

    /**
     * Evaluate any command.
     * 
     * @param command
     */
    public REXP eval( String command ) {
        log.debug( "eval: " + command );
        checkConnection();
        try {
            return connection.eval( command );
        } catch ( RSrvException e ) {
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
     * @see org.rosuda.JRclient.Rconnection#getLastError()
     */
    public String getLastError() {
        return this.connection.getLastError();
    }

    /**
     * Remove a variable from the R namespace
     * 
     * @param variableName
     */
    public void remove( String variableName ) {
        this.voidEval( "rm(" + variableName + ")" );
    }

    /**
     * Get a matrix back out of the R context. Row and Column names are filled in for the resulting object, if they are
     * present.
     * 
     * @param variableName
     * @return
     */
    public DoubleMatrixNamed retrieveMatrix( String variableName ) {

        log.debug( "Retrieving " + variableName );
        REXP r = this.eval( variableName );
        if ( r == null ) throw new IllegalArgumentException( variableName + " not found in R context" );

        double[][] results = r.asDoubleMatrix();

        if ( results == null )
            throw new RuntimeException( "Failed to get back matrix for variable " + variableName
                    + ", object has length " + r.getBinaryLength() + " bytes." );

        DoubleMatrixNamed resultObject = DoubleMatrix2DNamedFactory.dense( results );

        retrieveRowAndColumnNames( variableName, resultObject );
        return resultObject;

    }

    /**
     * @param variableName
     * @param resultObject
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

    /**
     * Starts the RServe server, unless one is already running.
     */
    public void startServer() {

        try {
            connect( QUIET );
            if ( connection.isConnected() ) {
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

            waitForServerStart();

        } catch ( IOException e ) {
            log.error( "Could not start Rserver" );
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
     * @see org.rosuda.JRclient.Rconnection#voidEval(java.lang.String)
     */
    public void voidEval( String command ) {
        if ( command == null ) throw new IllegalArgumentException( "Null command" );
        this.checkConnection();
        try {
            log.debug( "voidEval: " + command );
            connection.voidEval( command );
        } catch ( RSrvException e ) {
            log.error( "R failure with command " + command, e );
            throw new RuntimeException( e );
        } catch ( Exception e ) {
            log.error( "R failure with command " + command, e );
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

    /**
     * @param beQuiet
     */
    private void connect( boolean beQuiet ) {
        if ( connection != null && connection.isConnected() ) {
            return;
        }
        try {
            connection = new Rconnection();
            if ( !beQuiet ) log.info( "Connected to server" );
        } catch ( RSrvException e ) {
            if ( !beQuiet ) log.error( "Could not connect to RServe", e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @return
     * @throws ConfigurationException
     */
    private String findRserveCommand() throws ConfigurationException {
        URL userSpecificConfigFileLocation = ConfigurationUtils.locate( "build.properties" );

        Configuration userConfig = null;
        if ( userSpecificConfigFileLocation != null ) {
            userConfig = new PropertiesConfiguration( userSpecificConfigFileLocation );
        }
        String rserveExecutable = userConfig.getString( "rserve.start.command" );
        if ( rserveExecutable == null || rserveExecutable.length() == 0 ) {
            log.info( "Rserve command not configured, trying fallbacks" );
            if ( os.startsWith( "windows" ) ) {
                rserveExecutable = "C:/Program Files/R/rw2011pat/bin/Rserve.exe";
            } else {
                rserveExecutable = "R CMD Rserve";
            }
        }
        return rserveExecutable;
    }

    /**
     * Wait until we can get a connection to the server - if there is a better way to monitor when the server is up, we
     * should us it.
     */
    private void waitForServerStart() {
        try {

            boolean waiting = true;
            int tries = 0;
            while ( waiting ) {
                try {
                    connect( QUIET );
                    waiting = false;
                    return;
                } catch ( RuntimeException e1 ) {
                    // not running, keep trying
                    tries++;
                    Thread.sleep( 100 );
                }
                if ( tries > MAX_TRIES ) {
                    throw new RuntimeException( "Could not get a connection to server: timed out after " + MAX_TRIES
                            + " attempts." );
                }
            }

            serverProcess.exitValue();
            log.error( "Could not get a connection to the server." );
        } catch ( IllegalThreadStateException e ) {
            log.info( "Rserver seems to have started" );
        } catch ( InterruptedException e ) {
            ;
        }
    }

    public static RCommand newInstance() {
        return new RCommand();
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

}
