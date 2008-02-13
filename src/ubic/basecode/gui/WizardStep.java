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

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Homin Lee
 * @author pavlidis
 * @version $Id$
 */
public abstract class WizardStep extends JPanel {
    protected static Log log = LogFactory.getLog( WizardStep.class.getName() );
    Wizard owner;

    /**
     * @param wiz
     */
    public WizardStep( Wizard wiz ) {
        super();
        owner = wiz;
        try {
            this.setLayout( new BorderLayout() );
            // jbInit();
        } catch ( Exception e ) {
            e.printStackTrace();
        }
    }

    abstract public boolean isReady();

    /**
     * Print an error message to the status bar.
     * 
     * @param a error message to show.
     */
    public void showError( String a ) {
        owner.showError( a );
    }

    /**
     * Print a message to the status bar.
     * 
     * @param a message to show.
     */
    public void showStatus( String a ) {
        owner.showStatus( a );
    }

    /**
     * @param text
     */
    protected void addHelp( String text ) {
        JLabel label = new JLabel( text );
        JLabel jLabel1 = new JLabel( "      " );
        JLabel jLabel2 = new JLabel( " " );
        JLabel jLabel3 = new JLabel( " " );
        JLabel jLabel4 = new JLabel( "      " );
        JPanel labelPanel = new JPanel();
        labelPanel.setBackground( Color.WHITE );
        labelPanel.setLayout( new BorderLayout() );
        labelPanel.add( label, BorderLayout.CENTER );
        labelPanel.add( jLabel1, BorderLayout.WEST );
        labelPanel.add( jLabel2, BorderLayout.NORTH );
        labelPanel.add( jLabel3, BorderLayout.SOUTH );
        labelPanel.add( jLabel4, BorderLayout.EAST );
        this.add( labelPanel, BorderLayout.NORTH );
    }

    protected void addMain( JPanel panel ) {
        this.add( panel, BorderLayout.CENTER );
    }

    // Component initialization
    protected abstract void jbInit() throws Exception;

}