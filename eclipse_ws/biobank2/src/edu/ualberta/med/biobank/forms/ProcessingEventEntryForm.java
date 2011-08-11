package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.SessionManager;
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
import edu.ualberta.med.biobank.server.applicationservice.exceptions.ModificationConcurrencyException;
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

    protected List<SpecimenWrapper> removedSpecimens;

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
                .getActiveActivityStatus(appService));
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
            appService, ActivityStatusWrapper.CLOSED_STATUS_STRING);
        removedSpecimens = new ArrayList<SpecimenWrapper>();
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
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            pEvent.getActivityStatus(),
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

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            Messages.label_comments, null, pEvent,
            ProcessingEventPeer.COMMENT.getName(), null);
    }

    private void createSpecimensSection() {
        Composite client = createSectionWithClient(Messages.ProcessingEventEntryForm_specimens_title);
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        List<SpecimenWrapper> specimens = pEvent.getSpecimenCollection(true);

        specimenEntryWidget = new SpecimenEntryWidget(client, SWT.NONE,
            toolkit, appService, true);
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
                    else if (specimen.getProcessingEvent() != null) {
                        throw new VetoException(
                            NLS.bind(
                                Messages.ProcessingEventEntryForm_other_pEvent_error_msg,
                                specimen.getProcessingEvent().getWorksheet(),
                                specimen.getProcessingEvent()
                                    .getFormattedCreatedAt()));
                    }
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
                    removedSpecimens.add(specimen);
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
        try {
            pEvent.persist();
        } catch (ModificationConcurrencyException mc) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        // can be very annoying to start over, so reload
                        // everything and try again (only once!)
                        List<SpecimenWrapper> specs = new ArrayList<SpecimenWrapper>(
                            pEvent.getSpecimenCollection(false));
                        ActivityStatusWrapper as = pEvent.getActivityStatus();
                        CenterWrapper<?> center = pEvent.getCenter();
                        String comment = pEvent.getComment();
                        Date createdAt = pEvent.getCreatedAt();
                        String worksheet = pEvent.getWorksheet();
                        pEvent.reset();
                        pEvent.setActivityStatus(as);
                        pEvent.setCenter(center);
                        pEvent.setComment(comment);
                        pEvent.setCreatedAt(createdAt);
                        pEvent.setWorksheet(worksheet);
                        for (SpecimenWrapper spec : removedSpecimens) {
                            spec.reload();
                        }
                        pEvent.removeFromSpecimenCollection(removedSpecimens);
                        for (SpecimenWrapper spec : specs) {
                            spec.reload();
                            spec.setProcessingEvent(pEvent);
                            spec.setActivityStatus(closedActivityStatus);
                        }
                        pEvent.addToSpecimenCollection(specs);
                        pEvent.persist();
                    } catch (Exception ex) {
                        saveErrorCatch(ex, null, true);
                    }
                }
            });
        }
        removedSpecimens.clear();
        SessionManager.updateAllSimilarNodes(pEventAdapter, true);
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
                .getActiveActivityStatus(appService));
        }
        GuiUtil.reset(activityStatusComboViewer, pEvent.getActivityStatus());
        specimenEntryWidget.setSpecimens(pEvent.getSpecimenCollection(true));
        removedSpecimens.clear();
    }
}
