package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerLabelingSchemeWrapper extends
    ModelWrapper<ContainerLabelingScheme> {

    public static final String CBSR_LABELLING_PATTERN =
        "ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static final String SBS_ROW_LABELLING_PATTERN = "ABCDEFGHIJKLMNOP";

    public static String BOX81_LABELLING_PATTERN = "ABCDEFGHJ";

    private static Map<String, ContainerLabelingSchemeWrapper> allSchemes;

    public ContainerLabelingSchemeWrapper(
        WritableApplicationService appService,
        ContainerLabelingScheme wrappedObject) {
        super(appService, wrappedObject);
    }

    public ContainerLabelingSchemeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public Class<ContainerLabelingScheme> getWrappedClass() {
        return ContainerLabelingScheme.class;
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name", "maxRows", "maxCols", "maxCapacity",
            "minChars", "maxChars" };
    }

    public void setName(String name) {
        String oldName = wrappedObject.getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setMaxRows(Integer maxRows) {
        Integer oldMaxRows = wrappedObject.getMaxRows();
        wrappedObject.setMaxRows(maxRows);
        propertyChangeSupport.firePropertyChange("name", oldMaxRows, maxRows);
    }

    public Integer getMaxRows() {
        return wrappedObject.getMaxRows();
    }

    public Integer getMaxChars() {
        return wrappedObject.getMaxChars();
    }

    public void setMaxCols(Integer maxCols) {
        Integer oldMaxCols = wrappedObject.getMaxCols();
        wrappedObject.setMaxCols(maxCols);
        propertyChangeSupport.firePropertyChange("name", oldMaxCols, maxCols);
    }

    public Integer getMaxCols() {
        return wrappedObject.getMaxCols();
    }

    public Integer getMinChars() {
        return wrappedObject.getMinChars();
    }

    public void setMaxCapacity(Integer maxCapacity) {
        Integer oldMaxCapacity = wrappedObject.getMaxCapacity();
        wrappedObject.setMaxCapacity(maxCapacity);
        propertyChangeSupport.firePropertyChange("name", oldMaxCapacity,
            maxCapacity);
    }

    public Integer getMaxCapacity() {
        return wrappedObject.getMaxCapacity();
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
        if (hasContainerTypes()) {
            throw new BiobankCheckException(
                "Can't delete this ContainerLabelingScheme: container types are using it.");
        }
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    private boolean hasContainerTypes() throws ApplicationException {
        HQLCriteria criteria =
            new HQLCriteria("from " + ContainerType.class.getName()
                + " where childLabelingScheme=?",
                Arrays.asList(new Object[] { wrappedObject }));
        List<ContainerType> types = appService.query(criteria);
        return types.size() > 0;
    }

    public static Map<String, ContainerLabelingSchemeWrapper> getAllLabelingSchemesMap(
        WritableApplicationService appService) throws ApplicationException {
        if (allSchemes == null) {
            allSchemes = new HashMap<String, ContainerLabelingSchemeWrapper>();
            List<ContainerLabelingScheme> list =
                appService.search(ContainerLabelingScheme.class,
                    new ContainerLabelingScheme());
            if (list != null) {
                for (ContainerLabelingScheme scheme : list) {
                    allSchemes.put(scheme.getName(),
                        new ContainerLabelingSchemeWrapper(appService, scheme));
                }
            }
        }
        return allSchemes;
    }

    public static ContainerLabelingSchemeWrapper getLabelingSchemeByName(
        WritableApplicationService appService, String name)
        throws ApplicationException {
        getAllLabelingSchemesMap(appService);
        return allSchemes.get(name);
    }

    @Override
    public int compareTo(ModelWrapper<ContainerLabelingScheme> o) {
        return 0;
    }

    @Override
    public void persist() throws Exception {
        super.persist();
        allSchemes = null;
    }

    @Override
    public void delete() throws Exception {
        super.delete();
        allSchemes = null;
    }

    /**
     * Check labeling scheme limits for a given gridsize
     **/
    public static boolean checkBounds(WritableApplicationService appService,
        Integer labelingScheme, int totalRows, int totalCols) {

        if (totalRows <= 0 || totalCols <= 0) {
            return false;
        }

        try {
            getAllLabelingSchemesMap(appService);
        } catch (ApplicationException e) {
            throw new RuntimeException(
                "could not load container labeling schemes");
        }

        ContainerLabelingSchemeWrapper schemeWrapper =
            allSchemes.get(labelingScheme);
        if (schemeWrapper != null) {
            return schemeWrapper.checkBounds(totalRows, totalCols);
        }
        return false;
    }

    /**
     * Check labeling scheme limits for a given gridsize
     **/
    public boolean checkBounds(int totalRows, int totalCols) {
        Integer maxRows = getMaxRows();
        Integer maxCols = getMaxCols();
        Integer maxCapacity = getMaxCapacity();

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

    public static List<Integer> getPossibleLabelLength(
        WritableApplicationService appService) throws ApplicationException {
        String query =
            "select min(minChars), max(maxChars) from "
                + ContainerLabelingScheme.class.getName();
        HQLCriteria rangeQuery = new HQLCriteria(query);
        Object[] minMax = (Object[]) appService.query(rangeQuery).get(0);
        List<Integer> validLengths = new ArrayList<Integer>();
        for (int i = (Integer) minMax[0]; i < (Integer) minMax[1] + 1; i++) {
            validLengths.add(i);
        }
        return validLengths;
    }

    /**
     * Get the rowColPos corresponding to the given SBS standard 2 or 3 char
     * string position. Could be A2 or F12.
     */
    public static RowColPos sbsToRowCol(WritableApplicationService appService,
        String pos) throws Exception {
        ContainerLabelingSchemeWrapper scheme =
            getLabelingSchemeByName(appService, "SBS Standard");
        if (scheme == null) {
            throw new BiobankCheckException(
                "SBS Standard labeling scheme not found");
        }
        if ((pos.length() != scheme.getMinChars())
            && (pos.length() != scheme.getMaxChars())) {
            throw new Exception("binPos has an invalid length: " + pos);
        }
        int row = SBS_ROW_LABELLING_PATTERN.indexOf(pos.charAt(0));
        int col = Integer.parseInt(pos.substring(1)) - 1;
        return new RowColPos(row, col);
    }

    /**
     * Get the rowColPos corresponding to the given box81 2 or 3 char string
     * position. Could be A2 or F12. (box81 skip I and O)
     */
    public static RowColPos box81ToRowCol(
        WritableApplicationService appService, String pos) throws Exception {
        ContainerLabelingSchemeWrapper scheme =
            getLabelingSchemeByName(appService, "Box81");
        if (scheme == null) {
            throw new BiobankCheckException("Box81 labeling scheme not found");
        }
        if ((pos.length() != scheme.getMinChars())
            && (pos.length() != scheme.getMaxChars())) {
            throw new Exception("binPos has an invalid length: " + pos);
        }
        int row = BOX81_LABELLING_PATTERN.indexOf(pos.charAt(0));
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
     * Get the string corresponding to the given RowColPos and using the SBS
     * standard. 2:1 will return C2.
     */
    private static String rowColtoBox81(RowColPos rcp) {
        return "" + BOX81_LABELLING_PATTERN.charAt(rcp.row) + (rcp.col + 1);
    }

    /**
     * get the RowColPos in the given container corresponding to the given label
     * AB and will return 1:0.
     */
    public static RowColPos cbsrTwoCharToRowCol(
        WritableApplicationService appService, String label, int rowCap,
        int colCap, String containerTypeName) throws Exception {
        ContainerLabelingSchemeWrapper scheme =
            getLabelingSchemeByName(appService, "CBSR 2 char alphabetic");
        if (scheme == null) {
            throw new BiobankCheckException(
                "CBSR 2 char alphabetic labeling scheme not found");
        }
        int len = label.length();
        if ((len != scheme.getMinChars()) && (len != scheme.getMaxChars()))
            throw new Exception("Label should be " + scheme.getMinChars()
                + " characters");

        int index1 = CBSR_LABELLING_PATTERN.indexOf(label.charAt(len - 2));
        int index2 = CBSR_LABELLING_PATTERN.indexOf(label.charAt(len - 1));
        if ((index1 < 0) || (index2 < 0)) {
            throw new Exception(
                "Invalid characters in label. Are they in upper case?");
        }
        int pos = index1 * CBSR_LABELLING_PATTERN.length() + index2;

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
     * using the 2 char numeric labelling.
     */
    public static RowColPos twoCharNumericToRowCol(
        WritableApplicationService appService, String label, int totalRows)
        throws Exception {
        ContainerLabelingSchemeWrapper scheme =
            getLabelingSchemeByName(appService, "2 char numeric");
        if (scheme == null) {
            throw new BiobankCheckException(
                "2 char numeric labeling scheme not found");
        }
        String errorMsg =
            "Label " + label + " is incorrect: it should be 2 characters";
        int len = label.length();
        if ((len != scheme.getMinChars()) && (len != scheme.getMaxChars()))
            throw new Exception(errorMsg);
        try {
            int pos = Integer.parseInt(label) - 1;
            // has remove 1 because the two char numeric starts at 1
            RowColPos rowColPos = new RowColPos();
            rowColPos.row = pos % totalRows;
            rowColPos.col = pos / totalRows;
            return rowColPos;
        } catch (NumberFormatException nbe) {
            throw new Exception(errorMsg);
        }
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
     * using the dewar labelling.
     * 
     * @throws Exception
     */
    public static RowColPos dewarToRowCol(
        WritableApplicationService appService, String label, int totalCol)
        throws Exception {
        ContainerLabelingSchemeWrapper scheme =
            getLabelingSchemeByName(appService, "Dewar");
        if (scheme == null) {
            throw new BiobankCheckException(
                "CBSR 2 char alphabetic labeling scheme not found");
        }
        int len = label.length();
        if ((len != scheme.getMinChars()) && (len != scheme.getMaxChars()))
            throw new Exception("Label should be " + scheme.getMinChars()
                + " characters");

        if (label.charAt(0) != label.charAt(1)) {
            throw new Exception("Label should be double letter (BB).");
        }
        // letters are double (BB). need only one
        int letterPosition = SBS_ROW_LABELLING_PATTERN.indexOf(label.charAt(0));
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
        case 5:
            // box81
            return rowColtoBox81(rcp);
        }
        return null;
    }

    /**
     * Get the RowColPos position corresponding to the string position given the
     * container capacity
     */
    public static RowColPos getRowColFromPositionString(
        WritableApplicationService appService, String position,
        Integer childLabelingSchemeId, Integer rowCapacity, Integer colCapacity)
        throws Exception {
        switch (childLabelingSchemeId) {
        case 1:
            // SBS standard
            return sbsToRowCol(appService, position);
        case 2:
            // CBSR 2 char alphabetic
            return cbsrTwoCharToRowCol(appService, position, rowCapacity,
                colCapacity, null);
        case 3:
            // 2 char numeric
            return twoCharNumericToRowCol(appService, position, rowCapacity);
        case 4:
            // Dewar
            return dewarToRowCol(appService, position, colCapacity);
        case 5:
            // Box81
            return box81ToRowCol(appService, position);
        }
        return null;
    }
}
