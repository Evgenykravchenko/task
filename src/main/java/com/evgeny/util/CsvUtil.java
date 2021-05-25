package com.evgeny.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.*;

public class CsvUtil {

    private final List<String> csvFiles;
    private final Queue<Callable<Boolean>> callables;
    private final Queue<Future<Boolean>> futures;
    private final Map<String, Set<String>> uniqueData;


    public CsvUtil(List<String> csvFiles) {
        this.csvFiles = csvFiles;
        callables = new ConcurrentLinkedQueue<>();
        futures = new ConcurrentLinkedQueue<>();
        uniqueData = new ConcurrentHashMap<>();
    }

    public void execute() throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newWorkStealingPool();

        for (String csvFile: csvFiles) {
            callables.add(getReadTask(csvFile));
        }
        futures.addAll(executor.invokeAll(callables));
        waitThread();

        for (String title: uniqueData.keySet()) {
            callables.add(getWriteTask(title));
        }
        futures.addAll(executor.invokeAll(callables));
        waitThread();
        shutdownExecutor(executor);
    }

    private void shutdownExecutor(ExecutorService executor) {
        try {
            executor.shutdown();
            executor.awaitTermination(5, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            executor.shutdownNow();
        }
    }

    private Callable<Boolean> getReadTask(String path) {
        return () -> {
            Thread.sleep(1000);
            readCsvFile(path);
            return true;
        };
    }

    private Callable<Boolean> getWriteTask(String path) {
        return () -> {
            Thread.sleep(1000);
            writeCsvFile(path);
            return true;
        };
    }

    private void waitThread() throws ExecutionException, InterruptedException {
        while (!futures.isEmpty()) {
            futures.poll().get();
        }
    }

    private String[] splitData(List<String> data, int index) {
        return data.get(index).split(";");
    }

    private void readCsvFile(String path) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            String[] titles = splitData(lines, 0);
            for (int i = 1; i < lines.size(); i++) {
                String[] values = splitData(lines, i);
                for (int j = 0; j < titles.length; j++) {
                    uniqueData.putIfAbsent(titles[j], new ConcurrentSkipListSet<>());
                    uniqueData.get(titles[j]).add(values[j]);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeCsvFile(String title) {
        System.out.println(uniqueData.get(title));
        try {
            Path path = Paths.get(title + ".txt");
            StringBuffer dataToFile = new StringBuffer(title).append(":\n");
            System.out.println(path);
            if (!Files.exists(path)) {
                Files.createFile(path);
            }

            uniqueData.get(title).forEach(elem -> {
                dataToFile.append(elem).append(";");
            });

            Files.write(path, Collections.singleton(dataToFile.toString()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
