/*
 * The baseCode project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.basecode.io.writer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.Format;
import java.util.Iterator;
import java.util.Map;
import ubic.basecode.dataStructure.matrix.Matrix2D;
import ubic.basecode.dataStructure.matrix.Matrix3D;
import ubic.basecode.dataStructure.matrix.MatrixUtil;

/**
 * Class for writing matrices to disk
 * 
 * @author Raymond Lim
 * @author paul
 * @version $Id$
 */
public class MatrixWriter<R, C> {

    public static final String DEFAULT_SEP = "\t";
    public static final String DEFAULT_TOP_LEFT = "ID";
    protected Writer out;
    protected Format formatter;
    protected String sep;

    protected String topLeft;

    protected Map<?, String> sliceNameMap;
    protected Map<C, String> colNameMap;
    protected Map<R, String> rowNameMap;

    public MatrixWriter( OutputStream out ) {
        this( out, null, DEFAULT_SEP );
    }

    public MatrixWriter( OutputStream out, Format formatter ) {
        this( out, formatter, DEFAULT_SEP );
    }

    public MatrixWriter( OutputStream out, Format formatter, String sep ) {
        this( new BufferedWriter( new OutputStreamWriter( out ) ), formatter, sep );
    }

    public MatrixWriter( String fileName, Format formatter ) throws IOException {
        this( fileName, formatter, DEFAULT_SEP );
    }

    public MatrixWriter( String fileName, Format formatter, String sep ) throws IOException {
        this( new BufferedWriter( new FileWriter( fileName ) ), formatter, sep );
    }

    public MatrixWriter( Writer out ) {
        this( out, null, DEFAULT_SEP );
    }

    public MatrixWriter( Writer out, Format formatter ) {
        this( out, formatter, DEFAULT_SEP );
    }

    public MatrixWriter( Writer out, Format formatter, String sep ) {
        this.out = out;
        this.formatter = formatter;
        this.sep = sep;
        this.topLeft = DEFAULT_TOP_LEFT;
    }

    public MatrixWriter( Writer out, String sep ) {
        this( out, null, sep );
    }

    public void setColNameMap( Map<C, String> colNameMap ) {
        this.colNameMap = colNameMap;
    }

    public void setRowNameMap( Map<R, String> rowNameMap ) {
        this.rowNameMap = rowNameMap;
    }

    public void setSep( String sep ) {
        this.sep = sep;
    }

    public void setSliceNameMap( Map<?, String> sliceNameMap ) {
        this.sliceNameMap = sliceNameMap;
    }

    public void setTopLeft( String topLeft ) {
        this.topLeft = topLeft;
    }

    /**
     * @param <R>
     * @param <C>
     * @param <V>
     * @param matrix
     * @param printNames
     * @throws IOException
     */
    public <V> void writeMatrix( Matrix2D<R, C, V> matrix, boolean printNames ) throws IOException {
        // write headers
        StringBuffer buf = new StringBuffer( topLeft );
        if ( printNames ) {
            for ( Iterator<C> it = matrix.getColNames().iterator(); it.hasNext(); ) {
                Object colName = it.next();
                buf.append( sep );
                buf.append( colName );
            }
            buf.append( "\n" );
            out.write( buf.toString() );
        }

        for ( Iterator<R> rowIt = matrix.getRowNames().iterator(); rowIt.hasNext(); ) {
            R rowName = rowIt.next();
            int rowIndex = matrix.getRowIndexByName( rowName );
            buf = new StringBuffer();
            if ( printNames ) buf.append( rowName + sep );
            for ( Iterator<C> colIt = matrix.getColNames().iterator(); colIt.hasNext(); ) {
                C colName = colIt.next();
                int colIndex = matrix.getColIndexByName( colName );
                Object val = MatrixUtil.getObject( matrix, rowIndex, colIndex );

                if ( val != null ) {
                    String s = val.toString();
                    if ( formatter != null ) s = formatter.format( val );
                    buf.append( s );
                } else {
                    buf.append( "" ); // just to make explicit ...
                }
                if ( colIt.hasNext() ) buf.append( sep );
            }
            buf.append( "\n" );
            out.write( buf.toString() );
        }
        out.flush();
        out.close();
    }

    /**
     * Writes a 3d matrix, collapsing the rows and columns
     * 
     * @param matrix
     * @param printNames
     * @throws IOException
     */
    public <S, V> void writeMatrix( Matrix3D<R, C, S, V> matrix, boolean printNames ) throws IOException {
        if ( printNames ) {
            StringBuffer buf = new StringBuffer( topLeft );
            for ( Iterator<S> it = matrix.getSliceNameIterator(); it.hasNext(); ) {
                Object sliceObj = it.next();
                String sliceName = sliceObj.toString();
                if ( sliceNameMap != null && sliceNameMap.get( sliceObj ) != null )
                    sliceName = sliceNameMap.get( sliceObj ).toString();
                buf.append( sep );
                buf.append( sliceName );
            }
            buf.append( "\n" );
            out.write( buf.toString() );
        }
        for ( int i = 0; i < matrix.rows(); i++ ) {
            for ( int j = 0; j < matrix.columns(); j++ ) {
                Object rowObj = matrix.getRowName( i );
                Object colObj = matrix.getColName( j );
                String rowName = rowObj.toString();
                String colName = colObj.toString();
                if ( rowNameMap != null && rowNameMap.get( rowObj ) != null )
                    rowName = rowNameMap.get( rowObj ).toString();
                if ( colNameMap != null && colNameMap.get( colObj ) != null )
                    colName = colNameMap.get( colObj ).toString();
                String name = rowName + ":" + colName;

                StringBuffer buf = new StringBuffer();
                if ( printNames ) buf.append( name + sep );
                for ( int k = 0; k < matrix.slices(); k++ ) {
                    Object val = matrix.getObject( k, i, j );
                    if ( val != null ) {
                        String s = val.toString();
                        if ( formatter != null ) s = formatter.format( val );
                        buf.append( s );
                    }
                    if ( k + 1 < matrix.slices() ) buf.append( sep );
                }
                buf.append( "\n" );
                out.write( buf.toString() );

            }
        }
        out.flush();
        out.close();
    }

}
