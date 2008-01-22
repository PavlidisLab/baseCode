/*
 * The baseCode project
 * 
 * Copyright (c) 2008 Columbia University
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

import java.util.List;

import ubic.basecode.dataStructure.matrix.DoubleMatrixNamed;

/**
 * Abstraction of a connection to R
 * 
 * @author Paul
 * @version $Id$
 */
public interface RClient {

    /**
     * @param argName
     * @param arg
     */
    public abstract void assign( String argName, double[] arg );

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, int[])
     */
    public abstract void assign( String arg0, int[] arg1 );

    public abstract void assign( String argName, String[] array );

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#assign(java.lang.String, java.lang.String)
     */
    public abstract void assign( String sym, String ct );

    /**
     * Assign a 2-d matrix.
     * 
     * @param matrix
     * @return the name of the variable by which the R matrix can be referred.
     */
    public abstract String assignMatrix( DoubleMatrixNamed matrix );

    /**
     * Define a variable corresponding to a character array in the R context, given a List of Strings.
     * 
     * @param strings
     * @return the name of the variable in the R context.
     */
    public abstract String assignStringList( List<String> strings );

    /**
     * Assign a 2-d matrix.
     * 
     * @param matrix
     * @return the name of the variable by which the R matrix can be referred.
     */
    public abstract String assignMatrix( double[][] matrix );

    /**
     * Run a command that takes a double array as an argument and returns a boolean.
     * 
     * @param command
     * @param argName
     * @param arg
     * @return
     */
    public abstract boolean booleanDoubleArrayEval( String command, String argName, double[] arg );

    /**
     * Run a command that has a single double array parameter, and returns a double array.
     * 
     * @param command
     * @param argName
     * @param arg
     * @return
     */
    public abstract double[] doubleArrayDoubleArrayEval( String command, String argName, double[] arg );

    /**
     * Run a command that returns a double array with no arguments.
     * 
     * @param command
     * @return
     */
    public abstract double[] doubleArrayEval( String command );

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
    public abstract double[] doubleArrayTwoDoubleArrayEval( String command, String argName, double[] arg,
            String argName2, double[] arg2 );

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
    public abstract double doubleTwoDoubleArrayEval( String command, String argName, double[] arg, String argName2,
            double[] arg2 );

    /**
     * Evaluate any command and return a string
     * 
     * @param command
     * @return string
     */
    public abstract String stringEval( String command );

    public int[] intArrayEval( String command );

    public boolean loadLibrary( String libraryName );

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#getLastError()
     */
    public abstract String getLastError();

    /**
     * Remove a variable from the R namespace
     * 
     * @param variableName
     */
    public abstract void remove( String variableName );

    /**
     * Get a matrix back out of the R context. Row and Column names are filled in for the resulting object, if they are
     * present.
     * 
     * @param variableName
     * @return
     */
    public abstract DoubleMatrixNamed<String, String> retrieveMatrix( String variableName );

    /*
     * (non-Javadoc)
     * 
     * @see org.rosuda.JRclient.Rconnection#voidEval(java.lang.String)
     */
    public abstract void voidEval( String command );

    public boolean isConnected();

    public void disconnect();

}