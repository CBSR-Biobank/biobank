package edu.ualberta.med.biobank.common.util;

import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.internal.ContainerLabelingSchemeWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class LabelingScheme {

    public static final String CBSR_LABELLING_PATTERN = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHJKLMNPQR";

    /**
     * Get the rowColPos corresponding to the given SBS standard 2 or 3 char
     * string position. Could be A2 or F12.
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
     * standard. 2:1 will return C2.
     */
    public static String rowColToSbs(RowColPos rcp) {
        return "" + SBS_ROW_LABELLING_PATTERN.charAt(rcp.row) + (rcp.col + 1);
    }

    /**
     * Get the index corresponding to the given label, using the CBSR labelling.
     * Use the 2 last characters in case we have a full position string. For
     * 01AB, will use only AB and will return 1:0.
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
     * get the RowColPos in the given container corresponding to the given label
     * using the CBSR labelling. Use the 2 last characters in case we have a
     * full position string.For 01AB, will use only AB and will return 1:0.
     */
    public static RowColPos cbsrTwoCharToRowCol(String label, int rowCap,
        int colCap, String containerTypeName) throws Exception {
        if (label.length() != 2)
            throw new Exception("Label should be 2 characters");

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
     * using the 2 char numeric labelling. Use the 2 last characters in case we
     * have a full position string For 01AA11, will use only 11 and will return
     * 10
     */
    public static RowColPos twoCharNumericToRowCol(String label, int totalRows)
        throws Exception {
        int len = label.length();
        if (len != 2)
            throw new Exception("Label should be 2 characters");
        int pos = Integer.parseInt(label) - 1;
        // has remove 1 because the two char numeric starts at 1
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = pos % totalRows;
        rowColPos.col = pos / totalRows;
        return rowColPos;
    }

    /**
     * Convert a position in row*column to two letter (in the CBSR way)
     * 
     * @throws BiobankCheckException
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

        pos1 = index / lettersLength;
        pos2 = index % lettersLength;

        if (pos1 >= 0 && pos2 >= 0) {
            return String.valueOf(CBSR_LABELLING_PATTERN.charAt(pos1))
                + String.valueOf(CBSR_LABELLING_PATTERN.charAt(pos2));
        }
        return null;
    }

    /* Check labeling scheme limits for a given gridsize */
    public static boolean checkBounds(WritableApplicationService appService,
        Integer labelingScheme, int totalRows, int totalCols) {

        if (totalRows <= 0 || totalCols <= 0) {
            return false;
        }

        Map<Integer, ContainerLabelingSchemeWrapper> schemeWrappersMap;
        try {
            schemeWrappersMap = ContainerLabelingSchemeWrapper
                .getAllLabelingSchemesMap(appService);
        } catch (ApplicationException e) {
            throw new RuntimeException(
                "could not load container labeling schemes");
        }

        ContainerLabelingSchemeWrapper schemeWrapper = schemeWrappersMap
            .get(labelingScheme);
        if (schemeWrapper != null) {
            Integer maxRows = schemeWrapper.getMaxRows();
            Integer maxCols = schemeWrapper.getMaxCols();
            Integer maxCapacity = schemeWrapper.getMaxCapacity();

            boolean isInBounds = true;

            if (maxRows != null) {
                isInBounds &= totalRows <= maxRows;
            }

            if (maxCols != null) {
                isInBounds &= totalCols <= maxCols;
            }

            if (maxCapacity != null) {
                isInBounds &= totalRows * totalCols <= maxCapacity;
            }

            return isInBounds;
        }

        return false;
    }

    /**
     * Convert a position in row*column to two char numeric.
     */
    public static String rowColToTwoCharNumeric(RowColPos rcp, int totalRows) {
        return String.format("%02d", rcp.row + totalRows * rcp.col + 1);
    }

    /**
     * Convert a position in row*column to Dewar labelling (AA, BB, CC...).
     */
    public static String rowColToDewar(RowColPos rcp, Integer colCapacity) {
        int pos = rcp.col + (colCapacity * rcp.row);
        String letter = String.valueOf(CBSR_LABELLING_PATTERN.charAt(pos));
        return letter + letter;
    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the dewar labelling. Use the 2 last character in case we have a
     * full position string: for 01BB, will use only BB.
     * 
     * @throws Exception
     */
    public static RowColPos dewarToRowCol(String label, int totalCol)
        throws Exception {
        int len = label.length();
        if (len != 2)
            throw new Exception("Label should be 2 characters");
        int letterPosition = SBS_ROW_LABELLING_PATTERN.indexOf(label.charAt(0)); // letters
                                                                                 // are
                                                                                 // double
                                                                                 // (BB).
                                                                                 // need
                                                                                 // only
        // one
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = letterPosition / totalCol;
        rowColPos.col = letterPosition % totalCol;
        return rowColPos;
    }

    /**
     * Get the 2 char string corresponding to a RowColPos position given the
     * container capacity
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
     * Get the RowColPos position corresponding to the 2 char string position
     * given the container capacity
     */
    public static RowColPos getRowColFromPositionString(String position,
        Integer childLabelingSchemeId, Integer rowCapacity, Integer colCapacity)
        throws Exception {
        switch (childLabelingSchemeId) {
        case 1:
            // SBS standard
            return sbsToRowCol(position);
        case 2:
            // CBSR 2 char alphabetic
            return cbsrTwoCharToRowCol(position, rowCapacity, colCapacity, null);
        case 3:
            // 2 char numeric
            return twoCharNumericToRowCol(position, rowCapacity);
        }
        return null;
    }

}
