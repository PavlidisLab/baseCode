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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;

import ubic.basecode.dataStructure.matrix.DoubleMatrix2DNamedFactory;
import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;

/**
 * R connection implementation that uses the dynamic library interface JRI. For this to work the user must have
 * libjri.so or jir.dll and libr.so in their java.library.path.
 * 
 * @author paul
 * @version $Id$
 * @see RConnectionFactory
 */
public class JRIClient extends AbstractRClient {

    static Rengine connection;

    private static Log log = LogFactory.getLog( JRIClient.class.getName() );

    /**
     * @throws IOException if the JRI library could not e loaded.
     */
    public JRIClient() throws IOException {

        if ( connection == null ) {
            log.info( "Loading JRI library, looking in " + System.getProperty( "java.library.path" ) );
            try {
                System.loadLibrary( "jri" );
            } catch ( UnsatisfiedLinkError e ) {
                log.error( e, e );
                throw new IOException( "No jri library, looked in: " + System.getProperty( "java.library.path" ) );
            }
            connection = new Rengine( new String[] { "--no-save" }, false, null );
            if ( !connection.waitForR() ) {
                throw new IOException( "Cannot load R" );
            }
            log.info( "JRI looks good!" );
        }
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
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String[])
     */
    public void assign( String argName, String[] array ) {
        connection.assign( argName, array );
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

    public void disconnect() {
        connection.end();
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
        if ( !isConnected() ) {
            return null;
        }
        REXP r = this.eval( command );
        if ( r == null ) {
            log.warn( "No result for " + command );
            return null;
        }
        return r.asDoubleArray();
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
     * @see ubic.basecode.util.RClient#getLastError()
     */
    public String getLastError() {
        return "Sorry, no information";
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#intArrayEval(java.lang.String)
     */
    public int[] intArrayEval( String command ) {
        return this.eval( command ).asIntArray();
    }

    public boolean isConnected() {
        return connection.isAlive();
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#retrieveMatrix(java.lang.String)
     */
    public DoubleMatrixNamed<String, String> retrieveMatrix( String variableName ) {
        log.debug( "Retrieving " + variableName );
        REXP r = this.eval( variableName );
        if ( r == null ) throw new IllegalArgumentException( variableName + " not found in R context" );

        double[][] results = r.asDoubleMatrix();

        if ( results == null ) throw new RuntimeException( "Failed to get back matrix for variable " + variableName );

        DoubleMatrixNamed<String, String> resultObject = DoubleMatrix2DNamedFactory.dense( results );

        retrieveRowAndColumnNames( variableName, resultObject );
        return resultObject;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#stringEval(java.lang.String)
     */
    public String stringEval( String command ) {
        return this.eval( command ).asString();
    }

    /**
     * @param string
     * @return
     */
    public List<String> stringListEval( String command ) {
        String[] ar = this.eval( command ).asStringArray();
        if ( ar == null ) {
            log.warn( "No string list in R: " + command );
            return null;
        }
        List<String> results = new ArrayList<String>();
        for ( String s : ar ) {
            results.add( s );
        }
        return results;

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#voidEval(java.lang.String)
     */
    public void voidEval( String command ) {
        connection.eval( command );
    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#eval(java.lang.String)
     */
    private REXP eval( String command ) {
        return connection.eval( command );
    }

    /**
     * Get the dimnames associated with the matrix variable row and column names, if any, and assign them to the
     * resultObject NamedMatrix
     * 
     * @param variableName a matrix in R
     * @param resultObject corresponding NamedMatrix we are filling in.
     */
    private void retrieveRowAndColumnNames( String variableName, DoubleMatrixNamed<String, String> resultObject ) {
        log.debug( "Getting row & column names names" );

        List rowNamesREXP = this.eval( "dimnames(" + variableName + ")[1][[1]]" ).asVector();
        if ( rowNamesREXP != null ) {
            log.debug( "Got row names" );
            List<String> rowNames = new ArrayList<String>();
            for ( Iterator iter = rowNamesREXP.iterator(); iter.hasNext(); ) {
                REXP element = ( REXP ) iter.next();
                String rowName = element.asString();
                rowNames.add( rowName );
            }
            resultObject.setRowNames( rowNames );
        } else {
            log.debug( "No row names" );
        }

        // Getting the column names.
        List colNamesREXP = this.eval( "dimnames(" + variableName + ")[2][[1]]" ).asVector();
        if ( colNamesREXP != null ) {
            log.debug( "Got column names" );
            List<String> colNames = new ArrayList<String>();
            for ( Iterator iter = colNamesREXP.iterator(); iter.hasNext(); ) {
                REXP element = ( REXP ) iter.next();
                String rowName = element.asString();
                colNames.add( rowName );
            }
            resultObject.setColumnNames( colNames );
        } else {
            log.debug( "No column names" );
        }
        log.info( resultObject );
    }

}
