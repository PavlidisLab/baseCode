/*
 * MicroarraySet.java
 *
 * Created on May 27, 2004, 9:59 PM
 */

package baseCode.Gui;

import java.awt.Color;

/**
 *
 * @author  Will
 */
public class ColorMatrix {


    // data fields
    int m_geneCount;
    int m_chipCount;
    Color[][] m_colors;
    String[] m_geneNames;

    public ColorMatrix() {

        this( 50, 100 );
    }

    /** Creates a new instance of MicroarraySet */
    public ColorMatrix(int geneCount, int chipCount) {

        m_geneCount = geneCount;
        m_chipCount = chipCount;
        m_colors = new Color[geneCount][chipCount];
        m_geneNames = new String[geneCount];

        initColors();
    }

    public void initColors() {

        // for now do random colors
        for (int gene = 0;  gene < m_geneCount;  gene++)
            for (int chip = 0;  chip < m_chipCount;  chip++)
                m_colors[gene][chip] = Math.random() <= 0.5 ? Color.red : Color.green;
    }

    public int getGeneCount() {

        return m_geneCount;
    }

    public int getChipCount() {

        return m_chipCount;
    }

    public Color getColor( int gene, int chip ) {

        return m_colors[gene][chip];
    }

    public String getGeneName( int gene ) {

        return m_geneNames[gene];
    }
}
