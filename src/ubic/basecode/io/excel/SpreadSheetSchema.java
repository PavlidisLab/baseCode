/*
 * The baseCode project
 * 
 * Copyright (c) 2007-2019 University of British Columbia
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

import java.util.HashMap;
import java.util.Map;

/**
 * This class is to determine what information is put in the spreadsheet and its column position
 * 
 * @author lfrench
 */
public class SpreadSheetSchema {
    protected Map<String, Integer> positions;

    // ?geoLabel ?description ?CUI ?SUI ?mapping ?phrase ?mentionLabel
    public SpreadSheetSchema() {
        positions = new HashMap<String, Integer>();
    }

    public SpreadSheetSchema( Map<String, Integer> positions ) {
        this.positions = positions;
    }

    public String[] getHeaderRow() {
        String[] result = new String[positions.size()];
        for ( String key : positions.keySet() ) {
            result[positions.get( key )] = key;
        }
        return result;
    }

    public Integer getPosition( String varName ) {
        return positions.get( varName );
    }

}
