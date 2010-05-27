package edu.ualberta.med.biobank.common;

import org.springframework.util.Assert;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.model.ContainerType;

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
    public static String rowColToSbs(RowColPos rcp) {
        return "" + SBS_ROW_LABELLING_PATTERN.charAt(rcp.row) + (rcp.col + 1);
    }

    /**
     * Get the index corresponding to the given label, using the CBSR labelling.
     * Use the 2 last character in case we have a full position string
     * (01AA01A2).
     * 
     * @throws Exception
     */
    private static int cbsrTwoCharToInt(String label) throws Exception {
        int len = label.length();
        int index1 = CBSR_LABELLING_PATTERN.indexOf(label.charAt(len - 2));
        int index2 = CBSR_LABELLING_PATTERN.indexOf(label.charAt(len - 1));
        if ((index1 < 0) || (index2 < 0)) {
            throw new Exception(
                "Invalid characters in label. Are they in upper case?");
        }
        return index1 * CBSR_LABELLING_PATTERN.length() + index2;
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
        int lettersLength = CBSR_LABELLING_PATTERN.length();
        if (totalRows == 1) {
            index = rcp.col;
        } else if (totalCols == 1) {
            index = rcp.row;
        } else {
            index = totalRows * rcp.col + rcp.row;
        }

        Assert.isTrue(index < lettersLength * lettersLength);
        pos1 = index / lettersLength;
        pos2 = index % lettersLength;

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
     * Convert a position in row*column to Dewar labeling (AA, BB, CC...).
     */
    public static String rowColToDewar(RowColPos rcp, Integer colCapacity) {
        int pos = rcp.col + (colCapacity * rcp.row);
        String letter = String.valueOf(CBSR_LABELLING_PATTERN.charAt(pos));
        return letter + letter;
    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the dewar labeling. Use the 2 last character in case we have a full
     * position string (01AA)
     */
    public static RowColPos dewarToRowCol(String label, int totalCol) {
        int len = label.length();
        String letter = label.substring(len - 2);
        int letterPosition = SBS_ROW_LABELLING_PATTERN
            .indexOf(letter.charAt(0)); // letters are double (BB). need only
        // one
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = letterPosition / totalCol;
        rowColPos.col = letterPosition % totalCol;
        return rowColPos;
    }

    /**
     * Get the 2 char string corresponding to a RowColPos position inside the
     * given containerType
     */
    public static String getPositionString(RowColPos rcp,
        ContainerType containerType) {
        return getPositionString(rcp, containerType.getChildLabelingScheme()
            .getId(), containerType.getCapacity().getRowCapacity(),
            containerType.getCapacity().getColCapacity());
    }

    public static String getPositionString(RowColPos rcp,
        ContainerTypeWrapper containerType) {
        return getPositionString(rcp, containerType.getChildLabelingScheme(),
            containerType.getRowCapacity(), containerType.getColCapacity());
    }

    public static String getPositionString(RowColPos rcp,
        ContainerType containerType) {
        return getPositionString(rcp, containerType.getChildLabelingScheme()
            .getId(), containerType.getCapacity().getRowCapacity(),
            containerType.getCapacity().getColCapacity());
    }

    /**
     * Get the 2 char string corresponding to a RowColPos position inside the
     * given containerType
     */
    public static String getPositionString(RowColPos rcp,
        Integer childLabelingSchemeId, Integer rowCapacity, Integer colCapacity) {
        switch (childLabelingSchemeId) {
        case 1:
            // SBS standard
            return rowColToSbs(rcp);
        case 2:
            // CBSR 2 char alphabetic
            return rowColToCbsrTwoChar(rcp, rowCapacity, colCapacity);
        case 3:
            // 2 char numeric
            return rowColToTwoCharNumeric(rcp, rowCapacity);
        case 4:
            // dewar
            return rowColToDewar(rcp, colCapacity);
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

}
