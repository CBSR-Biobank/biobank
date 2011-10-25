package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingEventEntryForm extends BiobankEntryForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ProcessingEventEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_PEVENT_OK = Messages.ProcessingEventEntryForm_creation_msg;

    private static final String MSG_PEVENT_OK = Messages.ProcessingEventEntryForm_edition_msg;

    private ProcessingEventAdapter pEventAdapter;

    private ProcessingEventWrapper pEvent;

    private ComboViewer activityStatusComboViewer;

    private DateTimeWidget dateWidget;

    private SpecimenEntryWidget specimenEntryWidget;

    private ActivityStatusWrapper closedActivityStatus;

    protected boolean tryAgain = false;

    private boolean isTryingAgain;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ProcessingEventAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        pEventAdapter = (ProcessingEventAdapter) adapter;
        pEvent = (ProcessingEventWrapper) getModelObject();

        String tabName;
        if (pEvent.isNew()) {
            tabName = Messages.ProcessingEventEntryForm_title_new;
            pEvent.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(SessionManager.getAppService()));
        } else {
            if (pEvent.getWorksheet() == null)
                tabName = NLS.bind(
                    Messages.ProcessingEventEntryForm_title_edit_worksheet,
                    pEvent.getWorksheet(), pEvent.getFormattedCreatedAt());
            else
                tabName = NLS.bind(
                    Messages.ProcessingEventEntryForm_title_edit_noworksheet,
                    pEvent.getFormattedCreatedAt());
        }
        closedActivityStatus = ActivityStatusWrapper.getActivityStatus(
            SessionManager.getAppService(),
            ActivityStatusWrapper.CLOSED_STATUS_STRING);
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.ProcessingEventEntryForm_main_title);
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSpecimensSection();
    }

    private void createMainSection() throws ApplicationException {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.ProcessingEvent_field_center_label, pEvent.getCenter()
                .getName());

        dateWidget = createDateTimeWidget(client,
            Messages.ProcessingEvent_field_date_label, pEvent.getCreatedAt(),
            pEvent, ProcessingEventPeer.CREATED_AT.getName(),
            new NotNullValidator(
                Messages.ProcessingEventEntryForm_field_date_validation_msg));
        setFirstControl(dateWidget);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.ProcessingEvent_field_worksheet_label, null, pEvent,
            ProcessingEventPeer.WORKSHEET.getName(),
            (!pEvent.isNew() && pEvent.getWorksheet() == null) ? null
                : new NonEmptyStringValidator(
                    Messages.ProcessingEventEntryForm_worksheet_validation_msg));

        activityStatusComboViewer = createComboViewer(client,
            Messages.label_activity,
            ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                .getAppService()), pEvent.getActivityStatus(),
            Messages.ProcessingEventEntryForm_field_activity_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    setDirty(true);
                    pEvent
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });
        if (pEvent.getActivityStatus() != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                pEvent.getActivityStatus()));
            setDirty(false);
        }

    }

    private void createSpecimensSection() {
        Composite client = createSectionWithClient(Messages.ProcessingEventEntryForm_specimens_title);
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        List<SpecimenWrapper> specimens = pEvent.getSpecimenCollection(true);

        specimenEntryWidget = new SpecimenEntryWidget(client, SWT.NONE,
            toolkit, SessionManager.getAppService(), true);
        specimenEntryWidget
            .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        specimenEntryWidget
            .addDoubleClickListener(collectionDoubleClickListener);

        VetoListener<ItemAction, SpecimenWrapper> vetoListener = new VetoListener<ItemAction, SpecimenWrapper>() {

            @Override
            public void handleEvent(Event<ItemAction, SpecimenWrapper> event)
                throws VetoException {
                SpecimenWrapper specimen = event.getObject();
                switch (event.getType()) {
                case PRE_ADD:
                    if (specimen == null)
                        throw new VetoException(
                            Messages.ProcessingEventEntryForm_notfound_spec_error_msg);
                    else if (!SessionManager.getUser()
                        .getCurrentWorkingCenter()
                        .equals(specimen.getCurrentCenter())) {
                        String centerName = Messages.ProcessingEventEntryForm_none_text;
                        if (specimen.getCurrentCenter() != null)
                            centerName = specimen.getCurrentCenter()
                                .getNameShort();
                        throw new VetoException(
                            NLS.bind(
                                Messages.ProcessingEventEntryForm_center_spec_error_msg,
                                centerName));
                    } else if (specimen.getProcessingEvent() != null) {
                        throw new VetoException(
                            NLS.bind(
                                Messages.ProcessingEventEntryForm_other_pEvent_error_msg,
                                specimen.getProcessingEvent().getWorksheet(),
                                specimen.getProcessingEvent()
                                    .getFormattedCreatedAt()));
                    } else if (!specimen.isActive())
                        throw new VetoException(
                            NLS.bind(
                                Messages.ProcessingEventEntryForm_spec_active_only_error_msg,
                                specimen.getActivityStatus().getName()));
                    else if (specimen.isUsedInDispatch())
                        throw new VetoException(
                            Messages.ProcessingEventEntryForm_spec_dispatch_error_msg);
                    else if (specimen.getParentContainer() != null)
                        throw new VetoException(
                            Messages.ProcessingEventEntryForm_stored_spec_error_msg);
                    else if (!SessionManager
                        .getUser()
                        .getCurrentWorkingCenter()
                        .getStudyCollection()
                        .contains(
                            specimen.getCollectionEvent().getPatient()
                                .getStudy()))
                        throw new VetoException(
                            NLS.bind(
                                Messages.ProcessingEventEntryForm_spec_study_allowed_only_error_msg,
                                specimen.getCollectionEvent().getPatient()
                                    .getStudy().getNameShort()));
                    else if (pEvent.getSpecimenCollection(false).size() > 0
                        && !pEvent
                            .getSpecimenCollection(false)
                            .get(0)
                            .getCollectionEvent()
                            .getPatient()
                            .getStudy()
                            .equals(
                                specimen.getCollectionEvent().getPatient()
                                    .getStudy()))
                        throw new VetoException(
                            Messages.ProcessingEventEntryForm_study_spec_error_msg);
                    break;
                case POST_ADD:
                    specimen.setProcessingEvent(pEvent);
                    specimen.setActivityStatus(closedActivityStatus);
                    pEvent.addToSpecimenCollection(Arrays.asList(specimen));
                    break;
                case PRE_DELETE:
                    if (specimen.getChildSpecimenCollection(false).size() > 0) {
                        boolean ok = BgcPlugin
                            .openConfirm(
                                Messages.ProcessingEventEntryForm_confirm_remove_title,
                                Messages.ProcessingEventEntryForm_confirm_remove_msg);
                        event.doit = ok;
                    }
                    break;
                case POST_DELETE:
                    pEvent
                        .removeFromSpecimenCollection(Arrays.asList(specimen));
                    break;
                }
            }
        };

        specimenEntryWidget.addBinding(widgetCreator,
            Messages.ProcessingEventEntryForm_specimens_validation_msg);

        specimenEntryWidget.addVetoListener(ItemAction.PRE_ADD, vetoListener);
        specimenEntryWidget.addVetoListener(ItemAction.POST_ADD, vetoListener);
        specimenEntryWidget
            .addVetoListener(ItemAction.PRE_DELETE, vetoListener);
        specimenEntryWidget.addVetoListener(ItemAction.POST_DELETE,
            vetoListener);

        specimenEntryWidget.setSpecimens(specimens);
    }

    @Override
    protected void saveForm() throws Exception {
        List<Integer> specimens = new ArrayList<Integer>();
        for (SpecimenWrapper spc : pEvent.getSpecimenCollection(false)) {
            specimens.add(spc.getId());
        }

        Integer peventId = SessionManager.getAppService().doAction(
            new ProcessingEventSaveAction(pEvent.getId(), pEvent.getCenter()
                .getId(), pEvent.getCreatedAt(), pEvent.getWorksheet(), pEvent
                .getActivityStatus().getId(), null, specimens));
        adapter.setId(peventId);
        // FIXME figure out if still need this. But should probably be on the
        // action side now
        // try {
        // pEvent.persist();
        // } catch (ModificationConcurrencyException mc) {
        // if (isTryingAgain) {
        // // already tried once
        // throw mc;
        // }
        // Display.getDefault().syncExec(new Runnable() {
        // @Override
        // public void run() {
        // tryAgain = BgcPlugin
        // .openConfirm(
        // Messages.ProcessingEventEntryForm_save_error_title,
        // Messages.ProcessingEventEntryForm_concurrency_error_msg);
        // setDirty(true);
        // try {
        // doTrySettingAgain();
        // tryAgain = true;
        // } catch (Exception e) {
        // saveErrorCatch(e, null, true);
        // }
        // }
        // });
        // }
    }

    @Override
    protected void doAfterSave() throws Exception {
        if (tryAgain) {
            isTryingAgain = true;
            tryAgain = false;
            confirm();
        } else
            SessionManager.updateAllSimilarNodes(pEventAdapter, true);
    }

    protected void doTrySettingAgain() throws Exception {
        // remove added specimens and add removed specimens and try to
        // add/remove them again (after reloading them) through the
        // SpecimenEntryWidget to check again if can perform the action

        List<SpecimenWrapper> addedSpecimens = specimenEntryWidget
            .getAddedSpecimens();

        List<SpecimenWrapper> removedSpecimens = specimenEntryWidget
            .getRemovedSpecimens();
        List<SpecimenWrapper> pEventSpecs = pEvent.getSpecimenCollection(false);
        pEventSpecs.removeAll(addedSpecimens);
        pEventSpecs.addAll(removedSpecimens);
        for (SpecimenWrapper sp : pEventSpecs) {
            sp.reload();
        }
        pEvent.setSpecimenWrapperCollection(pEventSpecs);
        specimenEntryWidget.setSpecimens(pEventSpecs);

        Map<String, String> problems = new HashMap<String, String>();
        for (SpecimenWrapper spec : addedSpecimens) {
            String inventoryId = spec.getInventoryId();
            try {
                spec.reload();
                specimenEntryWidget.addSpecimen(spec);
            } catch (Exception ex) {
                problems
                    .put(
                        Messages.ProcessingEventEntryForm_try_again_adding_error_label
                            + " " + inventoryId, ex.getMessage()); //$NON-NLS-1$
            }
        }
        for (SpecimenWrapper spec : removedSpecimens) {
            String inventoryId = spec.getInventoryId();
            try {
                spec.reload();
                specimenEntryWidget.removeSpecimen(spec);
            } catch (Exception ex) {
                problems
                    .put(
                        Messages.ProcessingEventEntryForm_try_again_removing_error_label
                            + " " + inventoryId, ex.getMessage()); //$NON-NLS-1$
            }
        }
        if (problems.size() != 0) {
            StringBuffer msg = new StringBuffer();
            for (Entry<String, String> entry : problems.entrySet()) {
                if (msg.length() > 0)
                    msg.append("\n"); //$NON-NLS-1$
                msg.append(entry.getKey()).append(": ") //$NON-NLS-1$
                    .append(entry.getValue());
            }
            throw new BiobankException(
                Messages.ProcessingEventEntryForm_try_again_error_msg
                    + msg.toString());
        }
    }

    @Override
    protected String getOkMessage() {
        return (pEvent.isNew()) ? MSG_NEW_PEVENT_OK : MSG_PEVENT_OK;
    }

    @Override
    public String getNextOpenedFormID() {
        return ProcessingEventViewForm.ID;
    }

    @Override
    protected void onReset() throws Exception {
        CenterWrapper<?> center = pEvent.getCenter();
        pEvent.reset();
        pEvent.setCenter(center);
        if (pEvent.isNew()) {
            pEvent.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(SessionManager.getAppService()));
        }
        GuiUtil.reset(activityStatusComboViewer, pEvent.getActivityStatus());
        specimenEntryWidget.setSpecimens(pEvent.getSpecimenCollection(true));
    }
}
