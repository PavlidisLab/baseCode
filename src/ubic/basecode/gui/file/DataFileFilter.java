package ubic.basecode.gui.file;

import java.io.File;

import javax.swing.filechooser.FileFilter;

/**
 * <hr>
 * <p>
 * Copyright (c) 2006 University of British Columbia
 * 
 * @author Will Braynen
 * @version $Id$
 */
public class DataFileFilter extends FileFilter {

    public boolean accept( File f ) {

        if ( f.isDirectory() ) {
            return true;
        }

        return true;

    } // end accept

    public String getDescription() {

        return "TXT data files";
    }
}