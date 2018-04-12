package edu.ualberta.med.scannerconfig.widgets;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.GroupedRadioSelectionWidget;
import edu.ualberta.med.scannerconfig.PalletOrientation;

/**
 * A widget that allows the user to select an item from the enumerated type
 * {@link PalletOrientation}.
 * 
 * @author nelson
 * 
 */
public class PalletOrientationWidget extends GroupedRadioSelectionWidget<PalletOrientation> {

    private static final I18n i18n = I18nFactory.getI18n(PalletOrientationWidget.class);

    private static final Map<PalletOrientation, String> SELECTIONS_MAP;

    static {
        Map<PalletOrientation, String> map = new LinkedHashMap<PalletOrientation, String>();

        for (PalletOrientation orientation : PalletOrientation.values()) {
            map.put(orientation, orientation.getDisplayLabel());
        }

        SELECTIONS_MAP = Collections.unmodifiableMap(map);
    }

    @SuppressWarnings("nls")
    public PalletOrientationWidget(Composite parent, PalletOrientation initialValue) {
        super(parent, i18n.tr("Pallet orientation"), SELECTIONS_MAP, initialValue);
    }
}
