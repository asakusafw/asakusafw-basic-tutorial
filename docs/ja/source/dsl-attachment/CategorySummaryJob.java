package com.example.jobflow;

import com.asakusafw.vocabulary.flow.Export;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.Import;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.JobFlow;
import com.asakusafw.vocabulary.flow.Out;
import com.example.flowpart.CategorySummaryFlowPartFactory;
import com.example.flowpart.CategorySummaryFlowPartFactory.CategorySummaryFlowPart;
import com.example.modelgen.dmdl.model.CategorySummary;
import com.example.modelgen.dmdl.model.ErrorRecord;
import com.example.modelgen.dmdl.model.ItemInfo;
import com.example.modelgen.dmdl.model.SalesDetail;
import com.example.modelgen.dmdl.model.StoreInfo;

@JobFlow(name = "byCategory")
public class CategorySummaryJob extends FlowDescription {

    final In<SalesDetail> salesDetail;
    final In<StoreInfo> storeInfo;
    final In<ItemInfo> itemInfo;
    final Out<CategorySummary> categorySummary;
    final Out<ErrorRecord> errorRecord;
    
    public CategorySummaryJob(
            @Import(name = "salesDetail", description = SalesDetailFromCsv.class)
            In<SalesDetail> salesDetail,
            @Import(name = "storeInfo", description = StoreInfoFromCsv.class)
            In<StoreInfo> storeInfo,
            @Import(name = "itemInfo", description = ItemInfoFromCsv.class)
            In<ItemInfo> itemInfo,
            @Export(name = "categorySummary", description = CategorySummaryToCsv.class)
            Out<CategorySummary> categorySummary,
            @Export(name = "errorRecord", description = ErrorRecordToCsv.class)
            Out<ErrorRecord> errorRecord) {
        this.salesDetail = salesDetail;
        this.storeInfo = storeInfo;
        this.itemInfo = itemInfo;
        this.categorySummary = categorySummary;
        this.errorRecord = errorRecord;
    }
    
    @Override
    protected void describe() {
        CategorySummaryFlowPartFactory flowPartFactory = new CategorySummaryFlowPartFactory();

        CategorySummaryFlowPart flowPart = flowPartFactory.create(salesDetail, storeInfo, itemInfo);
        categorySummary.add(flowPart.categorySummary);
        errorRecord.add(flowPart.errorRecord);
    }

}
