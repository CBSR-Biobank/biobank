package edu.ualberta.med.biobank.common.util;

public class InventoryIdUtil {
    @SuppressWarnings("nls")
    private static final String MICROPLATE_MARKER = "##";

    @SuppressWarnings("nls")
    private static final String POSITION_MARKER = "##";

    private static final int MICROPLATE_MARKER_LEN = MICROPLATE_MARKER.length();
    private static final int POSITION_MARKER_LEN = POSITION_MARKER.length();

    @SuppressWarnings("nls")
    public static boolean isFormatMicroplatePosition(String inventoryId) {
        if (inventoryId == null) return false;
        return inventoryId.matches(MICROPLATE_MARKER + "[^#]+" + POSITION_MARKER + "[^#]+");
    }

    @SuppressWarnings("nls")
    public static boolean isFormatMicroplate(String microplateId) {
        if (microplateId == null) return false;
        return microplateId.matches("[^#]+");
    }

    public static String microplatePart(String inventoryId) {
        if (!isFormatMicroplatePosition(inventoryId)) {
            return null;
        }
        return inventoryId.substring(MICROPLATE_MARKER_LEN, inventoryId.lastIndexOf(POSITION_MARKER));
    }

    public static String positionPart(String inventoryId) {
        if (!isFormatMicroplatePosition(inventoryId)) return null;
        return inventoryId.substring(inventoryId.lastIndexOf(POSITION_MARKER) + POSITION_MARKER_LEN);
    }

    @SuppressWarnings("nls")
    public static String formatMicroplatePosition(String microplateId, String position) {
        if ((microplateId == null) || (position == null)) return null;
        if (microplateId.isEmpty() || position.isEmpty()) return null;
        if ((microplateId.indexOf("#") >= 0) || (position.indexOf("#") >= 0)) return null;
        return MICROPLATE_MARKER + microplateId + POSITION_MARKER + position;
    }

    @SuppressWarnings("nls")
    public static String patternFromMicroplateId(String microplateId) {
        if (!isFormatMicroplate(microplateId)) return null;
        return MICROPLATE_MARKER + microplateId + POSITION_MARKER + "%";
    }
}
