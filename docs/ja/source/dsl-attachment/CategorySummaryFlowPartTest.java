package com.example.flowpart;

import org.junit.Test;

import com.asakusafw.testdriver.FlowPartTester;
import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.example.modelgen.dmdl.model.CategorySummary;
import com.example.modelgen.dmdl.model.ErrorRecord;
import com.example.modelgen.dmdl.model.ItemInfo;
import com.example.modelgen.dmdl.model.SalesDetail;
import com.example.modelgen.dmdl.model.StoreInfo;

public class CategorySummaryFlowPartTest {

    @Test
    public void simple() {
        run("simple.xls");
    }

    @Test
    public void summarize() {
        run("summarize.xls");
    }

    @Test
    public void available_date() {
        run("available_range.xls");
    }

    @Test
    public void invalid_store() {
        run("invalid_store.xls");
    }

    private void run(String dataSet) {
        FlowPartTester tester = new FlowPartTester(getClass());

        In<StoreInfo> storeInfo = tester.input("storeInfo", StoreInfo.class)
                .prepare("masters.xls#store_info");
        In<ItemInfo> itemInfo = tester.input("itemInfo", ItemInfo.class)
                .prepare("masters.xls#item_info");
        In<SalesDetail> salesDetail = tester.input("salesDetail", SalesDetail.class)
                .prepare(dataSet + "#sales_detail");

        Out<CategorySummary> categorySummary = tester.output("categorySummary", CategorySummary.class)
                .verify(dataSet + "#result", dataSet + "#result_rule");
        Out<ErrorRecord> errorRecord = tester.output("errorRecord", ErrorRecord.class)
                .dumpActual("build/dump/error_" + dataSet);

        FlowDescription flowPart = new CategorySummaryFlowPart(
                salesDetail, storeInfo, itemInfo, categorySummary, errorRecord);
        tester.runTest(flowPart);
    }

}
