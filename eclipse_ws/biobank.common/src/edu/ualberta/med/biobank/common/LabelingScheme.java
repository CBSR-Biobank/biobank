package edu.ualberta.med.biobank.common;

import org.springframework.util.Assert;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class LabelingScheme {

    public static final String CBSR_LABELLING_PATTERN = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHJ";

    /**
     * Get the rowColPos corresponding to the given SBS standard 2 or 3 char
     * string position.
     */
    public static RowColPos sbsToRowCol(String pos) throws Exception {
        if ((pos.length() != 2) && (pos.length() != 3)) {
            throw new Exception("binPos has an invalid length: " + pos);
        }
        int row = SBS_ROW_LABELLING_PATTERN.indexOf(pos.charAt(0));
        int col = Integer.parseInt(pos.substring(1)) - 1;
        return new RowColPos(row, col);
    }

    /**
     * Get the string corresponding to the given RowColPos and using the SBS
     * standard
     */
    public static String RowColToSbs(RowColPos rcp) {
        return "" + SBS_ROW_LABELLING_PATTERN.charAt(rcp.row) + (rcp.col + 1);
    }

    /**
     * Get the index corresponding to the given label, using the CBSR labelling.
     * Use the 2 last character in case we have a full position string
     * (01AA01A2).
     */
    private static int cbsrTwoCharToInt(String label) {
        int len = label.length();
        return CBSR_LABELLING_PATTERN.indexOf(label.charAt(len - 2))
            * CBSR_LABELLING_PATTERN.length()
            + CBSR_LABELLING_PATTERN.indexOf(label.charAt(len - 1));
    }

    /**
     * get the RowColPos in the given container type corresponding to the given
     * label using the CBSR labeling. Use the 2 last character in case we have a
     * full position string (01AA01A2)
     */
    public static RowColPos cbsrTwoCharToRowCol(
        ContainerTypeWrapper containerType, String label) throws Exception {
        Integer rowCap = containerType.getRowCapacity();
        Integer colCap = containerType.getColCapacity();
        return cbsrTwoCharToRowCol(label, rowCap, colCap, containerType
            .getName());
    }

    /**
     * get the RowColPos in the given container corresponding to the given label
     * using the CBSR labeling. Use the 2 last character in case we have a full
     * position string (01AA01A2)
     */
    public static RowColPos cbsrTwoCharToRowCol(String label, int rowCap,
        int colCap, String containerTypeName) throws Exception {
        int pos = cbsrTwoCharToInt(label);
        if (pos >= rowCap * colCap) {
            throw new Exception("Address  " + label + " does not exist in "
                + containerTypeName + ". Max row: " + rowCap + " Max col: "
                + colCap);
        }
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = pos % rowCap;
        rowColPos.col = pos / rowCap;
        return rowColPos;

    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the 2 char numeric labeling.Use the 2 last character in case we
     * have a full position string (01AA01A2)
     */
    public static RowColPos twoCharNumericToRowCol(
        ContainerTypeWrapper containerType, String label) throws Exception {
        Integer rowCap = containerType.getRowCapacity();
        return twoCharNumericToRowCol(label, rowCap);
    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the 2 char numeric labeling.Use the 2 last character in case we
     * have a full position string (01AA01A2)
     */
    public static RowColPos twoCharNumericToRowCol(String label, int totalRows)
        throws Exception {
        int len = label.length();
        int pos = Integer.parseInt(label.substring(len - 2)) - 1;
        // has remove 1 because the two char numeric starts at 1
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = pos % totalRows;
        rowColPos.col = pos / totalRows;
        return rowColPos;
    }

    /**
     * convert a position in row*column to two letter (in the CBSR way)
     */
    public static String rowColToCbsrTwoChar(RowColPos rcp, int totalRows,
        int totalCols) {
        int pos1, pos2, index;
        if (totalRows == 1) {
            index = rcp.col;
        } else if (totalCols == 1) {
            index = rcp.row;
        } else {
            index = totalRows * rcp.col + rcp.row;
        }

        Assert.isTrue(index < 24 * 24);
        pos1 = index / 24;
        pos2 = index % 24;

        if (pos1 >= 0 && pos2 >= 0) {
            return String.valueOf(CBSR_LABELLING_PATTERN.charAt(pos1))
                + String.valueOf(CBSR_LABELLING_PATTERN.charAt(pos2));
        }
        return null;
    }

    /**
     * Convert a position in row*column to two char numeric.
     */
    public static String rowColToTwoCharNumeric(RowColPos rcp, int totalRows) {
        return String.format("%02d", rcp.row + totalRows * rcp.col + 1);
    }

    /**
     * Get the 2 char string corresponding to a RowColPos position inside the
     * given containerType
     */
    public static String getPositionString(RowColPos rcp,
        ContainerTypeWrapper containerType) {
        switch (containerType.getChildLabelingScheme()) {
        case 1:
            // SBS standard
            return RowColToSbs(rcp);
        case 2:
            // CBSR 2 char alphabetic
            return rowColToCbsrTwoChar(rcp, containerType.getRowCapacity(),
                containerType.getColCapacity());
        case 3:
            // 2 char numeric
            return rowColToTwoCharNumeric(rcp, containerType.getRowCapacity());
        }
        return null;
    }

    /**
     * Get the 2 char string corresponding to the given position
     */
    public static String getPositionString(RowColPos rcp,
        ContainerWrapper parent) {
        if (parent != null) {
            return getPositionString(rcp, parent.getContainerType());
        }
        return null;
    }

    public static String getPositionString(ContainerWrapper container) {
        return getPositionString(container.getPosition(), container.getParent());
    }

    /**
     * Get the RowColPos position corresponding to the 2 char string position
     * inside the given container type
     */
    public static RowColPos getRowColFromPositionString(String position,
        ContainerTypeWrapper containerType) throws Exception {
        switch (containerType.getChildLabelingScheme()) {
        case 1:
            // SBS standard
            return sbsToRowCol(position);
        case 2:
            // CBSR 2 char alphabetic
            return cbsrTwoCharToRowCol(containerType, position);
        case 3:
            // 2 char numeric
            return twoCharNumericToRowCol(containerType, position);
        }
        return null;
    }

    public static void main(String[] args) throws Exception {
        testCBSR();
        testTwoCharNumeric();
        testSbs();
    }

    private static void testTwoCharNumeric() throws Exception {
        int totalRows = 6;
        RowColPos rcp = new RowColPos(5, 0);
        System.out.println("Two char numeric: " + rcp.row + ":" + rcp.col
            + "=>" + rowColToTwoCharNumeric(rcp, totalRows));

        String label = "10";
        rcp = twoCharNumericToRowCol(label, totalRows);
        System.out.println("Two char numeric: " + label + "=>" + rcp.row + ":"
            + rcp.col);

    }

    private static void testCBSR() throws Exception {
        // In a 3*5 container, 1:4=AL
        int totalRows = 3;
        int totalCols = 5;

        String cbsrString = "AL";
        RowColPos rcp = cbsrTwoCharToRowCol(cbsrString, totalRows, totalCols,
            "test");
        System.out.println("CBSR: " + cbsrString + "=>" + rcp.row + ":"
            + rcp.col + " in a " + totalRows + "*" + totalCols + " container");

        rcp = new RowColPos(1, 3);
        System.out.println("CBSR: " + rcp.row + ":" + rcp.col + "=>"
            + rowColToCbsrTwoChar(rcp, totalRows, totalCols) + " in a "
            + totalRows + "*" + totalCols + " container");
    }

    private static void testSbs() throws Exception {
        String sample = "D12";
        RowColPos rcp = sbsToRowCol(sample);
        System.out.println("SBS: " + sample + "=>" + rcp.row + ":" + rcp.col
            + " in pallet");

        rcp.row = 2;
        rcp.col = 4;
        String pos = RowColToSbs(rcp);
        System.out.println("SBS: " + rcp.row + ":" + rcp.col + "=>" + pos
            + " in pallet");

    }

}
