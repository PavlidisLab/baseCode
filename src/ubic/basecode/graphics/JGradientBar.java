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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

/**
 * A GUI legend component that displays a color map as a color gradient from min to max, traversing all the colors in
 * the color map. Used in the GeneSetDetails view.
 * 
 * @author Will Braynen
 * 
 */
public class JGradientBar extends JPanel {

    protected final static Color[] EMPTY = { Color.GRAY, Color.GRAY };

    private static final long serialVersionUID = 6668982782351006998L;
    protected JGradientLabel gradient;
    protected JNumberLabel m_max;
    protected JNumberLabel m_min;

    public JGradientBar() {
        this( EMPTY );
    }

    public JGradientBar( Color[] map ) {
        gradient = new JGradientLabel( map );
        m_min = new JNumberLabel();
        m_max = new JNumberLabel();
        m_min.setHorizontalAlignment( SwingConstants.RIGHT );
        m_max.setHorizontalAlignment( SwingConstants.LEFT );

        Font font = getFont().deriveFont( Font.BOLD, 13.0f );
        m_min.setFont( font );
        m_max.setFont( font );

        setLayout( new BorderLayout() );
        add( m_min, BorderLayout.WEST );
        add( gradient, BorderLayout.CENTER );
        add( m_max, BorderLayout.EAST );
    }

    public void setColorMap( Color[] colorMap ) {
        gradient.setColorMap( colorMap );
    }

    public void setLabels( double min, double max ) {
        m_min.setText( String.format( "%.2f", min ) );
        m_max.setText( String.format( "%.2f", max ) );
    }
}

class JNumberLabel extends JLabel {

    protected static final DecimalFormat m_regular = new DecimalFormat();
    protected static final DecimalFormat m_scientificNotation = new DecimalFormat( "0.00" );
    protected static final int MINIMUM_WIDTH = 60;
    protected static final int w = 40;

    private static final long serialVersionUID = -3334047037807389979L;

    public JNumberLabel() {
        super();
        init();
    }

    public JNumberLabel( double number ) {
        this();
        m_regular.setMinimumFractionDigits( 2 );
        m_regular.setMaximumFractionDigits( 2 );
        setText( String.format( "%.2f", number ) );
    }

    protected void init() {
        this.setBorder( new EmptyBorder( 2, 3, 2, 3 ) );
    }
}
