package edu.ualberta.med.scannerconfig;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * Images of pallets saved to disk can be decoded, rather than being scanned with a flatbed scanner.
 * Also, the 2D barcodes can be present on the top or bottoms of the tubes. This enumeration is used
 * to define if the image was taken of the top or bottom of the pallet. The position of the camera
 * is important since it effects the ordering used to identify the tubes in the image.
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public enum BarcodePosition {
    TOP("BARCODE_POSITION_ABOVE", Constants.i18n.tr("Tube tops")),
    BOTTOM("BARCODE_POSITION_BELOW", Constants.i18n.tr("Tube bottoms"));

    private static class Constants {
        private static final I18n i18n = I18nFactory.getI18n(BarcodePosition.class);
    }

    public static final int size = values().length;

    private final String id;
    private final String displayLabel;

    private static final Map<String, BarcodePosition> ID_MAP;

    static {
        Map<String, BarcodePosition> map = new LinkedHashMap<String, BarcodePosition>();

        for (BarcodePosition enumValue : values()) {
            BarcodePosition check = map.get(enumValue.getId());
            if (check != null) {
                throw new IllegalStateException("scan plate value "
                    + enumValue.getId() + " used multiple times");
            }

            map.put(enumValue.getId(), enumValue);
        }

        ID_MAP = Collections.unmodifiableMap(map);
    }

    private BarcodePosition(String id, String displayString) {
        this.id = id;
        this.displayLabel = displayString;
    }

    public String getId() {
        return id;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static Map<String, BarcodePosition> valuesMap() {
        return ID_MAP;
    }

    public static BarcodePosition getFromIdString(String id) {
        BarcodePosition result = valuesMap().get(id);
        if (result == null) {
            throw new IllegalStateException("invalid barcode position: " + id);
        }
        return result;
    }

}
