package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ProcessingEventPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.NotNullValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget;
import edu.ualberta.med.biobank.widgets.SpecimenEntryWidget.ItemAction;
import edu.ualberta.med.biobank.widgets.listeners.BiobankEntryFormWidgetListener;
import edu.ualberta.med.biobank.widgets.listeners.MultiSelectEvent;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.Event;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoException;
import edu.ualberta.med.biobank.widgets.listeners.VetoListenerSupport.VetoListener;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingEventEntryForm extends
    BiobankEntryForm<ProcessingEventWrapper> {

    public static final String ID = "edu.ualberta.med.biobank.forms.ProcessingEventEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ProcessingEventEntryForm.class.getName());

    private static final String MSG_NEW_PEVENT_OK = Messages
        .getString("ProcessingEventEntryForm.creation.msg"); //$NON-NLS-1$
    private static final String MSG_PEVENT_OK = Messages
        .getString("ProcessingEventEntryForm.edition.msg"); //$NON-NLS-1$

    private ProcessingEventAdapter pEventAdapter;

    private ComboViewer activityStatusComboViewer;

    private DateTimeWidget dateWidget;

    private SpecimenEntryWidget specimenEntryWidget;

    @Override
    protected void init() throws Exception {
        super.init();
        Assert.isTrue(adapter instanceof ProcessingEventAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        pEventAdapter = (ProcessingEventAdapter) adapter;
        String tabName;
        if (modelObject.isNew()) {
            tabName = Messages.getString("ProcessingEventEntryForm.title.new"); //$NON-NLS-1$
            modelObject.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            if (modelObject.getWorksheet() == null)
                tabName = Messages.getString(
                    "ProcessingEventEntryForm.title.edit.worksheet", //$NON-NLS-1$
                    modelObject.getWorksheet(),
                    modelObject.getFormattedCreatedAt());
            else
                tabName = Messages.getString(
                    "ProcessingEventEntryForm.title.edit.noworksheet", //$NON-NLS-1$
                    modelObject.getFormattedCreatedAt());
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("ProcessingEventEntryForm.main.title")); //$NON-NLS-1$
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

        createReadOnlyLabelledField(
            client,
            SWT.NONE,
            Messages.getString("ProcessingEvent.field.center.label"), modelObject //$NON-NLS-1$
                .getCenter().getName());

        dateWidget = createDateTimeWidget(
            client,
            Messages.getString("ProcessingEvent.field.date.label"), //$NON-NLS-1$
            modelObject.getCreatedAt(),
            modelObject,
            ProcessingEventPeer.CREATED_AT.getName(),
            new NotNullValidator(
                Messages
                    .getString("ProcessingEventEntryForm.field.date.validation.msg"))); //$NON-NLS-1$

        setFirstControl(dateWidget);

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.NONE,
            Messages.getString("ProcessingEvent.field.worksheet.label"), null, //$NON-NLS-1$
            modelObject, ProcessingEventPeer.WORKSHEET.getName(),
            (!modelObject.isNew() && modelObject.getWorksheet() == null) ? null
                : new NonEmptyStringValidator("Worksheet cannot be null"));

        activityStatusComboViewer = createComboViewer(
            client,
            Messages.getString("label.activity"), //$NON-NLS-1$
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            modelObject.getActivityStatus(),
            Messages
                .getString("ProcessingEventEntryForm.field.activity.validation.msg"), //$NON-NLS-1$
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    setDirty(true);
                    modelObject
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });
        if (modelObject.getActivityStatus() != null)
            activityStatusComboViewer.setSelection(new StructuredSelection(
                modelObject.getActivityStatus()));

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            Messages.getString("label.comments"), null, modelObject, //$NON-NLS-1$
            ProcessingEventPeer.COMMENT.getName(), null);
    }

    private void createSpecimensSection() {
        Composite client = createSectionWithClient(Messages
            .getString("ProcessingEventEntryForm.specimens.title")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(1, false);
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL, GridData.FILL));
        toolkit.paintBordersFor(client);

        List<SpecimenWrapper> specimens = modelObject
            .getSpecimenCollection(true);

        specimenEntryWidget = new SpecimenEntryWidget(client, SWT.NONE,
            toolkit, appService, true);
        specimenEntryWidget
            .addSelectionChangedListener(new BiobankEntryFormWidgetListener() {
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
                            "No specimen found for that inventory id.");
                    else if (!SessionManager.getUser()
                        .getCurrentWorkingCenter()
                        .equals(specimen.getCurrentCenter())) {
                        String centerName = "'none'";
                        if (specimen.getCurrentCenter() != null)
                            centerName = specimen.getCurrentCenter()
                                .getNameShort();
                        throw new VetoException(
                            "Specimen is currently in center " + centerName
                                + ". You can't process it.");
                    } else if (specimen.isUsedInDispatch())
                        throw new VetoException(
                            "Specimen is currently listed in a dispatch.");
                    else if (specimen.getParentContainer() != null)
                        throw new VetoException(
                            "Specimen is currently listed as stored in a container.");
                    else if (modelObject.getSpecimenCollection(false).size() > 0
                        && !modelObject
                            .getSpecimenCollection(false)
                            .get(0)
                            .getCollectionEvent()
                            .getPatient()
                            .getStudy()
                            .equals(
                                specimen.getCollectionEvent().getPatient()
                                    .getStudy()))
                        throw new VetoException(
                            "All specimens must be part of the same study.");
                    break;
                case POST_ADD:
                    specimen.setProcessingEvent(modelObject);
                    modelObject
                        .addToSpecimenCollection(Arrays.asList(specimen));
                    break;
                case PRE_DELETE:
                    break;
                case POST_DELETE:
                    modelObject.removeFromSpecimenCollection(Arrays
                        .asList(specimen));
                    break;
                }
            }
        };

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
        modelObject.persist();
        SessionManager.updateAllSimilarNodes(pEventAdapter, true);
    }

    @Override
    protected String getOkMessage() {
        return (modelObject.isNew()) ? MSG_NEW_PEVENT_OK : MSG_PEVENT_OK;
    }

    @Override
    public String getNextOpenedFormID() {
        return ProcessingEventViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        modelObject.reset();
        if (modelObject.getActivityStatus() != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                modelObject.getActivityStatus()));
        }
        specimenEntryWidget.setSpecimens(modelObject
            .getSpecimenCollection(true));
        setDirty(false);
    }
}
