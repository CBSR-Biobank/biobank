package edu.ualberta.med.scannerconfig.widgets;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.GroupedRadioSelectionWidget;
import edu.ualberta.med.scannerconfig.preferences.scanner.ScannerDpi;

public class ScannerDpiWidget extends GroupedRadioSelectionWidget<ScannerDpi> {

    private static final I18n i18n = I18nFactory.getI18n(ScannerDpiWidget.class);

    private static final Map<ScannerDpi, String> SELECTIONS_MAP;

    static {
        Map<ScannerDpi, String> map = new LinkedHashMap<ScannerDpi, String>();

        for (ScannerDpi orientation : ScannerDpi.getValidDpis()) {
            map.put(orientation, orientation.getDisplayLabel());
        }

        SELECTIONS_MAP = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("nls")
    public ScannerDpiWidget(Composite parent, ScannerDpi initialValue) {
        super(parent, i18n.tr("DPI"), SELECTIONS_MAP, initialValue);
    }
}
