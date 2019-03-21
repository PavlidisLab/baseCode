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
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import ubic.basecode.dataStructure.matrix.DoubleMatrix;

/**
 * @author Paul
 * 
 */
public class StringToStringSetMapTest {
    StringToStringSetMap map;

    @Before
    public void setup() {
        map = new StringToStringSetMap();
        map.put( "foo", "bar" );
        map.put( "foo", "bar1" );
        map.put( "foo2", "bar1" );
    }

    @Test
    public void testExpandedSize() {
        int expandedSize = map.getExpandedSize();
        assertEquals( 3, expandedSize );
    }

    @Test
    public void testExpandedValues() {
        List<String> expandedValues = map.getExpandedValues();
        assertEquals( 3, expandedValues.size() );
        assertEquals( 2, map.getSize( "foo" ) );
    }

    @Test
    public void testMatrix() {
        DoubleMatrix<String, String> matrix = StringToStringSetMap.setMapToMatrix( map );
        assertEquals( 2, matrix.rows() );
        assertEquals( 2, matrix.columns() );
    }

    @Test
    public void testMatrix2() {
        DoubleMatrix<String, String> matrix = StringToStringSetMap.setMapToMatrix( map );

        map.put( "foo2", "bar2" );
        map.put( "foo2", "bar3" );
        matrix = StringToStringSetMap.setMapToMatrix( map );
        assertEquals( 2, matrix.rows() );
        assertEquals( 4, matrix.columns() );
    }

    @Test
    public void testSeenValues() {
        Set<String> seenValues = map.getSeenValues();
        assertEquals( 2, seenValues.size() );
        assertTrue( seenValues.contains( "bar" ) );
        assertTrue( seenValues.contains( "bar1" ) );

    }

    @Test
    public void testWhereIs() {
        map.put( "foo2", "bar2" );
        map.put( "foo2", "bar3" );
        Set<String> whereIs = map.whereIs( "foo" );
        assertEquals( 0, whereIs.size() );
        whereIs = map.whereIs( "bar2" );
        assertEquals( 1, whereIs.size() );
        assertTrue( whereIs.contains( "foo2" ) );
    }

}
