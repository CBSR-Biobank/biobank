package edu.ualberta.med.biobank.model;

import java.text.MessageFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.util.RowColPos;

// TODO: should be an enum? Maybe make types that require java code, but put parameters and names into the database?
@Entity
@Table(name = "CONTAINER_LABELING_SCHEME")
public class ContainerLabelingScheme extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHIJKLMNOP";

    @SuppressWarnings("nls")
    public static final String CBSR_2_CHAR_LABELLING_PATTERN =
        "ABCDEFGHJKLMNPQRSTUVWXYZ";

    @SuppressWarnings("nls")
    public static String BOX81_LABELLING_PATTERN = "ABCDEFGHJ";

    @SuppressWarnings("nls")
    public static final String TWO_CHAR_LABELLING_PATTERN =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private String name;
    private Integer minChars;
    private Integer maxChars;
    private Integer maxRows;
    private Integer maxCols;
    private Integer maxCapacity;

    @Column(name = "NAME", length = 50, unique = true)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "MIN_CHARS")
    public Integer getMinChars() {
        return this.minChars;
    }

    public void setMinChars(Integer minChars) {
        this.minChars = minChars;
    }

    @Column(name = "MAX_CHARS")
    public Integer getMaxChars() {
        return this.maxChars;
    }

    public void setMaxChars(Integer maxChars) {
        this.maxChars = maxChars;
    }

    @Column(name = "MAX_ROWS")
    public Integer getMaxRows() {
        return this.maxRows;
    }

    public void setMaxRows(Integer maxRows) {
        this.maxRows = maxRows;
    }

    @Column(name = "MAX_COLS")
    public Integer getMaxCols() {
        return this.maxCols;
    }

    public void setMaxCols(Integer maxCols) {
        this.maxCols = maxCols;
    }

    @Column(name = "MAX_CAPACITY")
    public Integer getMaxCapacity() {
        return this.maxCapacity;
    }

    public void setMaxCapacity(Integer maxCapacity) {
        this.maxCapacity = maxCapacity;
    }

    /**
     * Get the string corresponding to the given RowColPos and using the SBS
     * standard. 2:1 will return C2.
     */
    public static String rowColToSbs(RowColPos rcp) {
        return "" + SBS_ROW_LABELLING_PATTERN.charAt(rcp.getRow()) //$NON-NLS-1$
            + (rcp.getCol() + 1);
    }

    /**
     * Convert a position in row*column to two letter (in the CBSR way)
     * 
     * @throws Exception
     * 
     * @throws BiobankCheckException
     */
    public static String rowColToCbsrTwoChar(RowColPos rcp, int totalRows,
        int totalCols) {
        int pos1, pos2, index;
        int lettersLength = CBSR_2_CHAR_LABELLING_PATTERN.length();
        if (totalRows == 1) {
            index = rcp.getCol();
        } else if (totalCols == 1) {
            index = rcp.getRow();
        } else {
            index = totalRows * rcp.getCol() + rcp.getRow();
        }

        pos1 = index / lettersLength;
        pos2 = index % lettersLength;

        if (pos1 >= 0 && pos2 >= 0) {
            return String.valueOf(CBSR_2_CHAR_LABELLING_PATTERN.charAt(pos1))
                + String.valueOf(CBSR_2_CHAR_LABELLING_PATTERN.charAt(pos2));
        }
        return null;
    }

    /**
     * Convert a position in row*column to two char numeric.
     */
    public static String rowColToTwoCharNumeric(RowColPos rcp, int totalRows) {
        return String.format("%02d", rcp.getRow() + totalRows * rcp.getCol() //$NON-NLS-1$
            + 1);
    }

    /**
     * Convert a position in row*column to Dewar labelling (AA, BB, CC...).
     */
    public static String rowColToDewar(RowColPos rcp, Integer colCapacity) {
        int pos = rcp.getCol() + (colCapacity * rcp.getRow());
        String letter = String.valueOf(SBS_ROW_LABELLING_PATTERN.charAt(pos));
        return letter + letter;
    }

    /**
     * Get the string corresponding to the given RowColPos and using the SBS
     * standard. 2:1 will return C2.
     */
    private static String rowColtoCbsrSbs(RowColPos rcp) {
        return "" + BOX81_LABELLING_PATTERN.charAt(rcp.getRow()) //$NON-NLS-1$
            + (rcp.getCol() + 1);
    }

    /**
     * Convert a position in row*column to two letter (in the CBSR way)
     * 
     * @throws Exception
     * 
     * @throws BiobankCheckException
     */
    public static String rowColToTwoChar(RowColPos rcp, int totalRows,
        int totalCols) {
        int pos1, pos2, index;
        int lettersLength = TWO_CHAR_LABELLING_PATTERN.length();
        if (totalRows == 1) {
            index = rcp.getCol();
        } else if (totalCols == 1) {
            index = rcp.getRow();
        } else {
            index = totalRows * rcp.getCol() + rcp.getRow();
        }

        pos1 = index / lettersLength;
        pos2 = index % lettersLength;

        if (pos1 >= 0 && pos2 >= 0) {
            return String.valueOf(TWO_CHAR_LABELLING_PATTERN.charAt(pos1))
                + String.valueOf(TWO_CHAR_LABELLING_PATTERN.charAt(pos2));
        }
        return null;
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
        case 5:
            // CBSR SBS
            return rowColtoCbsrSbs(rcp);
        case 6:
            // 2 char alphabetic
            return rowColToTwoChar(rcp, rowCapacity, colCapacity);
        }
        return null;
    }

    /**
     * Get the rowColPos corresponding to the given SBS standard 2 or 3 char
     * string position. Could be A2 or F12.
     */
    public RowColPos sbsToRowCol(String pos) throws Exception {
        if ((pos.length() != getMinChars())
            && (pos.length() != getMaxChars())) {
            throw new Exception("binPos has an invalid length: " + pos); //$NON-NLS-1$
        }
        int row = SBS_ROW_LABELLING_PATTERN.indexOf(pos.charAt(0));
        int col = Integer.parseInt(pos.substring(1)) - 1;
        return new RowColPos(row, col);
    }

    /**
     * get the RowColPos in the given container corresponding to the given label
     * AB and will return 1:0.
     */
    @SuppressWarnings("nls")
    public RowColPos cbsrTwoCharToRowCol(String label, int rowCap,
        int colCap, String containerTypeName)
        throws IllegalArgumentException {
        int len = label.length();
        if ((len != getMinChars()) && (len != getMaxChars())) {
            throw new IllegalArgumentException(
                MessageFormat.format("Label should be {0} characters.",
                    getMinChars()));
        }

        int index1 = CBSR_2_CHAR_LABELLING_PATTERN.indexOf(label
            .charAt(len - 2));
        int index2 = CBSR_2_CHAR_LABELLING_PATTERN.indexOf(label
            .charAt(len - 1));
        if ((index1 < 0) || (index2 < 0)) {
            throw new IllegalArgumentException(
                "Invalid characters in label. Are they in upper case?");
        }
        int pos = index1 * CBSR_2_CHAR_LABELLING_PATTERN.length() + index2;

        if (pos >= rowCap * colCap) {
            String maxValue = ContainerLabelingSchemeWrapper
                .rowColToCbsrTwoChar(new RowColPos(rowCap - 1, colCap - 1),
                    rowCap, colCap);
            String msgStart =
                MessageFormat
                    .format("Label {0} does not exist in this scheme", label);
            if (containerTypeName != null) {
                msgStart = MessageFormat.format(
                    "Label {0} does not exist in {1}", label,
                    containerTypeName);
            }
            String msgMax = MessageFormat.format(
                "Max value is {0}. (Max row: {1}. Max col: {2}.)", maxValue,
                rowCap, colCap);
            throw new IllegalArgumentException(msgStart + " " + msgMax);
        }
        Integer row = pos % rowCap;
        Integer col = pos / rowCap;
        RowColPos rowColPos = new RowColPos(row, col);
        return rowColPos;
    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the 2 char numeric labelling.
     */
    @SuppressWarnings("nls")
    public RowColPos twoCharNumericToRowCol(String label, int totalRows)
        throws IllegalArgumentException {
        String errorMsg =
            MessageFormat
                .format("Label {0} is incorrect: it should be 2 characters",
                    label);
        int len = label.length();

        if ((len != getMinChars()) && (len != getMaxChars())) {
            throw new IllegalArgumentException(errorMsg);
        }

        try {
            int pos = Integer.parseInt(label) - 1;
            // has remove 1 because the two char numeric starts at 1
            Integer row = pos % totalRows;
            Integer col = pos / totalRows;
            RowColPos rowColPos = new RowColPos(row, col);
            return rowColPos;
        } catch (NumberFormatException nbe) {
            throw new IllegalArgumentException(errorMsg);
        }
    }

    /**
     * Get the RowColPos in the given container corresponding to the given label
     * using the dewar labelling.
     * 
     * @throws Exception
     */
    @SuppressWarnings("nls")
    public RowColPos dewarToRowCol(String label, int totalCol)
        throws IllegalArgumentException {
        int len = label.length();
        if ((len != getMinChars()) && (len != getMaxChars())) {
            throw new IllegalArgumentException(
                MessageFormat.format("Label should be {0} characters.",
                    getMinChars()));
        }

        if (label.charAt(0) != label.charAt(1)) {
            throw new IllegalArgumentException(
                "Label should be double letter (BB).");
        }
        // letters are double (BB). need only one
        int letterPosition = SBS_ROW_LABELLING_PATTERN.indexOf(label.charAt(0));
        Integer row = letterPosition / totalCol;
        Integer col = letterPosition % totalCol;
        RowColPos rowColPos = new RowColPos(row, col);
        return rowColPos;
    }

    /**
     * Get the rowColPos corresponding to the given CBSR SBS 2 char string
     * position. Could be A2 or F9. (CBSR SBS skip I and O)
     */
    @SuppressWarnings("nls")
    public RowColPos cbsrSbsToRowCol(String pos)
        throws IllegalArgumentException {
        if ((pos.length() != getMinChars()) && (pos.length() != getMaxChars())) {
            throw new IllegalArgumentException("binPos has an invalid length: "
                + pos);
        }
        int row = BOX81_LABELLING_PATTERN.indexOf(pos.charAt(0));
        int col = Integer.parseInt(pos.substring(1)) - 1;
        return new RowColPos(row, col);
    }

    /**
     * get the RowColPos in the given container corresponding to the given label
     * AB and will return 1:0.
     */
    @SuppressWarnings("nls")
    public RowColPos twoCharToRowCol(String label, int rowCap, int colCap,
        String containerTypeName)
        throws IllegalArgumentException {
        int len = label.length();
        if ((len != getMinChars()) && (len != getMaxChars())) {
            throw new IllegalArgumentException(
                MessageFormat.format("Label should be {0} characters.",
                    getMinChars()));
        }

        int index1 = TWO_CHAR_LABELLING_PATTERN.indexOf(label.charAt(len - 2));
        int index2 = TWO_CHAR_LABELLING_PATTERN.indexOf(label.charAt(len - 1));
        if ((index1 < 0) || (index2 < 0)) {
            throw new IllegalArgumentException(
                "Invalid characters in label. Are they in upper case?");
        }
        int pos = index1 * TWO_CHAR_LABELLING_PATTERN.length() + index2;

        if (pos >= rowCap * colCap) {
            String maxValue = ContainerLabelingSchemeWrapper
                .rowColToCbsrTwoChar(new RowColPos(rowCap - 1, colCap - 1),
                    rowCap, colCap);
            String msgStart =
                MessageFormat
                    .format("Label {0} does not exist in this scheme.", label);
            if (containerTypeName != null)
                msgStart =
                    MessageFormat.format(
                        "Label {0} does not exist in {1}", label,
                        containerTypeName);
            String msgMax = MessageFormat.format(
                "Max value is {0}. (Max row: {1}. Max col: {2}.)", maxValue,
                rowCap, colCap);
            throw new IllegalArgumentException(msgStart + " " + msgMax); //$NON-NLS-1$
        }
        Integer row = pos % rowCap;
        Integer col = pos / rowCap;
        RowColPos rowColPos = new RowColPos(row, col);
        return rowColPos;
    }

    /**
     * Get the RowColPos position corresponding to the string position given the
     * container capacity
     */
    public RowColPos getRowColFromPositionString(String position,
        Integer rowCapacity, Integer colCapacity)
        throws Exception {
        switch (getId()) {
        case 1:
            // SBS standard
            return sbsToRowCol(position);
        case 2:
            // CBSR 2 char alphabetic
            return cbsrTwoCharToRowCol(position, rowCapacity, colCapacity, null);
        case 3:
            // 2 char numeric
            return twoCharNumericToRowCol(position, rowCapacity);
        case 4:
            // Dewar
            return dewarToRowCol(position, colCapacity);
        case 5:
            // Box81
            return cbsrSbsToRowCol(position);
        case 6:
            // 2 char alphabetic
            return twoCharToRowCol(position, rowCapacity, colCapacity, null);
        }
        return null;
    }

    @Transient
    public boolean canLabel(Capacity capacity) {
        boolean canLabel = true;

        if (capacity.getRowCapacity() == null
            || capacity.getColCapacity() == null) {
            return false;
        }

        if (canLabel && getMaxRows() != null) {
            canLabel &= capacity.getRowCapacity() <= getMaxRows();
        }

        if (canLabel && getMaxCols() != null) {
            canLabel &= capacity.getColCapacity() <= getMaxCols();
        }

        if (canLabel && getMaxCapacity() != null) {
            int max = capacity.getRowCapacity() * capacity.getColCapacity();
            canLabel &= max <= getMaxCapacity();
        }

        return canLabel;
    }
}
