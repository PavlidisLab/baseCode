package baseCode.util;

import java.io.File;
import java.io.IOException;


/**
 * Copyright (c) 2004 Columbia University
 * @author Pavlidis
 * @version $Id$
 */
public class FileTools {

   /**
    * @param file
    * @throws IOException
    */
   public static void checkPathIsReadableFile( String file ) throws IOException {
      File infile = new File( file );
      if ( !infile.exists() || !infile.canRead() ) {
        throw new IOException( "Could not find file: " + file );
      }
   }

}
