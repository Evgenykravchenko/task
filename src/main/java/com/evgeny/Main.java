package com.evgeny;

import com.evgeny.util.CsvUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class Main {

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        List<String> csvFiles = new ArrayList<>();
        try(BufferedReader consoleReader = new BufferedReader(new InputStreamReader(System.in))) {
            String currentCsv;
            while (!(currentCsv = consoleReader.readLine()).isEmpty()) {
                if (!Files.exists(Paths.get(currentCsv))) {
                    System.err.println("File doesn't exists!");
                    continue;
                }
                csvFiles.add(currentCsv);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        CsvUtil csvUtil = new CsvUtil(csvFiles);
        csvUtil.execute();
    }
}
