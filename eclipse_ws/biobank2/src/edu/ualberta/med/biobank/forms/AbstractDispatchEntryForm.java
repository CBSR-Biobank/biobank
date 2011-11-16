package edu.ualberta.med.biobank.forms;

import java.util.List;

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
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.info.CommentInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchFormReadInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.info.ShippingMethodInfo;
import edu.ualberta.med.biobank.common.action.shipment.OriginInfoSaveAction;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.util.WrapperUtil;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModificationConcurrencyException;
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

    private boolean isTryingAgain;

    protected boolean tryAgain;

    protected DispatchFormReadInfo dispatchInfo;

    private Dispatch dispatchModel;
    
    protected Comment comment;

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null"); //$NON-NLS-1$
        Assert.isTrue((adapter instanceof DispatchAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        dispatchModel = new Dispatch();
        if (adapter.getId()!=null) {
        dispatchInfo = SessionManager.getAppService().doAction(
            new DispatchGetInfoAction(adapter.getId()));
            copyInfo();
        }
        dispatch= new DispatchWrapper(SessionManager.getAppService(), dispatchModel);
        SessionManager.logEdit(dispatch);

        setPartName(getTextForPartName());
    }
    
    public void copyInfo() {
        dispatchModel.setReceiverCenter(dispatchInfo.dispatch.getReceiverCenter());
        dispatchModel.setSenderCenter(dispatchInfo.dispatch.getSenderCenter());
        dispatchModel.setShipmentInfo(dispatchInfo.dispatch.getShipmentInfo());
        dispatchModel.setState(dispatchInfo.dispatch.getState());
        dispatchModel.setDispatchSpecimenCollection(dispatchInfo.specimens);
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
        toolkit.createLabel(addComposite,
            Messages.AbstractDispatchEntryForm_add_spec_label);
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
                newSpecimenText.setText(""); //$NON-NLS-1$
            }
        });
        if (setAsFirstControl) {
            setFirstControl(newSpecimenText);
        }
        Button addButton = toolkit.createButton(addComposite, "", SWT.PUSH); //$NON-NLS-1$
        addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        addButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                doSpecimenTextAction(newSpecimenText.getText());
                newSpecimenText.setFocus();
                newSpecimenText.setText(""); //$NON-NLS-1$
            }
        });
        toolkit.createLabel(addComposite,
            Messages.AbstractDispatchEntryForm_scanDialog_label);
        Button openScanButton = toolkit
            .createButton(addComposite, "", SWT.PUSH); //$NON-NLS-1$
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

    protected boolean needToTryAgainIfConcurrency() {
        return false;
    }

    @SuppressWarnings("unused")
    protected void doTrySettingAgain() throws Exception {
        // default does nothing
    }

    @Override
    protected void doAfterSave() throws Exception {
        if (tryAgain) {
            isTryingAgain = true;
            tryAgain = false;
            confirm();
        } else
            SpecimenTransitView.getCurrent().reload();
    }

    protected abstract void reloadSpecimens();

    @Override
    protected void onReset() throws Exception {
        dispatch.reset();
        reloadSpecimens();
    }
}
