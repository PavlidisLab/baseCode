package baseCode.gui.file;

import java.io.File;

import javax.swing.filechooser.FileFilter;

import baseCode.util.FileTools;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004 Columbia University
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class XMLFileFilter extends FileFilter {

   public boolean accept( File f ) {

      if ( f.isDirectory() ) {
         return true;
      }

      return FileTools.hasXMLExtension( f.getName() );

   } // end accept

   public String getDescription() {

      return "XML data files";
   }
}