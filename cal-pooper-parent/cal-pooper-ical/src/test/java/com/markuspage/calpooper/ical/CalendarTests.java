/*
 * Copyright 2020 Markus Kilås <markus@kilas.se>.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.markuspage.calpooper.ical;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Properties;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for the Calendar class.
 *
 * @author Markus Kilås <markus@kilas.se>
 */
public class CalendarTests {

    /** A sample calendar with an event. */
    private static final String EXAMPLE_1 = 
            "BEGIN:VCALENDAR\r\n" +
            "VERSION:2.0\r\n" +
            "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\r\n" +
            "BEGIN:VEVENT\r\n" +
            "UID:uid1@example.com\r\n" +
            "DTSTAMP:19970714T170000Z\r\n" +
            "ORGANIZER;CN=John Doe:MAILTO:john.doe@example.com\r\n" +
            "DTSTART:19970714T170000Z\r\n" +
            "DTEND:19970715T035959Z\r\n" +
            "SUMMARY:Bastille Day Party\r\n" +
            "GEO:48.85299;2.36885\r\n" +
            "END:VEVENT\r\n" +
            "END:VCALENDAR";
    
    /** Same as EXAMPLE_1 but with three lines SUMMARY. **/
    private static final String EXAMPLE_2 = 
            "BEGIN:VCALENDAR\r\n" +
            "VERSION:2.0\r\n" +
            "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\r\n" +
            "BEGIN:VEVENT\r\n" +
            "UID:uid1@example.com\r\n" +
            "DTSTAMP:19970714T170000Z\r\n" +
            "ORGANIZER;CN=John Doe:MAILTO:john.doe@example.com\r\n" +
            "DTSTART:19970714T170000Z\r\n" +
            "DTEND:19970715T035959Z\r\n" +
            "SUMMARY:Bastille Day Party\r\n" +
            "  and some more, \r\n" +
            " and even more.\r\n" +
            "GEO:48.85299;2.36885\r\n" +
            "END:VEVENT\r\n" +
            "END:VCALENDAR";
    
    /** Same as EXAMPLE_2 but with two lines property key. **/
    private static final String EXAMPLE_3 = 
            "BEGIN:VCALENDAR\r\n" +
            "VERSION:2.0\r\n" +
            "PRODID:-//hacksw/handcal//NONSGML v1.0//EN\r\n" +
            "BEGIN:VEVENT\r\n" +
            "UID:uid1@example.com\r\n" +
            "DTSTAMP:19970714T170000Z\r\n" +
            "ORGANIZER;CN=John \r\n" +
            " Doe:MAILTO:john.doe@example.com\r\n" +
            "DTSTART:19970714T170000Z\r\n" +
            "DTEND:19970715T035959Z\r\n" +
            "SUMMARY:Bastille Day Party\r\n" +
            "  and some more, \r\n" +
            " and even more.\r\n" +
            "GEO:48.85299;2.36885\r\n" +
            "END:VEVENT\r\n" +
            "END:VCALENDAR";
    
    
    @Test
    @DisplayName("Creates a calendar")
    void createsCalendar() {
        final Properties properties = new Properties();
        final double version = 2.0;
        final String progId = "-//hacksw/handcal//NONSGML v1.0//EN";
        
        final Properties eventProperties = new Properties();
        eventProperties.setProperty("UID", "uid1@example.com");
        eventProperties.setProperty("DTSTAMP", "19970714T170000Z");
        final Event event = new Event(eventProperties);
        
        final Calendar calendar = new Calendar(version, progId, properties, Arrays.asList(event));
        
        assertEquals(version, calendar.getVersion());
        assertEquals(progId, calendar.getProdId());
        assertEquals("[Event{{properties={UID=uid1@example.com, DTSTAMP=19970714T170000Z}}}]", calendar.getEvents().toString());
    }
    
    @Test
    @DisplayName("Parse example1")
    void parsesExample1() throws Exception {
        CalendarParser parser = new CalendarParser();
        Calendar calendar = parser.parseFirst(new StringReader(EXAMPLE_1));
        System.out.println(calendar);
        assertEquals(2.0, calendar.getVersion());
        assertEquals("-//hacksw/handcal//NONSGML v1.0//EN", calendar.getProdId());
        assertEquals("{VERSION=2.0, PRODID=-//hacksw/handcal//NONSGML v1.0//EN}", calendar.getProperties().toString());
        assertEquals(1, calendar.getEvents().size());
        Event actualEvent = calendar.getEvents().get(0);
        Properties expectedEventProperties = new Properties();
        expectedEventProperties.setProperty("UID", "uid1@example.com");
        expectedEventProperties.setProperty("DTSTAMP", "19970714T170000Z");
        expectedEventProperties.setProperty("ORGANIZER;CN=John Doe", "MAILTO:john.doe@example.com");
        expectedEventProperties.setProperty("DTSTART", "19970714T170000Z");
        expectedEventProperties.setProperty("DTEND", "19970715T035959Z");
        expectedEventProperties.setProperty("SUMMARY", "Bastille Day Party");
        expectedEventProperties.setProperty("GEO", "48.85299;2.36885");
        assertEquals(expectedEventProperties, actualEvent.getProperties());
        assertEquals("Bastille Day Party", actualEvent.getSummary());
    }
    
    @Test
    @DisplayName("Parse example2 (with 3 lines summary)")
    void parsesExample2() throws Exception {
        CalendarParser parser = new CalendarParser();
        Calendar calendar = parser.parseFirst(new StringReader(EXAMPLE_2));
        System.out.println(calendar);
        assertEquals(2.0, calendar.getVersion());
        assertEquals("-//hacksw/handcal//NONSGML v1.0//EN", calendar.getProdId());
        assertEquals("{VERSION=2.0, PRODID=-//hacksw/handcal//NONSGML v1.0//EN}", calendar.getProperties().toString());
        assertEquals(1, calendar.getEvents().size());
        Event actualEvent = calendar.getEvents().get(0);
        Properties expectedEventProperties = new Properties();
        expectedEventProperties.setProperty("UID", "uid1@example.com");
        expectedEventProperties.setProperty("DTSTAMP", "19970714T170000Z");
        expectedEventProperties.setProperty("ORGANIZER;CN=John Doe", "MAILTO:john.doe@example.com");
        expectedEventProperties.setProperty("DTSTART", "19970714T170000Z");
        expectedEventProperties.setProperty("DTEND", "19970715T035959Z");
        expectedEventProperties.setProperty("SUMMARY", "Bastille Day Party and some more, and even more.");
        expectedEventProperties.setProperty("GEO", "48.85299;2.36885");
        assertEquals(expectedEventProperties, actualEvent.getProperties());
        assertEquals("Bastille Day Party and some more, and even more.", actualEvent.getSummary());
    }
    
    @Test
    @DisplayName("Parse example3 (with 2 lines property key)")
    void parsesExample3() throws Exception {
        CalendarParser parser = new CalendarParser();
        Calendar calendar = parser.parseFirst(new StringReader(EXAMPLE_3));
        System.out.println(calendar);
        assertEquals(2.0, calendar.getVersion());
        assertEquals("-//hacksw/handcal//NONSGML v1.0//EN", calendar.getProdId());
        assertEquals("{VERSION=2.0, PRODID=-//hacksw/handcal//NONSGML v1.0//EN}", calendar.getProperties().toString());
        assertEquals(1, calendar.getEvents().size());
        Event actualEvent = calendar.getEvents().get(0);
        Properties expectedEventProperties = new Properties();
        expectedEventProperties.setProperty("UID", "uid1@example.com");
        expectedEventProperties.setProperty("DTSTAMP", "19970714T170000Z");
        expectedEventProperties.setProperty("ORGANIZER;CN=John Doe", "MAILTO:john.doe@example.com");
        expectedEventProperties.setProperty("DTSTART", "19970714T170000Z");
        expectedEventProperties.setProperty("DTEND", "19970715T035959Z");
        expectedEventProperties.setProperty("SUMMARY", "Bastille Day Party and some more, and even more.");
        expectedEventProperties.setProperty("GEO", "48.85299;2.36885");
        assertEquals(expectedEventProperties, actualEvent.getProperties());
        assertEquals("Bastille Day Party and some more, and even more.", actualEvent.getSummary());
    }
}
