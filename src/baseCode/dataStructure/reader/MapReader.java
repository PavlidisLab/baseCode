package baseCode.dataStructure.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * <p>Title: MapReader</p>
 * <p>Description: Reads a tab-delimited file with lines of the format Key Value.
 * If there are multiple values, then a Set is created for each key containing its values.</p>
 * <p>Copyright: Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public class MapReader {

    /**
     * @param filename name of the tab-delimited file
     * @return Map from the file.
     */
    public Map read(String filename) throws IOException {
        Map result = new HashMap();

        File infile = new File(filename);
        if (!infile.exists() || !infile.canRead()) {
            throw new IllegalArgumentException("Could not read from " +
                                               filename);
        }

        BufferedReader dis = new BufferedReader(new FileReader(filename));
        String row;

        while ((row = dis.readLine()) != null) {
            StringTokenizer st = new StringTokenizer(row, "\t");
            String key = st.nextToken();

            String value = st.nextToken();

            if (st.hasMoreTokens()) {
                Set innerList = new HashSet();
                innerList.add(value);
                while (st.hasMoreTokens()) {
                    value = st.nextToken();
                }
                innerList.add(value);
                result.put(key, innerList);
            } else {
                result.put(key, value);
            }
        }
        dis.close();

        return result;
    }
} // end of class
