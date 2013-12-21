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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.search.SpecimenByInventorySearchAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDeleteItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable.ColumnsShown;
import edu.ualberta.med.biobank.widgets.infotables.entry.NewSpecimenEntryInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;

public class SpecimenEntryWidget extends BgcBaseWidget {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenEntryWidget.class);

    private List<SpecimenInfo> specimens;
    private final List<SpecimenInfo> addedSpecimens =
        new ArrayList<SpecimenInfo>();
    private final List<SpecimenInfo> removedSpecimens =
        new ArrayList<SpecimenInfo>();
    private NewSpecimenInfoTable specTable;
    private BgcBaseText newSpecimenInventoryId;
    private final boolean editable;
    private Button addButton;
    private final IObservableValue hasSpecimens = new WritableValue(
        Boolean.FALSE,
        Boolean.class);

    // TODO: not sure these should all be interruptable
    public static enum ItemAction {
        PRE_ADD,
        POST_ADD,
        PRE_DELETE,
        POST_DELETE;
    }

    private final VetoListenerSupport<ItemAction, SpecimenWrapper> vetoListenerSupport =
        new VetoListenerSupport<ItemAction, SpecimenWrapper>();

    public void addVetoListener(ItemAction action,
        VetoListener<ItemAction, SpecimenWrapper> listener) {
        vetoListenerSupport.addListener(action, listener);
    }

    public void removeVetoListener(ItemAction action,
        VetoListener<ItemAction, SpecimenWrapper> listener) {
        vetoListenerSupport.removeListener(action, listener);
    }

    @SuppressWarnings("nls")
    public SpecimenEntryWidget(Composite parent, int style,
        FormToolkit toolkit, boolean editable) {
        super(parent, style);
        Assert.isNotNull(toolkit, "toolkit is null");
        this.editable = editable;

        setLayout(new GridLayout(2, false));
        setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        toolkit.paintBordersFor(this);

        if (editable) {
            Label label = toolkit.createLabel(this,
                i18n.tr("Enter specimen inventory ID to add:"));
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
                        newSpecimenInventoryId.setText(StringUtil.EMPTY_STRING);
                    }
                });
            addButton =
                toolkit.createButton(this, StringUtil.EMPTY_STRING, SWT.PUSH);
            addButton.setImage(BgcPlugin.getDefault().getImage(BgcPlugin.Image.ADD));
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

    @SuppressWarnings("nls")
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
                    appService.doAction(new SpecimenGetInfoAction(specId));

                // Need to convert to table type
                SpecimenInfo spcInfo = new SpecimenInfo();
                spcInfo.specimen = bspecimen.getSpecimen();
                spcInfo.parentLabel = (bspecimen.getParents().size() > 0)
                    ? bspecimen.getParents().pop().getLabel()
                    : StringUtil.EMPTY_STRING;

                spcInfo.positionString =
                    (bspecimen.getSpecimen().getSpecimenPosition() != null)
                        ? bspecimen.getSpecimen().getSpecimenPosition()
                            .getPositionString()
                        : null;

                spcInfo.comment =
                    (bspecimen.getSpecimen().getComments().size() == 0)
                        ? i18n.trc("no abbeviation", "N")
                        : i18n.trc("yes abbeviation", "Y");

                try {
                    addSpecimen(spcInfo);
                } catch (VetoException e) {
                    BgcPlugin.openAsyncError(
                        e.getMessage());
                }
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    // dialog title.
                    i18n.tr("Error while looking up specimen"),
                    // dialog message.
                    i18n.tr("Specimen not found."));
            }
        }
    }

    @SuppressWarnings("nls")
    public void addSpecimen(SpecimenInfo specimen) throws VetoException {
        if (specimen != null && specimens.contains(specimen)) {
            BgcPlugin.openAsyncError(
                i18n.tr("Specimen {0} has already been added to this list",
                    specimen.specimen.getInventoryId()));
            return;
        }

        SpecimenWrapper wrap = new SpecimenWrapper(
            SessionManager.getAppService(), specimen.specimen);

        VetoListenerSupport.Event<ItemAction, SpecimenWrapper> preAdd =
            VetoListenerSupport.Event.newEvent(ItemAction.PRE_ADD, wrap);

        VetoListenerSupport.Event<ItemAction, SpecimenWrapper> postAdd =
            VetoListenerSupport.Event.newEvent(ItemAction.POST_ADD, wrap);

        vetoListenerSupport.notifyListeners(preAdd);

        specimens.add(specimen);
        // FIXME: sorting? Collections.sort(specimens);
        specTable.getList().clear();
        specTable.getList().addAll(specimens);
        addedSpecimens.add(specimen);
        removedSpecimens.remove(specimen);

        notifyListeners();
        hasSpecimens.setValue(true);

        vetoListenerSupport.notifyListeners(postAdd);
    }

    private void addDeleteSupport() {
        if (!editable)
            return;

        specTable.addDeleteItemListener(
            new IInfoTableDeleteItemListener<SpecimenInfo>() {
                @SuppressWarnings("nls")
                @Override
                public void deleteItem(InfoTableEvent<SpecimenInfo> event) {
                    SpecimenInfo specimen = specTable.getSelection();
                    if (specimen != null) {
                        if (!MessageDialog
                            .openConfirm(
                                PlatformUI.getWorkbench()
                                    .getActiveWorkbenchWindow().getShell(),
                                // dialog title.
                                i18n.tr("Delete Specimen"),
                                // dialog message.
                                i18n.tr(
                                    "Are you sure you want to remove specimen {0}?",
                                    specimen.specimen.getInventoryId()))) {
                            return;
                        }

                        try {
                            removeSpecimen(specimen);
                        } catch (VetoException e) {
                            BgcPlugin.openAsyncError(
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
            specTable.getList().remove(specimen);
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

    public void addDoubleClickListener(
        IInfoTableDoubleClickItemListener<SpecimenInfo> listener) {
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
