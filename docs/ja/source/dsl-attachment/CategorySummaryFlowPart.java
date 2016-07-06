package com.example.flowpart;

import com.asakusafw.vocabulary.flow.FlowDescription;
import com.asakusafw.vocabulary.flow.FlowPart;
import com.asakusafw.vocabulary.flow.In;
import com.asakusafw.vocabulary.flow.Out;
import com.asakusafw.vocabulary.flow.util.CoreOperatorFactory;
import com.example.modelgen.dmdl.model.CategorySummary;
import com.example.modelgen.dmdl.model.ErrorRecord;
import com.example.modelgen.dmdl.model.ItemInfo;
import com.example.modelgen.dmdl.model.SalesDetail;
import com.example.modelgen.dmdl.model.StoreInfo;
import com.example.operator.CategorySummaryOperatorFactory;
import com.example.operator.CategorySummaryOperatorFactory.CheckStore;
import com.example.operator.CategorySummaryOperatorFactory.JoinItemInfo;
import com.example.operator.CategorySummaryOperatorFactory.SetErrorMessage;
import com.example.operator.CategorySummaryOperatorFactory.SummarizeByCategory;

@FlowPart
public class CategorySummaryFlowPart extends FlowDescription {

    final In<SalesDetail> salesDetail;
    final In<StoreInfo> storeInfo;
    final In<ItemInfo> itemInfo;
    final Out<CategorySummary> categorySummary;
    final Out<ErrorRecord> errorRecord;

    public CategorySummaryFlowPart(
            In<SalesDetail> salesDetail,
            In<StoreInfo> storeInfo,
            In<ItemInfo> itemInfo,
            Out<CategorySummary> categorySummary,
            Out<ErrorRecord> errorRecord) {
        this.salesDetail = salesDetail;
        this.storeInfo = storeInfo;
        this.itemInfo = itemInfo;
        this.categorySummary = categorySummary;
        this.errorRecord = errorRecord;
    }

    @Override
    protected void describe() {
        CoreOperatorFactory core = new CoreOperatorFactory();
        CategorySummaryOperatorFactory operators = new CategorySummaryOperatorFactory();

        // 1. 店舗マスタ結合
        CheckStore checkStore = operators.checkStore(storeInfo, salesDetail);

        // 2. 商品マスタ結合
        JoinItemInfo joinItemInfo = operators.joinItemInfo(itemInfo, checkStore.found);

        // 3. カテゴリ別集計
        SummarizeByCategory summarize = operators.summarizeByCategory(joinItemInfo.joined);

        // カテゴリ別売上集計の出力
        categorySummary.add(summarize.out);

        // 4.1. エラー情報編集（店舗）
        SetErrorMessage unknownStore = operators.setErrorMessage(
            core.restructure(checkStore.missed, ErrorRecord.class), "店舗不明");
        // エラー情報の出力（店舗）
        errorRecord.add(unknownStore.out);

        // 4.2. エラー情報編集（商品）
        SetErrorMessage unknownItem = operators.setErrorMessage(
            core.restructure(joinItemInfo.missed, ErrorRecord.class), "商品不明");
        // エラー情報の出力（商品）
        errorRecord.add(unknownItem.out);
    }

}
