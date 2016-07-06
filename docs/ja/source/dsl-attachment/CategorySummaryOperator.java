package com.example.operator;

import java.util.List;

import com.asakusafw.runtime.value.Date;
import com.asakusafw.runtime.value.DateTime;
import com.asakusafw.runtime.value.DateUtil;
import com.asakusafw.vocabulary.model.Key;
import com.asakusafw.vocabulary.operator.MasterCheck;
import com.asakusafw.vocabulary.operator.MasterJoin;
import com.asakusafw.vocabulary.operator.MasterSelection;
import com.asakusafw.vocabulary.operator.Summarize;
import com.asakusafw.vocabulary.operator.Update;
import com.example.modelgen.dmdl.model.CategorySummary;
import com.example.modelgen.dmdl.model.ErrorRecord;
import com.example.modelgen.dmdl.model.ItemInfo;
import com.example.modelgen.dmdl.model.JoinedSalesInfo;
import com.example.modelgen.dmdl.model.SalesDetail;
import com.example.modelgen.dmdl.model.StoreInfo;

public abstract class CategorySummaryOperator {

    @MasterCheck
    public abstract boolean checkStore(@Key(group = "store_code") StoreInfo info,
            @Key(group = "store_code") SalesDetail sales);

    @MasterJoin(selection = "selectAvailableItem")
    public abstract JoinedSalesInfo joinItemInfo(ItemInfo info, SalesDetail sales);

    private final Date dateBuffer = new Date();

    @MasterSelection
    public ItemInfo selectAvailableItem(List<ItemInfo> candidates, SalesDetail sales) {
        DateTime dateTime = sales.getSalesDateTime();
        dateBuffer.setElapsedDays(DateUtil.getDayFromDate(dateTime.getYear(), dateTime.getMonth(), dateTime.getDay()));
        for (ItemInfo item : candidates) {
            if (item.getBeginDate().compareTo(dateBuffer) <= 0 && dateBuffer.compareTo(item.getEndDate()) <= 0) {
                return item;
            }
        }
        return null;
    }

    @Summarize
    public abstract CategorySummary summarizeByCategory(JoinedSalesInfo info);

    @Update
    public void setErrorMessage(ErrorRecord record, String message) {
        record.setMessageAsString(message);
    }

}
