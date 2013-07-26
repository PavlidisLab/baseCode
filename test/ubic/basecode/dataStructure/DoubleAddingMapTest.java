/*
 * The baseCode project
 * 
 * Copyright (c) 2013 University of British Columbia
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
package ubic.basecode.dataStructure;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Paul
 * @version $Id$
 */
public class DoubleAddingMapTest {

    @Test
    public void test() {
        DoubleAddingMap<String> test = new DoubleAddingMap<String>();
        test.addPut( "A", .5 );
        assertEquals( 0.5, test.get( "A" ), 0.0001 );
        test.addPut( "A", .5 );
        assertEquals( 1.0, test.get( "A" ), 0.0001 );
        test.addPut( "B", 22d );
        assertEquals( 22d, test.get( "B" ), 0.001 );
        test.addPut( "A", .5 );

        test.addPutAll( test );
        assertEquals( 3.0, test.get( "A" ), 0.0001 ); // 2*(0.5 + 0.5 + 0.5)
        test.addPut( "A", .5 );
        assertEquals( 3.5, test.get( "A" ), 0.0001 );
        assertEquals( 44, test.get( "B" ), 0.001 );

    }

}
