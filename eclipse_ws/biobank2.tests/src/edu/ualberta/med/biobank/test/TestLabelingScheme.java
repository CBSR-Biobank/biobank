package edu.ualberta.med.biobank.test;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;

public class TestLabelingScheme extends TestDatabase {

    private static final Map<Integer, String> CBSR_ALPHA;
    static {
        Map<Integer, String> aMap = new HashMap<Integer, String>();
        aMap.put(0, "A");
        aMap.put(1, "B");
        aMap.put(2, "C");
        aMap.put(3, "D");
        aMap.put(4, "E");
        aMap.put(5, "F");
        aMap.put(6, "G");
        aMap.put(7, "H");
        aMap.put(8, "J");
        aMap.put(9, "K");
        aMap.put(10, "L");
        aMap.put(11, "M");
        aMap.put(12, "N");
        aMap.put(13, "P");
        aMap.put(14, "Q");
        aMap.put(15, "R");
        aMap.put(16, "S");
        aMap.put(17, "T");
        aMap.put(18, "U");
        aMap.put(19, "V");
        aMap.put(20, "W");
        aMap.put(21, "X");
        aMap.put(22, "Y");
        aMap.put(23, "Z");
        CBSR_ALPHA = Collections.unmodifiableMap(aMap);
    };

    @Override
    @Before
    public void setUp() throws Exception {
        r = new Random();
    }

    @Override
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testTwoCharNumeric() throws Exception {
        int totalRows = 5 + r.nextInt(5);
        RowColPos pos = new RowColPos();

        for (int i = 0; i < totalRows; ++i) {
            pos.row = i % totalRows;
            pos.col = i / totalRows;
            String result = LabelingScheme.rowColToTwoCharNumeric(pos,
                totalRows);
            Assert.assertEquals(result.length(), 2);
            Assert.assertEquals(new Integer(pos.row + 1).toString(),
                result.substring(1, 2));
            Assert.assertEquals(pos.col.toString(), result.substring(0, 1));
        }

        for (int i = 0; i < totalRows; ++i) {
            Integer row = i % totalRows + 1;
            Integer col = i / totalRows;
            String label = col.toString() + row.toString();
            pos = LabelingScheme.twoCharNumericToRowCol(label, totalRows);
            Assert.assertEquals(pos.row, new Integer(row - 1));
            Assert.assertEquals(pos.col, col);
        }
    }

    @Test
    public void testCBSR() throws Exception {
        int totalRows = 3 + r.nextInt(3);
        int totalCols = 5 + r.nextInt(5);

        String cbsrString;
        RowColPos pos = new RowColPos();

        for (int col = 0; col < totalCols; ++col) {
            for (int row = 0; row < totalRows; ++row) {
                pos.row = row;
                pos.col = col;
                cbsrString = LabelingScheme.rowColToCbsrTwoChar(pos, totalRows,
                    totalCols);
                Assert.assertTrue((cbsrString.length() == 2)
                    || (cbsrString.length() == 3));
                Assert
                    .assertEquals(
                        CBSR_ALPHA.get((row + col * totalRows)
                            % CBSR_ALPHA.size()), cbsrString.substring(1));
                Assert
                    .assertEquals(
                        CBSR_ALPHA.get((row + col * totalRows)
                            / CBSR_ALPHA.size()), cbsrString.substring(0, 1));
            }
        }

        for (int col = 0; col < totalCols; ++col) {
            for (int row = 0; row < totalRows; ++row) {
                cbsrString = CBSR_ALPHA.get((row + col * totalRows)
                    / CBSR_ALPHA.size())
                    + CBSR_ALPHA.get((row + col * totalRows)
                        % CBSR_ALPHA.size());
                pos = LabelingScheme.cbsrTwoCharToRowCol(cbsrString, totalRows,
                    totalCols, "test");
                Assert.assertEquals(new Integer(row), pos.row);
                Assert.assertEquals(new Integer(col), pos.col);
            }
        }

        try {
            pos = LabelingScheme.cbsrTwoCharToRowCol("aa", totalRows,
                totalCols, "test");
            Assert.fail("should not be allowed to use lower case characters");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testSbs() throws Exception {
        int totalRows;
        int totalCols;
        String posString;
        RowColPos pos = new RowColPos();

        for (int i = 1; i <= 2; ++i) {
            switch (i) {
            case 1:
                totalRows = 9;
                totalCols = 9;
                break;

            case 2:
            default:
                totalRows = 8;
                totalCols = 12;
                break;
            }

            for (int col = 0; col < totalCols; ++col) {
                for (int row = 0; row < totalRows; ++row) {
                    pos.row = row;
                    pos.col = col;
                    posString = LabelingScheme.rowColToSbs(pos);
                    if (col >= 9) {
                        Assert.assertTrue(posString.length() == 3);
                    } else {
                        Assert.assertTrue(posString.length() == 2);
                    }
                    Assert.assertEquals(CBSR_ALPHA.get(row).charAt(0),
                        posString.charAt(0));
                    Assert.assertEquals(col + 1,
                        Integer.valueOf(posString.substring(1)).intValue());
                }
            }

            for (int col = 0; col < totalCols; ++col) {
                for (int row = 0; row < totalRows; ++row) {
                    pos = LabelingScheme.sbsToRowCol(String.format("%s%02d",
                        CBSR_ALPHA.get(row), col + 1));
                    Assert.assertEquals(row, pos.row.intValue());
                    Assert.assertEquals(col, pos.col.intValue());
                }
            }
        }
    }

}
