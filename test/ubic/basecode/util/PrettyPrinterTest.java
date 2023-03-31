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
package ubic.basecode.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PrettyPrinterTest {

    @SuppressWarnings("unused")
    public static class OntologyTermSimple {

        private final String s;
        private final String label;

        public OntologyTermSimple( String uri, String label ) {
            this.s = uri;
            this.label = label;
        }

        public String getLabel() {
            return label;
        }

        public String getTerm() {
            return label;
        }

        public String getUri() {
            return s;
        }

        public String getComment() {
            return "";
        }

        public String getLocalName() {
            return s.replace( "http://my.test/", "" );
        }

        public boolean isTermObsolete() {
            return false;
        }
    }

    @Test
    public void testPrintBeans() {
        Collection<OntologyTermSimple> tests = new ArrayList<OntologyTermSimple>();
        tests.add( new OntologyTermSimple( "http://my.test/foo", "foo" ) );
        tests.add( new OntologyTermSimple( "http://my.test/bar", "bar" ) );
        tests.add( new OntologyTermSimple( "http://my.test/ack", "ack" ) );
        tests.add( new OntologyTermSimple( "http://my.test/poo", "poo" ) );

        String actual = PrettyPrinter.print( Collections.singletonList( "ubic.basecode" ), tests );
        String expected = "   OntologyTermSimple Properties:\n" + "      OntologyTermSimple.comment: \n"
                + "      OntologyTermSimple.label: foo\n" + "      OntologyTermSimple.localName: foo\n"
                + "      OntologyTermSimple.term: foo\n" + "      OntologyTermSimple.termObsolete: false\n"
                + "      OntologyTermSimple.uri: http://my.test/foo\n" + "   OntologyTermSimple Properties:\n"
                + "      OntologyTermSimple.comment: \n" + "      OntologyTermSimple.label: bar\n"
                + "      OntologyTermSimple.localName: bar\n" + "      OntologyTermSimple.term: bar\n"
                + "      OntologyTermSimple.termObsolete: false\n"
                + "      OntologyTermSimple.uri: http://my.test/bar\n" + "   OntologyTermSimple Properties:\n"
                + "      OntologyTermSimple.comment: \n" + "      OntologyTermSimple.label: ack\n"
                + "      OntologyTermSimple.localName: ack\n" + "      OntologyTermSimple.term: ack\n"
                + "      OntologyTermSimple.termObsolete: false\n"
                + "      OntologyTermSimple.uri: http://my.test/ack\n" + "   OntologyTermSimple Properties:\n"
                + "      OntologyTermSimple.comment: \n" + "      OntologyTermSimple.label: poo\n"
                + "      OntologyTermSimple.localName: poo\n" + "      OntologyTermSimple.term: poo\n"
                + "      OntologyTermSimple.termObsolete: false\n"
                + "      OntologyTermSimple.uri: http://my.test/poo\n";
        assertEquals( expected, actual );

    }
}
