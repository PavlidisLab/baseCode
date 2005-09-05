/*
 * The baseCode project
 * 
 * Copyright (c) 2005 Columbia University
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 *
 */
package baseCode.dataFilter;

import junit.framework.TestCase;
import baseCode.dataStructure.matrix.DoubleMatrixNamed;
import baseCode.dataStructure.matrix.StringMatrix2DNamed;
import baseCode.io.reader.DoubleMatrixReader;
import baseCode.io.reader.StringMatrixReader;

/**
 * Fixture for testing filtering of matrices.
 * 
 * @author Pavlidis
 * @version $Id$
 */

public abstract class AbstractTestFilter extends TestCase {

    protected DoubleMatrixNamed testdata = null;
    protected StringMatrix2DNamed teststringdata = null;
    protected DoubleMatrixNamed testmissingdata = null;
    protected StringMatrix2DNamed teststringmissingdata = null;

    public AbstractTestFilter() {
        super();
    }

    protected void setUp() throws Exception {
        super.setUp();
        DoubleMatrixReader f = new DoubleMatrixReader();
        StringMatrixReader s = new StringMatrixReader();

        testdata = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class.getResourceAsStream( "/data/testdata.txt" ) );

        testmissingdata = ( DoubleMatrixNamed ) f.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdatamissing.txt" ) );

        teststringdata = ( StringMatrix2DNamed ) s.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdata.txt" ) );

        teststringmissingdata = ( StringMatrix2DNamed ) s.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdatamissing.txt" ) );

    }

    protected void tearDown() throws Exception {
        super.tearDown();
        testdata = null;
        testmissingdata = null;
        teststringdata = null;
        teststringmissingdata = null;
    }

}