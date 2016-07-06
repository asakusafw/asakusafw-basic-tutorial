package com.example.batch;

import com.asakusafw.vocabulary.batch.Batch;
import com.asakusafw.vocabulary.batch.BatchDescription;
import com.example.jobflow.CategorySummaryJob;

@Batch(name = "example.summarizeSales")
public class SummarizeBatch extends BatchDescription {

    @Override
    protected void describe() {
        run(CategorySummaryJob.class).soon();
    }

}
