package baseCode.dataStructure.reader;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.Vector;

import baseCode.dataFilter.Filter;
import baseCode.dataStructure.NamedMatrix;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Abstract class representing an object that can read in a {@link NamedMatrix} from a file.
 * <p> Copyright (c) 2004</p>
 * <p>Institution:: Columbia University</p>
 * @author Paul Pavlidis
 * @version $Id$
 */
public abstract class AbstractNamedMatrixReader {

   public abstract NamedMatrix read( String filename ) throws IOException;

   protected static Log log = LogFactory.getLog(AbstractNamedMatrixReader.class);
   
   protected Vector readHeader( BufferedReader dis ) throws IOException {
      Vector headerVec = new Vector();
      String header = dis.readLine();
      StringTokenizer st = new StringTokenizer( header, "\t", true ); // return delims.
      
      String previousToken = "";
      int columnNumber = 0;
      while ( st.hasMoreTokens() ) {
         String s = st.nextToken();
         boolean missing = false;
         
         if ( s.compareTo( "\t" ) == 0 ) {
            /* two tabs in a row */
            if ( previousToken.compareTo( "\t" ) == 0 ) {
               missing = true;
            } else if ( !st.hasMoreTokens() ) { // at end of line.
               missing = true;
            } else {
               previousToken = s;
               continue;
            }
         } else if ( s.compareTo( " " ) == 0 ) {
            if ( previousToken.compareTo( "\t" ) == 0 ) {
               missing = true;
            }
         }

         if ( missing ) {
            throw new IOException( "Warning: Missing values not allowed in the header (column " +
                                   columnNumber + ")" );
         } else if ( columnNumber > 0 ) {
            headerVec.add( s );
         }
         // otherwise, just the corner string.
         columnNumber++;
         previousToken = s;
      }

      //return columnNumber - 1;
      if (headerVec.size() == 0) {
         log.warn("No headings found");
      }
      
      return headerVec;

   }

}
