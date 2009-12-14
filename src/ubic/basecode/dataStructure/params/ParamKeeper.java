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

        s.save();
        // write file
    }

    public String toCSVString() {
        String result = "";
        for ( Map<String, String> params : paramLines ) {
            result += ParameterGrabber.paramsToLine( params ) + "\n";
        }
        return result;
    } 

}
