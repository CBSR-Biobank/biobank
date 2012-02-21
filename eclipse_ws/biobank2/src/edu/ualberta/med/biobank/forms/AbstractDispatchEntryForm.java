package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public abstract class AbstractDispatchEntryForm extends BiobankEntryForm {

    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractDispatchEntryForm.class.getName());

    protected DispatchWrapper dispatch;

    protected BgcEntryFormWidgetListener biobankListener = new BgcEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            reloadSpecimens();
            setDirty(true);
        }
    };

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null");
        Assert.isTrue((adapter instanceof DispatchAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        dispatch = (DispatchWrapper) getModelObject();
        SessionManager.logEdit(dispatch);
        retrieveShipment();

        setPartName(getTextForPartName());
    }

    private void retrieveShipment() {
        try {
            dispatch.reload();
        } catch (Exception ex) {
            logger.error("Error while retrieving shipment "
                + dispatch.getShipmentInfo().getWaybill(), ex);
        }
    }

    protected abstract String getTextForPartName();

    /**
     * Create a field to enter inventory id one by one + a button to open a scan
     * dialog
     */
    protected void createSpecimensSelectionActions(Composite composite,
        boolean setAsFirstControl) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter inventory ID to add:");
        final BgcBaseText newSpecimenText = new BgcBaseText(addComposite,
            SWT.NONE, toolkit);
        GridData gd = new GridData();
        gd.widthHint = 100;
        newSpecimenText.setLayoutData(gd);
        newSpecimenText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                doSpecimenTextAction(newSpecimenText.getText());
                newSpecimenText.setFocus();
                newSpecimenText.setText("");
            }
        });
        if (setAsFirstControl) {
            setFirstControl(newSpecimenText);
        }
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
        addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doSpecimenTextAction(newSpecimenText.getText());
                newSpecimenText.setFocus();
                newSpecimenText.setText("");
            }
        });
        toolkit.createLabel(addComposite, "or open scan dialog:");
        Button openScanButton = toolkit
            .createButton(addComposite, "", SWT.PUSH);
        openScanButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_DISPATCH_SHIPMENT_ADD_SPECIMEN));
        openScanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openScanDialog();
            }
        });
    }

    /**
     * open scan when click on button
     */
    protected abstract void openScanDialog();

    /**
     * add specimen represented by the inventoryid entered in the text field
     */
    protected abstract void doSpecimenTextAction(String text);

    @Override
    protected void saveForm() throws Exception {

        // If there is no TempLogger Device ID entered
        // Note: If there is has been a TempLogger previously persisted clearing
        // the device ID entry will not delete the record, no update will occur
        // for device ID
        if (dispatch.getShipmentInfo() != null
            && dispatch.getShipmentInfo().getShipmentTempLogger() != null
            && (dispatch.getShipmentInfo().getShipmentTempLogger()
                .getDeviceId() == null || dispatch.getShipmentInfo()
                .getShipmentTempLogger().getDeviceId().isEmpty())) {
            dispatch.getShipmentInfo().setShipmentTempLogger(null);
        }
        dispatch.persist();

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                SpecimenTransitView.getCurrent().reload();
            }
        });
    }

    protected abstract void reloadSpecimens();

    @Override
    protected void onReset() throws Exception {
        dispatch.reset();
        reloadSpecimens();
    }
}
