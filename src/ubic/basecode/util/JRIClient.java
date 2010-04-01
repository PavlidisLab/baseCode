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
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.REngine;
import org.rosuda.REngine.REngineException;
import org.rosuda.REngine.RList;
import org.rosuda.REngine.JRI.JRIEngine;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.dataStructure.matrix.DoubleMatrixFactory;

/**
 * R connection implementation that uses the dynamic library interface JRI. For this to work the user must have
 * libjri.so or jri.dll and libr.so in their java.library.path.
 * 
 * @author paul
 * @version $Id$
 * @see RConnectionFactory
 */
public class JRIClient extends AbstractRClient {

    private static REngine connection = null;

    private static Log log = LogFactory.getLog( JRIClient.class.getName() );

    static {
        try {
            connection = new JRIEngine( new String[] { "--no-save", "--vanilla" }, null );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @throws IOException if the JRI library could not e loaded.
     */
    public JRIClient() throws IOException {

        try {
            // / couple of very quick tests to ensure everything is in order.
            connection.assign( "testtomakesurethedamnthingworks", "1" );
            this.eval( "cor(c(2,3),c(1,2))" );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#assign(java.lang.String, double[])
     */
    public void assign( String argName, double[] arg ) {
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
        try {
            connection.assign( arg0, arg1 );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#assign(java.lang.String, java.lang.String)
     */
    public void assign( String sym, String ct ) {
        try {
            connection.assign( sym, ct );
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        }
    }

    /*
     * (non-Javadoc)
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
     * @see ubic.basecode.util.RClient#booleanDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
    public boolean booleanDoubleArrayEval( String command, String argName, double[] arg ) {
        // this.assign( argName, arg );
        // REXP x = this.eval( command );
        // return x.asBool().isTRUE();
        return false;
    }

    public void disconnect() {
        connection.close();
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#doubleArrayDoubleArrayEval(java.lang.String, java.lang.String, double[])
     */
    public double[] doubleArrayDoubleArrayEval( String command, String argName, double[] arg ) {
        this.assign( argName, arg );
        org.rosuda.REngine.RList l;
        try {
            l = this.eval( command ).asList();

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
        REXP r = this.eval( command );
        if ( r == null ) {
            return null;
        }
        try {
            return r.asDoubles();
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
            throw new RuntimeException( "Problems executing R command " + command.toString(), e );
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
     * @see ubic.basecode.util.RClient#getLastError()
     */
    public String getLastError() {
        return "Sorry, no information";
    }

    // public boolean isConnected() {
    // return connection.isAlive();
    // }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#intArrayEval(java.lang.String)
     */
    public int[] intArrayEval( String command ) {
        try {
            return this.eval( command ).asIntegers();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    public boolean isConnected() {
        return true;
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#retrieveMatrix(java.lang.String)
     */
    public DoubleMatrix<String, String> retrieveMatrix( String variableName ) {
        log.debug( "Retrieving " + variableName );
        REXP r = this.eval( variableName );
        if ( r == null ) throw new IllegalArgumentException( variableName + " not found in R context" );

        double[][] results;
        try {
            results = r.asDoubleMatrix();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }

        if ( results == null ) throw new RuntimeException( "Failed to get back matrix for variable " + variableName );

        DoubleMatrix<String, String> resultObject = DoubleMatrixFactory.dense( results );

        retrieveRowAndColumnNames( variableName, resultObject );
        return resultObject;

    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#stringEval(java.lang.String)
     */
    public String stringEval( String command ) {
        REXP result = this.eval( command );
        try {
            return result.asString();
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * @param string
     * @return
     */
    public List<String> stringListEval( String command ) {
        String[] ar;
        try {
            ar = this.eval( command ).asStrings();
        } catch ( REXPMismatchException e ) {
            return null;
        }
        if ( ar == null ) {
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
     * @see ubic.basecode.util.RClient#twoWayAnovaEval(java.lang.String)
     */
    public TwoWayAnovaResult twoWayAnovaEval( String command ) {
        REXP rawResult = this.eval( command );

        if ( rawResult == null ) {
            return null;
        }

        org.rosuda.REngine.RList mainList;
        try {
            mainList = rawResult.asList();
        } catch ( REXPMismatchException e1 ) {
            throw new RuntimeException( e1 );
        }
        if ( mainList == null ) {
            return null;
        }

        /*
         * The values are in the correct order, but the keys in the mainList (from mainList.keys()) are not for some
         * reason so I'm not using them. In the debugger, the key is the composite sequence, but this isn't the same
         * composite sequence I see directly in R. The order of the p values and statistics are correct, however.
         */
        LinkedHashMap<String, double[]> pvalues = new LinkedHashMap<String, double[]>();
        LinkedHashMap<String, double[]> statistics = new LinkedHashMap<String, double[]>();
        try {
            for ( int i = 0; i < mainList.keys().length; i++ ) {
                REXP r1 = mainList.at( i );
                org.rosuda.REngine.RList l1 = r1.asList();
                if ( l1 == null ) {
                    return null;
                }

                String[] keys = l1.keys();
                for ( String key : keys ) {
                    if ( StringUtils.equals( "Pr(>F)", key ) ) {
                        REXP r2 = l1.at( key );
                        double[] pValsFromR;

                        pValsFromR = r2.asDoubles();

                        double[] pValsToUse = new double[pValsFromR.length - 1];
                        for ( int j = 0; j < pValsToUse.length; j++ ) {
                            pValsToUse[j] = pValsFromR[j];
                        }

                        pvalues.put( Integer.toString( i ), pValsToUse );
                    } else if ( StringUtils.equals( "F value", key ) ) {
                        REXP r2 = l1.at( key );
                        double[] statisticsFromR = r2.asDoubles();
                        double[] statisticsToUse = new double[statisticsFromR.length - 1];
                        for ( int j = 0; j < statisticsToUse.length; j++ ) {
                            statisticsToUse[j] = statisticsFromR[j];
                        }
                        statistics.put( Integer.toString( i ), statisticsToUse );
                    }

                }
            }
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        }
        TwoWayAnovaResult result = new TwoWayAnovaResult( pvalues, statistics );

        return result;

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
            log.error( "Problems executing R command " + command.toString(), e );
        } finally {
            if ( rLoggingThread != null ) {
                log.debug( "Shutting down logging thread." );
                rLoggingThread.done();
            }
        }
        return twoWayAnovaResult;
    }

    /*
     * (non-Javadoc)
     * @see ubic.basecode.util.RClient#voidEval(java.lang.String)
     */
    public void voidEval( String command ) {
        eval( command );
    }

    public REXP eval( String command ) {
        REXP result;
        int key = connection.lock();
        try {

            result = connection.parseAndEval( "try(" + command + ", silent=T)" );

            if ( !result.isString() ) {
                return result;
            }
            String a = result.asString();

            /*
             * There is no better way to do this, apparently.
             */
            if ( a != null && a.startsWith( "Error" ) ) {
                throw new RuntimeException( "Error from R when running " + command + ": " + a );
            }

            if ( result == null ) {
                throw new RuntimeException( "Error from R, could not sucessfully evaluate: " + command );
            }
            return result;
        } catch ( REngineException e ) {
            throw new RuntimeException( e );
        } catch ( REXPMismatchException e ) {
            throw new RuntimeException( e );
        } finally {
            connection.unlock( key );
        }
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
        RList asList;
        try {
            asList = r1.asList();
        } catch ( REXPMismatchException e1 ) {
            return;
        }

        if ( asList == null ) {
            return;
        }

        REXP rowNamesREXP = asList.at( 0 );
        REXP colNamesREXP = asList.at( 1 );

        if ( rowNamesREXP != null ) {
            log.debug( "Got row names" );
            String[] rowNamesAr;
            try {
                rowNamesAr = rowNamesREXP.asStrings();
            } catch ( REXPMismatchException e ) {
                return;
            }
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
            String[] colNamesAr;
            try {
                colNamesAr = colNamesREXP.asStrings();
            } catch ( REXPMismatchException e ) {
                return;
            }
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
