/*
 * The basecode project
 * 
 * Copyright (c) 2007-2019 University of British Columbia
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
import java.util.Date;
import java.util.HashSet;

import junit.framework.TestCase;

import org.apache.commons.lang3.time.DateUtils;

/**
 * @author pavlidis
 * 
 */
public class DateUtilTest extends TestCase {

    public void testgetRelative5yearsago() {

        Date now = new Date();

        Date expectedValue = DateUtils.addYears( now, -5 );
        Date actualValue = DateUtil.getRelativeDate( now, "-5y" );

        assertEquals( expectedValue, actualValue );

    }

    public void testgetRelative5yearsFromnow() {

        Date now = new Date();

        Date expectedValue = DateUtils.addYears( now, 5 );
        Date actualValue = DateUtil.getRelativeDate( now, "+5y" );

        assertEquals( expectedValue, actualValue );

    }

    public void testgetRelativeDateDayago() {

        Date now = new Date();

        Date expectedValue = DateUtils.addDays( now, -1 );
        Date actualValue = DateUtil.getRelativeDate( now, "-1d" );

        assertEquals( expectedValue, actualValue );

    }

    public void testgetRelativeTomorrow() {

        Date now = new Date();

        Date expectedValue = DateUtils.addDays( now, 1 );
        Date actualValue = DateUtil.getRelativeDate( now, "1d" );

        assertEquals( expectedValue, actualValue );

    }

    public void testRange() throws Exception {

        Collection<Date> ds = new HashSet<Date>();
        Date now = new Date();
        ds.add( now );
        ds.add( DateUtils.addSeconds( now, -1 ) );
        ds.add( DateUtils.addSeconds( now, 2 ) );

        assertEquals( 3L, DateUtil.numberOfSecondsBetweenDates( ds ) );

    }

    public void testRangeBad() {

        Collection<Date> ds = new HashSet<Date>();
        Date now = new Date();
        ds.add( now );

        assertEquals( 0L, DateUtil.numberOfSecondsBetweenDates( ds ) );

    }
}
