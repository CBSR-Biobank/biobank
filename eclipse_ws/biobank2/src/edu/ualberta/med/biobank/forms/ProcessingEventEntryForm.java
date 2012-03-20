package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventGetInfoAction.PEventInfo;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.loggers.ProcessingEventLogProvider;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingEventEntryForm extends BiobankEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ProcessingEventEntryForm"; //$NON-NLS-1$

    private static final String MSG_NEW_PEVENT_OK =
        Messages.ProcessingEventEntryForm_creation_msg;

    private static final String MSG_PEVENT_OK =
        Messages.ProcessingEventEntryForm_edition_msg;

    private ProcessingEventAdapter pEventAdapter;

    private ComboViewer activityStatusComboViewer;

    private DateTimeWidget dateWidget;

    private SpecimenEntryWidget specimenEntryWidget;

    private ActivityStatus closedActivityStatus;

    private CommentsInfoTable commentEntryTable;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private ProcessingEventWrapper pevent = new ProcessingEventWrapper(
        SessionManager.getAppService());

    private CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private ProcessingEventLogProvider logProvider =
        new ProcessingEventLogProvider();

    private List<SpecimenInfo> specimens;

    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ProcessingEventAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        pEventAdapter = (ProcessingEventAdapter) adapter;
        setPEventInfo(adapter.getId());
        String tabName;
        if (pEventAdapter.getId() == null) {
            tabName = Messages.ProcessingEventEntryForm_title_new;
        } else {
            if (pevent.getWorksheet() == null)
                tabName =
                    NLS.bind(
                        Messages.ProcessingEventEntryForm_title_edit_worksheet,
                        pevent.getWorksheet(), pevent.getFormattedCreatedAt());
            else
                tabName =
                    NLS.bind(
                        Messages.ProcessingEventEntryForm_title_edit_noworksheet,
                        DateFormatter.formatAsDateTime(pevent.getCreatedAt()));
        }

        closedActivityStatus = ActivityStatus.CLOSED;
        setPartName(tabName);
    }

    private void setPEventInfo(Integer id) throws Exception {
        if (id == null) {
            ProcessingEvent p = new ProcessingEvent();
            p.setActivityStatus(ActivityStatus.ACTIVE);
            pevent.setWrappedObject(p);
            pevent
                .setCenter(SessionManager.getUser().getCurrentWorkingCenter());
            specimens = new ArrayList<SpecimenInfo>();
        } else {
            PEventInfo read =
                SessionManager.getAppService().doAction(
                    new ProcessingEventGetInfoAction(adapter.getId()));
            pevent.setWrappedObject(read.pevent);
            specimens = read.sourceSpecimenInfos;
            SessionManager.logLookup(read.pevent);
        }
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
            Messages.ProcessingEvent_field_center_label, pevent.getCenter()
                .getName());

        dateWidget =
            createDateTimeWidget(
                client,
                Messages.ProcessingEvent_field_date_label,
                pevent.getCreatedAt(),
                pevent,
                ProcessingEventPeer.CREATED_AT.getName(),
                new NotNullValidator(
                    Messages.ProcessingEventEntryForm_field_date_validation_msg));
        setFirstControl(dateWidget);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            Messages.ProcessingEvent_field_worksheet_label, null, pevent,
            ProcessingEventPeer.WORKSHEET.getName(),
            (!pevent.isNew() && pevent.getWorksheet() == null) ? null
                : new NonEmptyStringValidator(
                    Messages.ProcessingEventEntryForm_worksheet_validation_msg));

        activityStatusComboViewer =
            createComboViewer(
                client,
                Messages.label_activity,
                ActivityStatus.valuesList(),
                pevent.getActivityStatus(),
                Messages.ProcessingEventEntryForm_field_activity_validation_msg,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        setDirty(true);
                        pevent
                            .setActivityStatus((ActivityStatus) selectedObject);
                    }
                });
        if (pevent.getActivityStatus() != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                pevent.getActivityStatus()));
            setDirty(false);
        }

        createCommentSection();

    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client,
                pevent.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add);

    }

    private void createSpecimensSection() {
        Composite client =
            createSectionWithClient(Messages.ProcessingEventEntryForm_specimens_title);
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        specimenEntryWidget =
            new SpecimenEntryWidget(client, SWT.NONE, toolkit,
                SessionManager.getAppService(), true);
        specimenEntryWidget
            .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        specimenEntryWidget
            .addDoubleClickListener(collectionDoubleClickListener);

        VetoListener<ItemAction, SpecimenWrapper> vetoListener =
            new VetoListener<ItemAction, SpecimenWrapper>() {

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
                            String centerName =
                                Messages.ProcessingEventEntryForm_none_text;
                            if (specimen.getCurrentCenter() != null)
                                centerName =
                                    specimen.getCurrentCenter().getNameShort();
                            throw new VetoException(
                                NLS.bind(
                                    Messages.ProcessingEventEntryForm_center_spec_error_msg,
                                    centerName));
                        } else if (specimen.getProcessingEvent() != null) {
                            throw new VetoException(
                                NLS.bind(
                                    Messages.ProcessingEventEntryForm_other_pEvent_error_msg,
                                    specimen.getProcessingEvent()
                                        .getWorksheet(), specimen
                                        .getProcessingEvent()
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
                        else if (specimens.size() > 0) {
                            try {
                                Study study =
                                    SessionManager
                                        .getAppService()
                                        .doAction(
                                            new SpecimenGetInfoAction(specimens
                                                .get(0).specimen.getId()))
                                        .getSpecimen().getCollectionEvent()
                                        .getPatient().getStudy();
                                if (!study.equals(
                                    specimen.getCollectionEvent().getPatient()
                                        .getStudy()))
                                    throw new VetoException(
                                        Messages.ProcessingEventEntryForm_study_spec_error_msg);
                            } catch (Exception e) {
                                BgcPlugin
                                    .openAsyncError(
                                        Messages.ProcessingEventEntryForm_error,
                                        e);
                            }
                            break;
                        }
                    case POST_ADD:
                        specimen.setProcessingEvent(pevent);
                        specimen.setActivityStatus(closedActivityStatus);
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
        Set<Integer> added = new HashSet<Integer>();
        Set<Integer> removed = new HashSet<Integer>();

        for (SpecimenInfo spec : specimenEntryWidget.getAddedSpecimens())
            added.add(spec.specimen.getId());

        for (SpecimenInfo spec : specimenEntryWidget.getRemovedSpecimens())
            removed.add(spec.specimen.getId());

        Integer peventId =
            SessionManager
                .getAppService()
                .doAction(
                    new ProcessingEventSaveAction(pevent.getId(), pevent
                        .getCenter().getId(), pevent.getCreatedAt(), pevent
                        .getWorksheet(), pevent.getActivityStatus(), comment
                        .getMessage(),
                        added, removed)).getId();
        pevent.setId(peventId);
        ((AdapterBase) adapter).setModelObject(pevent);
    }

    @Override
    protected void doAfterSave() throws Exception {
        SessionManager.updateAllSimilarNodes(pEventAdapter, true);
    }

    @Override
    protected String getOkMessage() {
        return (pevent.isNew()) ? MSG_NEW_PEVENT_OK : MSG_PEVENT_OK;
    }

    @Override
    public String getNextOpenedFormId() {
        return ProcessingEventViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        CenterWrapper<?> center = pevent.getCenter();
        pevent.reset();
        pevent.setCenter(center);
        if (pevent.isNew()) {
            pevent.setActivityStatus(ActivityStatus.ACTIVE);
        }
        GuiUtil.reset(activityStatusComboViewer, pevent.getActivityStatus());
        specimenEntryWidget.setSpecimens(specimens);
    }
}
