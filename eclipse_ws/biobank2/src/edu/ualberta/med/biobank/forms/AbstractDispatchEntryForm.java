package edu.ualberta.med.biobank.forms;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchGetInfoAction;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.info.DispatchReadInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.views.SpecimenTransitView;

public abstract class AbstractDispatchEntryForm extends BiobankEntryForm {

    @SuppressWarnings("unused")
    private static BgcLogger logger = BgcLogger
        .getLogger(AbstractDispatchEntryForm.class.getName());

    protected DispatchWrapper dispatch = new DispatchWrapper(
        SessionManager.getAppService());

    protected BgcEntryFormWidgetListener biobankListener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                reloadSpecimens();
                setDirty(true);
            }
        };

    protected boolean tryAgain;

    protected DispatchReadInfo dispatchInfo;

    protected CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    protected Set<Integer> oldSpecIds;

    @Override
    protected void init() throws Exception {
        Assert.isNotNull(adapter, "Adapter should be no null"); //$NON-NLS-1$
        Assert.isTrue((adapter instanceof DispatchAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        setDispatchInfo(adapter.getId());

        SessionManager.logEdit(dispatch);

        setPartName(getTextForPartName());
    }

    private void setDispatchInfo(Integer id) throws Exception {
        if (id == null) {
            Dispatch d = new Dispatch();
            dispatch.setWrappedObject(d);
        } else {
            DispatchReadInfo read =
                SessionManager.getAppService().doAction(
                    new DispatchGetInfoAction(adapter.getId()));
            read.dispatch
                .setDispatchSpecimenCollection(read.specimens);
            dispatch.setWrappedObject(read.dispatch);
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

    protected void doTrySettingAgain() throws Exception {
        // default does nothing
    }

    @Override
    protected void doAfterSave() throws Exception {
        if (tryAgain) {
            tryAgain = false;
            confirm();
        } else
            SpecimenTransitView.getCurrent().reload();
    }

    protected abstract void reloadSpecimens();

    @Override
    public void setValues() throws Exception {
        reloadSpecimens();
    }

    @Override
    protected void saveForm() throws Exception {

        Set<DispatchSpecimenInfo> dsInfos = new HashSet<DispatchSpecimenInfo>();
        for (DispatchSpecimenWrapper ds : dispatch
            .getDispatchSpecimenCollection(false))
            dsInfos.add(new DispatchSpecimenInfo(ds.getId(), ds.getSpecimen()
                .getId(), ds.getState()));
        DispatchSaveInfo dInfo =
            new DispatchSaveInfo(dispatch.getId(), dispatch.getReceiverCenter()
                .getId(), dispatch.getSenderCenter().getId(),
                dispatch.getState(), comment.getMessage() == null ? ""
                    : comment.getMessage());
        DispatchSaveAction save = new DispatchSaveAction(dInfo, dsInfos, null);
        dispatch.setId(SessionManager.getAppService().doAction(save).getId());
        ((AdapterBase) adapter).setModelObject(dispatch);
    }
}
