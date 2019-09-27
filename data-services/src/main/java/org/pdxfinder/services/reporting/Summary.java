package org.pdxfinder.services.reporting;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.Table;

public class Summary {
    public static Table exportSummary() {
        int[] ar1 = {1, 2};
        DoubleColumn c1 = DoubleColumn.create("Column1", ar1);
        Table t = Table.create("test").addColumns(c1);

        return t;
    }
}
