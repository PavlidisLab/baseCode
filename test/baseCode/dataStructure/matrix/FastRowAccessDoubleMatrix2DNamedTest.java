package baseCode.dataStructure.matrix;

import cern.colt.list.DoubleArrayList;
import cern.colt.matrix.DoubleMatrix1D;
import junit.framework.TestCase;
import baseCode.io.reader.DoubleMatrixReader;

/**
 * <hr>
 * <p>
 * Copyright (c) 2004-2005 Columbia University
 * 
 * @author pavlidis
 * @version $Id$
 */
public class FastRowAccessDoubleMatrix2DNamedTest extends TestCase {

    DoubleMatrixNamed testdata;
    DoubleMatrixReader f = new DoubleMatrixReader();

    /*
     * @see TestCase#setUp()
     */
    protected void setUp() throws Exception {
        testdata = ( DoubleMatrixNamed ) f.read( FastRowAccessDoubleMatrix2DNamedTest.class
                .getResourceAsStream( "/data/testdata.txt" ) );
        assert ( testdata instanceof FastRowAccessDoubleMatrix2DNamed );
        super.setUp();
    }

    /*
     * @see TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        super.tearDown();
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.rows()'
     */
    public void testRows() {
        assertTrue( testdata.rows() == 30 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.columns()'
     */
    public void testColumns() {
        assertTrue( testdata.columns() == 12 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.set(int, int, Object)'
     */
    public void testSetIntIntObject() {
        testdata.set( 1, 2, new Double( 3.0 ) );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getRowObj(int)'
     */
    public void testGetRowObj() {

    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getColObj(int)'
     */
    public void testGetColObj() {

    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.isMissing(int, int)'
     */
    public void testIsMissing() {

    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getRow(int)'
     */
    public void testGetRow() {
        double[] res = testdata.getRow( 2 );
        assertEquals( 27873.8, res[4], 0.001 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getRowArrayList(int)'
     */
    public void testGetRowArrayList() {
        DoubleArrayList res = testdata.getRowArrayList( 2 );
        assertEquals( 27873.8, res.get( 4 ), 0.001 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.get(int, int)'
     */
    public void testGet() {
        assertEquals( 27873.8, testdata.get( 2, 4 ), 0.0001 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.getQuick(int, int)'
     */
    public void testGetQuick() {
        assertEquals( 27873.8, testdata.getQuick( 2, 4 ), 0.0001 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.set(int, int, double)'
     */
    public void testSetIntIntDouble() {
        testdata.set( 1, 2, 3.0 );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.viewRow(int)'
     */
    public void testViewRow() {
        DoubleMatrix1D res = testdata.viewRow( 1 );
        assertEquals( 242.1, res.get( 2 ), 0.001 );
    }

    /*
     * Test method for 'baseCode.dataStructure.matrix.FastRowAccessDoubleMatrix2DNamed.setQuick(int, int, double)'
     */
    public void testSetQuick() {
        testdata.setQuick( 1, 2, 3.0 );
        assertEquals( 3.0, testdata.get( 1, 2 ), 0.00001 );
    }

}
