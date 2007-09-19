/*
 * The Gemma project
 * 
 * Copyright (c) 2006 University of British Columbia
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
package ubic.basecode.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author keshav
 * @version $Id$
 */
public class MapUtils {

    /**
     * Sorts the map by its values and returns the sorted map.
     * 
     * @param pvaluesMap
     * @return {@link Map}
     */
    public static Map sortMapByValues( Map pvaluesMap ) {
        List pvaluesKeys = new ArrayList( pvaluesMap.keySet() );
        List pvaluesValues = new ArrayList( pvaluesMap.values() );
        TreeSet sortedPvalues = new TreeSet( pvaluesValues );
        pvaluesMap.clear();

        Object[] sortedArray = sortedPvalues.toArray();

        int size = sortedArray.length;

        for ( int i = 0; i < size; i++ ) {

            pvaluesMap.put( pvaluesKeys.get( pvaluesValues.indexOf( sortedArray[i] ) ), sortedArray[i] );
        }

        return pvaluesMap;
    }

}
