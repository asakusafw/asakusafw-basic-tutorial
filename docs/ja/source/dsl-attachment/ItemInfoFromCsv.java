package com.example.jobflow;

import com.example.modelgen.dmdl.csv.AbstractItemInfoCsvInputDescription;

public class ItemInfoFromCsv extends AbstractItemInfoCsvInputDescription {

    @Override
    public String getBasePath() {
        return "master";
    }

    @Override
    public String getResourcePattern() {
        return "item_info.csv";
    }

    @Override
    public DataSize getDataSize() {
        return DataSize.LARGE;
    }

}
