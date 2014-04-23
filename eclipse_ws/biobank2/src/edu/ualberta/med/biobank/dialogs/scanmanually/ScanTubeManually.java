package edu.ualberta.med.biobank.dialogs.scanmanually;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.dialogs.PersistedDialog;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.scannerconfig.dialogs.DecodeImageDialog;

public class ScanTubeManually extends PersistedDialog implements Listener {

    private static final I18n i18n = I18nFactory.getI18n(DecodeImageDialog.class);

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Scan Tube Manually");

    private final String labelToScan;

    private final BidiMap existingInventoryIds = new DualHashBidiMap();

    private Text inventoryIdText;

    private String inventoryId;

    public ScanTubeManually(
        Shell parentShell,
        Set<String> labels,
        Map<String, String> existingInventoryIdsByLabel) {
        super(parentShell);
        this.labelToScan = labels.iterator().next();

        // convert to a BidiMap
        for (Entry<String, String> entry : existingInventoryIdsByLabel.entrySet()) {
            existingInventoryIds.put(entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("nls")
    @Override
    protected String getTitleAreaMessage() {
        return i18n.tr("Scan the tube at position {0}", labelToScan);
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        Composite area = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 0;
        area.setLayout(layout);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        area.setLayoutData(gridData);

        Label label = new Label(area, SWT.LEFT);
        label.setText(Specimen.PropertyName.INVENTORY_ID.toString() + ":");

        inventoryIdText = new Text(area, SWT.BORDER | SWT.SINGLE);
        inventoryIdText.setText(StringUtil.EMPTY_STRING);
        inventoryIdText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        inventoryIdText.addListener(SWT.KeyUp, this);
        inventoryIdText.addListener(SWT.Selection, this);
        inventoryIdText.addListener(SWT.MouseUp, this);

        inventoryIdText.setFocus();
    }

    @SuppressWarnings("nls")
    @Override
    public void handleEvent(Event event) {
        inventoryId = inventoryIdText.getText();

        // check if this value already exists
        String label = (String) existingInventoryIds.getKey(inventoryId);

        if (!inventoryId.isEmpty() && (label != null) && !label.equals(labelToScan)) {
            // TR: wizard page error message
            setErrorMessage(i18n.tr("The value entered already exists at position {0}", label));
            setMessage(null);
        } else {
            existingInventoryIds.put(labelToScan, inventoryId);
            setErrorMessage(null);
            setMessage(i18n.tr("Scan the tube at position {0}", labelToScan));
        }
    }

    public Map<String, String> getInventoryId() {
        Map<String, String> result = new HashMap<String, String>(1);
        result.put(labelToScan, inventoryId);
        return result;
    }

}
