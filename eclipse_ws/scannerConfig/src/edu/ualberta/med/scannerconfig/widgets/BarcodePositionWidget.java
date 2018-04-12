package edu.ualberta.med.scannerconfig.widgets;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.GroupedRadioSelectionWidget;
import edu.ualberta.med.scannerconfig.BarcodePosition;

/**
 * A widget that allows the user to select the positions of the barcodes in an image. The barcodes
 * can all be either on top or bottom of the tubes.
 * 
 * @author loyola
 * 
 */
public class BarcodePositionWidget extends GroupedRadioSelectionWidget<BarcodePosition> {

    private static final I18n i18n = I18nFactory.getI18n(BarcodePositionWidget.class);

    private static final Map<BarcodePosition, String> SELECTIONS_MAP;

    static {
        Map<BarcodePosition, String> map = new LinkedHashMap<BarcodePosition, String>();

        for (BarcodePosition orientation : BarcodePosition.values()) {
            map.put(orientation, orientation.getDisplayLabel());
        }

        SELECTIONS_MAP = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("nls")
    public BarcodePositionWidget(Composite parent, BarcodePosition initialValue) {
        super(parent, i18n.tr("Barcode position"), SELECTIONS_MAP, initialValue);
    }
}
