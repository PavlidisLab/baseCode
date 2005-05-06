package baseCode.util;

import com.Ostermiller.util.CSVParser;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class StringUtil {

    /**
     * @param numFields
     * @param line
     * @return
     */
    public static String[] csvSplit( int numFields, String line ) {
        String[][] parsedFields = CSVParser.parse( line );
        return parsedFields[0];
    }
}
