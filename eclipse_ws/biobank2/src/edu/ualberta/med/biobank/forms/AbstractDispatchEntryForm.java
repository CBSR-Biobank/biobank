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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public abstract class AbstractDispatchEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AbstractDispatchEntryForm.class.getName());

    protected DispatchWrapper dispatch;

    protected BiobankEntryFormWidgetListener biobankListener = new BiobankEntryFormWidgetListener() {
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

        dispatch = (DispatchWrapper) adapter.getModelObject();
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

    protected void createSpecimensSelectionActions(Composite composite,
        boolean setAsFirstControl) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter inventory ID to add:");
        final BiobankText newAliquotText = new BiobankText(addComposite,
            SWT.NONE, toolkit);
        GridData gd = new GridData();
        gd.widthHint = 100;
        newAliquotText.setLayoutData(gd);
        newAliquotText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                doSpecimenTextAction(newAliquotText.getText());
                newAliquotText.setFocus();
                newAliquotText.setText("");
            }
        });
        if (setAsFirstControl) {
            setFirstControl(newAliquotText);
        }
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
        addButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doSpecimenTextAction(newAliquotText.getText());
                newAliquotText.setFocus();
                newAliquotText.setText("");
            }
        });
        toolkit.createLabel(addComposite, "or open scan dialog:");
        Button openScanButton = toolkit
            .createButton(addComposite, "", SWT.PUSH);
        openScanButton.setImage(BiobankPlugin.getDefault().getImageRegistry()
            .get(BiobankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT));
        openScanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openScanDialog();
            }
        });
    }

    protected abstract void openScanDialog();

    protected abstract void doSpecimenTextAction(String text);

    @Override
    protected void saveForm() throws Exception {
        for (DispatchSpecimenWrapper da : dispatch
            .getDispatchSpecimenCollection(false)) {
            da.getSpecimen().persist();
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

}
