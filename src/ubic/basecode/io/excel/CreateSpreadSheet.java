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
package ubic.basecode.io.excel;

import java.io.File;
import java.io.FileOutputStream;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

/**
 * TODO Document Me
 * 
 * @author lfrench
 * @version $Id$
 */
public class CreateSpreadSheet {

    protected String filename;
    protected SpreadSheetSchema schema;
    /**
     * @param args
     */
    protected HSSFSheet spreadsheet;

    protected HSSFWorkbook workbook;

    public CreateSpreadSheet( String filename, SpreadSheetSchema schema ) {
        if ( new File( filename ).exists() ) {
            // throw new Exception( "please delete previous file to prevent overwrite" );
        }
        this.filename = filename;
        this.schema = schema;

        init();
    }

    public void init() {
        try {
            workbook = new HSSFWorkbook();
            spreadsheet = workbook.createSheet();
        } catch ( Exception e ) {
            e.printStackTrace();
            System.exit( 1 );
        }
        // make the header
        createHeader();
    }

    public void save() throws Exception {
        FileOutputStream fileOut = new FileOutputStream( filename );
        workbook.write( fileOut );
        fileOut.close();
    }

    private void createHeader() {
        String[] header = schema.getHeaderRow();
        for ( int i = 0; i < header.length; i++ ) {
            // System.out.println( header[i] );
            ExcelUtil.setValue( spreadsheet, 0, i, header[i] );

        }
    }

}
