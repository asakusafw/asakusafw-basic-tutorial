package com.example.jobflow;

import com.example.modelgen.dmdl.csv.AbstractSalesDetailCsvInputDescription;

public class SalesDetailFromCsv extends AbstractSalesDetailCsvInputDescription {

    @Override
    public String getBasePath() {
        return "sales";
    }

    @Override
    public String getResourcePattern() {
        return "**/${date}.csv";
    }

    @Override
    public DataSize getDataSize() {
        return DataSize.LARGE;
    }

}
