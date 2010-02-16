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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPList;
import org.rosuda.REngine.REXPLogical;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REXPString;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import ubic.basecode.dataStructure.matrix.DenseDoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;

/**
 * @author pavlidis
 * @version $Id$
 */
public class RServeClient extends AbstractRClient {

    final static Log log = LogFactory.getLog( RServeClient.class.getName() );

    static Process serverProcess;

    private static final int MAX_TRIES = 10;

    private final static String os = System.getProperty( "os.name" ).toLowerCase();

    private static final boolean QUIET = true;

    private static RConnection connection = null;

    /**
     * @return
     * @throws ConfigurationException
     */
    protected static String findRserveCommand() throws ConfigurationException {
        URL userSpecificConfigFileLocation = ConfigurationUtils.locate( "local.properties" );

        Configuration userConfig = null;
        if ( userSpecificConfigFileLocation != null ) {
            userConfig = new PropertiesConfiguration( userSpecificConfigFileLocation );
        }
        String rserveExecutable = null;
        if ( userConfig != null ) {
            rserveExecutable = userConfig.getString( "rserve.start.command" );
        }
        if ( StringUtils.isBlank( rserveExecutable ) ) {
            log.info( "Rserve command not configured? Trying fallbacks" );
            if ( os.startsWith( "windows" ) ) { // lower cased
                rserveExecutable = System.getenv( "R_HOME" ) + File.separator + "library" + File.separator + "Rserve"
                        + File.separator + "Rserve.exe";
            } else {
                rserveExecutable = "R CMD Rserve";
            }
        }
        return rserveExecutable;
    }

    /**
     * @param host
     * @throws IOException
     */
    protected RServeClient( String host ) throws IOException {
        // 6311 is default port for Rserve.
        if ( !connect( host, 6311 ) ) {
            throw new IOException( "Could not connect to Rserve" );
        }
    }

    /**
     * Gets connection on default host (localhost) and port (6311)
     * 
     * @throws IOException
     */
    protected RServeClient() throws IOException {
        if ( !connect() ) {
            throw new IOException( "Could not connect to Rserve" );
        }
    }

    /**
     * @param startServer
     * @throws IOException
     */
    protected RServeClient( boolean startServer ) throws IOException {
        if ( startServer ) {
            this.startServer();
        }
        if ( !connect() ) {
            throw new IOException( "Could not connect to Rserve" );
        }
    }

    /*
     * (non-Javadoc)
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
     * @see ubic.basecode.util.RClient#assign(java.lang.String, int[])
     */
    public void assign( String arg0, int[] arg1 ) {
        if ( StringUtils.isBlank( arg0 ) ) {
            throw new IllegalArgumentException( "Must supply valid variable name" );
        }
        checkConnection();
        try {
            connection.assign( arg0, arg1 );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, java.lang.String)
     */
    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String)
     */
    public void assign( String sym, String ct ) {
        if ( StringUtils.isBlank( sym ) ) {
            throw new IllegalArgumentException( "Must supply valid variable name" );
        }
        try {
            connection.assign( sym, ct );
        } catch ( RserveException e ) {
            throw new RuntimeException( "Assignment failed: " + sym + " value " + ct, e );
        }
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String[])
     */
    public void assign( String argName, String[] array ) {
        if ( array == null || array.length == 0 ) {
            throw new IllegalArgumentException( "Array must not be null or empty" );
        }
        if ( StringUtils.isBlank( argName ) ) {
            throw new IllegalArgumentException( "Must supply valid variable name" );
        }
        try {
            log.debug( "assign: " + argName + "<-" + array.length + " strings." );
            connection.assign( argName, array );
        } catch ( REngineException e ) {
            throw new RuntimeException( "Failure with assignment: " + argName + "<-" + array.length + " strings." + e );
        }
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#booleanDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
    public boolean booleanDoubleArrayEval( String command, String argName, double[] arg ) {
        checkConnection();
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
     * @see ubic.basecode.util.RClient#doubleArrayTwoDoubleArrayEval(java.lang.String, java.lang.String, double[],
     * java.lang.String, double[])
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
     * @see ubic.basecode.util.RClient#doubleTwoDoubleArrayEval(java.lang.String, java.lang.String, double[],
     * java.lang.String, double[])
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
     * @see java.lang.Object#finalize()
     */
    @Override
    public void finalize() {
        this.disconnect();
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#getLastError()
     */
    public String getLastError() {
        return connection.getLastError();
    }

    /*
     * (non-Javadoc)
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
     * @see ubic.basecode.util.RClient#retrieveMatrix(java.lang.String)
     */
    public DoubleMatrix<String, String> retrieveMatrix( String variableName ) {
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
            for ( Iterator<?> it = dataframe.iterator(); it.hasNext(); ) {
                REXP next = ( REXP ) it.next();
                double[] row = next.asDoubles();
                results[i] = row;
                i++;
            }

            DoubleMatrix<String, String> resultObject = new DenseDoubleMatrix<String, String>( results );

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
     * @see ubic.basecode.util.RClient#stringEval(java.lang.String)
     */
    public String stringEval( String command ) {
        try {
            return this.eval( command ).asString();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param string
     * @return
     */
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
     * @see org.rosuda.JRclient.Rconnection#voidEval(java.lang.String)
     */
    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#voidEval(java.lang.String)
     */
    public void voidEval( String command ) {
        if ( command == null ) throw new IllegalArgumentException( "Null command" );
        this.checkConnection();
        try {
            log.debug( "voidEval: " + command );
            connection.voidEval( command );
        } catch ( RserveException e ) {
            throw new RuntimeException( "R failure with command " + command, e );
        } catch ( Exception e ) {
            throw new RuntimeException( "R failure with command " + command, e );
        }
    }

    /**
     * 
     */
    private void checkConnection() {
        if ( !this.isConnected() ) throw new RuntimeException( "Not connected" );
    }

    /**
     * @param host
     * @param port
     * @return
     */
    private boolean connect( String host, int port ) {
        if ( connection != null && connection.isConnected() ) {
            return true;
        }
        try {
            log.info( "Trying Rserve connection ..." );
            connection = new RConnection( host, port );
        } catch ( RserveException e ) {
            log.error( "Could not connect to RServe: " + e.getMessage() );
            return false;
        }
        log.info( "Rserve connection looks good!" );
        return true;
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

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#eval(java.lang.String)
     */
    private REXP eval( String command ) {
        log.debug( "eval: " + command );
        checkConnection();
        try {
            return connection.eval( command );
        } catch ( RserveException e ) {
            log.error( "Error excecuting " + command + ":" + e.getMessage(), e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param variableName
     * @param resultObject
     * @throws REXPMismatchException
     */
    private void retrieveRowAndColumnNames( String variableName, DoubleMatrix<String, String> resultObject ) {
        List<String> rowNames = this.stringListEval( "dimnames(" + variableName + ")[1][[1]]" );

        if ( rowNames.size() == resultObject.rows() ) {
            resultObject.setRowNames( rowNames );
        }

        List<String> colNames = this.stringListEval( "dimnames(" + variableName + ")[2][[1]]" );

        if ( colNames.size() == resultObject.columns() ) {
            resultObject.setColumnNames( colNames );
        }
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
                    }
                    // not running, keep trying
                    tries++;
                    Thread.sleep( 2000 );

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
            //
        }
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#anovaEval(java.lang.String)
     */
    public TwoWayAnovaResult twoWayAnovaEval( String command ) {
        try {
            REXP rawResult = this.eval( command );

            if ( rawResult == null ) {
                log.error( "R command returned null: " + command );
                return null;
            }

            RList mainList = rawResult.asList();
            if ( mainList == null ) {
                log.warn( "No string list in R: " + command );
                return null;
            }

            /*
             * The values are in the correct order, but the keys in the mainList (from mainList.keys()) are not for some
             * reason so I'm not using them. In the debugger, the key is the composite sequence, but this isn't the same
             * composite sequence I see directly in R. The order of the p values and statistics are correct, however.
             */
            LinkedHashMap<String, double[]> pvalues = new LinkedHashMap<String, double[]>();
            LinkedHashMap<String, double[]> statistics = new LinkedHashMap<String, double[]>();

            for ( int i = 0; i < mainList.keys().length; i++ ) {
                REXP r1 = mainList.at( i );
                RList l1 = r1.asList();
                if ( l1 == null ) {
                    log.warn( "No string list in R: " + command );
                    return null;
                }

                String[] keys = l1.keys();
                for ( String key : keys ) {
                    if ( StringUtils.equals( "Pr(>F)", key ) ) {
                        REXP r2 = l1.at( key );
                        double[] pValsFromR = ( double[] ) r2.asDoubles();
                        double[] pValsToUse = new double[pValsFromR.length - 1];
                        for ( int j = 0; j < pValsToUse.length; j++ ) {
                            pValsToUse[j] = pValsFromR[j];
                        }

                        pvalues.put( Integer.toString( i ), pValsToUse );
                    } else if ( StringUtils.equals( "F value", key ) ) {
                        REXP r2 = l1.at( key );
                        double[] statisticsFromR = ( double[] ) r2.asDoubles();
                        double[] statisticsToUse = new double[statisticsFromR.length - 1];
                        for ( int j = 0; j < statisticsToUse.length; j++ ) {
                            statisticsToUse[j] = statisticsFromR[j];
                        }
                        statistics.put( Integer.toString( i ), statisticsToUse );
                    }

                }
            }

            TwoWayAnovaResult result = new TwoWayAnovaResult( pvalues, statistics );

            return result;
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#doubleArrayEvalWithLogging(java.lang.String)
     */
    public double[] doubleArrayEvalWithLogging( String command ) {
        RLoggingThread rLoggingThread = null;
        double[] doubleArray = null;
        try {
            rLoggingThread = RLoggingThreadFactory.createRLoggingThread();
            doubleArray = this.doubleArrayEval( command );
        } catch ( Exception e ) {
            throw new RuntimeException( "Problems executing R command " + command + ": " + e.getMessage() );
        } finally {
            if ( rLoggingThread != null ) {
                log.debug( "Shutting down logging thread." );
                rLoggingThread.done();
            }
        }
        return doubleArray;
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#twoWayAnovaEvalWithLogging(java.lang.String)
     */
    public TwoWayAnovaResult twoWayAnovaEvalWithLogging( String command ) {
        RLoggingThread rLoggingThread = null;
        TwoWayAnovaResult twoWayAnovaResult = null;
        try {
            rLoggingThread = RLoggingThreadFactory.createRLoggingThread();
            twoWayAnovaResult = this.twoWayAnovaEval( command );
        } catch ( Exception e ) {
            throw new RuntimeException( "Problems executing R command " + command + ": " + e.getMessage(), e );
        } finally {
            if ( rLoggingThread != null ) {
                log.debug( "Shutting down logging thread." );
                rLoggingThread.done();
            }
        }
        return twoWayAnovaResult;
    }

}
