package edu.ualberta.med.biobank;

import edu.ualberta.med.biobank.model.ContainerType;

public class LabelingScheme {

    private static final String posAlpha = "ABCDEFGHJKLMNPQRSTUVWXYZ";

    public static int pos2Int(String alpha) throws Exception {
        if (alpha.length() != 1) {
            throw new Exception("binPos has an invalid length: " + alpha);
        }
        return posAlpha.indexOf(alpha.charAt(0));
    }

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
    public static String rowColToTwoCharAlpha(RowColPos rcp,
        ContainerType containerType) {
        int totalRows = containerType.getCapacity().getDimensionOneCapacity();
        int totalColumns = containerType.getCapacity()
            .getDimensionTwoCapacity();
        if (totalColumns == 1) { // if we got 120*1, we wan't only to act like a
                                 // 1*120
            totalRows = 1;
        }

        char letter1 = 'A';
        char letter2 = 'A';

        int total1 = totalRows * rcp.col + rcp.row;
        letter1 = (char) (letter1 + (total1 / 24));
        letter1 = correctPositionLetter(letter1);

        // int total2 = (row + 1) * totalRows * column + row; // + 1 because
        // start at zero
        letter2 = (char) (letter2 + (total1 % 24));
        letter2 = correctPositionLetter(letter2);

        return String.valueOf(letter1) + String.valueOf(letter2);
    }

    public static char correctPositionLetter(char letter) {
        if (letter < 'I') {
            return letter;
        }
        if (letter >= 'I' && letter < 'O') {
            return (char) (letter + 1);
        }
        return (char) (letter + 2);
    }
}
