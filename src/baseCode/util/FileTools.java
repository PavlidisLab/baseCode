package baseCode.util;

import java.io.File;
import java.io.IOException;

/**
 * Copyright (c) 2004 Columbia University
 * 
 * @author Pavlidis
 * @author Will Braynen
 * @version $Id$
 */
public class FileTools {

   protected final static String PNG_EXTENSION = "png";
   protected final static String GIF_EXTENSION = "gif";
   protected final static String TXT_EXTENSION = "txt";
   protected final static String[] XML_EXTENSIONS = {
         "XML", "xml"
   };

   protected final static String[] IMAGE_EXTENSIONS = {
         PNG_EXTENSION, GIF_EXTENSION, "PNG", "GIF"
   };
   protected final static String[] DATA_EXTENSIONS = {
         TXT_EXTENSION, "TXT"
   };
   // default values
   public final static String DEFAULT_DATA_EXTENSION = TXT_EXTENSION;
   public final static String DEFAULT_IMAGE_EXTENSION = PNG_EXTENSION;
   public final static String DEFAULT_XML_EXTENSION = "xml";

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

   /**
    * Returns the extension of a file.
    * 
    * @param filename
    * @return @return
    */
   public static String getExtension( String filename ) {

      String extension = null;
      int i = filename.lastIndexOf( '.' );

      if ( i > 0 && i < filename.length() - 1 ) {
         extension = filename.substring( i + 1 ).toLowerCase();
      }
      return extension;
   } // end getExtension

   /**
    * @param filename
    * @return
    */
   public static String getWithoutExtension( String filename ) {

      String[] s = filename.split( "." );
      String extension = s[s.length - 1];
      String filenameWithoutExtension = filename.substring( filename.length()
            - extension.length() - 1, filename.length() - 1 );

      return filenameWithoutExtension;
   } // end getFilenameWithoutExtension

   /**
    * @param filename
    * @param newExtension
    * @return the new filename with the changed extension, but does not modify the <code>filename</code> parameter.
    */
   public static String changeExtension( String filename, String newExtension ) {

      String filenameWithoutExtension = getWithoutExtension( filename );
      return ( filenameWithoutExtension + newExtension );
   } // end getWithChangedExtension

   /**
    * @param filename
    * @return
    */
   public static boolean hasImageExtension( String filename ) {

      String extension = getExtension( filename );
      if ( extension != null ) {
         for ( int i = 0; i < FileTools.IMAGE_EXTENSIONS.length; i++ ) {
            if ( FileTools.IMAGE_EXTENSIONS[i].equals( extension ) ) {
               return true;
            }
         }
      }
      return false;
   } // end hasImageExtension

   /**
    * @param filename
    * @return
    */
   public static boolean hasXMLExtension( String filename ) {
      String extension = getExtension( filename );
      if ( extension != null ) {
         for ( int i = 0; i < FileTools.XML_EXTENSIONS.length; i++ ) {
            if ( FileTools.XML_EXTENSIONS[i].equals( extension ) ) {
               return true;
            }
         }
      }
      return false;
   }

   /**
    * @param filename
    * @return
    */
   public static boolean hasDataExtension( String filename ) {

      String extension = getExtension( filename );
      if ( extension != null ) {
         for ( int i = 0; i < FileTools.DATA_EXTENSIONS.length; i++ ) {
            if ( FileTools.DATA_EXTENSIONS[i].equals( extension ) ) {
               return true;
            }
         }
      }
      return false;
   } // end hasImageExtension

   /**
    * @param filename
    * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
    */
   public static String addImageExtension( String filename ) {
      return ( filename + "." + FileTools.DEFAULT_IMAGE_EXTENSION );
   }

   /**
    * @param filename
    * @return the new filename with the added extension, but does not modify the <code>filename</code> parameter.
    */
   public static String addDataExtension( String filename ) {
      return ( filename + "." + FileTools.DEFAULT_DATA_EXTENSION );
   }

   /**
    * @param dirname directory name
    * @return
    */
   public static boolean testDir( String dirname ) {
      if ( dirname != null && dirname.length() > 0 ) {
         File f = new File( dirname );
         if ( f.isDirectory() && f.canRead() ) {
            return true;
         }
      }
      return false;
   }

   /**
    * @param filename
    * @return
    */
   public static boolean testFile( String filename ) {
      if ( filename != null && filename.length() > 0 ) {
         File f = new File( filename );
         if ( f.isFile() && f.canRead() ) {
            return true;
         }
      }
      return false;
   }

}