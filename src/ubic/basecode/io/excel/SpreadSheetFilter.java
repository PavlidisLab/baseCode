package ubic.basecode.io.excel;

import org.apache.poi.hssf.usermodel.HSSFSheet;

public interface SpreadSheetFilter {
    boolean accept( HSSFSheet sheet, int row );
}
