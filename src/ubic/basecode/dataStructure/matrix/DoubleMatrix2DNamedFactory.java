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
package ubic.basecode.dataStructure.matrix;

/**
 * Use this factory to create matrices of type selected at runtime.
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class DoubleMatrix2DNamedFactory {

    public static SparseDoubleMatrix2DNamed sparse( double T[][] ) {
        return new SparseDoubleMatrix2DNamed( T );
    }

    public static SparseDoubleMatrix2DNamed sparse( int rows, int cols ) {
        return new SparseDoubleMatrix2DNamed( rows, cols );
    }

    public static DenseDoubleMatrix2DNamed dense( double T[][] ) {
        return new DenseDoubleMatrix2DNamed( T );
    }

    public static DenseDoubleMatrix2DNamed dense( int rows, int cols ) {
        return new DenseDoubleMatrix2DNamed( rows, cols );
    }

    public static FastRowAccessDoubleMatrix2DNamed fastrow( double T[][] ) {
        return new FastRowAccessDoubleMatrix2DNamed( T );
    }

    public static FastRowAccessDoubleMatrix2DNamed fastrow( int rows, int cols ) {
        return new FastRowAccessDoubleMatrix2DNamed( rows, cols );
    }

}