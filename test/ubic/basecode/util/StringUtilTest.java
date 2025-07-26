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
        assertNull( suf );
    }

    @Test
    public void testMakeNames() {
        assertFalse( Character.isDigit( '.' ) );
        assertEquals( "NA", StringUtil.makeNames( null ) );
        assertEquals( "test", StringUtil.makeNames( "test" ) );
        assertEquals( "X", StringUtil.makeNames( "X" ) );
        assertEquals( "X123", StringUtil.makeNames( "123" ) );
        assertEquals( "X", StringUtil.makeNames( "" ) );
        assertEquals( "X..", StringUtil.makeNames( "  " ) );
        assertEquals( "if.", StringUtil.makeNames( "if" ) );
        assertEquals( "TRUE.", StringUtil.makeNames( "TRUE" ) );
        assertEquals( "...", StringUtil.makeNames( "..." ) );
        assertEquals( "..", StringUtil.makeNames( ". " ) );
        assertEquals( "X.2way", StringUtil.makeNames( ".2way" ) );
        assertEquals( "f33oo.dd....f..a", StringUtil.makeNames( "f33oo dd . [f] a" ) );
        assertEquals( ".f33oo", StringUtil.makeNames( ".f33oo" ) );
        assertEquals( "...f33oo", StringUtil.makeNames( "...f33oo" ) );
        assertEquals( "X1foo.dd....f..a", StringUtil.makeNames( "1foo dd . [f] a" ) );
        assertEquals( "X.1foo.dd....f..a", StringUtil.makeNames( ".1foo dd . [f] a" ) );
        assertArrayEquals( new String[] { "foo", "foo.1", "foo.2", "bar" }, StringUtil.makeNames( new String[] { "foo", "foo", "foo", "bar" }, true ) );
    }

    @Test
    public void testMakeUnique() {
        assertArrayEquals( new String[] { "foo", "foo.1" }, StringUtil.makeUnique( new String[] { "foo", "foo" } ) );
        assertArrayEquals( new String[] { "foo", "bar", "foo.1", "foo.2" }, StringUtil.makeUnique( new String[] { "foo", "bar", "foo", "foo" } ) );
    }
}
