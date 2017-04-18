package org.vaadin.alump.searchdropdown.demo.data;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.nio.charset.Charset;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by alump on 17/04/2017.
 */
public class DemoSource {

    public final static int MAX_RESULTS = 5;

    /**
     * Politically incorrect gender, does not have option Apache Helicopter, sorry, it's just a demo data
     */
    public enum Gender {
        FEMALE("F"), MALE("M");

        private final String code;

        Gender(String code) {
            this.code = code;
        }

        public static Gender parse(String value) {
            return Arrays.stream(Gender.values()).filter(g -> g.code.equals(value)).findFirst().get();
        }
    }

    public static class Data {
        private final String name;
        private final String city;
        private final String country;
        private final Gender gender;
        private final int rank;

        /**
         *
         * @param name
         * @param city
         * @param county
         * @param gender Yes it's boolean, gender apache helicopter not supported, this is just demo data
         * @param rank
         */
        public Data(String name, String city, String county, Gender gender, int rank) {
            this.name = name;
            this.city = city;
            this.country = county;
            this.gender = gender;
            this.rank = rank;
        }

        public String getName() {
            return name;
        }

        public String getCity() {
            return city;
        }

        public String getCountry() {
            return country;
        }

        @Override
        public String toString() {
            return getName() + ", " + getCity() + ", " + getCountry();
        }

        public Gender getGender() {
            return gender;
        }

        public int getRank() {
            return rank;
        }
    }

    /**
     * This is slow by design, to allow to simulation of slow external queries
     * @param query
     * @return
     */
    public List<Data> findData(String query) {

        final LocalDateTime startedAt = LocalDateTime.now();

        String[] words = query.toLowerCase().split("\\s");

        try {
            File csvData = new File(getClass().getClassLoader().getResource("mockdata.csv").toURI());
            CSVParser parser = CSVParser.parse(csvData, Charset.forName("UTF-8"), CSVFormat.EXCEL.withFirstRecordAsHeader());
            Iterator<CSVRecord> iterator = parser.iterator();
            List<Data> result = new ArrayList<>();
            while(iterator.hasNext()) {
                CSVRecord record = iterator.next();
                List<Integer> ranks = Arrays.stream(words).map(word -> {
                    if (record.get("name").toLowerCase().contains(word)) {
                        return 1;
                    } else if (record.get("city").toLowerCase().contains(word)) {
                        return 2;
                    } else if (record.get("country").toLowerCase().contains(word)) {
                        return 3;
                    } else {
                        return 4;
                    }
                }).filter(i -> i < 4).sorted().collect(Collectors.toList());
                if(ranks.size() == words.length) {
                    int rank = ranks.get(0);
                    result.add(new Data(record.get("name"), record.get("city"), record.get("country"),
                            Gender.parse(record.get("gender")), rank));
                }
            }

            result.sort(Comparator.comparingInt(Data::getRank));
            if(result.size() > MAX_RESULTS) {
                result = result.subList(0, MAX_RESULTS);
            }

            // Make sure we spent at least 2 seconds in this method
            long sleepMilli = 2000 - (Duration.between(startedAt, LocalDateTime.now()).getNano() / 1000000);
            if(sleepMilli > 0) {
                System.out.println("Sleep " + sleepMilli + "ms");
                Thread.sleep(sleepMilli);
            }

            return result;

        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }

    }
}
