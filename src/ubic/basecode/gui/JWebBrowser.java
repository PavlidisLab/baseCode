/*
 * The baseCode project
 * 
 * Copyright (c) 2006 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package ubic.basecode.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

/**
 * @author Will Braynen;
 */
public class JWebBrowser extends JFrame implements HyperlinkListener, ActionListener {

    private JTextField m_urlField;
    private JEditorPane m_htmlPane;
    private String m_initialURL;

    public static void main( String[] args ) {
        if ( args.length == 0 )
            new JWebBrowser( "http://microarray.genomecenter.columbia.edu/ermineJ" );
        else
            new JWebBrowser( args[0] );
    }

    public JWebBrowser( String initialURL ) {
        super( "Web Browser" );
        m_initialURL = initialURL;

        JPanel topPanel = new JPanel();
        topPanel.setBackground( Color.lightGray );
        JLabel urlLabel = new JLabel( "URL:" );
        m_urlField = new JTextField( 30 );
        m_urlField.setText( initialURL );
        m_urlField.addActionListener( this );
        topPanel.add( urlLabel );
        topPanel.add( m_urlField );
        getContentPane().add( topPanel, BorderLayout.NORTH );

        try {
            m_htmlPane = new JEditorPane( initialURL );
            m_htmlPane.setEditable( false );
            m_htmlPane.addHyperlinkListener( this );
            JScrollPane scrollPane = new JScrollPane( m_htmlPane );
            getContentPane().add( scrollPane, BorderLayout.CENTER );
        } catch ( IOException e ) {
            GuiUtil.error( "Can't build HTML pane for " + initialURL, e );
        }

        Dimension screenSize = getToolkit().getScreenSize();
        int width = screenSize.width * 8 / 10;
        int height = screenSize.height * 8 / 10;
        setBounds( width / 8, height / 8, width, height );
        setVisible( true );
    }

    public void actionPerformed( ActionEvent event ) {
        String url;
        if ( event.getSource() == m_urlField )
            url = m_urlField.getText();
        else
            // Clicked "home" button instead of entering URL (if we had a home button)
            url = m_initialURL;
        try {
            m_htmlPane.setPage( new URL( url ) );
            m_urlField.setText( url );
        } catch ( IOException e ) {
            GuiUtil.error( "Can't follow link to " + url, e );
        }
    }

    public void hyperlinkUpdate( HyperlinkEvent event ) {
        if ( event.getEventType() == HyperlinkEvent.EventType.ACTIVATED ) {
            try {
                m_htmlPane.setPage( event.getURL() );
                m_urlField.setText( event.getURL().toExternalForm() );
            } catch ( IOException e ) {
                GuiUtil.error( "Can't follow link to " + event.getURL().toExternalForm(), e );
            }
        }
    }
}
