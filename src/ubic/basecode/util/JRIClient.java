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
import java.util.List;

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
     * 
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
     * 
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
     * 
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
     * @see ubic.basecode.util.RClient#getLastError()
     */
    public String getLastError() {
        return "Sorry, no information";
    }

    public boolean isConnected() {
        return true;
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
     * 
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

            if ( result == null ) {
                throw new RuntimeException( "Error from R, could not sucessfully evaluate: " + command );
            }

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
            log.warn( "Matrix had no valid dimnames" );
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
                log.warn( "Invalid rownames" );
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
