/*
 * The baseCode project
 * 
 * Copyright (c) 2008 University of British Columbia
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

import java.sql.Blob;

import javax.sql.rowset.serial.SerialBlob;

import junit.framework.TestCase;

/**
 * @author paul
 * @version $Id$
 */
public class SQLUtilsTest extends TestCase {

    public void testBlobToString() throws Exception {
        String expectedValue = "a,b";
        byte[] charArray = expectedValue.getBytes();
        Blob blob = new SerialBlob( charArray );
        String actualValue = SQLUtils.blobToString( blob );
        assertEquals( expectedValue, actualValue );
    }
}
