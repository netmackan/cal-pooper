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
package com.markuspage.calpooper.cli;

import com.markuspage.calpooper.ical.CalendarParser;
import com.markuspage.calpooper.ical.Event;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author Markus Kilås <markus@kilas.se>
 */
public class CalPooperApp {

    private static final String IN_FILE_LONG = "in-file";
    private static final String IN_FILE = "f";
    //private static final String OUT_FILE_LONG = "out-file";
    //private static final String OUT_FILE = "o";
    private static final String GREP = "g";
    //private static final String GREP_LONG = "grep";
    
    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        
        try {
            final CommandLine line = parseArguments(args);
            
            if (!line.hasOption(IN_FILE) || !line.hasOption(GREP)) {
                printHelp();
                System.exit(1);
            } else {
                final CalPooperApp app = new CalPooperApp();
                
                final String inFile = line.getOptionValue(IN_FILE);
                final String grep = line.getOptionValue(GREP);
                final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                
                try (Reader r = new FileReader(inFile)) {
                
                    final List<Event> events = app.grepForEvents(r, grep);
                    
                    System.out.println("Number of events; " + events.size());
            
                    System.out.print("Event times (date); ");
                    events.stream().forEach(e -> {
                        LocalDateTime ldt = LocalDateTime.ofInstant(e.getStartDate().toInstant(), ZoneId.systemDefault());
                        System.out.print(formatter.format(ldt) + "; ");
                    });
                    System.out.println();
                }
            }
        } catch (ParseException ex) {
            System.err.println("Failed parsing command line:");
            System.err.println(ex.getLocalizedMessage());
            printHelp();
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("Error reading input file:");
            System.err.println(ex.getLocalizedMessage());
            System.exit(2);
        }
        
    }

    private static CommandLine parseArguments(final String[] args) throws ParseException {
        return new GnuParser()
                .parse(getOptions(), args);
    }

    private static Options getOptions() {
        var result = new Options();
        
        result.addOption(IN_FILE, IN_FILE_LONG, true, "Input file");
        //result.addOption(OUT_FILE, OUT_FILE_LONG, true, "Output file");
        
        result.addOption("g", "grep", true, "Find events matching regex");
        
        return result;
    }

    private static void printHelp() {
        new HelpFormatter()
                .printHelp("CalPooperApp", getOptions(), true);
    }

    private List<Event> grepForEvents(final Reader r, final String grep) throws IOException {
        return new CalendarParser()
                .parseFirst(r)
                .getEvents()
                .stream()
                .filter(p -> p.getSummary().matches(grep))
                .sorted((x, y) -> x.getStartDate().compareTo(y.getStartDate()))
                .collect(Collectors.toList());
    }
    
}
