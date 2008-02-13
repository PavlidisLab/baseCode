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

package ubic.basecode.dataStructure.matrix;

/**
 * @author Paul
 * @version $Id$
 */
public class NamedMatrixUtil {

    public static <R, C, V> V getObject( NamedMatrix<R, C, V> matrix, int rowIndex, int colIndex ) {
        if ( matrix instanceof NamedObjectMatrix ) {
            return ( ( NamedObjectMatrix<R, C, V> ) matrix ).get( rowIndex, colIndex );
        } else if ( matrix instanceof NamedPrimitiveMatrix ) {
            return ( ( NamedPrimitiveMatrix<R, C, V> ) matrix ).getObject( rowIndex, colIndex );
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public static <R, C, V> V[] getRow( NamedMatrix<R, C, V> matrix, int rowIndex ) {
        if ( matrix instanceof NamedObjectMatrix ) {
            return ( ( NamedObjectMatrix<R, C, V> ) matrix ).getRow( rowIndex );
        } else if ( matrix instanceof NamedPrimitiveMatrix ) {
            return ( ( NamedPrimitiveMatrix<R, C, V> ) matrix ).getRowObj( rowIndex );
        } else {
            throw new UnsupportedOperationException();
        }
    }

}
