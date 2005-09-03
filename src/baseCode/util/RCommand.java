/*
 * The Gemma project
 * 
 * Copyright (c) 2005 Columbia University
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
package baseCode.util;

import java.io.IOException;
import java.net.URL;
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

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class RCommand {

    private final static String os = System.getProperty( "os.name" ).toLowerCase();

    private Rconnection connection = null;

    private static Process serverProcess;

    private final static Log log = LogFactory.getLog( RCommand.class.getName() );

    private static final int MAX_TRIES = 10;

    private static final boolean QUIET = true;

    /**
     * This class cannot be directly instantiated.
     */
    private RCommand() {
        if ( serverProcess == null ) this.startServer();
        log.info( "Trying to connect...." );
        this.connect();
    }

    public static RCommand newInstance() {
        return new RCommand();
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
                rserveExecutable = "C:/Program Files/R/rw2001/bin/Rserve.exe";
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
                    throw new RuntimeException( "Could not get a connection to server: timed out" );
                }
            }

            serverProcess.exitValue();
            log.error( "Could not start the Rserver" );
        } catch ( IllegalThreadStateException e ) {
            log.info( "Rserver seems to have started" );
        } catch ( InterruptedException e ) {
            ;
        }
    }

    /**
     * @param beQuiet
     */
    private void connect( boolean beQuiet ) {
        if ( connection != null && connection.isConnected() ) return;
        try {
            connection = new Rconnection();
            if ( !beQuiet ) log.info( "Connected to server" );
        } catch ( RSrvException e ) {
            if ( !beQuiet ) log.error( "Could not connect to RServe", e );
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

    /**
     * 
     *
     */
    public void connect() {
        log.debug( "Entering connect()" );
        connect( false );
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
     * @see org.rosuda.JRclient.Rconnection#voidEval(java.lang.String)
     */
    public void voidEval( String arg ) {
        if ( arg == null ) throw new IllegalArgumentException( "Null command" );
        checkConnection();
        try {
            connection.voidEval( arg );
        } catch ( RSrvException e ) {
            log.error( "R failure with command " + arg, e );
            throw new RuntimeException( e );
        }
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

    /**
     * Run a command that has a single double array parameter, and returns a double array.
     * 
     * @param command
     * @param argName
     * @param arg
     * @return
     */
    public double[] doubleArrayDoubleArrayEval( String command, String argName, double[] arg ) {
        try {
            connection.assign( argName, arg );
            RList l = connection.eval( command ).asList();
            return ( double[] ) l.at( argName ).getContent();
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
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
        checkConnection();
        try {
            connection.assign( argName, arg );
            connection.assign( argName2, arg2 );
            return ( double[] ) connection.eval( command ).getContent();
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
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
        try {
            connection.assign( argName, arg );
            connection.assign( argName2, arg2 );
            REXP x = connection.eval( command );
            return x.asDouble();
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
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
        try {
            connection.assign( argName, arg );
            REXP x = connection.eval( command );
            RBool b = x.asBool();
            return b.isTRUE();
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * Evaluate any command.
     * 
     * @param commmand
     */
    public REXP eval( String commmand ) {
        log.debug( "Entering RCommand.eval" );
        checkConnection();
        try {
            return connection.eval( commmand );
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * Run a command that returns a double array with no arguments.
     * 
     * @param command
     * @return
     */
    public double[] doubleArrayEval( String command ) {
        checkConnection();
        try {
            return ( double[] ) connection.eval( command ).getContent();
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * 
     */
    private void checkConnection() {
        log.debug( "Checking connection" );
        assert connection != null && connection.isConnected();
    }

    /**
     * 
     */
    public void disconnect() {
        if ( connection != null && connection.isConnected() ) connection.close();
    }

    public void finalize() {
        this.disconnect();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, java.lang.String)
     */
    public void assign( String sym, String ct ) throws RSrvException {
        this.connection.assign( sym, ct );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#getLastError()
     */
    public String getLastError() {
        return this.connection.getLastError();
    }

}
