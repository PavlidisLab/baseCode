package ubic.basecode.math;

import junit.framework.TestCase;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class StringDistanceTest extends TestCase {

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'basecode.math.StringDistance.hammingDistance(String, String)'
     */
    public void testHammingDistance() {
        assertEquals( 1, StringDistance.hammingDistance( "foobly", "goobly" ) );
        assertEquals( 2, StringDistance.hammingDistance( "fooblybar", "gooblyfar" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceA() {
        assertEquals( 1, StringDistance.editDistance( "foobly", "goobly" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceB() {
        assertEquals( 1, StringDistance.editDistance( "goobly", "foobly" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceC() {
        assertEquals( 2, StringDistance.editDistance( "GUMBO", "GAMBOL" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceD() {
        assertEquals( 1, StringDistance.editDistance( "GAMBOL", "GAMBOLA" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceE() {
        assertEquals( 1, StringDistance.editDistance( "GAMBFOL", "GAMBOL" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceF() {
        assertEquals( 1, StringDistance.editDistance( "GAMBOL", "GAMBFOL" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceG() {
        assertEquals( 5, StringDistance.editDistance( "GAMBOL", "GAMBFOLIAGE" ) );
    }

    /*
     * Test method for 'basecode.math.StringDistance.editDistance(String, String)'
     */
    public void testEditDistanceH() {
        assertEquals( 6, StringDistance.editDistance( "GAMBOL", "" ) );
    }

    public void testBadMatch() {
        String sa = "HCC1954Cy3vsHMECCy5r2; src: MDA-MB436 Breast Cancer cell line; src: Human Mammary Epithelial Cells";
        String sb = "Normal Breast Epithelium Control replicate 2 133B; src: Human Mammary Epithelial Cells";
        int distance = StringDistance.editDistance( sa, sb );
        double normalizedDistance = ( double ) distance / Math.max( sa.length(), sb.length() );
        assertEquals( 0.5208, normalizedDistance, 0.001 );
    }

}
