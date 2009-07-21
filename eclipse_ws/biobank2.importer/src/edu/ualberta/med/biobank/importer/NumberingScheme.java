
package edu.ualberta.med.biobank.importer;

import edu.ualberta.med.biobank.model.ContainerType;

public class NumberingScheme {

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

    public static int binPos2Int(String binPos) throws Exception {
        if (binPos.length() != 2) {
            throw new Exception("binPos has an invalid length: " + binPos);
        }
        return posAlpha.indexOf(binPos.charAt(0)) * 24
            + posAlpha.indexOf(binPos.charAt(1));
    }

    public static int palettePos2Int(String pos) throws Exception {
        if (pos.length() != 2) {
            throw new Exception("binPos has an invalid length: " + pos);
        }
        return posAlpha.indexOf(pos.charAt(0)) * 12
            + Integer.parseInt(pos.substring(1)) - 1;
    }

    public static RowColPos hotelPos2RowCol(ContainerType freezer, String pos)
        throws Exception {
        Integer rowCap = freezer.getCapacity().getDimensionOneCapacity();
        Integer colCap = freezer.getCapacity().getDimensionTwoCapacity();

        int alphaPos = binPos2Int(pos);
        if (alphaPos >= rowCap * colCap) {
            throw new Exception("position out of bounds: containerType/"
                + freezer.getName() + " pos/" + pos + " rowCap/" + rowCap
                + " colCap/" + colCap);
        }
        RowColPos rowColPos = new RowColPos();
        rowColPos.row = alphaPos % rowCap;
        rowColPos.col = alphaPos / rowCap;
        return rowColPos;

    }
}
