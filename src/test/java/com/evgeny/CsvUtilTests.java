package com.evgeny;

import com.evgeny.util.CsvUtil;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;

public class CsvUtilTests {

    @Test
    public void csvUtilExecuteTest() {
        CsvUtil csvUtil = new CsvUtil(Arrays.asList("input1.csv", "input2.csv"));
        try {
            csvUtil.execute();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
