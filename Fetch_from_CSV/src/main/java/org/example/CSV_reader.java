package org.example;

import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class CSV_reader {

    private static final String POISON_PILL = "__END__";

    public static List<Map<String, Object>> readCSV(String fileName, String[] attributes) {
        InputStream file = CSV_reader.class
                .getClassLoader()
                .getResourceAsStream(fileName);


        if (file == null) {
            throw new RuntimeException("CSV file not found: " + fileName);
        }

        int processors = Runtime.getRuntime().availableProcessors();

        if (processors <= 1) {
            return readCSVSequential(file, attributes);
        }

        int workerCount = Math.max(1, processors - 1);

        BlockingQueue<String> queue = new ArrayBlockingQueue<>(2000);
        List<Map<String, Object>> rows = Collections.synchronizedList(new ArrayList<>());
        List<Thread> workers = new ArrayList<>();

        try {
            for (int i = 0; i < workerCount; i++) {
                Thread worker = new Thread(() -> {
                    try {
                        while (true) {
                            String line = queue.take();

                            if (POISON_PILL.equals(line)) {
                                break;
                            }

                            Map<String, Object> row = parseLine(line, attributes);
                            rows.add(row);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                worker.start();
                workers.add(worker);
            }

            try (BufferedReader br = new BufferedReader(new InputStreamReader(file))) {
                String line;

                br.readLine(); // header skip

                while ((line = br.readLine()) != null) {
                    queue.put(line);
                }
            }

            for (int i = 0; i < workerCount; i++) {
                queue.put(POISON_PILL);
            }

            for (Thread worker : workers) {
                worker.join();
            }

        } catch (Exception e) {
            throw new RuntimeException("Error while reading CSV: " + e.getMessage(), e);
        }

        return rows;
    }

    private static List<Map<String, Object>> readCSVSequential(InputStream file, String[] attributes) {
        List<Map<String, Object>> rows = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file))) {
            String line;

            br.readLine(); // header skip

            while ((line = br.readLine()) != null) {
                rows.add(parseLine(line, attributes));
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while reading CSV: " + e.getMessage(), e);
        }

        return rows;
    }

    private static Map<String, Object> parseLine(String line, String[] attributes) {
        line = line.replace("\"", "");
        String[] values = line.split(",");

        Map<String, Object> row = new HashMap<>();

        for (int i = 0; i < attributes.length; i++) {
            String key = attributes[i];
            String value = i < values.length ? values[i].trim() : null;
            row.put(key, value);
        }

        return row;
    }
}
