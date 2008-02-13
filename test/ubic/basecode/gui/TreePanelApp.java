/*
 * Created on Jun 21, 2004
 *
 */
package ubic.basecode.gui;

import java.io.IOException;
import java.io.InputStream;

import javax.swing.JFrame;
import javax.swing.JTree;
import javax.swing.UIManager;

import org.xml.sax.SAXException;

import ubic.basecode.xml.GOParser;

/**
 * Not a 'real' test.
 * <p>
 * Copyright (c) University of British Columbia
 * 
 * @author Paul Pavlidis
 * @version $Id$
 */
public class TreePanelApp {
    public static void main( String[] args ) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName() );
            new TreePanelApp();
        } catch ( Exception e ) {
            e.printStackTrace();
        }

    }

    private GOParser gOParser = null;

    /**
     * Constructor for TestTreePanel.
     * 
     * @throws IOException
     * @throws SAXException
     */
    public TreePanelApp() throws SAXException, IOException {

        InputStream i =
        // GOParser.class.getResourceAsStream("/data/go-termdb-sample.xml");
        GOParser.class.getResourceAsStream( "/data/go_200406-termdb.xml" );
        gOParser = new GOParser( i );
        final JTree t = gOParser.getGraph().treeView();

        // Create and set up the window.
        JFrame frame = new JFrame( "GOTreeDemo" );
        frame.setSize( 200, 200 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        // Create and set up the content pane.
        TreePanel newContentPane = new TreePanel( t );
        newContentPane.setOpaque( true ); // content panes must be opaque
        frame.setContentPane( newContentPane );
        // Display the window.
        frame.pack();
        frame.setVisible( true );

    }

}