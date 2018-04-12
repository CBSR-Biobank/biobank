package edu.ualberta.med.scannerconfig;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The possible two accepted ways of placing a pallet on a flatbed scanner. Note that for each
 * orientation, the first cell, A1, must be at the top.
 * 
 * @author nelson
 * 
 */
@SuppressWarnings("nls")
public enum PalletOrientation {
    LANDSCAPE("Landscape", Constants.i18n.tr("Landscape")),
    PORTRAIT("Portrait", Constants.i18n.tr("Portrait"));

    private static class Constants {
        private static final I18n i18n = I18nFactory.getI18n(PalletOrientation.class);
    }

    public static final int size = values().length;

    private final String id;
    private final String displayLabel;

    private static final Map<String, PalletOrientation> ID_MAP;

    static {
        Map<String, PalletOrientation> map = new LinkedHashMap<String, PalletOrientation>();

        for (PalletOrientation orientationEnum : values()) {
            PalletOrientation check = map.get(orientationEnum.getId());
            if (check != null) {
                throw new IllegalStateException("pallet orientation value "
                    + orientationEnum.getId() + " used multiple times");
            }

            map.put(orientationEnum.getId(), orientationEnum);
        }

        ID_MAP = Collections.unmodifiableMap(map);
    }

    private PalletOrientation(String id, String displayLabel) {
        this.id = id;
        this.displayLabel = displayLabel;
    }

    public String getId() {
        return id;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static Map<String, PalletOrientation> valuesMap() {
        return ID_MAP;
    }

    public static PalletOrientation getFromIdString(String id) {
        PalletOrientation result = valuesMap().get(id);
        if (result == null) {
            throw new IllegalStateException("invalid pallet orientation: " + id);
        }
        return result;
    }
}
