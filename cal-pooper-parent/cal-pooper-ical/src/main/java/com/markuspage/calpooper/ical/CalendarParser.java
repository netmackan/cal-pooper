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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 *
 * @author Markus Kilås <markus@kilas.se>
 */
public class CalendarParser {
    
    public Calendar parseFirst(Reader reader) throws IOException {
        
        CalendarInput input = new CalendarInput(reader);
        
        final double version;
        final String prodId;
        final Properties properties = new Properties();
        final List<Event> events = new ArrayList<>();
        
        String line;
        
        line = input.nextLine();
        if (!"BEGIN:VCALENDAR".equals(line)) {
            throw parseException(input, "Expected VCALENDAR");
        }
        
        String lastProperty = null;
        String partialProperty = null;
        boolean ended = false;
        while ((line = input.nextLine()) != null) {           
            
            String[] property = line.split(":", 2);
            
            if (line.startsWith(" ")) {
                if (partialProperty != null) {
                    property[0] = partialProperty + line.substring(1);
                    partialProperty = null;
                } else if (lastProperty != null) {
                    properties.setProperty(lastProperty, properties.getProperty(lastProperty) + line.substring(1));
                    continue;
                } else {
                    throw parseException(input, "Unexpected with property value at this point");
                }
            }
            
            if (property.length != 2) {
                partialProperty = line;
                continue;
            }

            if ("BEGIN".equals(property[0])) {
                switch (property[1]) {
                    case "VEVENT":
                        events.add(parseEvent(input));
                        break;
                    default:
                        ignoreComponent(property[1], input);
                        //throw new IOException("Unsupport component: " + property[1]);
                }
                lastProperty = null;
            } else if ("END".equals(property[0])) { 
                if ("VCALENDAR".equals(property[1])) {
                    ended = true;
                    break;
                } else {
                    throw parseException(input, "Expected end of VEVENT but got: " + property[1]);
                }
            } else {
                if (properties.containsKey(property[0])) {
                    throw parseException(input, "Duplicate property: " + property[0]);
                }

                properties.setProperty(property[0], property[1]);
                lastProperty = property[0];
            }
        }
        
        if (!ended) {
            throw parseException(input, "Unexpected end of file in VCALENDAR");
        } else {
            version = Double.parseDouble(properties.getProperty("VERSION", "0.0"));
            prodId = properties.getProperty("PRODID");

            return new Calendar(version, prodId, properties, events);
        }
    }

    private Event parseEvent(final CalendarInput input) throws IOException {
        final Properties properties = new Properties();
        
        String lastProperty = null;
        String partialProperty = null;
        boolean ended = false;
        String line;
        while ((line = input.nextLine()) != null) {
            
            String[] property = line.split(":", 2);
            
            if (line.startsWith(" ")) {
                if (partialProperty != null) {
                    property[0] = partialProperty + property[0].substring(1);
                    partialProperty = null;
                } else if (lastProperty != null) {
                    properties.setProperty(lastProperty, properties.getProperty(lastProperty) + line.substring(1));
                    continue;
                } else {
                    throw parseException(input, "Unexpected with property value at this point");
                }
            }
            
            if (property.length != 2) {
                partialProperty = line;
                continue;
            }
            
            if ("BEGIN".equals(property[0])) {
                switch (property[1]) {
                    default:
                        ignoreComponent(property[1], input);
                        //throw new IOException("Unsupport component: " + property[1]);
                }
                lastProperty = null;
            } else if ("END".equals(property[0])) {
                if ("VEVENT".equals(property[1])) {
                    ended = true;
                    break;
                } else {
                    throw parseException(input, "Expected end of VEVENT but got: " + property[1]);
                }
            } else {
                properties.setProperty(property[0], property[1]);
                lastProperty = property[0];
            }
        }
        
        if (!ended) {
            throw parseException(input, "Unexpected end of file in VEVENT");
        } else {
            return new Event(properties);
        }
    }

    private IOException parseException(CalendarInput input, String message) {
        return new IOException("Parse error at " + input.getReadCount() + ": " + message);
    }
    
    private void ignoreComponent(String name, final CalendarInput input) throws IOException {
        System.out.println("Ignoring unsupported component: " + name);
        boolean ended = false;
        String line;
        while ((line = input.nextLine()) != null) {
            String[] property = line.split(":", 2);
            if (property.length != 2) {
                throw parseException(input, "Expected property");
            }
            
            if ("BEGIN".equals(property[0])) {
                switch (property[1]) {
                    default:
                        ignoreComponent(property[1], input);
                        //throw new IOException("Unsupport component: " + property[1]);
                }
            } else if ("END".equals(property[0])) {
                if (name.equals(property[1])) {
                    ended = true;
                    break;
                } else {
                    throw parseException(input, "Expected end of " + name + " but got: " + property[1]);
                }
            } else {
                System.out.println("Ignoring " + name + " property: " + property[0]);
            }
        }
        
        if (!ended) {
            throw parseException(input, "Unexpected end of file in " + name);
        }
    }
    
    private static class CalendarInput {
        private final BufferedReader reader;
        private long readCount;

        public CalendarInput(Reader reader) {
            this.reader = new BufferedReader(reader);
        }
        
        public String nextLine() throws IOException {
            final String result = reader.readLine();
            readCount++;
            return result;
        }

        public long getReadCount() {
            return readCount;
        }
        
    }
}
