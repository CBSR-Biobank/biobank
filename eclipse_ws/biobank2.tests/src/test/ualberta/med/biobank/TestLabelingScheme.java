package test.ualberta.med.biobank;

import org.junit.Test;

import test.ualberta.med.biobank.wrappers.TestDatabase;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;

public class TestLabelingScheme extends TestDatabase {

    @Test
    public void testTwoCharNumeric() throws Exception {
        int totalRows = 6;
        RowColPos rcp = new RowColPos(5, 0);
        System.out.println("Two char numeric: " + rcp.row + ":" + rcp.col
            + "=>" + LabelingScheme.rowColToTwoCharNumeric(rcp, totalRows));

        String label = "10";
        rcp = LabelingScheme.twoCharNumericToRowCol(label, totalRows);
        System.out.println("Two char numeric: " + label + "=>" + rcp.row + ":"
            + rcp.col);
    }

    @Test
    public void testCBSR() throws Exception {
        // In a 3*5 container, 1:4=AL
        int totalRows = 3;
        int totalCols = 5;

        String cbsrString = "AL";
        RowColPos rcp = LabelingScheme.cbsrTwoCharToRowCol(cbsrString,
            totalRows, totalCols, "test");
        System.out.println("CBSR: " + cbsrString + "=>" + rcp.row + ":"
            + rcp.col + " in a " + totalRows + "*" + totalCols + " container");

        rcp = new RowColPos(1, 3);
        System.out.println("CBSR: " + rcp.row + ":" + rcp.col + "=>"
            + LabelingScheme.rowColToCbsrTwoChar(rcp, totalRows, totalCols)
            + " in a " + totalRows + "*" + totalCols + " container");
    }

    @Test
    public void testSbs() throws Exception {
        String sample = "D12";
        RowColPos rcp = LabelingScheme.sbsToRowCol(sample);
        System.out.println("SBS: " + sample + "=>" + rcp.row + ":" + rcp.col
            + " in pallet");

        rcp.row = 2;
        rcp.col = 4;
        String pos = LabelingScheme.rowColToSbs(rcp);
        System.out.println("SBS: " + rcp.row + ":" + rcp.col + "=>" + pos
            + " in pallet");

    }

}
