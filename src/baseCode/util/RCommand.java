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

    private static Rconnection connection;

    private final static Log log = LogFactory.getLog( RCommand.class.getName() );

    static {
        try {
            connection = new Rconnection();
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

}
