package edu.ualberta.med.scannerconfig;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * An enumeration for each pallet scanning region that can be defined in the preferences. The term
 * "plate" is used to define the are where a pallet will be placed on the scanning region.
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public enum ScanPlate {
    PLATE_1(1, Constants.i18n.tr("Plate 1")),
    PLATE_2(2, Constants.i18n.tr("Plate 2")),
    PLATE_3(3, Constants.i18n.tr("Plate 3")),
    PLATE_4(4, Constants.i18n.tr("Plate 4")),
    PLATE_5(5, Constants.i18n.tr("Plate 5"));

    private static class Constants {
        private static final I18n i18n = I18nFactory.getI18n(ScanPlate.class);
    }

    public static final int size = values().length;

    private final int id;
    private final String displayLabel;

    private static final Map<Integer, ScanPlate> ID_MAP;

    static {
        Map<Integer, ScanPlate> map = new LinkedHashMap<Integer, ScanPlate>();

        for (ScanPlate enumValue : values()) {
            ScanPlate check = map.get(enumValue.getId());
            if (check != null) {
                throw new IllegalStateException("scan plate value "
                    + enumValue.getId() + " used multiple times");
            }

            map.put(enumValue.getId(), enumValue);
        }

        ID_MAP = Collections.unmodifiableMap(map);
    }

    private ScanPlate(int id, String displayString) {
        this.id = id;
        this.displayLabel = displayString;
    }

    public int getId() {
        return id;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static Map<Integer, ScanPlate> valuesMap() {
        return ID_MAP;
    }

    public static ScanPlate getFromId(int id) {
        ScanPlate result = valuesMap().get(id);
        if (result == null) {
            throw new IllegalStateException("invalid plate dimensions: " + id);
        }
        return result;
    }

}
