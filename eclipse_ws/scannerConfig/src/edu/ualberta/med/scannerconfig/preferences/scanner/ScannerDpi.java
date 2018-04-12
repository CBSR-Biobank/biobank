package edu.ualberta.med.scannerconfig.preferences.scanner;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

@SuppressWarnings("nls")
public enum ScannerDpi {
    DPI_UNKNOWN(0, Constants.i18n.tr("Unknown")),
    DPI_300(300, Constants.i18n.tr("300")),
    DPI_400(400, Constants.i18n.tr("400")),
    DPI_600(600, Constants.i18n.tr("600"));

    private static class Constants {
        private static final I18n i18n = I18nFactory.getI18n(ScannerDpi.class);
    }

    public static final int size = values().length;

    private final int value;
    private final String displayLabel;

    private static final Map<Integer, ScannerDpi> VALUES_MAP;
    private static final ScannerDpi[] validDpis;

    static {
        Map<Integer, ScannerDpi> map = new LinkedHashMap<Integer, ScannerDpi>();

        for (ScannerDpi enumValue : values()) {
            ScannerDpi check = map.get(enumValue.getValue());
            if (check != null) {
                throw new IllegalStateException("scan plate value "
                    + enumValue.getValue() + " used multiple times");
            }

            map.put(enumValue.getValue(), enumValue);
        }

        VALUES_MAP = Collections.unmodifiableMap(map);

        // NOTE! update this array if new DPIs are added
        validDpis = new ScannerDpi[] { DPI_300, DPI_400, DPI_600 };
    }

    private ScannerDpi(int value, String displayString) {
        this.value = value;
        this.displayLabel = displayString;
    }

    public int getValue() {
        return value;
    }

    public String getDisplayLabel() {
        return displayLabel;
    }

    public static ScannerDpi[] getValidDpis() {
        return validDpis;
    }

    public static Map<Integer, ScannerDpi> valuesMap() {
        return VALUES_MAP;
    }

    public static ScannerDpi getFromId(int id) {
        ScannerDpi result = valuesMap().get(id);
        if (result == null) {
            return DPI_UNKNOWN;
        }
        return result;
    }

}
