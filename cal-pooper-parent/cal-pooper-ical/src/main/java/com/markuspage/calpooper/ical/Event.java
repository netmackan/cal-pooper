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

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

/**
 *
 * @author Markus Kilås <markus@kilas.se>
 */
public class Event extends CalendarComponent {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("uuuuMMdd'T'HHmmssX", Locale.ENGLISH);

    public Event(Properties properties) {
        super(properties);
    }
    
    public String getSummary() {
        return properties.getProperty("SUMMARY");
    }
    
    public Date getStartDate() {        
        final String start = properties.getProperty("DTSTART");
        
        final TemporalAccessor parsed = formatter.parse(start);
        final ZonedDateTime zonedDateTime = ZonedDateTime.from(parsed);
        
        return Date.from(zonedDateTime.toInstant());
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Event{");
        sb.append(super.toString());
        sb.append('}');
        return sb.toString();
    }
    
}
