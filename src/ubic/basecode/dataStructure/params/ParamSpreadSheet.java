package ubic.basecode.dataStructure.params;

import java.util.List;
import java.util.Map;

import ubic.basecode.io.excel.CreateSpreadSheet;
import ubic.basecode.io.excel.ExcelUtil;
import ubic.basecode.io.excel.SpreadSheetSchema;

/**
 * A class to convert parametergrabber information/instances into excel speadsheet lines  
 * @author leon
 *
 */
public class ParamSpreadSheet extends CreateSpreadSheet {

    public ParamSpreadSheet( String filename, SpreadSheetSchema schema ) throws Exception {
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
                    ExcelUtil.setValue( spreadsheet, row, pos, value );
                }
            }
        }
    }

    /**
     * @param args
     */
    public static void main( String[] args ) {
        // TODO Auto-generated method stub

    }

}
