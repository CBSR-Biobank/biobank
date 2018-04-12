package edu.ualberta.med.scannerconfig;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

/**
 * The current pallet dimensions allowed for decoding from an image.
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public enum PalletDimensions {

    DIM_ROWS_8_COLS_12(
        "ROWS_8_COLS_12",
        new ImmutablePair<Integer, Integer>(8, 12),
        Constants.i18n.tr("8x12")),

    DIM_ROWS_10_COLS_10(
        "ROWS_10_COLS_10",
        new ImmutablePair<Integer, Integer>(10, 10),
        Constants.i18n.tr("10x10")),

    DIM_ROWS_9_COLS_9(
        "ROWS_9_COLS_9",
        new ImmutablePair<Integer, Integer>(9, 9),
        Constants.i18n.tr("9x9")),

    DIM_ROWS_12_COLS_12(
        "ROWS_12_COLS_12",
        new ImmutablePair<Integer, Integer>(12, 12),
        Constants.i18n.tr("12x12")),

    DIM_ROWS_7_COLS_7(
        "ROWS_7_COLS_7",
        new ImmutablePair<Integer, Integer>(7, 7),
        Constants.i18n.tr("7x7"));

    private static class Constants {
        private static final I18n i18n = I18nFactory.getI18n(PalletDimensions.class);
    }

    private final ImmutablePair<Integer, Integer> dimensions;
    private final String id;
    private final String displayLabel;

    private static final Map<String, PalletDimensions> ID_MAP;

    static {
        Map<String, PalletDimensions> map = new LinkedHashMap<String, PalletDimensions>();

        for (PalletDimensions enumValue : values()) {
            PalletDimensions check = map.get(enumValue.getId());
            if (check != null) {
                throw new IllegalStateException("pallet dimensions value "
                    + enumValue.getId() + " used multiple times");
            }

            map.put(enumValue.getId(), enumValue);
        }

        ID_MAP = Collections.unmodifiableMap(map);
    }

    private PalletDimensions(String id, ImmutablePair<Integer, Integer> dimensions,
        String displayString) {
        this.id = id;
        this.dimensions = dimensions;
        this.displayLabel = displayString;
    }

    public String getId() {
        return id;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public Pair<Integer, Integer> getDimensions() {
        return dimensions;
    }

    public Integer getRows() {
        return dimensions.left;
    }

    public Integer getCols() {
        return dimensions.right;
    }

    public static Map<String, PalletDimensions> valuesMap() {
        return ID_MAP;
    }

    public static PalletDimensions getFromIdString(String id) {
        PalletDimensions result = valuesMap().get(id);
        if (result == null) {
            throw new IllegalStateException("invalid pallet dimensions: " + id);
        }
        return result;
    }

    public static PalletDimensions getDimensionsWithMaxRows() {
        PalletDimensions result = PalletDimensions.values()[0];
        for (PalletDimensions dimensions : PalletDimensions.values()) {
            if (result.getRows() < dimensions.getRows()) {
                result = dimensions;
            }
        }
        return result;
    }
}
