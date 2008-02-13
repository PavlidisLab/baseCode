package ubic.basecode.math;

import junit.framework.TestCase;

/**
 * @author pavlidis
 * @version $Id$
 */
public class StringDistanceTest extends TestCase {

    public void testBadMatch() {
        String sa = "HCC1954Cy3vsHMECCy5r2; src: MDA-MB436 Breast Cancer cell line; src: Human Mammary Epithelial Cells";
        String sb = "Normal Breast Epithelium Control replicate 2 133B; src: Human Mammary Epithelial Cells";
        int distance = StringDistance.editDistance( sa, sb );
        double normalizedDistance = ( double ) distance / Math.max( sa.length(), sb.length() );
        assertEquals( 0.5208, normalizedDistance, 0.001 );
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

    /*
     * Test method for 'basecode.math.StringDistance.hammingDistance(String, String)'
     */
    public void testHammingDistance() {
        assertEquals( 1, StringDistance.hammingDistance( "foobly", "goobly" ) );
        assertEquals( 2, StringDistance.hammingDistance( "fooblybar", "gooblyfar" ) );
    }

    public void testWeightedHammingA() {
        String a = "AAAAAAAAAAAA";
        String b = "AAAAAABBBBBB";
        double actualValue = StringDistance.prefixWeightedHammingDistance( a, b, 0.5 );
        double expectedValue = 0.0; // suffix should be ignored.
        assertEquals( expectedValue, actualValue, 0.000001 );
    }

    public void testWeightedHammingB() {
        String a = "AAAA";
        String b = "ABAA";
        double actualValue = StringDistance.prefixWeightedHammingDistance( a, b, 0.5 );
        double expectedValue = 0.0 + 0.5;
        assertEquals( expectedValue, actualValue, 0.000001 );
    }

    public void testWeightedHammingC() {
        String a = "AAAA";
        String b = "BBBB";
        double actualValue = StringDistance.prefixWeightedHammingDistance( a, b, 1.0 );
        double expectedValue = 1.0 + 0.75 + 0.50 + 0.25;
        assertEquals( expectedValue, actualValue, 0.000001 );
    }

    public void testWeightedSuffixHammingA() {
        String a = "AAAA";
        String b = "BBBB";
        double actualValue = StringDistance.suffixWeightedHammingDistance( a, b, 1.0 );
        double expectedValue = 0.0 + 0.25 + 0.5 + 0.75;
        assertEquals( expectedValue, actualValue, 0.000001 );
    }

    public void testWeightedSuffixHammingB() {
        String a = "AAAA";
        String b = "BBBB";
        double actualValue = StringDistance.suffixWeightedHammingDistance( a, b, 0.5 );
        double expectedValue = 0.0 + 0.0 + 0.5 + 1.0;
        assertEquals( expectedValue, actualValue, 0.000001 );
    }

    public void testWeightedSuffixHammingC() {
        String a = "AAAA";
        String b = "BAAB";
        double actualValue = StringDistance.suffixWeightedHammingDistance( a, b, 0.5 );
        double expectedValue = 0.0 + 0.0 + 0.0 + 1.0;
        assertEquals( expectedValue, actualValue, 0.000001 );
    }

    /*
     * @see TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
    }

}
