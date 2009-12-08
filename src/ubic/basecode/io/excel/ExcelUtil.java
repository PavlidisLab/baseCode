/*
 * The Gemma project
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
package ubic.basecode.io.excel;

import java.io.FileInputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRichTextString;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;


public class ExcelUtil {

    public static HSSFSheet getSheetFromFile( String filename, String sheetName ) throws Exception {
        POIFSFileSystem fs = new POIFSFileSystem( new FileInputStream( filename ) );
        HSSFWorkbook wb = new HSSFWorkbook( fs );
        return wb.getSheet( sheetName );
    }

    public static String getValue( HSSFSheet sheet, int row, int col ) {
        if ( col > 255 ) {
            throw new RuntimeException( "Column position is over 255" );
        }
        return getValue( sheet, row, ( short ) col );
    }

    public static String getValue( HSSFSheet sheet, int row, short col ) {
        if ( sheet.getRow( row ) == null ) return null;
        HSSFCell cell = sheet.getRow( row ).getCell( col );
        if ( cell == null ) {
            return null;
        }
        try {
            if ( cell.getCellType() == HSSFCell.CELL_TYPE_STRING ) return cell.getRichStringCellValue().getString();
            if ( cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC ) {
                // WARNING bad for doubles
                return "" + cell.getNumericCellValue();
            }
            if ( cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA ) return cell.getCellFormula();

        } catch ( Exception e ) {
            System.err.println( "row:" + row + " col:" + col );
            e.printStackTrace();
            System.exit( 0 );
        }
        return "";
    }

    public static void main( String args[] ) throws Exception {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet spreadsheet = workbook.createSheet();
        ExcelUtil.setFormula( spreadsheet, 1, 1, "HYPERLINK(\"x\",\"x\")" );

    }

    public static void setFormula( HSSFSheet sheet, int row, int col, String value ) {
        HSSFRow r = sheet.getRow( row );
        if ( r == null ) {
            r = sheet.createRow( row );
        }
        HSSFCell c = r.createCell( ( short ) col );
        c.setCellType( HSSFCell.CELL_TYPE_FORMULA );
        c.setCellFormula( value );
    }

    public static void setValue( HSSFSheet sheet, int row, int col, int value ) {
        setValue( sheet, row, col, ( double ) value );
    }

    public static void setValue( HSSFSheet sheet, int row, int col, double value ) {
        HSSFRow r = sheet.getRow( row );
        if ( r == null ) {
            r = sheet.createRow( row );
        }
        HSSFCell c = r.createCell( ( short ) col );
        c.setCellType( HSSFCell.CELL_TYPE_NUMERIC );
        c.setCellValue( value );

    }

    public static void setValue( HSSFSheet sheet, int row, int col, String value ) {
        HSSFRow r = sheet.getRow( row );
        if ( r == null ) {
            r = sheet.createRow( row );
        }
        HSSFCell c = r.createCell( ( short ) col );
        c.setCellType( HSSFCell.CELL_TYPE_STRING );
        c.setCellValue( new HSSFRichTextString( value ) );
    }

    public static Set<String> grabColumnValues( HSSFSheet sheet, int column, boolean header, boolean clean ) {
        return new HashSet<String>( grabColumnValuesList( sheet, column, header, clean ) );
    }

    public static List<String> grabColumnValuesList( HSSFSheet sheet, int column, boolean header, boolean clean ) {
        return grabColumnValuesList( sheet, column, header, clean, new SpreadSheetFilter() {
            public boolean accept( HSSFSheet sheet, int row ) {
                return true;
            }
        } );
    }

    /**
     * Gets all the strings from a column, possibly exlcuding header and possibly triming and lowercasing
     * 
     * @param sheet
     * @param column
     * @param header true if it has a header
     * @param clean if true it will trim and lowercase the strings
     * @return
     */
    public static Set<String> grabColumnValues( HSSFSheet sheet, int column, boolean header, boolean clean,
            SpreadSheetFilter f ) {
        return new HashSet<String>( grabColumnValuesList( sheet, column, header, clean, f ) );
    }

    public static List<String> grabColumnValuesList( HSSFSheet sheet, int column, boolean header, boolean clean,
            SpreadSheetFilter f ) {
        List<String> result = new LinkedList<String>();
        int row;
        if ( header )
            row = 0; // header is row = 0
        else
            row = -1;

        while ( true ) {
            row++;
            String term = ExcelUtil.getValue( sheet, row, column );
            if ( term == null ) break;
            // System.out.println( term );
            if ( f.accept( sheet, row ) ) {
                term = term.trim();
                if ( clean ) term = term.toLowerCase();
                result.add( term );
            }
        }
        return result;
    }
}
