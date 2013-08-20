/*
 * The baseCode project
 * 
 * Copyright (c) 2010 University of British Columbia
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
package ubic.basecode.dataStructure.params;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ubic.basecode.io.excel.CreateSpreadSheet;
import ubic.basecode.io.excel.ExcelUtil;
import ubic.basecode.io.excel.SpreadSheetSchema;

/**
 * A class to convert parametergrabber information/instances into excel speadsheet lines
 * 
 * @author leon
 * @version $Id$
 */
public class ParamSpreadSheet extends CreateSpreadSheet {

    private static Logger log = LoggerFactory.getLogger( ParamSpreadSheet.class );

    public ParamSpreadSheet( String filename, SpreadSheetSchema schema ) {
        super( filename, schema );
    }

    public void populate( List<Map<String, String>> paramLines ) {
        int row = 0;
        for ( Map<String, String> paramLine : paramLines ) {
            row++;
            for ( String key : paramLine.keySet() ) {
                String value = paramLine.get( key );
                int pos = schema.getPosition( key );
                try {
                    double dValue = Double.parseDouble( value );
                    if ( Double.isNaN( dValue ) ) throw new NumberFormatException();
                    ExcelUtil.setValue( spreadsheet, row, pos, dValue );
                } catch ( NumberFormatException e ) {
                    try {
                        ExcelUtil.setFormula( spreadsheet, row, pos, value );
                        // having trouble finding the exception to catch
                    } catch ( Exception xe ) {
                        log.warn( "Setting as value, not formula: " + xe.getMessage() );
                        ExcelUtil.setValue( spreadsheet, row, pos, value );
                    }
                }
            }
        }
    }
}
