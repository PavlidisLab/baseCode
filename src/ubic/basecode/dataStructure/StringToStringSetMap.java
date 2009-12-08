/*
 * The Gemma project
 * 
 * Copyright (c) 2007 University of British Columbia
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
package ubic.basecode.dataStructure;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Implemented to store string based many to many relationships.
 * 
 * @author leon
 * @version $Id$
 */
public class StringToStringSetMap extends HashMap<String, Set<String>> {

    public StringToStringSetMap() {
        super();
    }

    public StringToStringSetMap( Map<String, Set<String>> start ) {
        super();
        this.putAll( start );
    }

    public int getExpandedSize() {
        return getExpandedValues().size();
    }

    public List<String> getExpandedValues() {
        List<String> result = new LinkedList<String>();
        for ( Set<String> valueSet : values() ) {
            if ( valueSet != null ) result.addAll( valueSet );
        }
        return result;
    }

    public Set<String> getSeenValues() {
        Set<String> result = new HashSet<String>();
        for ( Set<String> valueSet : values() ) {
            if ( valueSet != null ) result.addAll( valueSet );
        }
        return result;
    }

    public int getSize( String key ) {
        Set<String> valueSet = get( key );
        if ( valueSet == null ) return 0;
        return get( key ).size();
    }

    public void put( String key, String value ) {
        Set<String> valueSet = get( key );
        if ( valueSet == null ) {
            valueSet = new HashSet<String>();
            put( key, valueSet );
        }
        valueSet.add( value );
    }

    public String toPrettyString() {
        StringBuilder result = new StringBuilder();
        for ( String key : keySet() ) {
            result.append( key + "->\n" );
            Set<String> valueSet = get( key );
            if ( valueSet == null ) {
                result.append( "  null\n" );
                continue;
            }
            for ( String value : valueSet ) {
                result.append( "  " + value + "\n" );
            }
        }
        return result.toString();
    }

    public Set<String> whereIs( String value ) {
        Set<String> keySet = new HashSet<String>();
        for ( String key : keySet() ) {
            if ( get( key ).contains( value ) ) keySet.add( key );
        }
        return keySet;
    }
}
