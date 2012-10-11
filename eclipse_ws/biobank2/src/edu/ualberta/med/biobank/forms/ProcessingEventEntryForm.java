package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class ProcessingEventEntryForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(ProcessingEventEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ProcessingEventEntryForm";

    @SuppressWarnings("nls")
    private static final String MSG_NEW_PEVENT_OK =
        i18n.tr("Creating a new processing event");

    @SuppressWarnings("nls")
    private static final String MSG_PEVENT_OK =
        i18n.tr("Editing a processing event");

    private ProcessingEventAdapter pEventAdapter;

    private ComboViewer activityStatusComboViewer;

    private DateTimeWidget dateWidget;

    private SpecimenEntryWidget specimenEntryWidget;

    private ActivityStatus closedActivityStatus;

    private CommentsInfoTable commentEntryTable;

    private final ProcessingEventWrapper pevent = new ProcessingEventWrapper(
        SessionManager.getAppService());

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private List<SpecimenInfo> specimens;

    protected Study study;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        Assert.isTrue(adapter instanceof ProcessingEventAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        pEventAdapter = (ProcessingEventAdapter) adapter;
        setPEventInfo(adapter.getId());
        String tabName;
        if (pEventAdapter.getId() == null) {
            tabName = i18n.tr("New processing event");
        } else {
            if (pevent.getWorksheet() == null)
                tabName =
                    i18n.tr(
                        "Processing event {0} on {1}",
                        pevent.getWorksheet(), pevent.getFormattedCreatedAt());
            else
                tabName =
                    i18n.tr(
                        "Processing event date {0}",
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
            pevent.setCenter(SessionManager.getUser()
                .getCurrentWorkingCenter());
            specimens = new ArrayList<SpecimenInfo>();
        } else {
            PEventInfo read =
                SessionManager.getAppService().doAction(
                    new ProcessingEventGetInfoAction(
                        (ProcessingEvent) pEventAdapter
                            .getModelObject().getWrappedObject()));
            pevent.setWrappedObject(read.pevent);
            specimens = read.sourceSpecimenInfos;
            SessionManager.logLookup(read.pevent);
        }
        comment.setWrappedObject(new Comment());
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Processing event information"));
        form.setMessage(getOkMessage(), IMessageProvider.NONE);
        page.setLayout(new GridLayout(1, false));
        createMainSection();
        createSpecimensSection();
    }

    @SuppressWarnings("nls")
    private void createMainSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createReadOnlyLabelledField(client, SWT.NONE,
            Center.NAME.singular().toString(), pevent.getCenter()
                .getName());

        dateWidget = createDateTimeWidget(client, i18n.tr("Start time"),
            pevent.getCreatedAt(), pevent,
            ProcessingEventPeer.CREATED_AT.getName(),
            new NotNullValidator(
                // validation error message.
                i18n.tr("A creation date/time should be selected")));
        setFirstControl(dateWidget);

        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
            ProcessingEvent.PropertyName.WORKSHEET.toString(), null, pevent,
            ProcessingEventPeer.WORKSHEET.getName(),
            (!pevent.isNew() && pevent.getWorksheet() == null) ? null
                : new NonEmptyStringValidator(
                    // validation error message.
                    i18n.tr("Worksheet cannot be null")));

        activityStatusComboViewer = createComboViewer(client,
            ActivityStatus.NAME.singular().toString(),
            ActivityStatus.valuesList(), pevent.getActivityStatus(),
            // validation error message.
            i18n.tr("Processing event must have an activity status"),
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    setDirty(true);
                    pevent.setActivityStatus((ActivityStatus) selectedObject);
                }
            });
        if (pevent.getActivityStatus() != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                pevent.getActivityStatus()));
            setDirty(false);
        }

        createCommentSection();
    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentsInfoTable(client,
            pevent.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.MULTI, i18n.tr("Add a comment"), null, comment, "message", null);

    }

    @SuppressWarnings("nls")
    private void createSpecimensSection() {
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());

        Composite client =
            createSectionWithClient(SourceSpecimen.NAME.plural().toString());
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        specimenEntryWidget =
            new SpecimenEntryWidget(client, SWT.NONE, toolkit, true);
        specimenEntryWidget
            .addSelectionChangedListener(new BgcEntryFormWidgetListener() {
                @Override
                public void selectionChanged(MultiSelectEvent event) {
                    setDirty(true);
                }
            });
        specimenEntryWidget
            .addDoubleClickListener(new IInfoTableDoubleClickItemListener<SpecimenInfo>() {

                @Override
                public void doubleClick(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenViewForm.ID);
                }
            });

        VetoListener<ItemAction, SpecimenWrapper> vetoListener =
            new VetoListener<ItemAction, SpecimenWrapper>() {

                @Override
                public void handleEvent(Event<ItemAction, SpecimenWrapper> event)
                    throws VetoException {
                    SpecimenWrapper specimen = event.getObject();
                    switch (event.getType()) {
                    case PRE_ADD:
                        if (specimen == null) {
                            throw new VetoException(
                                // exception message.
                                i18n.tr("No specimen found for that inventory id."));
                        }
                        else if (!SessionManager.getUser()
                            .getCurrentWorkingCenter()
                            .equals(specimen.getCurrentCenter())) {
                            String centerName = i18n.tr("'none'");
                            if (specimen.getCurrentCenter() != null)
                                centerName =
                                    specimen.getCurrentCenter().getNameShort();
                            throw new VetoException(
                                // exception message.
                                i18n.tr(
                                    "Specimen is currently in center {0}. You can''t process it.",
                                    centerName));
                        } else if (specimen.getProcessingEvent() != null) {
                            throw new VetoException(
                                // exception message.
                                i18n.tr(
                                    "This specimen is already in processing event ''{0}'' ({1}). Remove it from the other processing event first.",
                                    specimen.getProcessingEvent()
                                        .getWorksheet(), specimen
                                        .getProcessingEvent()
                                        .getFormattedCreatedAt()));
                        } else if (!specimen.isActive())
                            throw new VetoException(
                                // exception message.
                                i18n.tr(
                                    "This specimen has status ''{0}''. Only ''Active'' specimens can be added to a processing event.",
                                    specimen.getActivityStatus().getName()));
                        else if (specimen.isUsedInDispatch()) {
                            throw new VetoException(
                                // exception message.
                                i18n.tr("Specimen is currently listed in a dispatch."));
                        }
                        else if (specimen.getParentContainer() != null) {
                            throw new VetoException(
                                // exception message.
                                i18n.tr("Specimen is currently listed as stored in a container."));
                        }
                        else if (!SessionManager.getUser()
                            .getCurrentWorkingCenter().getStudyCollection()
                            .contains(
                                specimen.getCollectionEvent().getPatient()
                                    .getStudy())) {
                            throw new VetoException(
                                // exception message.
                                i18n.tr(
                                    "This specimen is from study ''{0}''. This study is not linked to your current working center. Processing is not allowed.",
                                    specimen.getCollectionEvent().getPatient()
                                        .getStudy().getNameShort()));
                        }
                        else if (study == null) {
                            if (specimens.size() == 0) {
                                study =
                                    specimen.getCollectionEvent().getPatient()
                                        .getStudy().getWrappedObject();
                            } else {
                                try {
                                    study = SessionManager
                                        .getAppService()
                                        .doAction(
                                            new SpecimenGetInfoAction(specimens
                                                .get(0).specimen.getId()))
                                        .getSpecimen().getCollectionEvent()
                                        .getPatient().getStudy();
                                } catch (Exception e) {
                                    throw new VetoException(
                                        // exception message.
                                        i18n.tr("All Specimens must be part of the same study."));
                                }
                            }
                        }
                        if (!specimen.getCollectionEvent().getPatient()
                            .getStudy().getWrappedObject().equals(study)) {
                            throw new VetoException(
                                // exception message.
                                i18n.tr("In a processing event, all specimens must be part of the same study."));
                        }
                        break;

                    case POST_ADD:
                        specimen.setProcessingEvent(pevent);
                        specimen.setActivityStatus(closedActivityStatus);
                        break;

                    case PRE_DELETE:
                        if (specimen.getChildSpecimenCollection(false).size() != 0) {
                            throw new VetoException(
                                // exception message.
                                i18n.tr("Cannot remove processed specimens with children."));
                        }
                        break;
                    }
                }
            };

        specimenEntryWidget.addBinding(widgetCreator,
            i18n.tr("Specimens should be added to a processing event"));

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

        Integer peventId = SessionManager.getAppService().doAction(
            new ProcessingEventSaveAction(pevent.getWrappedObject(), pevent
                .getCenter().getWrappedObject(), pevent.getCreatedAt(),
                pevent.getWorksheet(), pevent.getActivityStatus(),
                comment.getMessage(), added, removed)).getId();
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
