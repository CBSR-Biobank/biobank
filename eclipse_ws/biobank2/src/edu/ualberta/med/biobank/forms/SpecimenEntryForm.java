package edu.ualberta.med.biobank.forms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenUpdateAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetAliquotedSpecimensAction;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankWizardDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import edu.ualberta.med.biobank.wizards.ReparentingWizard;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenEntryForm extends BiobankEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenEntryForm"; 

    public static final String OK_MESSAGE = "Edit specimen";

    private SpecimenWrapper specimen = new SpecimenWrapper(
        SessionManager.getAppService());

    private ComboViewer activityStatusComboViewer;

    private ComboViewer specimenTypeComboViewer;

    private BgcBaseText volumeField;

    private BgcBaseText centerLabel;

    private BgcBaseText originCenterLabel;

    private BgcBaseText patientField;

    private BgcBaseText ceventText;

    private BgcBaseText commentText;

    private List<SpecimenWrapper> origchildren;
    private List<SpecimenWrapper> allchildren;

    protected CollectionEventWrapper newCollectionEvent;
    protected CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private CommentsInfoTable commentEntryTable;

    private SpecimenBriefInfo specimenInfo;
    private SpecimenInfo newParent;

    private SpecimenAdapter specimenAdapter;

    private Button isSourceSpcButton;

    private BgcBaseText pEventField;

    private Label pEventLabel;

    private BgcBaseText sourceSpecimenField;

    private Set<AliquotedSpecimen> aliquotedSpecTypes;

    private Set<SpecimenType> containerSpecimenTypeList;

    private Label sourceSpecimenLabel;

    @Override
    protected void init() throws Exception {
        specimenAdapter = (SpecimenAdapter) adapter;
        updateSpecimenInfo(adapter.getId());
        setPartName("Specimen Entry");
        allchildren = new ArrayList<SpecimenWrapper>();
        origchildren = new ArrayList<SpecimenWrapper>();
    }

    private void updateSpecimenInfo(Integer id) throws ApplicationException {
        if (id != null) {
            specimenInfo = SessionManager.getAppService().doAction(
                new SpecimenGetInfoAction(id));
            aliquotedSpecTypes =
                SessionManager.getAppService().doAction(
                    new StudyGetAliquotedSpecimensAction(specimenInfo
                        .getSpecimen().getCollectionEvent().
                        getPatient().getStudy().getId())).getSet();
            if (specimenInfo
                .getSpecimen().getSpecimenPosition() != null)
                containerSpecimenTypeList =
                    SessionManager
                        .getAppService()
                        .doAction(
                            new ContainerTypeGetInfoAction(specimenInfo
                                .getSpecimen().getSpecimenPosition()
                                .getContainer()
                                .getContainerType().getId()))
                        .getContainerType()
                        .getSpecimenTypes();
            else
                containerSpecimenTypeList = new HashSet<SpecimenType>();
            specimen.setWrappedObject(specimenInfo.getSpecimen());
        } else {
            specimenInfo = new SpecimenBriefInfo();
            aliquotedSpecTypes = new HashSet<AliquotedSpecimen>();
            containerSpecimenTypeList = new HashSet<SpecimenType>();
            specimen.setWrappedObject((Specimen) specimenAdapter
                .getModelObject().getWrappedObject());
        }

        SessionManager.logLookup(specimen.getWrappedObject());
        ((AdapterBase) adapter).setModelObject(specimen);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind("Specimen {0} Information",
            specimen.getInventoryId()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
            false));

        final Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        List<SpecimenTypeWrapper> specimenTypes =
            new ArrayList<SpecimenTypeWrapper>();
        for (AliquotedSpecimen ss : aliquotedSpecTypes) {
            SpecimenType sst = ss.getSpecimenType();
            if (containerSpecimenTypeList == null) {
                specimenTypes.add(new SpecimenTypeWrapper(SessionManager
                    .getAppService(), sst));
            } else {
                for (SpecimenType st : containerSpecimenTypeList) {
                    if (sst.equals(st))
                        specimenTypes.add(new SpecimenTypeWrapper(
                            SessionManager.getAppService(), st));
                }
            }
        }
        if (specimen.getSpecimenType() != null
            && !specimenTypes.contains(specimen.getSpecimenType())) {
            specimenTypes
                .add(new SpecimenTypeWrapper(SessionManager.getAppService(),
                    specimenInfo.getSpecimen().getSpecimenType()));
        }

        specimenTypeComboViewer = createComboViewer(client,
            "Type", specimenTypes,
            specimen.getSpecimenType(),
            "Specimen must have a type",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    specimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                    specimen.setQuantity(setQuantityFromType(specimen
                        .getSpecimenType().getWrappedObject()));
                    BigDecimal volume = specimen.getQuantity();
                    if (volumeField != null) {
                        if (volume == null) {
                            volumeField.setText(""); 
                        } else {
                            volumeField.setText(volume.toString());
                        }
                    }
                }

                private BigDecimal setQuantityFromType(
                    SpecimenType specimenType) {
                    for (AliquotedSpecimen as : aliquotedSpecTypes) {
                        if (specimenType.equals(as.getSpecimenType())) {
                            return as.getVolume();
                        }
                    }
                    return null;
                }
            });

        createReadOnlyLabelledField(client, SWT.NONE,
            "Created",
            specimen.getFormattedCreatedAt());

        volumeField = createReadOnlyLabelledField(client, SWT.NONE,
            "Volume (ml)",
            specimen.getQuantity() == null ? null : specimen.getQuantity()
                .toString());

        createReadOnlyLabelledField(client, SWT.NONE,
            "Study", specimen
                .getCollectionEvent().getPatient().getStudy().getNameShort());

        Label label = widgetCreator.createLabel(client,
            "Patient");

        Composite c = new Composite(client, SWT.NONE);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        c.setLayoutData(gd);
        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        c.setLayout(gl);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        patientField = (BgcBaseText) widgetCreator.createBoundWidget(c,
            BgcBaseText.class, SWT.READ_ONLY, null, BeansObservables
                .observeValue(specimen, Property.concatNames(
                    SpecimenPeer.COLLECTION_EVENT, CollectionEventPeer.PATIENT,
                    PatientPeer.PNUMBER)), null);
        patientField.setBackground(BgcWidgetCreator.READ_ONLY_TEXT_BGR);

        Button editPatientButton = new Button(c, SWT.NONE);
        editPatientButton
            .setText("Change Patient");

        toolkit.adapt(c);

        editPatientButton.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                ReparentingWizard wizard =
                    new ReparentingWizard(
                        SessionManager.getAppService(), specimen
                            .getWrappedObject());
                WizardDialog dialog = new BiobankWizardDialog(page.getShell(),
                    wizard);
                int res = dialog.open();
                if (res == Status.OK) {
                    newCollectionEvent = wizard.getCollectionEvent();
                    newParent = wizard.getSpecimen();
                    ProcessingEventWrapper topPevent;
                    if (newParent == null) {
                        specimen.setParentSpecimen(null);
                        ((GridData) sourceSpecimenField.getLayoutData()).exclude =
                            true;
                        isSourceSpcButton.setSelection(true);
                        topPevent = specimen.getProcessingEvent();
                        pEventLabel
                            .setText("Processing Event");
                    } else {
                        specimen.getWrappedObject().setParentSpecimen(
                            newParent.specimen);
                        ((GridData) sourceSpecimenField.getLayoutData()).exclude =
                            false;
                        sourceSpecimenField.setText(newParent.specimen
                            .getInventoryId());
                        isSourceSpcButton.setSelection(false);
                        topPevent =
                            specimen.getTopSpecimen().getProcessingEvent();
                        pEventLabel
                            .setText("Source Processing Event");
                    }
                    transferSpecimen(specimen, newCollectionEvent,
                        wizard.getComment());

                    patientField.setText(specimen.getCollectionEvent()
                        .getPatient().getPnumber());
                    ceventText.setText(specimen.getCollectionInfo());
                    if (topPevent != null)
                        pEventField.setText(new StringBuilder(topPevent
                            .getFormattedCreatedAt())
                            .append(" (") 
                            .append(
                                NLS.bind(
                                    "worksheet: {0}",
                                    topPevent.getWorksheet()))
                            .append(")")
                            .toString());
                    else
                        pEventField.setText("");
                    commentText.setText(wizard.getComment());
                    setDirty(true); // so changes can be saved
                    client.getParent().layout();
                }
            }
        });

        originCenterLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Origin center");
        setTextValue(originCenterLabel, specimen.getOriginInfo().getCenter()
            .getNameShort());
        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Current center");
        setTextValue(centerLabel, specimen.getCenterString());

        createReadOnlyLabelledField(client, SWT.NONE,
            "Position",
            specimen.getPositionString(true, false));

        boolean isSourceSpc = specimen.getTopSpecimen().equals(specimen);

        isSourceSpcButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE,
            "Source Specimen");
        isSourceSpcButton.setEnabled(false);
        isSourceSpcButton.setSelection(isSourceSpc);

        sourceSpecimenLabel =
            widgetCreator.createLabel(client,
                "Source Inventory ID");
        sourceSpecimenField = createReadOnlyWidget(client, SWT.NONE,
            specimen
                .getTopSpecimen().getInventoryId());

        GridData gds = new GridData();
        gds.exclude = isSourceSpc;
        sourceSpecimenLabel.setLayoutData(gds);
        sourceSpecimenField.setLayoutData(gds);

        ceventText = createReadOnlyLabelledField(client, SWT.NONE,
            "Collection Event",
            specimen.getCollectionInfo());

        ProcessingEventWrapper topPevent = specimen.getTopSpecimen()
            .getProcessingEvent();
        pEventLabel =
            widgetCreator.createLabel(client,
                "Source Processing Event");
        String pEventString;
        if (topPevent == null)
            pEventString = "";
        else
            pEventString = new StringBuilder(topPevent.getFormattedCreatedAt())
                .append(" (") 
                .append(
                    NLS.bind("worksheet: {0}",
                        topPevent.getWorksheet())).append(")").toString();
        pEventField = createReadOnlyWidget(
            client,
            SWT.NONE,
            pEventString); //$NON-NLS-1$
        pEventLabel.setLayoutData(gds);
        pEventField.setLayoutData(gds);

        ProcessingEventWrapper pevent = specimen.getProcessingEvent();
        if (pevent != null) {
            pEventLabel =
                widgetCreator.createLabel(client,
                    "Processing Event");
            pEventField = createReadOnlyWidget(
                client,
                SWT.NONE,
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (") 
                    .append(
                        NLS.bind("worksheet: {0}",
                            pevent.getWorksheet())).append(")").toString()); 
        }

        createReadOnlyLabelledField(client, SWT.NONE,
            "Children #",
            String.valueOf(specimen.getChildSpecimenCollection(false).size()));

        activityStatusComboViewer = createComboViewer(client,
            "Activity Status",
            ActivityStatus.valuesList(), specimen.getActivityStatus(),
            "Specimen must have an activity status",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    specimen
                        .setActivityStatus((ActivityStatus) selectedObject);
                }
            });

        createCommentSection();

        setFirstControl(specimenTypeComboViewer.getControl());
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient("Comments");
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client,
                specimen.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        commentText =
            (BgcBaseText) createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.MULTI,
                "Add a comment", null, comment, "message", null);

    }

    protected void transferSpecimen(SpecimenWrapper specimen2,
        CollectionEventWrapper collectionEvent,
        String wcomment) {
        if (specimen2.equals(specimen.getTopSpecimen())) {
            // is original
            origchildren.add(specimen2);
            specimen2.setOriginalCollectionEvent(collectionEvent);
        }
        allchildren.add(specimen2);
        specimen2.setCollectionEvent(collectionEvent);
        CommentWrapper newComment = new CommentWrapper(
            SessionManager.getAppService());
        newComment.setCreatedAt(new Date());
        newComment.setUser(SessionManager.getUser());
        newComment.setMessage("Patient/Collection Event modification: "
            + wcomment);
        specimen2.addToCommentCollection(Arrays.asList(newComment));
        for (SpecimenWrapper spec : specimen2.getChildSpecimenCollection(false)) {
            transferSpecimen(spec, collectionEvent, wcomment);
        }
    }

    @Override
    protected void saveForm() throws Exception {
        SpecimenUpdateAction updateAction = new SpecimenUpdateAction();
        updateAction.setSpecimenId(specimen.getId());
        updateAction.setSpecimenTypeId(specimen.getSpecimenType().getId());
        updateAction
            .setCollectionEventId(specimen.getCollectionEvent().getId());
        updateAction
            .setParentSpecimenId(specimen.getParentSpecimen() == null ? null
                : specimen.getParentSpecimen().getId());

        final Holder<String> commentMessage = new Holder<String>(null);
        commentText.getDisplay().syncExec(new Runnable() {
            @Override
            public void run() {
                commentMessage.setValue(commentText.getText());
            }
        });

        updateAction.setCommentMessage(commentMessage.getValue());
        updateAction.setActivityStatus(specimen.getActivityStatus());

        SessionManager.getAppService().doAction(updateAction);
    }

    @Override
    protected String getOkMessage() {
        return OK_MESSAGE;
    }

    @Override
    public String getNextOpenedFormId() {
        return SpecimenViewForm.ID;
    }

    @Override
    public void setFocus() {
        // specimens are not present in treeviews, unnecessary reloads can be
        // prevented with this method
    }

    @Override
    public void setValues() throws Exception {
        allchildren.clear();
        origchildren.clear();
        GuiUtil.reset(activityStatusComboViewer, specimen.getActivityStatus());
        GuiUtil.reset(specimenTypeComboViewer, specimen.getSpecimenType());
    }

}
