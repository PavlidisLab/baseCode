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
package ubic.basecode.graphics;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A GUI legend component that displays a color map as a color gradient from min to max, traversing all the colors in
 * the color map.
 * 
 * @author Will Braynen
 * @version $Id$
 */
public class JGradientBar extends JPanel {

    protected final static Color[] EMPTY = { Color.GRAY, Color.GRAY };
    /**
     * 
     */
    private static final long serialVersionUID = 6668982782351006998L;
    protected JNumberLabel m_min;
    protected JNumberLabel m_max;
    protected JGradientLabel m_gradient;

    /** Creates a new instance of JGradientBar */
    public JGradientBar() {

        // setOpaque( true );
        // setBackground( Color.lightGray );

        m_gradient = new JGradientLabel( EMPTY );
        m_min = new JNumberLabel();
        m_max = new JNumberLabel();
        m_min.setHorizontalAlignment( SwingConstants.RIGHT );
        m_max.setHorizontalAlignment( SwingConstants.LEFT );

        Font font = getFont().deriveFont( Font.BOLD, 13.0f );
        m_min.setFont( font );
        m_max.setFont( font );

        setLayout( new FlowLayout( FlowLayout.LEADING ) );
        add( m_min );
        add( m_gradient );
        add( m_max );
    } // end constructor

    public void setColorMap( Color[] colorMap ) {
        m_gradient.m_colorMap = colorMap;
        this.m_gradient.m_colorMap = colorMap;
    } // end setColorMap

    public void setLabels( double min, double max ) {
        m_min.setText( min );
        m_max.setText( max );
    } // end setLabels

} // end class JGradientBar

class JNumberLabel extends JLabel {

    protected static final DecimalFormat m_scientificNotation = new DecimalFormat( "0.##E0" );
    protected static final DecimalFormat m_regular = new DecimalFormat();
    protected static final int MINIMUM_WIDTH = 100;
    protected static final int w = 40;
    /**
     * 
     */
    private static final long serialVersionUID = -3334047037807389979L;

    public JNumberLabel() {
        super();
        init();
    }

    public JNumberLabel( double number ) {
        this();
        m_regular.setMaximumFractionDigits( 3 );
        setText( number );
    }

    public void setText( double number ) {

        // Only very small numbers (except for zero) as well as very large numbers
        // should be displayed in scientific notation
        String text;
        if ( number != 0 && Math.abs( number ) < 0.01 || Math.abs( number ) > 999 ) {
            text = m_scientificNotation.format( number );
        } else {
            text = m_regular.format( number );
        }

        super.setText( text );
    }

    protected void init() {
        // setOpaque( true );
        // setBackground( Color.lightGray );
        Dimension d = new Dimension( w, JGradientLabel.h );
        setSize( d );
        setPreferredSize( d );
    }
}
