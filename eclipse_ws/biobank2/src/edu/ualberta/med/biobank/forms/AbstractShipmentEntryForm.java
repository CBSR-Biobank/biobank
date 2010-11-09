package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.DispatchAdministrationView;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;

public abstract class AbstractShipmentEntryForm extends BiobankEntryForm {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(AbstractShipmentEntryForm.class.getName());

    protected DispatchWrapper dispatch;

    protected BiobankEntryFormWidgetListener biobankListener = new BiobankEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            reloadAliquots();
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
        retrieveShipment();

        setPartName(getTextForPartName());
    }

    private void retrieveShipment() {
        try {
            dispatch.reload();
        } catch (Exception ex) {
            logger.error(
                "Error while retrieving shipment " + dispatch.getWaybill(), ex);
        }
    }

    protected abstract String getTextForPartName();

    protected void createAliquotsSelectionActions(Composite composite,
        boolean setAsFirstControl) {
        Composite addComposite = toolkit.createComposite(composite);
        addComposite.setLayout(new GridLayout(5, false));
        toolkit.createLabel(addComposite, "Enter inventory ID to add:");
        final BiobankText newAliquotText = new BiobankText(addComposite,
            SWT.NONE, toolkit);
        newAliquotText.addListener(SWT.DefaultSelection, new Listener() {
            @Override
            public void handleEvent(Event e) {
                doAliquotTextAction(newAliquotText.getText());
                newAliquotText.setFocus();
                newAliquotText.setText("");
            }
        });
        if (setAsFirstControl) {
            setFirstControl(newAliquotText);
        }
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH);
        addButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doAliquotTextAction(newAliquotText.getText());
                newAliquotText.setFocus();
                newAliquotText.setText("");
            }
        });
        toolkit.createLabel(addComposite, "or open scan dialog:");
        Button openScanButton = toolkit
            .createButton(addComposite, "", SWT.PUSH);
        openScanButton.setImage(BioBankPlugin.getDefault().getImageRegistry()
            .get(BioBankPlugin.IMG_DISPATCH_SHIPMENT_ADD_ALIQUOT));
        openScanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                openScanDialog();
            }
        });
    }

    protected abstract void openScanDialog();

    protected abstract void doAliquotTextAction(String text);

    @Override
    protected void saveForm() throws Exception {
        dispatch.persist();
        // adapter.getParent().performExpand();
        // FIXME: Would prefer to use this call, but in cases of errors
        // sometimes tree structure can change
        // This reload call results in more searches when interacting with the
        // form (tree becomes out of sync with adapters in forms)
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                DispatchAdministrationView.getCurrent().reload();
            }
        });
    }

    protected abstract void reloadAliquots();

}
