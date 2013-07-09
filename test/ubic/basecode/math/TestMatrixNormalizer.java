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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;
import ubic.basecode.datafilter.AbstractTestFilter;
import ubic.basecode.io.reader.DoubleMatrixReader;

/**
 * @author paul
 * @version $Id$
 */
public class TestMatrixNormalizer {

    /**
     * <pre>
     * mm<-read.table("testdata.txt", header=T, row.names=1)
     * library(preprocessCore)
     * 
     * rr<-normalize.quantiles(as.matrix(mm))
     *  rr[3,5]
     * [1] 29860.93
     * > rr[13,5]
     * [1] 466.7833
     * > rr[3,12]
     * [1] 31620.79
     * > rr[4,10]
     * [1] 1071.525
     * > rr[6,7]
     * [1] 288.925
     * >
     * 
     * </pre>
     * 
     * @throws Exception
     */
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testQuantileNormalize() throws Exception {

        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testdata = f.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdata.txt" ) );

        MatrixNormalizer m = new MatrixNormalizer();

        DoubleMatrix normalized = m.quantileNormalize( testdata );

        assertEquals( 29860.93, normalized.get( 2, 4 ), 0.01 );
        assertEquals( 466.7833, normalized.get( 12, 4 ), 0.01 );
        assertEquals( 31620.79, normalized.get( 2, 11 ), 0.01 );
        assertEquals( 1071.525, normalized.get( 3, 9 ), 0.01 );
        assertEquals( 288.925, normalized.get( 5, 6 ), 0.01 );

    }

    /**
     * Note that we do this differnetly than the Bioconductor implementation. For what it is worth here are the values
     * they get
     * 
     * <pre>
     * dm<-read.delim("testdatamissing.txt", row.names=1, header=T)
     *     rr<-normalize.quantiles(as.matrix(dm))
     * >  rr[3,5]
     * [1] 30458.47
     * > rr[13,5]
     * [1] 737.1687
     * > rr[3,12]
     * [1] NA
     * > rr[4,10]
     * [1] 1187.655
     * > rr[6,7]
     * [1] 570.0667
     * 
     * </pre>
     * 
     * @throws Exception
     */
    @Test
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public void testQuantileNormalizeWithMissing() throws Exception {

        DoubleMatrixReader f = new DoubleMatrixReader();
        DoubleMatrix<String, String> testdata = f.read( AbstractTestFilter.class
                .getResourceAsStream( "/data/testdatamissing.txt" ) );

        MatrixNormalizer m = new MatrixNormalizer();

        DoubleMatrix normalized = m.quantileNormalize( testdata );
        assertEquals( Double.NaN, normalized.get( 2, 11 ), 0.01 );

        assertEquals( 29725.388, normalized.get( 2, 4 ), 0.01 );
        assertEquals( 466.78, normalized.get( 12, 4 ), 0.01 );
        assertEquals( 1085.89, normalized.get( 3, 9 ), 0.01 );
        assertEquals( 290.125, normalized.get( 5, 6 ), 0.01 );

    }

}
