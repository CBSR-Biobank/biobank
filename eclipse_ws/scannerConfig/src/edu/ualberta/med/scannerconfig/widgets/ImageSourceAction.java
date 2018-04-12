package edu.ualberta.med.scannerconfig.widgets;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("nls")
public enum ImageSourceAction {
    SCAN(1),
    RESCAN(2),
    FILENAME(3),
    PLATE_ORIENTATION(4),
    PLATE_DIMENSIONS(5),
    BARCODE_POSITION(6),
    IMAGE_SOURCE_CHANGED(7),
    DPI_CHANGED(8),
    SCAN_AND_DECODE(9);

    private final int id;

    private static final Map<Integer, ImageSourceAction> ID_MAP;

    static {
        Map<Integer, ImageSourceAction> map = new LinkedHashMap<Integer, ImageSourceAction>();

        for (ImageSourceAction enumValue : values()) {
            ImageSourceAction check = map.get(enumValue.getId());
            if (check != null) {
                throw new IllegalStateException(
                    "scan plate value " + enumValue.getId() + " used multiple times");
            }

            map.put(enumValue.getId(), enumValue);
        }

        ID_MAP = Collections.unmodifiableMap(map);
    }

    private ImageSourceAction(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public static Map<Integer, ImageSourceAction> valuesMap() {
        return ID_MAP;
    }

    public static ImageSourceAction getFromId(int id) {
        ImageSourceAction result = valuesMap().get(id);
        if (result == null) {
            throw new IllegalStateException("invalid plate dimensions: " + id);
        }
        return result;
    }

}
