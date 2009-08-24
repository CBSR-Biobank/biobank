package edu.ualberta.med.biobank.common;

import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;

public class LabelingScheme {

    private static final String posAlpha = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static char int2pos(int pos) {
        return posAlpha.charAt(pos);
    }

    public static int sbsToInt(String pos) throws Exception {
        if (pos.length() != 2) {
            throw new Exception("binPos has an invalid length: " + pos);
        }
        return posAlpha.indexOf(pos.charAt(0)) * 12
            + Integer.parseInt(pos.substring(1)) - 1;
    }

    public static int twoCharAlphaToInt(String label) {
        int len = label.length();
        return posAlpha.indexOf(label.charAt(len - 2)) * 24
            + posAlpha.indexOf(label.charAt(len - 1));
    }

    public static RowColPos twoCharAlphaToRowCol(ContainerType container,
        String label) throws Exception {
        Integer rowCap = container.getCapacity().getDimensionOneCapacity();
        Integer colCap = container.getCapacity().getDimensionTwoCapacity();

        int pos = twoCharAlphaToInt(label);
        if (pos >= rowCap * colCap) {
            throw new Exception("position out of bounds: containerType/"
                + container.getName() + " pos/" + pos + " rowCap/" + rowCap
                + " colCap/" + colCap);
        }
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = pos % rowCap;
        rowColPos.col = pos / rowCap;
        return rowColPos;

    }

    public static RowColPos twoCharNumericToRowCol(ContainerType container,
        String label) throws Exception {
        int pos = Integer.parseInt(label.substring(label.length() - 1));
        Integer rowCap = container.getCapacity().getDimensionOneCapacity();
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = pos % rowCap;
        rowColPos.col = pos / rowCap;
        return rowColPos;
    }

    /**
     * convert a position in row*column to two letter (in the cbsr way)
     */
    public static String rowColToTwoCharAlpha(RowColPos rcp, Capacity capacity) {
        int pos1, pos2, index;
        int totalRows = capacity.getDimensionOneCapacity();
        int totalCols = capacity.getDimensionTwoCapacity();

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

        return String.valueOf(int2pos(pos1)) + String.valueOf(int2pos(pos2));
    }

    public static String rowColToInt(RowColPos rcp, Capacity capacity) {
        int totalCols = capacity.getDimensionTwoCapacity();
        return String.format("%02d", totalCols * rcp.row + rcp.col + 1);
    }

    public static String getPositionString(RowColPos rcp,
        ContainerType containerType) {
        ContainerLabelingScheme scheme = containerType.getChildLabelingScheme();
        Capacity capacity = containerType.getCapacity();
        String posString = "";
        switch (scheme.getId()) {
        case 2:
            posString = rowColToTwoCharAlpha(rcp, capacity);
            break;
        case 3:
            posString = rowColToInt(rcp, capacity);
            break;
        }
        return posString;
    }

    public static String getPositionString(ContainerPosition position) {
        RowColPos rcp = new RowColPos();
        rcp.row = position.getPositionDimensionOne();
        rcp.col = position.getPositionDimensionTwo();
        return getPositionString(rcp, position.getParentContainer()
            .getContainerType());
    }
}
