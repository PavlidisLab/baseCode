package ubic.basecode.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class ScrollingTextAreaDialog extends JDialog {
    private JTextArea ta;

    public ScrollingTextAreaDialog( Frame owner, String title, boolean modal ) {
        super( owner, title, modal );
        jbInit();
        pack();
    }

    /**
     * @param text
     */
    public void setText( String text ) {
        ta.setText( text );
        validate();
    }

    private void jbInit() {
        JPanel pan = new JPanel( new BorderLayout() );
        pan.setPreferredSize( new Dimension( 650, 500 ) );
        this.getContentPane().add( pan );
        ta = new JTextArea( 60, 80 );
        ta.setBackground( Color.WHITE );
        JScrollPane sp = new JScrollPane( ta );
        pan.add( sp, BorderLayout.CENTER );
        pan.setBorder( BorderFactory.createEmptyBorder( 10, 10, 10, 10 ) );
        pack();
    }
}
