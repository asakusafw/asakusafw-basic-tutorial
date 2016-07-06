package com.example.jobflow;

import java.util.Arrays;
import java.util.List;

import com.example.modelgen.dmdl.csv.AbstractCategorySummaryCsvOutputDescription;

public class CategorySummaryToCsv extends AbstractCategorySummaryCsvOutputDescription {

    @Override
    public String getBasePath() {
        return "result/category";
    }

    @Override
    public String getResourcePattern() {
        return "result.csv";
    }

    @Override
    public List<String> getOrder() {
        return Arrays.asList("-selling_price_total");
    }

    @Override
    public List<String> getDeletePatterns() {
        return Arrays.asList("*");
    }

}
