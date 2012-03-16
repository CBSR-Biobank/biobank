package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.search.SpecimenByInventorySearchAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable.ColumnsShown;
import edu.ualberta.med.biobank.widgets.infotables.entry.NewSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenEntryWidget extends BgcBaseWidget {
    private WritableApplicationService appService;
    private List<SpecimenInfo> specimens;
    private List<SpecimenInfo> addedSpecimens =
        new ArrayList<SpecimenInfo>();
    private List<SpecimenInfo> removedSpecimens =
        new ArrayList<SpecimenInfo>();
    private NewSpecimenInfoTable specTable;
    private BgcBaseText newSpecimenInventoryId;
    private boolean editable;
    private Button addButton;
    private IObservableValue hasSpecimens = new WritableValue(Boolean.FALSE,
        Boolean.class);

    // TODO: not sure these should all be interruptable
    public static enum ItemAction {
        PRE_ADD,
        POST_ADD,
        PRE_DELETE,
        POST_DELETE;
    }

    private VetoListenerSupport<ItemAction, SpecimenWrapper> vetoListenerSupport =
        new VetoListenerSupport<ItemAction, SpecimenWrapper>();

    public void addVetoListener(ItemAction action,
        VetoListener<ItemAction, SpecimenWrapper> listener) {
        vetoListenerSupport.addListener(action, listener);
    }

    public void removeVetoListener(ItemAction action,
        VetoListener<ItemAction, SpecimenWrapper> listener) {
        vetoListenerSupport.removeListener(action, listener);
    }

    public SpecimenEntryWidget(Composite parent, int style,
        FormToolkit toolkit, WritableApplicationService appService,
        boolean editable) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null"); //$NON-NLS-1$
        this.appService = appService;
        this.editable = editable;

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        toolkit.paintBordersFor(this);

        if (editable) {
            Label label = toolkit.createLabel(this,
                Messages.SpecimenEntryWidget_inventoryid_label);
            GridData gd = new GridData();
            gd.horizontalSpan = 2;
            label.setLayoutData(gd);
            newSpecimenInventoryId = new BgcBaseText(this, SWT.NONE, toolkit);
            newSpecimenInventoryId.addListener(SWT.DefaultSelection,
                new Listener() {
                    @Override
                    public void handleEvent(Event e) {
                        addSpecimen();
                        newSpecimenInventoryId.setFocus();
                        newSpecimenInventoryId.setText(""); //$NON-NLS-1$
                    }
                });
            addButton = toolkit.createButton(this, "", SWT.PUSH); //$NON-NLS-1$
            addButton.setImage(BgcPlugin.getDefault().getImageRegistry()
                .get(BgcPlugin.IMG_ADD));
            addButton.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    addSpecimen();
                }
            });
        }

        if (editable) {
            specTable = new NewSpecimenEntryInfoTable(this, null,
                ColumnsShown.PEVENT_SOURCE_SPECIMENS);
        } else {
            specTable = new NewSpecimenInfoTable(this, null,
                ColumnsShown.PEVENT_SOURCE_SPECIMENS, 20);
        }

        specTable.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.horizontalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        specTable.setLayoutData(gd);
        addDeleteSupport();
    }

    public List<SpecimenInfo> getAddedSpecimens() {
        return new ArrayList<SpecimenInfo>(addedSpecimens);
    }

    public List<SpecimenInfo> getRemovedSpecimens() {
        return new ArrayList<SpecimenInfo>(removedSpecimens);
    }

    private void addSpecimen() {
        BiobankApplicationService appService = SessionManager.getAppService();
        String inventoryId = newSpecimenInventoryId.getText().trim();
        if (!inventoryId.isEmpty()) {
            SpecimenBriefInfo bspecimen = null;
            try {
                Integer specId = appService.doAction(
                    new SpecimenByInventorySearchAction(inventoryId,
                        SessionManager.getUser().getCurrentWorkingCenter()
                            .getId())).getList().get(0);
                bspecimen =
                    appService.doAction(
                        new SpecimenGetInfoAction(specId));
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    Messages.SpecimenEntryWidget_retrieve_error_title,
                    "Specimen not found.");
            }
            // Need to convert to table type
            SpecimenInfo ispecimen =
                new SpecimenInfo();
            ispecimen.specimen = bspecimen.getSpecimen();
            ispecimen.parentLabel =
                bspecimen.getParents().size() > 0 ? bspecimen.getParents()
                    .pop().getLabel() : "";
            ispecimen.positionString =
                bspecimen.getSpecimen().getSpecimenPosition() != null ?
                    bspecimen.getSpecimen().getSpecimenPosition()
                        .getPositionString() : null;
            ispecimen.comments =
                bspecimen.getSpecimen().getComments().size() == 0 ? "N"
                    : "Y";

            if (ispecimen != null)
                try {
                    addSpecimen(ispecimen);
                } catch (VetoException e) {
                    BgcPlugin.openAsyncError(
                        Messages.SpecimenEntryWidget_error_title,
                        e.getMessage());
                }
        }
    }

    public void addSpecimen(SpecimenInfo specimen) throws VetoException {
        if (specimen != null && specimens.contains(specimen)) {
            BgcPlugin.openAsyncError(Messages.SpecimenEntryWidget_error_title,
                NLS.bind(Messages.SpecimenEntryWidget_already_added_error_msg,
                    specimen.specimen.getInventoryId()));
            return;
        }

        SpecimenWrapper wrap =
            new SpecimenWrapper(SessionManager.getAppService(),
                specimen.specimen);

        VetoListenerSupport.Event<ItemAction, SpecimenWrapper> preAdd =
            VetoListenerSupport.Event
                .newEvent(ItemAction.PRE_ADD, wrap);

        VetoListenerSupport.Event<ItemAction, SpecimenWrapper> postAdd =
            VetoListenerSupport.Event
                .newEvent(ItemAction.POST_ADD, wrap);

        vetoListenerSupport.notifyListeners(preAdd);

        specimens.add(specimen);
        // FIXME: sorting? Collections.sort(specimens);
        specTable.setList(specimens);
        addedSpecimens.add(specimen);
        removedSpecimens.remove(specimen);

        notifyListeners();
        hasSpecimens.setValue(true);

        vetoListenerSupport.notifyListeners(postAdd);
    }

    private void addDeleteSupport() {
        if (!editable)
            return;

        specTable
            .addDeleteItemListener(new IInfoTableDeleteItemListener<SpecimenInfo>() {
                @Override
                public void deleteItem(InfoTableEvent<SpecimenInfo> event) {
                    SpecimenInfo specimen = specTable.getSelection();
                    if (specimen != null) {
                        if (!MessageDialog.openConfirm(
                            PlatformUI.getWorkbench()
                                .getActiveWorkbenchWindow().getShell(),
                            Messages.SpecimenEntryWidget_delete_question_title,
                            NLS.bind(
                                Messages.SpecimenEntryWidget_delete_question_msg,
                                specimen.specimen.getInventoryId()))) {
                            return;
                        }

                        try {
                            removeSpecimen(specimen);
                        } catch (VetoException e) {
                            BgcPlugin.openAsyncError(
                                Messages.SpecimenEntryWidget_error_title,
                                e.getMessage());
                        }
                    }
                }

            });
    }

    public void removeSpecimen(SpecimenInfo specimen) throws VetoException {
        SpecimenWrapper wrap =
            new SpecimenWrapper(SessionManager.getAppService(),
                specimen.specimen);

        VetoListenerSupport.Event<ItemAction, SpecimenWrapper> preDelete =
            VetoListenerSupport.Event
                .newEvent(ItemAction.PRE_DELETE, wrap);

        VetoListenerSupport.Event<ItemAction, SpecimenWrapper> postDelete =
            VetoListenerSupport.Event
                .newEvent(ItemAction.POST_DELETE, wrap);

        vetoListenerSupport.notifyListeners(preDelete);
        if (preDelete.doit) {
            specimens.remove(specimen);
            // FIXME: sorting? Collections.sort(specimens);
            specTable.setList(specimens);
            removedSpecimens.add(specimen);
            addedSpecimens.remove(specimen);

            notifyListeners();
            hasSpecimens.setValue(specimens.size() > 0);

            vetoListenerSupport.notifyListeners(postDelete);
        }
    }

    public void setSpecimens(List<SpecimenInfo> specimens) {
        // don't want to work on exactly the same list. This will be the gui
        // list only.
        this.specimens = new ArrayList<SpecimenInfo>(specimens);

        if (specimens != null)
            specTable.setList(specimens);
        else
            specTable.setList(new ArrayList<SpecimenInfo>());

        addedSpecimens.clear();
        removedSpecimens.clear();

        hasSpecimens.setValue(specimens != null && specimens.size() > 0);
    }

    public void addDoubleClickListener(IDoubleClickListener listener) {
        specTable.addClickListener(listener);
    }

    public void addBinding(BgcWidgetCreator dbc, final String message) {
        final ControlDecoration controlDecoration = createDecorator(addButton,
            message);
        WritableValue wv = new WritableValue(Boolean.FALSE, Boolean.class);
        UpdateValueStrategy uvs = new UpdateValueStrategy();
        uvs.setAfterGetValidator(new IValidator() {
            @Override
            public IStatus validate(Object value) {
                if (value instanceof Boolean && !(Boolean) value) {
                    controlDecoration.show();
                    return ValidationStatus.error(message);
                }
                controlDecoration.hide();
                return Status.OK_STATUS;
            }
        });
        dbc.bindValue(wv, hasSpecimens, uvs, uvs);
    }
}
