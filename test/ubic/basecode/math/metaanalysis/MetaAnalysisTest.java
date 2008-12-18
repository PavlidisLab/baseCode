/*
 * The Gemma project.
 * 
 * Copyright (c) 2006-2007 University of British Columbia
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
package ubic.basecode.math.metaanalysis;

import cern.colt.list.DoubleArrayList;
import junit.framework.TestCase;

/**
 * @author paul
 * @version $Id$
 */
public class MetaAnalysisTest extends TestCase {

    /**
     * @throws Exception
     */
    public final void testFisherCombinePvalues() throws Exception {

        /*
         * Example from Cooper and Hedges section 4.4 pg 223.
         */
        DoubleArrayList values = new DoubleArrayList( new double[] { 0.016, 0.405, 0.067, 0.871, 0.250 } );
        double actualResult = MetaAnalysis.fisherCombinePvalues( values );

        // Value computed from R using 1 - pchisq(18.533, 10)

        assertEquals( 0.0466, actualResult, 0.001 );

    }

    public final void testFisherCombinePvaluesB() throws Exception {

        /*
         * Example from gemma
         */
        DoubleArrayList values = new DoubleArrayList( new double[] { 4.453e-27, 1.482e-11, 0.003, 0.078, 0.316, 0.609,
                0.662, 0.832 } );
        double actualResult = MetaAnalysis.fisherCombinePvalues( values );

        // R gives pvalues of zero, this number comes from feeding log sum values = 192.4316 (computed in R) and dof=16
        // to colt.
        assertEquals( 2.67e-32, actualResult, 1e-33 );

    }

}
