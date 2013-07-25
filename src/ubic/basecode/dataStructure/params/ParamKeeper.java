/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
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
package ubic.basecode.dataStructure.params;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import ubic.basecode.io.excel.SpreadSheetSchema;

/**
 * Keeps track of results in the form of several key/value maps (each one can represent an experiment run), can output
 * to spreadsheet or csv file
 * 
 * @author leon
 */
public class ParamKeeper {
    List<Map<String, String>> paramLines;

    public ParamKeeper() {
        paramLines = new LinkedList<Map<String, String>>();
    }

    public void addParamInstance( Map<String, String> params ) {
        paramLines.add( params );
    }

    public String toCSVString() {
        String result = "";
        for ( Map<String, String> params : paramLines ) {
            result += ParameterGrabber.paramsToLine( params ) + "\n";
        }
        return result;
    }

    public void writeExcel( String filename ) throws Exception {
        // get all the keys
        Set<String> keySet = new HashSet<String>();
        for ( Map<String, String> params : paramLines ) {
            keySet.addAll( params.keySet() );
        }

        List<String> header = new LinkedList<String>( keySet );
        // sort?
        Map<String, Integer> positions = new HashMap<String, Integer>();
        for ( int i = 0; i < header.size(); i++ ) {
            positions.put( header.get( i ), i );
        }
        SpreadSheetSchema schema = new SpreadSheetSchema( positions );

        ParamSpreadSheet s = new ParamSpreadSheet( filename, schema );

        s.populate( paramLines );

        // write file
        s.save();
    }

}
