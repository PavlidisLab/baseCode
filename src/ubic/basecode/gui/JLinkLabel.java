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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.IOException;

import javax.swing.JLabel;

import ubic.basecode.util.BrowserLauncher;

/**
 * A clickable link label that contains a URL. When a mouse pointer is placed over it, it turns into a hand.
 * 
 * @author Will Braynen
 * @version $Id$
 */
public class JLinkLabel extends JLabel implements MouseListener {

    /**
     * 
     */
    private static final long serialVersionUID = -7916376574174272599L;

    protected String m_url = null;

    protected String m_text = "";

    /** Creates a new instance of JLinkLabel */
    public JLinkLabel() {
        super();
    }

    public JLinkLabel( String text ) {
        this();
        setText( text );
    }

    public JLinkLabel( String text, String url ) {
        this();
        setText( text, url );
    }

    /**
     * @return
     */
    public String getURL() {
        return m_url;
    }

    @SuppressWarnings("unused")
    public void mouseClicked( MouseEvent e ) {
        if ( m_url != null ) {
            try {
                BrowserLauncher.openURL( m_url );
            } catch ( IOException ex ) {
                GuiUtil.error( "Could not open a web browser window." );
            }
        }
    }

    @SuppressWarnings("unused")
    public void mouseEntered( MouseEvent e ) {
    }

    @SuppressWarnings("unused")
    public void mouseExited( MouseEvent e ) {
    }

    @SuppressWarnings("unused")
    public void mousePressed( MouseEvent e ) {
    }

    @SuppressWarnings("unused")
    public void mouseReleased( MouseEvent e ) {
    }

    @Override
    public void setText( String text ) {
        if ( m_url != null ) {
            setText( text, m_url );
        } else {
            setText( text, text );
        }
    }

    public void setText( String text, String url ) {
        m_text = text;
        m_url = url;
        super.setText( "<html><a href=\"" + url + "\">" + text + "</a></html>" );
    }

    /**
     * @param url
     */
    public void setURL( String url ) {
        setText( m_text, url );
    }

    @Override
    public String toString() {
        return "<html><a href=\"" + m_url + "\">" + m_text + "</a></html>";
    }

}