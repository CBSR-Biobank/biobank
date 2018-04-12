package edu.ualberta.med.scannerconfig;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The sources where an image can be acquired from. The image is usually of a pallet (box) that
 * contains one or more tubes with a 2D data matrix barcode.
 * <p>
 * The sources can be regions on a flatbed scanner, identified by a "plate number" or a file on the
 * file system.
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public enum ImageSource {
    FLATBED_SCANNER_PLATE_1(
        "FLATBED_SCANNER_PLATE_1",
        ScanPlate.PLATE_1,
        Constants.i18n.tr("Scanner plate 1")),
    FLATBED_SCANNER_PLATE_2(
        "FLATBED_SCANNER_PLATE_2",
        ScanPlate.PLATE_2,
        Constants.i18n.tr("Scanner plate 2")),
    FLATBED_SCANNER_PLATE_3(
        "FLATBED_SCANNER_PLATE_3",
        ScanPlate.PLATE_3,
        Constants.i18n.tr("Scanner plate 3")),
    FLATBED_SCANNER_PLATE_4(
        "FLATBED_SCANNER_PLATE_4",
        ScanPlate.PLATE_4,
        Constants.i18n.tr("Scanner plate 4")),
    FLATBED_SCANNER_PLATE_5(
        "FLATBED_SCANNER_PLATE_5",
        ScanPlate.PLATE_5,
        Constants.i18n.tr("Scanner plate 5")),
    FILE(
        "FILE",
        null,
        Constants.i18n.tr("File"));

    private static class Constants {
        private static final I18n i18n = I18nFactory.getI18n(ImageSource.class);
    }

    private final String id;
    private final String displayLabel;
    private final ScanPlate scanPlate;

    public static final int size = values().length;

    private static final Map<String, ImageSource> ID_MAP;

    static {
        Map<String, ImageSource> map = new LinkedHashMap<String, ImageSource>();

        for (ImageSource enumValue : values()) {
            ImageSource check = map.get(enumValue.getId());
            if (check != null) {
                throw new IllegalStateException("image source value "
                    + enumValue.getId() + " used multiple times");
            }

            map.put(enumValue.getId(), enumValue);
        }

        ID_MAP = Collections.unmodifiableMap(map);
    }

    private ImageSource(String id, ScanPlate scanPlate, String displayString) {
        this.id = id;
        this.scanPlate = scanPlate;
        this.displayLabel = displayString;
    }

    public String getId() {
        return id;
    }

    public ScanPlate getScanPlate() {
        if (this == FILE) {
            throw new IllegalStateException("this value does not have a scan plate");
        }
        return scanPlate;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static Map<String, ImageSource> valuesMap() {
        return ID_MAP;
    }

    public static ImageSource getFromIdString(String id) {
        ImageSource result = valuesMap().get(id);
        if (result == null) {
            throw new IllegalStateException("invalid image source: " + id);
        }
        return result;
    }

}
