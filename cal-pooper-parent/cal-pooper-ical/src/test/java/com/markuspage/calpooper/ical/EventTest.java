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

import java.util.Date;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Tests for the Event class.
 *
 * @author Markus Kilås <markus@kilas.se>
 */
public class EventTest {
    
    private static final Properties defaultProperties = new Properties();
    
    public EventTest() {
    }
    
    @BeforeAll
    public static void setUpClass() {
        defaultProperties.setProperty("UID", "uid1@example.com");
        defaultProperties.setProperty("DTSTAMP", "19970714T170000Z");
        defaultProperties.setProperty("ORGANIZER;CN=John Doe", "MAILTO:john.doe@example.com");
        defaultProperties.setProperty("DTSTART", "19970714T170000Z");
        defaultProperties.setProperty("DTEND", "19970715T035959Z");
        defaultProperties.setProperty("SUMMARY", "Bastille Day Party");
        defaultProperties.setProperty("GEO", "48.85299;2.36885");
    }
    
    @AfterAll
    public static void tearDownClass() {
    }
    
    @BeforeEach
    public void setUp() {
    }
    
    @AfterEach
    public void tearDown() {
    }


    /**
     * Test of getStartDate method, of class Event.
     * @param calDate Date in iCal format
     * @param expected Expected date in Java Date format
     */
    @ParameterizedTest(name = "Start date {0} = {1}")
    @CsvSource({
        "20201113T090000Z, Fri Nov 13 10:00:00 CET 2020",
        "20200908T201314Z, Tue Sep 08 22:13:14 CEST 2020",
    })
    public void testGetStartDate(String calDate, String expected) {
        System.out.println("getStartDate");
        Properties properties = new Properties(defaultProperties);
        
        properties.setProperty("DTSTART", calDate);
        
        Event instance = new Event(properties);
        Date result = instance.getStartDate();
        assertEquals(expected, result.toString());
    }
    
}
