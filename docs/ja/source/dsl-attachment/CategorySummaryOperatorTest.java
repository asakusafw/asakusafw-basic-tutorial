package com.example.operator;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.asakusafw.runtime.value.Date;
import com.asakusafw.runtime.value.DateTime;
import com.example.modelgen.dmdl.model.ErrorRecord;
import com.example.modelgen.dmdl.model.ItemInfo;
import com.example.modelgen.dmdl.model.SalesDetail;

public class CategorySummaryOperatorTest {

    @Test
    public void setErrorMessage() {
        CategorySummaryOperator operator = new CategorySummaryOperatorImpl();

        ErrorRecord record = new ErrorRecord();
        String errorMessage = "エラー内容";
        operator.setErrorMessage(record, errorMessage);

        assertThat(record.getMessageAsString(), is(errorMessage));
    }

    @Test
    public void selectAvailableItem() {
        List<ItemInfo> candidates = new ArrayList<ItemInfo>();
        candidates.add(item("A", 1, 10));
        candidates.add(item("B", 11, 20));
        candidates.add(item("C", 21, 30));

        CategorySummaryOperator operator = new CategorySummaryOperatorImpl();
        ItemInfo item1 = operator.selectAvailableItem(candidates, sales(1));
        ItemInfo item5 = operator.selectAvailableItem(candidates, sales(5));
        ItemInfo item10 = operator.selectAvailableItem(candidates, sales(10));
        ItemInfo item15 = operator.selectAvailableItem(candidates, sales(11));
        ItemInfo item20 = operator.selectAvailableItem(candidates, sales(20));
        ItemInfo item30 = operator.selectAvailableItem(candidates, sales(30));
        ItemInfo item31 = operator.selectAvailableItem(candidates, sales(31));

        assertThat(item1.getCategoryCodeAsString(), is("A"));
        assertThat(item5.getCategoryCodeAsString(), is("A"));
        assertThat(item10.getCategoryCodeAsString(), is("A"));
        assertThat(item15.getCategoryCodeAsString(), is("B"));
        assertThat(item20.getCategoryCodeAsString(), is("B"));
        assertThat(item30.getCategoryCodeAsString(), is("C"));
        assertThat(item31, is(nullValue()));
    }

    private SalesDetail sales(int day) {
        SalesDetail object = new SalesDetail();
        object.setSalesDateTime(new DateTime(2011, 1, day, 0, 0, 0));
        return object;
    }

    private ItemInfo item(String categoryCode, int begin, int end) {
        ItemInfo object = new ItemInfo();
        object.setCategoryCodeAsString(categoryCode);
        object.setBeginDate(new Date(2011, 1, begin));
        object.setEndDate(new Date(2011, 1, end));
        return object;
    }
}
