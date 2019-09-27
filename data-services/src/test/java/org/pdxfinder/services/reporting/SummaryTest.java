package org.pdxfinder.services.reporting;

import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public class SummaryTest {
    @Before public void init() {
    }

    @Test
    public void run() {
        Summary.exportSummary();
    }

    @Test
    public void returnsDataframe() {
        int[] ar1 = {1, 2};
        DoubleColumn c1 = DoubleColumn.create("Column1", ar1);
        Table t = Table.create("test").addColumns(c1);

        Assert.assertEquals(t, Summary.exportSummary());
    }
}
