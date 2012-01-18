package edu.ualberta.med.biobank.test.wrappers;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.CapacityWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.ContainerHelper;
import edu.ualberta.med.biobank.test.internal.ContainerTypeHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import gov.nih.nci.system.applicationservice.ApplicationException;

@Deprecated
public class TestContainerLabelingScheme extends TestDatabase {

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

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
    }

    @Test
    public void TestGetAllLabelingSchemes() throws Exception {
        ContainerLabelingSchemeWrapper.getAllLabelingSchemesMap(appService)
            .values();
    }

    @Test
    public void testTwoCharNumeric() throws Exception {
        int totalRows = 5 + r.nextInt(5);

        for (int i = 0; i < totalRows; ++i) {
            Integer row = i % totalRows;
            Integer col = i / totalRows;
            RowColPos pos = new RowColPos(row, col);
            String result = ContainerLabelingSchemeWrapper
                .rowColToTwoCharNumeric(pos, totalRows);
            Assert.assertEquals(result.length(), 2);
            Assert.assertEquals(new Integer(pos.getRow() + 1).toString(),
                result.substring(1, 2));
            Assert
                .assertEquals(pos.getCol().toString(), result.substring(0, 1));
        }

        for (int i = 0; i < totalRows; ++i) {
            Integer row = (i % totalRows) + 1;
            Integer col = i / totalRows;
            String label = col.toString() + row.toString();
            RowColPos pos = ContainerLabelingSchemeWrapper
                .twoCharNumericToRowCol(appService, label, totalRows);
            Assert.assertEquals(pos.getRow(), new Integer(row - 1));
            Assert.assertEquals(pos.getCol(), col);
        }
    }

    @Test
    public void testTwoCharAlpha() throws Exception {
        int totalRows = 3 + r.nextInt(3);
        int totalCols = 5 + r.nextInt(5);

        String cbsrString;

        for (int col = 0; col < totalCols; ++col) {
            for (int row = 0; row < totalRows; ++row) {
                RowColPos pos = new RowColPos(row, col);
                cbsrString = ContainerLabelingSchemeWrapper.rowColToTwoChar(
                    pos, totalRows, totalCols);
                Assert.assertTrue(cbsrString.length() == 2);
                Assert.assertEquals(
                    ALPHA.get((row + (col * totalRows)) % ALPHA.size()),
                    cbsrString.substring(1));
                Assert.assertEquals(
                    ALPHA.get((row + (col * totalRows)) / ALPHA.size()),
                    cbsrString.substring(0, 1));
            }
        }

        for (int col = 0; col < (totalCols); ++col) {
            for (int row = 0; row < (totalRows); ++row) {
                cbsrString = ALPHA
                    .get((row + (col * totalRows)) / ALPHA.size())
                    + ALPHA.get((row + (col * totalRows)) % ALPHA.size());

                RowColPos pos = ContainerLabelingSchemeWrapper.twoCharToRowCol(
                    appService, cbsrString, totalRows, totalCols, "test");
                Assert.assertEquals(new Integer(row), pos.getRow());
                Assert.assertEquals(new Integer(col), pos.getCol());
            }
        }

        try {
            cbsrString = ALPHA.get(((totalRows - 1)
                + ((totalCols - 1) * totalRows) + 15)
                / ALPHA.size())
                + ALPHA
                    .get(((totalRows - 1) + ((totalCols - 1) * totalRows) + 15)
                        % ALPHA.size());
            ContainerLabelingSchemeWrapper.twoCharToRowCol(appService,
                cbsrString, totalRows, totalCols, "test");
            Assert.fail("Should not be allowed to go out of bounds.");
        } catch (BiobankCheckException e) {
            Assert.assertTrue(true);
        }

        try {
            RowColPos pos = ContainerLabelingSchemeWrapper.cbsrTwoCharToRowCol(
                appService, "aa", totalRows, totalCols, "test");
            Assert.fail("should not be allowed to use lower case characters");
        } catch (Exception e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testCbsr() throws Exception {
        int totalRows = 3 + r.nextInt(3);
        int totalCols = 5 + r.nextInt(5);

        String cbsrString;

        for (int col = 0; col < totalCols; ++col) {
            for (int row = 0; row < totalRows; ++row) {
                RowColPos pos = new RowColPos(row, col);
                cbsrString = ContainerLabelingSchemeWrapper
                    .rowColToCbsrTwoChar(pos, totalRows, totalCols);
                Assert.assertTrue(cbsrString.length() == 2);
                Assert.assertEquals(
                    CBSR_ALPHA.get((row + (col * totalRows))
                        % CBSR_ALPHA.size()), cbsrString.substring(1));
                Assert.assertEquals(
                    CBSR_ALPHA.get((row + (col * totalRows))
                        / CBSR_ALPHA.size()), cbsrString.substring(0, 1));
            }
        }

        for (int col = 0; col < totalCols; ++col) {
            for (int row = 0; row < totalRows; ++row) {
                cbsrString = CBSR_ALPHA.get((row + (col * totalRows))
                    / CBSR_ALPHA.size())
                    + CBSR_ALPHA.get((row + (col * totalRows))
                        % CBSR_ALPHA.size());
                RowColPos pos = ContainerLabelingSchemeWrapper
                    .cbsrTwoCharToRowCol(appService, cbsrString, totalRows,
                        totalCols, "test");
                Assert.assertEquals(new Integer(row), pos.getRow());
                Assert.assertEquals(new Integer(col), pos.getCol());
            }
        }

        try {
            RowColPos pos = ContainerLabelingSchemeWrapper.cbsrTwoCharToRowCol(
                appService, "aa", totalRows, totalCols, "test");
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
                    RowColPos pos = new RowColPos(row, col);
                    posString = ContainerLabelingSchemeWrapper.rowColToSbs(pos);
                    if (col >= 9) {
                        Assert.assertTrue(posString.length() == 3);
                    } else {
                        Assert.assertTrue(posString.length() == 2);
                    }
                    Assert.assertEquals(SBS_ALPHA.get(row).charAt(0),
                        posString.charAt(0));
                    Assert.assertEquals(col + 1,
                        Integer.valueOf(posString.substring(1)).intValue());
                }
            }

            for (int col = 0; col < totalCols; ++col) {
                for (int row = 0; row < totalRows; ++row) {
                    RowColPos pos = ContainerLabelingSchemeWrapper.sbsToRowCol(
                        appService,
                        String.format("%s%02d", SBS_ALPHA.get(row), col + 1));
                    Assert.assertEquals(row, pos.getRow().intValue());
                    Assert.assertEquals(col, pos.getCol().intValue());
                }
            }
        }
    }

    private static final int DEWAR_MAX_ROWS = 2;

    private static final int DEWAR_MAX_COLS = 2;

    @Test
    public void testDewar() throws Exception {
        String posString;

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
                RowColPos pos = ContainerLabelingSchemeWrapper.dewarToRowCol(
                    appService, label, DEWAR_MAX_COLS);
                Assert.assertEquals(row, pos.getRow().intValue());
                Assert.assertEquals(col, pos.getCol().intValue());
            }
        }

    }

    @Test
    public void testDelete() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testSite");
        ContainerWrapper container = ContainerHelper.addContainer("01AA",
            "asd", site, ContainerTypeHelper.addContainerType(site, "testCT",
                "tct", ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA, 1,
                1, true));

        ContainerLabelingSchemeWrapper newCLSW =
            new ContainerLabelingSchemeWrapper(
                appService);
        newCLSW.persist();
        newCLSW.delete();

        try {
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA).delete();
            Assert.fail("Should not be able to delete schemes that are in use");
        } catch (BiobankSessionException e) {
            Assert.assertTrue(true);
        }
    }

    @Test
    public void testBounds() throws Exception {
        if (ContainerLabelingSchemeWrapper.checkBounds(appService,
            ContainerLabelingSchemeWrapper.SCHEME_DEWAR, 9, 1) == true)
            Assert.fail("Should be out of bounds");
        else if (ContainerLabelingSchemeWrapper.checkBounds(appService,
            ContainerLabelingSchemeWrapper.SCHEME_CBSR_SBS, 8, 13) == true)
            Assert.fail("Should be out of bounds");
        else if (ContainerLabelingSchemeWrapper.getLabelingSchemeById(
            appService, ContainerLabelingSchemeWrapper.SCHEME_CBSR_SBS)
            .checkBounds(14, 1) == true)
            Assert.fail("Should be out of bounds");
        else if (ContainerLabelingSchemeWrapper.getLabelingSchemeById(
            appService, ContainerLabelingSchemeWrapper.SCHEME_CBSR_SBS)
            .checkBounds(8, 13) == true)
            Assert.fail("Should be out of bounds");

        // test canlabel
        CapacityWrapper cap = new CapacityWrapper(appService);
        cap.setCol(4);
        cap.setRow(2);

        Assert.assertTrue(ContainerLabelingSchemeWrapper.canLabel(
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA)
                .getWrappedObject(), cap.getWrappedObject()));

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

    @Test
    public void testCompareTo() throws Exception {
        // fake test... this wrapper always returns 0
        ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
            ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA).compareTo(
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                ContainerLabelingSchemeWrapper.SCHEME_2_CHAR_ALPHA));
    }

    @Test
    public void testErrors() throws Exception {
        try {
            ContainerLabelingSchemeWrapper.getLabelingSchemeById(appService,
                13123);
            Assert.fail("Should have received an exception.");
        } catch (ApplicationException e) {
            Assert.assertTrue(true);
        }

        String original;
        ContainerLabelingSchemeWrapper scheme;
        for (int i = 1; i < 7; i++) {
            Map<Integer, ContainerLabelingSchemeWrapper> map =
                ContainerLabelingSchemeWrapper
                    .getAllLabelingSchemesMap(appService);
            scheme = map.get(i);
            original = scheme.getName();
            try {
                scheme.setName("asds");
                scheme.persist();
                ContainerLabelingSchemeWrapper
                    .getAllLabelingSchemesMap(appService);
                Assert.fail("Should have thrown an exception");
            } catch (Exception e) {
                Assert.assertTrue(true);
            } finally {
                scheme.setName(original);
                scheme.persist();
            }
        }
    }
}
