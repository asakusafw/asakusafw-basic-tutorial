package com.example.jobflow;

import com.example.modelgen.dmdl.csv.AbstractStoreInfoCsvInputDescription;

public class StoreInfoFromCsv extends AbstractStoreInfoCsvInputDescription {

    @Override
    public String getBasePath() {
        return "master";
    }

    @Override
    public String getResourcePattern() {
        return "store_info.csv";
    }

    @Override
    public DataSize getDataSize() {
        return DataSize.TINY;
    }

}
