package baseCode.io.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Reads a tab-delimited file with lines of the format Key Value. If there are multiple values, then a Set is created
 * for each key containing its values.
 * </p>
 * <p>
 * Copyright (c) 2004
 * </p>
 * <p>
 * Institution: Columbia University
 * </p>
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class MapReader {

   /**
    * @param filename String
    * @throws IOException
    * @return Map
    */
   public Map read( String filename ) throws IOException {
      return this.read( filename, false );
   }

   /**
    * @param stream InputStream
    * @return @throws IOException
    * @throws IOException
    */
   public Map read( InputStream stream ) throws IOException {
      return this.read( stream, false );
   }

   /**
    * @param filename name of the tab-delimited file
    * @param hasHeader boolean
    * @return Map from the file.
    * @throws IOException
    */
   public Map read( String filename, boolean hasHeader ) throws IOException {
      File infile = new File( filename );
      if ( !infile.exists() || !infile.canRead() ) {
         throw new IllegalArgumentException( "Could not read from " + filename );
      }
      FileInputStream stream = new FileInputStream( infile );
      return read( stream, hasHeader );

   }

   /**
    * @param stream InputStream
    * @param hasHeader boolean
    * @return @throws IOException
    * @throws IOException
    */
   public Map read( InputStream stream, boolean hasHeader ) throws IOException {
      Map result = new HashMap();

      BufferedReader dis = new BufferedReader( new InputStreamReader( stream ) );
      if ( hasHeader ) {
         dis.readLine();
      }

      String row;
      while ( ( row = dis.readLine() ) != null ) {
         StringTokenizer st = new StringTokenizer( row, "\t" );
         String key = st.nextToken();

         String value = st.nextToken();

         if ( st.hasMoreTokens() ) {
            Set innerList = new HashSet();
            innerList.add( value );
            while ( st.hasMoreTokens() ) {
               value = st.nextToken();
            }
            innerList.add( value );
            result.put( key, innerList );
         } else {
            result.put( key, value );
         }
      }
      dis.close();

      return result;
   }
} // end of class
