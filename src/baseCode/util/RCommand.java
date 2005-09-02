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

    private static String os = System.getProperty( "os.name" ).toLowerCase();

    private static Rconnection connection;

    private static Process serverProcess;

    private final static Log log = LogFactory.getLog( RCommand.class.getName() );

    private static final int MAX_TRIES = 10;

    private static final boolean QUIET = true;

    /**
     * 
     *
     */
    public static void startServer() {

        try {
            connect( QUIET );
            log.info( "RServer is already running" );
            return;
        } catch ( RuntimeException e1 ) {
            // okay, not running
        }

        try {

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

            log.info( "Starting Rserve with command " + rserveExecutable );
            serverProcess = Runtime.getRuntime().exec( rserveExecutable );

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
     * @param beQuiet
     */
    private static void connect( boolean beQuiet ) {
        if ( connection != null && connection.isConnected() ) return;
        try {
            connection = new Rconnection();
            if ( !beQuiet ) log.debug( "Connected to server" );
        } catch ( RSrvException e ) {
            if ( !beQuiet ) log.error( "Could not connect to RServe", e );
            throw new RuntimeException( e );
        }
    }

    /**
     * Stop the server, if it was started by this.
     */
    public static void stopServer() {
        if ( serverProcess != null ) {
            log.info( "Stopping server by killing it." );
            serverProcess.destroy();
        }
    }

    /**
     * 
     *
     */
    public static void connect() {
        connect( false );
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, int[])
     */
    public static void assign( String arg0, int[] arg1 ) {
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
    public static void assign( String arg0, REXP arg1 ) {
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
    public static void voidEval( String arg0 ) {
        checkConnection();
        try {
            connection.voidEval( arg0 );
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param argName
     * @param arg
     */
    public static void assign( String argName, double[] arg ) {
        try {
            connection.assign( argName, arg );
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param command
     * @param argName
     * @param arg
     * @return
     */
    public static double[] doubleArrayDoubleArrayExec( String command, String argName, double[] arg ) {
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
     * @param command
     * @param argName
     * @param arg
     * @param argName2
     * @param arg2
     * @return
     */
    public static double[] doubleArrayTwoDoubleArrayExec( String command, String argName, double[] arg,
            String argName2, double[] arg2 ) {
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
     * @param commmand
     */
    public static REXP exec( String commmand ) {
        checkConnection();
        try {
            return connection.eval( commmand );
        } catch ( RSrvException e ) {
            log.error( e, e );
            throw new RuntimeException( e );
        }
    }

    /**
     * @param command
     * @return
     */
    public static double[] execDoubleArray( String command ) {
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
    private static void checkConnection() {
        assert connection != null && connection.isConnected();
    }

    /**
     * 
     */
    public static void disconnect() {
        if ( connection != null && connection.isConnected() ) connection.close();
    }

}
