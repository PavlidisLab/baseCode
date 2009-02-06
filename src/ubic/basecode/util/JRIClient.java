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
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.RList;
import org.rosuda.JRI.Rengine;

import ubic.basecode.TwoWayAnovaResult;
import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;

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
                throw new IOException( "Couldn't load jri library, looked in: "
                        + System.getProperty( "java.library.path" ) );
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
    public DoubleMatrix<String, String> retrieveMatrix( String variableName ) {
        log.debug( "Retrieving " + variableName );
        REXP r = this.eval( variableName );
        if ( r == null ) throw new IllegalArgumentException( variableName + " not found in R context" );

        double[][] results = r.asDoubleMatrix();

        if ( results == null ) throw new RuntimeException( "Failed to get back matrix for variable " + variableName );

        DoubleMatrix<String, String> resultObject = DoubleMatrixFactory.dense( results );

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

    /**
     * @param command
     * @return
     */
    public TwoWayAnovaResult twoWayAnovaEval( String command ) {

        REXP regularExp = this.eval( command );

        RList mainList = regularExp.asList();
        if ( mainList == null ) {
            log.warn( "No string list in R: " + command );
            return null;
        }

        // FIXME The values are in the correct order, but the keys in the mainList
        // are not for some reason so I'm not using them. In the debugger, the key is the composite sequence,
        // but this isn't the same composite sequence I see directly in R. The order
        // of the p values and statistics are correct, however.
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
                    double[] pValsFromR = ( double[] ) r2.getContent();
                    double[] pValsToUse = new double[pValsFromR.length - 1];
                    for ( int j = 0; j < pValsToUse.length; j++ ) {
                        pValsToUse[j] = pValsFromR[j];
                    }

                    pvalues.put( Integer.toString( i ), pValsToUse );
                } else if ( StringUtils.equals( "F value", key ) ) {
                    REXP r2 = l1.at( key );
                    double[] statisticsFromR = ( double[] ) r2.getContent();
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

    }

    /*
     * (non-Javadoc)
     * 
     * @see ubic.basecode.util.RClient#voidEval(java.lang.String)
     */
    public void voidEval( String command ) {
        REXP result = connection.eval( command );
        if ( result == null ) {
            throw new RuntimeException( "Unknown error" );
        }
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
    private void retrieveRowAndColumnNames( String variableName, DoubleMatrix<String, String> resultObject ) {
        log.debug( "Getting row & column names names" );

        REXP r1 = this.eval( "dimnames(" + variableName + ")" );
        RList asList = r1.asList();

        if ( asList == null ) {
            return;
        }

        REXP rowNamesREXP = asList.at( 0 );
        REXP colNamesREXP = asList.at( 1 );

        if ( rowNamesREXP != null ) {
            log.debug( "Got row names" );
            String[] rowNamesAr = rowNamesREXP.asStringArray();
            List<String> rowNames = new ArrayList<String>();
            for ( String rowName : rowNamesAr ) {
                rowNames.add( rowName );
            }
            resultObject.setRowNames( rowNames );
        } else {
            log.debug( "No row names" );
        }

        // Getting the column names.
        if ( colNamesREXP != null ) {
            String[] colNamesAr = colNamesREXP.asStringArray();
            log.debug( "Got column names" );
            List<String> colNames = new ArrayList<String>();
            for ( String colName : colNamesAr ) {
                colNames.add( colName );
            }
            resultObject.setColumnNames( colNames );
        } else {
            log.debug( "No column names" );
        }
        if ( log.isDebugEnabled() ) log.debug( resultObject );
    }

}
