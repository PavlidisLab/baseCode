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

import org.junit.Test;

import java.util.Collection;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * @author pavlidis
 *
 */
public class StringUtilTest {

    @Test
    public void testCommonPrefix() {
        Collection<String> test = new HashSet<>();
        test.add( "aadaab" );
        test.add( "aaaaab" );
        test.add( "aabaab" );
        test.add( "aaaacb" );
        String p = StringUtil.commonPrefix( test );
        assertEquals( "aa", p );
    }

    @Test
    public void testCommonPrefixNone() {
        Collection<String> test = new HashSet<>();
        test.add( "aadaac" );
        test.add( "daaaaab" );
        test.add( "aabaab" );
        test.add( "aaaacb" );
        String p = StringUtil.commonPrefix( test );
        assertNull( p );
    }

    @Test
    public void testCommonSuffix() {
        Collection<String> test = new HashSet<>();
        test.add( "aadaab" );
        test.add( "aaaaab" );
        test.add( "aabaab" );
        test.add( "aaaacb" );
        String suf = StringUtil.commonSuffix( test );
        assertEquals( "b", suf );
    }

    @Test
    public void testCommonSuffixNone() {
        Collection<String> test = new HashSet<>();
        test.add( "aaabf" );
        test.add( "ab" );
        test.add( "aaaab" );
        test.add( "aaaacb" );
        String suf = StringUtil.commonSuffix( test );
        assertEquals( null, suf );
    }

    @Test
    public void testMakeRnames() {
        assertFalse( Character.isDigit( '.' ) );
        assertEquals( "NA", StringUtil.makeValidForR( null ) );
        assertEquals( "test", StringUtil.makeValidForR( "test" ) );
        assertEquals( "X", StringUtil.makeValidForR( "X" ) );
        assertEquals( "X123", StringUtil.makeValidForR( "123" ) );
        assertEquals( "X", StringUtil.makeValidForR( "" ) );
        assertEquals( "X..", StringUtil.makeValidForR( "  " ) );
        assertEquals( "if.", StringUtil.makeValidForR( "if" ) );
        assertEquals( "TRUE.", StringUtil.makeValidForR( "TRUE" ) );
        assertEquals( "...", StringUtil.makeValidForR( "..." ) );
        assertEquals( "..", StringUtil.makeValidForR( ". " ) );
        assertEquals( "X.2way", StringUtil.makeValidForR( ".2way" ) );
        assertEquals( "f33oo.dd....f..a", StringUtil.makeValidForR( "f33oo dd . [f] a" ) );
        assertEquals( ".f33oo", StringUtil.makeValidForR( ".f33oo" ) );
        assertEquals( "...f33oo", StringUtil.makeValidForR( "...f33oo" ) );
        assertEquals( "X1foo.dd....f..a", StringUtil.makeValidForR( "1foo dd . [f] a" ) );
        assertEquals( "X.1foo.dd....f..a", StringUtil.makeValidForR( ".1foo dd . [f] a" ) );
    }

}
