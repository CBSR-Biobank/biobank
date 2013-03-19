package edu.ualberta.med.biobank.test.action;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction;
import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction.ContainerLabelingSchemeInfo;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.type.LabelingLayout;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.SbsLabeling;

public class TestContainerLabelingScheme extends TestAction {

    private static Logger log = LoggerFactory.getLogger(TestContainerLabelingScheme.class.getName());

    private static final Map<Integer, String> ALPHA;
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
        aMap.put(8, "I");
        aMap.put(9, "J");
        aMap.put(10, "K");
        aMap.put(11, "L");
        aMap.put(12, "M");
        aMap.put(13, "N");
        aMap.put(14, "O");
        aMap.put(15, "P");
        aMap.put(16, "Q");
        aMap.put(17, "R");
        aMap.put(18, "S");
        aMap.put(19, "T");
        aMap.put(20, "U");
        aMap.put(21, "V");
        aMap.put(22, "W");
        aMap.put(23, "X");
        aMap.put(24, "Y");
        aMap.put(25, "Z");
        ALPHA = Collections.unmodifiableMap(aMap);
    };

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

    private static final Map<Integer, String> SBS_ALPHA;
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
        aMap.put(8, "I");
        aMap.put(9, "J");
        aMap.put(10, "K");
        aMap.put(11, "L");
        aMap.put(12, "M");
        aMap.put(13, "N");
        aMap.put(14, "O");
        aMap.put(15, "P");
        SBS_ALPHA = Collections.unmodifiableMap(aMap);
    };

    private static final Map<Integer, String> DEWAR_ALPHA;
    static {
        Map<Integer, String> aMap = new HashMap<Integer, String>();
        aMap.put(0, "A");
        aMap.put(1, "B");
        aMap.put(2, "C");
        aMap.put(3, "D");
        DEWAR_ALPHA = Collections.unmodifiableMap(aMap);
    };

    @Test
    public void checkGetAction() {
        session.beginTransaction();
        ContainerLabelingScheme scheme = factory.createContainerLabelingScheme();
        session.getTransaction().commit();

        ContainerLabelingSchemeInfo info =
            exec(new ContainerLabelingSchemeGetInfoAction(scheme.getName()));

        Assert.assertEquals(scheme.getName(), info.getLabelingScheme().getName());
        Assert.assertEquals(scheme.getMinChars(), info.getLabelingScheme().getMinChars());
        Assert.assertEquals(scheme.getMaxChars(), info.getLabelingScheme().getMaxChars());
        Assert.assertEquals(scheme.getMaxCapacity(),
            info.getLabelingScheme().getMaxCapacity());
    }

    @Test
    public void posToLabelTwoCharNumeric() throws Exception {
        // can only label between "01" to "99"
        int totalRows = 9;
        int totalCols = 10;

        for (LabelingLayout labelingLayout : LabelingLayout.values()) {
            for (int row = 0; row < totalRows; ++row) {
                for (int col = 0; col < totalCols; ++col) {
                    RowColPos pos = new RowColPos(row, col);
                    String result = ContainerLabelingScheme.rowColToTwoCharNumeric(
                        pos, totalRows, totalCols, labelingLayout);

                    // log.debug("pos:{}, result:{}", pos, result);

                    Assert.assertEquals(result.length(), 2);
                    if (labelingLayout.equals(LabelingLayout.VERTICAL)) {
                        Assert.assertEquals(String.format("%02d", col * totalRows + row + 1), result);
                    } else {
                        Assert.assertEquals(String.format("%02d", row * totalCols + col + 1), result);
                    }
                }
            }
        }
    }

    @Test
    public void posToLabelTwoCharNumericBadParams() throws Exception {
        RowColPos pos = new RowColPos(10, 10);

        try {
            ContainerLabelingScheme.rowColToTwoCharNumeric(pos, 10, 10, LabelingLayout.VERTICAL);
            Assert.fail("should not allow labeling more than 2 digits");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void labelToPosTwoCharNumeric() throws Exception {
        // can only label between "01" to "99"
        int totalRows = 9;
        int totalCols = 10;

        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("2 char numeric")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);

        for (LabelingLayout labelingLayout : LabelingLayout.values()) {
            for (int i = 0, n = totalRows * totalCols; i < n; ++i) {
                String label = String.format("%02d", i + 1);

                RowColPos pos = labelingScheme.twoCharNumericToRowCol(
                    label, totalRows, totalCols, labelingLayout);

                // log.debug("index:{}, layout:{}, label:{}, pos:{}", new Object[] {
                // i, labelingLayout, label, pos });

                if (labelingLayout.equals(LabelingLayout.VERTICAL)) {
                    Assert.assertEquals(pos.getRow().intValue(), i % totalRows);
                    Assert.assertEquals(pos.getCol().intValue(), i / totalRows);
                } else {
                    Assert.assertEquals(pos.getRow().intValue(), i / totalCols);
                    Assert.assertEquals(pos.getCol().intValue(), i % totalCols);
                }
            }
        }
    }

    @Test
    public void labelToPosTwoCharNumericBadParams() throws Exception {
        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("2 char numeric")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);

        try {
            labelingScheme.twoCharNumericToRowCol("100", 10, 10, LabelingLayout.VERTICAL);
            Assert.fail("should not allow a labeling of length greater than 2");
        } catch (IllegalArgumentException e) {
            // do nothing
        }

        try {
            labelingScheme.twoCharNumericToRowCol("0", 10, 10, LabelingLayout.VERTICAL);
            Assert.fail("should not allow a labeling of length less than 2");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void posToLabelTwoCharAlpha() throws Exception {
        // can only label between "01" to "99"
        int totalRows = 9;
        int totalCols = 10;
        String label;

        for (LabelingLayout labelingLayout : LabelingLayout.values()) {
            for (int col = 0; col < totalCols; ++col) {
                for (int row = 0; row < totalRows; ++row) {
                    RowColPos pos = new RowColPos(row, col);
                    label = ContainerLabelingScheme.rowColToTwoChar(
                        pos, totalRows, totalCols, labelingLayout);

                    Assert.assertEquals(label.length(), 2);
                    if (labelingLayout.equals(LabelingLayout.VERTICAL)) {
                        Assert.assertEquals(ALPHA.get((col * totalRows + row) % ALPHA.size()),
                            label.substring(1));
                        Assert.assertEquals(ALPHA.get((col * totalRows + row) / ALPHA.size()),
                            label.substring(0, 1));
                    } else {
                        Assert.assertEquals(ALPHA.get((row * totalCols + col) % ALPHA.size()),
                            label.substring(1));
                        Assert.assertEquals(ALPHA.get((row * totalCols + col) / ALPHA.size()),
                            label.substring(0, 1));
                    }
                }
            }
        }
    }

    @Test
    public void posToLabelTwoCharAlphaBadPos() throws Exception {
        try {
            ContainerLabelingScheme.rowColToTwoChar(
                new RowColPos(10, 10), 10, 10, LabelingLayout.VERTICAL);
            Assert.fail("should not allow labeling more than 2 digits");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void labelToPosTwoCharAlpha() throws Exception {
        // can only label between "01" to "99"
        int totalRows = 9;
        int totalCols = 10;
        String label;

        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("2 char alphabetic")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);

        for (LabelingLayout labelingLayout : LabelingLayout.values()) {
            for (int i = 0, n = totalRows * totalCols; i < n; ++i) {
                label = ALPHA.get(i / ALPHA.size()) + ALPHA.get(i % ALPHA.size());

                RowColPos pos = labelingScheme.twoCharToRowCol(
                    label, totalRows, totalCols, "test", labelingLayout);
                if (labelingLayout.equals(LabelingLayout.VERTICAL)) {
                    Assert.assertEquals(i % totalRows, pos.getRow().intValue());
                    Assert.assertEquals(i / totalRows, pos.getCol().intValue());
                } else {
                    Assert.assertEquals(i / totalCols, pos.getRow().intValue());
                    Assert.assertEquals(i % totalCols, pos.getCol().intValue());
                }
            }
        }
    }

    @Test
    public void labelToPosTwoCharAlphaBadLabel() throws Exception {
        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("2 char alphabetic")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);
        try {
            labelingScheme.twoCharToRowCol("100", 10, 10, "test", LabelingLayout.VERTICAL);
            Assert.fail("Should not be allowed to go out of bounds.");
        } catch (IllegalArgumentException e) {
            // do nothing
        }

        try {
            labelingScheme.cbsrTwoCharToRowCol("aa", 10, 10, "test", LabelingLayout.VERTICAL);
            Assert.fail("should not be allowed to use lower case characters");
        } catch (IllegalArgumentException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void labelToPosCbsrTwoChar() throws Exception {
        // can only label between "01" to "99"
        int totalRows = 9;
        int totalCols = 10;
        String label;

        for (LabelingLayout labelingLayout : LabelingLayout.values()) {
            for (int col = 0; col < totalCols; ++col) {
                for (int row = 0; row < totalRows; ++row) {
                    RowColPos pos = new RowColPos(row, col);
                    label = ContainerLabelingScheme.rowColToCbsrTwoChar(
                        pos, totalRows, totalCols, labelingLayout);
                    Assert.assertEquals(label.length(), 2);
                    if (labelingLayout.equals(LabelingLayout.VERTICAL)) {
                        Assert.assertEquals(
                            CBSR_ALPHA.get((row + (col * totalRows)) % CBSR_ALPHA.size()),
                            label.substring(1));
                        Assert.assertEquals(
                            CBSR_ALPHA.get((row + (col * totalRows)) / CBSR_ALPHA.size()),
                            label.substring(0, 1));
                    } else {
                        Assert.assertEquals(
                            CBSR_ALPHA.get((row * totalCols + col) % CBSR_ALPHA.size()),
                            label.substring(1));
                        Assert.assertEquals(
                            CBSR_ALPHA.get((row * totalCols + col) / CBSR_ALPHA.size()),
                            label.substring(0, 1));

                    }
                }
            }
        }
    }

    @Test
    public void posToLabelCbsrTwoCharBadPos() throws Exception {
        try {
            ContainerLabelingScheme.rowColToCbsrTwoChar(
                new RowColPos(10, 10), 10, 10, LabelingLayout.VERTICAL);
            Assert.fail("should not allow labeling more than 2 digits");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    @Test
    public void posToLabelCbsrTwoChar() {
        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("CBSR 2 char alphabetic")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);

        // can only label between "01" to "99"
        int totalRows = 9;
        int totalCols = 10;
        String label;

        for (LabelingLayout labelingLayout : LabelingLayout.values()) {
            for (int i = 0, n = totalRows * totalCols; i < n; ++i) {
                label = CBSR_ALPHA.get(i / CBSR_ALPHA.size())
                    + CBSR_ALPHA.get(i % CBSR_ALPHA.size());
                RowColPos pos = labelingScheme.cbsrTwoCharToRowCol(
                    label, totalRows, totalCols, "test", labelingLayout);
                if (labelingLayout.equals(LabelingLayout.VERTICAL)) {
                    Assert.assertEquals(i % totalRows, pos.getRow().intValue());
                    Assert.assertEquals(i / totalRows, pos.getCol().intValue());
                } else {
                    Assert.assertEquals(i / totalCols, pos.getRow().intValue());
                    Assert.assertEquals(i % totalCols, pos.getCol().intValue());
                }
            }
        }
    }

    @Test
    public void labelToPosCbsrTwoCharBadLabel() {
        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("CBSR 2 char alphabetic")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);
        try {
            labelingScheme.cbsrTwoCharToRowCol("100", 10, 10, "test", LabelingLayout.VERTICAL);
            Assert.fail("Should not be allowed to go out of bounds.");
        } catch (IllegalArgumentException e) {
            // do nothing
        }

        try {
            labelingScheme.cbsrTwoCharToRowCol(
                "aa", 10, 10, "test", LabelingLayout.VERTICAL);
            Assert.fail("should not be allowed to use lower case characters");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    private static class Dimensions {
        int totalRows;
        int totalCols;

        Dimensions(int totalRows, int totalCols) {
            this.totalRows = totalRows;
            this.totalCols = totalCols;
        }
    }

    @Test
    public void testSbs() throws Exception {
        String posString;

        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("SBS Standard")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);

        Dimensions[] containerDimensions = new Dimensions[] {
            new Dimensions(9, 9),
            new Dimensions(10, 10),
            new Dimensions(8, 12)
        };

        for (Dimensions dimensions : containerDimensions) {
            for (int col = 0; col < dimensions.totalCols; ++col) {
                for (int row = 0; row < dimensions.totalRows; ++row) {
                    RowColPos pos = new RowColPos(row, col);
                    posString = ContainerLabelingSchemeWrapper.rowColToSbs(pos);
                    if (col >= 9) {
                        Assert.assertTrue(posString.length() == 3);
                    } else {
                        Assert.assertTrue(posString.length() == 2);
                    }
                    Assert.assertEquals(SBS_ALPHA.get(row).charAt(0), posString.charAt(0));
                    Assert.assertEquals(col + 1, Integer.valueOf(posString.substring(1)).intValue());
                }
            }

            for (int col = 0; col < dimensions.totalCols; ++col) {
                for (int row = 0; row < dimensions.totalRows; ++row) {
                    RowColPos pos = SbsLabeling.toRowCol(
                        String.format("%s%02d", SBS_ALPHA.get(row), col + 1));
                    Assert.assertEquals(row, pos.getRow().intValue());
                    Assert.assertEquals(col, pos.getCol().intValue());
                }
            }
        }
    }

    @Test
    public void sbsBounds() throws Exception {
        try {
            SbsLabeling.fromRowCol(new RowColPos(16, 23));
            Assert.fail("SBS cannot label more than 16 rows");
        } catch (IllegalArgumentException e) {
            // do nothing
        }

        try {
            SbsLabeling.fromRowCol(new RowColPos(15, 24));
            Assert.fail("SBS cannot label more than 16 rows");
        } catch (IllegalArgumentException e) {
            // do nothing
        }

        try {
            SbsLabeling.toRowCol("R12");
            Assert.fail("invalid row in label");
        } catch (IllegalArgumentException e) {
            // do nothing
        }

        try {
            SbsLabeling.toRowCol("P25");
            Assert.fail("invalid column in label");
        } catch (IllegalArgumentException e) {
            // do nothing
        }
    }

    private static final int DEWAR_MAX_ROWS = 2;

    private static final int DEWAR_MAX_COLS = 2;

    @Test
    public void testDewar() throws Exception {
        String posString;

        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("Dewar")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);

        for (int row = 0; row < DEWAR_MAX_ROWS; ++row) {
            for (int col = 0; col < DEWAR_MAX_COLS; ++col) {
                RowColPos pos = new RowColPos(row, col);
                posString = ContainerLabelingSchemeWrapper.rowColToDewar(pos,
                    DEWAR_MAX_COLS);
                Assert.assertEquals(
                    DEWAR_ALPHA.get((row * DEWAR_MAX_COLS) + col).charAt(0),
                    posString.charAt(0));
                Assert.assertEquals(
                    DEWAR_ALPHA.get((row * DEWAR_MAX_COLS) + col).charAt(0),
                    posString.charAt(1));
            }
        }

        for (int row = 0; row < DEWAR_MAX_ROWS; ++row) {
            for (int col = 0; col < DEWAR_MAX_COLS; ++col) {
                String label = DEWAR_ALPHA.get((row * DEWAR_MAX_COLS) + col);
                label += label;
                RowColPos pos = labelingScheme.dewarToRowCol(label, DEWAR_MAX_COLS);
                Assert.assertEquals(row, pos.getRow().intValue());
                Assert.assertEquals(col, pos.getCol().intValue());
            }
        }

    }

    @Test
    public void testBounds() throws Exception {
        ContainerLabelingScheme labelingScheme = exec(
            new ContainerLabelingSchemeGetInfoAction("SBS Standard")).getLabelingScheme();
        Assert.assertNotNull(labelingScheme);

        if (ContainerLabelingSchemeWrapper.checkBounds(appService,
            ContainerLabelingSchemeWrapper.SCHEME_DEWAR, 9, 1) == true)
            Assert.fail("Should be out of bounds");

        // test canlabel
        Capacity cap = new Capacity();
        cap.setColCapacity(4);
        cap.setRowCapacity(2);

        Assert.assertTrue(ContainerLabelingSchemeWrapper.canLabel(
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA)
                .getWrappedObject(), cap));

    }

    @Test
    public void testGetPosition() throws Exception {
        String output = ContainerLabelingSchemeWrapper.getPositionString(
            new RowColPos(8, 0),
            ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA, 9, 9);
        Assert.assertEquals("AI", output);

        Assert.assertEquals(new RowColPos(8, 0), ContainerLabelingSchemeWrapper
            .getRowColFromPositionString(appService, "AI",
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA, 9, 9));

        output = ContainerLabelingSchemeWrapper.getPositionString(
            new RowColPos(8, 0),
            ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_NUMERIC, 9, 9);
        Assert.assertEquals("09", output);

        Assert.assertEquals(new RowColPos(8, 0), ContainerLabelingSchemeWrapper
            .getRowColFromPositionString(appService, "09",
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_NUMERIC, 9, 9));

        output = ContainerLabelingSchemeWrapper.getPositionString(
            new RowColPos(8, 0),
            ContainerLabelingSchemeWrapper.SCHEME_CBSR_2_CHAR_ALPHA, 9, 9);
        Assert.assertEquals("AJ", output);

        Assert.assertEquals(new RowColPos(8, 0), ContainerLabelingSchemeWrapper
            .getRowColFromPositionString(appService, "AJ",
                ContainerLabelingSchemeWrapper.SCHEME_CBSR_2_CHAR_ALPHA, 9, 9));

        output = ContainerLabelingSchemeWrapper.getPositionString(
            new RowColPos(8, 0), ContainerLabelingSchemeWrapper.SCHEME_DEWAR,
            9, 1);
        Assert.assertEquals("II", output);

        Assert.assertEquals(new RowColPos(8, 0), ContainerLabelingSchemeWrapper
            .getRowColFromPositionString(appService, "II",
                ContainerLabelingSchemeWrapper.SCHEME_DEWAR, 9, 1));

        output = ContainerLabelingSchemeWrapper.getPositionString(
            new RowColPos(8, 0), ContainerLabelingSchemeWrapper.SCHEME_SBS, 9,
            9);
        Assert.assertEquals("I1", output);

        Assert.assertEquals(new RowColPos(8, 0), ContainerLabelingSchemeWrapper
            .getRowColFromPositionString(appService, "I1",
                ContainerLabelingSchemeWrapper.SCHEME_SBS, 9, 9));

        output = ContainerLabelingSchemeWrapper.getPositionString(
            new RowColPos(8, 0),
            ContainerLabelingSchemeWrapper.SCHEME_CBSR_SBS, 9, 9);
        Assert.assertEquals("J1", output);

        Assert.assertEquals(new RowColPos(8, 0), ContainerLabelingSchemeWrapper
            .getRowColFromPositionString(appService, "J1",
                ContainerLabelingSchemeWrapper.SCHEME_CBSR_SBS, 9, 9));
    }

    @Test
    public void testLabelLength() throws Exception {
        List<Integer> lengths = ContainerLabelingSchemeWrapper
            .getPossibleLabelLength(appService);
        for (Integer i : lengths)
            Assert.assertTrue(i <= 3);
    }

    @Test
    public void testCBSRSbs() throws Exception {
        RowColPos pos = new RowColPos(0, 8);
        Assert.assertEquals(pos,
            ContainerLabelingSchemeWrapper.cbsrSbsToRowCol(appService, "A9"));
    }

}
