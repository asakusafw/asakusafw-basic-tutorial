package com.example.jobflow;

import java.util.Arrays;
import java.util.List;

import com.example.modelgen.dmdl.csv.AbstractErrorRecordCsvOutputDescription;

public class ErrorRecordToCsv extends AbstractErrorRecordCsvOutputDescription {

    @Override
    public String getBasePath() {
        return "result/error";
    }

    @Override
    public String getResourcePattern() {
        return "${date}.csv";
    }

    @Override
    public List<String> getOrder() {
        return Arrays.asList("+file_name");
    }

    @Override
    public List<String> getDeletePatterns() {
        return Arrays.asList("*");
    }

}
