/*
 * The baseCode project
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

import java.util.Collection;
import java.util.HashSet;

import junit.framework.TestCase;

/**
 * @author pavlidis
 * @version $Id$
 */
public class StringUtilTest extends TestCase {

    public void testCommonPrefix() {
        Collection<String> test = new HashSet<String>();
        test.add( "aadaab" );
        test.add( "aaaaab" );
        test.add( "aabaab" );
        test.add( "aaaacb" );
        String p = StringUtil.commonPrefix( test );
        assertEquals( "aa", p );
    }

    public void testCommonPrefixNone() {
        Collection<String> test = new HashSet<String>();
        test.add( "aadaac" );
        test.add( "daaaaab" );
        test.add( "aabaab" );
        test.add( "aaaacb" );
        String p = StringUtil.commonPrefix( test );
        assertNull( p );
    }

    public void testCommonSuffix() {
        Collection<String> test = new HashSet<String>();
        test.add( "aadaab" );
        test.add( "aaaaab" );
        test.add( "aabaab" );
        test.add( "aaaacb" );
        String suf = StringUtil.commonSuffix( test );
        assertEquals( "b", suf );
    }

    public void testCommonSuffixNone() {
        Collection<String> test = new HashSet<String>();
        test.add( "aaabf" );
        test.add( "ab" );
        test.add( "aaaab" );
        test.add( "aaaacb" );
        String suf = StringUtil.commonSuffix( test );
        assertEquals( null, suf );
    }

}
