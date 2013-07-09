/*
 * The baseCode project
 * 
 * Copyright (c) 2011 University of British Columbia
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package ubic.basecode.math;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.datafilter.AbstractTestFilter;
import ubic.basecode.io.reader.DoubleMatrixReader;

/**
 * @author paul
 * @version $Id$
 */
public class SingularValueDecompositionTest {

    @Test
    public void testSingularValueDecomposition() throws Exception {

        DoubleMatrixReader f = new DoubleMatrixReader();

        DoubleMatrix<String, String> testdata = f.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdata.txt" ) );
        SingularValueDecomposition<String, String> svd = new SingularValueDecomposition<String, String>( testdata );
        assertNotNull( svd.getU() );
        assertNotNull( svd.getSingularValues() );
    }

}
