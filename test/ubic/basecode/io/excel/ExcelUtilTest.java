/*
 * The baseCode project
 * 
 * Copyright (c) 2012 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.io.excel;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.Test;

/**
 * @author paul
 * @version $Id$
 */
public class ExcelUtilTest {

    @Test
    public void testGetSheetFromFile() throws Exception {
        String f = new File( this.getClass().getResource( "/data/matrix-testing.xls" ).toURI() ).getAbsolutePath();
        String s = "range,variance, normalized";
        HSSFSheet sheetFromFile = ExcelUtil.getSheetFromFile( f, s );
        String value = ExcelUtil.getValue( sheetFromFile, 10, 10 );
        assertEquals( "0.158", value );
        value = ExcelUtil.getValue( sheetFromFile, 0, 0 );
        assertEquals( "gene", value );

        List<String> grabColumnValuesList = ExcelUtil.grabColumnValuesList( sheetFromFile, 2, true, false );
        assertEquals( "-0.157", grabColumnValuesList.get( 3 ) );

        ExcelUtil.setValue( sheetFromFile, 10, 10, 0.44 );
        assertEquals( "0.44", ExcelUtil.getValue( sheetFromFile, 10, 10 ) );
    }

    @Test
    public void testSetFormula() {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet spreadsheet = workbook.createSheet();
        ExcelUtil.setFormula( spreadsheet, 1, 1, "HYPERLINK(\"x\",\"x\")" );
    }

}
