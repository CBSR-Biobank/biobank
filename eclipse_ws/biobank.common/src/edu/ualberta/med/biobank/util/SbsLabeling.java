package edu.ualberta.med.biobank.util;

import edu.ualberta.med.biobank.model.util.RowColPos;

public class SbsLabeling {

    @SuppressWarnings("nls")
    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHIJKLMNOP";

    public static final int ROW_DEFAULT = 8;
    public static final int COL_DEFAULT = 12;

    /**
     * Get the string corresponding to the given RowColPos and using the SBS
     * standard. 2:1 will return C2.
     */
    public static String fromRowCol(final RowColPos rcp) {
        return fromRowCol(rcp.getRow(), rcp.getCol());
    }

    @SuppressWarnings("nls")
    public static String fromRowCol(int row, int col) {
        if (row > SBS_ROW_LABELLING_PATTERN.length()) {
            throw new IllegalArgumentException(
                "invalid row size for position: " + row);
        }
        StringBuffer sb = new StringBuffer();
        sb.append(SBS_ROW_LABELLING_PATTERN.charAt(row));
        sb.append(col + 1);
        return sb.toString();

    }

    /**
     * Get the rowColPos corresponding to the given SBS standard 2 or 3 char
     * string position. Could be A2 or F12.
     */
    @SuppressWarnings("nls")
    public static RowColPos toRowCol(String pos) {
        if ((pos.length() < 2) || (pos.length() > 3)) {
            throw new IllegalArgumentException(
                "invalid length for position string: " + pos);
        }
        int row = SBS_ROW_LABELLING_PATTERN.indexOf(pos.charAt(0));
        int col = Integer.parseInt(pos.substring(1)) - 1;
        return new RowColPos(row, col);
    }

}
