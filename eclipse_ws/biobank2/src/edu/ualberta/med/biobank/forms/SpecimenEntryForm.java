package edu.ualberta.med.biobank.forms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetPossibleTypesAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenUpdateAction;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
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
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import edu.ualberta.med.biobank.wizards.ReparentingWizard;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenEntryForm extends BiobankEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenEntryForm"; //$NON-NLS-1$

    public static final String OK_MESSAGE = Messages.SpecimenEntryForm_ok_msg;

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

    protected CollectionEventWrapper newCollectionEvent;
    protected CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    private CommentsInfoTable commentEntryTable;

    private SpecimenBriefInfo specimenInfo;
    private SpecimenInfo newParent;

    private Button isSourceSpcButton;

    private BgcBaseText sourceSpecimenField;

    private List<AliquotedSpecimen> aliquotedSpecTypes;

    private Label sourceSpecimenLabel;

    protected ReparentingWizard wizard;

    private BgcBaseText parentPEventField;

    @Override
    protected void init() throws Exception {
        updateSpecimenInfo(adapter.getId());
        setPartName(Messages.SpecimenEntryForm_title);
    }

    private void updateSpecimenInfo(Integer id) throws ApplicationException {
        if (id != null) {
            specimenInfo = SessionManager.getAppService().doAction(
                new SpecimenGetInfoAction(id));
            aliquotedSpecTypes =
                SessionManager.getAppService().doAction(
                    new SpecimenGetPossibleTypesAction(specimenInfo
                        .getSpecimen().getId())).getList();
            specimen.setWrappedObject(specimenInfo.getSpecimen());
        }
        // not possible to have a specimen entry form with id=null
        comment.setWrappedObject(new Comment());
        SessionManager.logLookup(specimen.getWrappedObject());
        ((AdapterBase) adapter).setModelObject(specimen);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.SpecimenEntryForm_form_title,
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

        List<SpecimenType> specimenTypes = new ArrayList<SpecimenType>();
        for (AliquotedSpecimen a : aliquotedSpecTypes)
            specimenTypes.add(a.getSpecimenType());
        if (specimenInfo.getSpecimen().getSpecimenType() != null
            && !specimenTypes.contains(specimenInfo.getSpecimen()
                .getSpecimenType())) {
            specimenTypes
                .add(specimenInfo.getSpecimen().getSpecimenType());
        }
        specimenTypeComboViewer =
            createComboViewer(client,
                Messages.SpecimenEntryForm_type_label,
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(),
                    specimenTypes,
                    SpecimenTypeWrapper.class),
                specimen.getSpecimenType(),
                Messages.SpecimenEntryForm_type_validation_msg,
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
                                volumeField.setText(""); //$NON-NLS-1$
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
            Messages.SpecimenEntryForm_created_label,
            specimen.getFormattedCreatedAt());

        volumeField = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_volume_label,
            specimen.getQuantity() == null ? null : specimen.getQuantity()
                .toString());

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_study_label, specimen
                .getCollectionEvent().getPatient().getStudy().getNameShort());

        Label label = widgetCreator.createLabel(client,
            Messages.SpecimenEntryForm_pnumber_label);

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

        Button editSourceButton = new Button(c, SWT.NONE);
        editSourceButton
            .setText(Messages.SpecimenEntryForm_change_button_label);

        toolkit.adapt(c);

        editSourceButton.addListener(SWT.MouseUp, new Listener() {

            @Override
            public void handleEvent(Event event) {
                wizard =
                    new ReparentingWizard(
                        SessionManager.getAppService(), specimen
                            .getWrappedObject());
                WizardDialog dialog = new BiobankWizardDialog(page.getShell(),
                    wizard);
                int res = dialog.open();
                if (res == Status.OK) {
                    newCollectionEvent = wizard.getCollectionEvent();
                    specimen.setCollectionEvent(newCollectionEvent);
                    newParent = wizard.getSpecimen();
                    ProcessingEventWrapper parentPEvent;
                    if (newParent == null) {
                        specimen.setParentSpecimen(null);
                        ((GridData) sourceSpecimenLabel.getLayoutData()).exclude =
                            true;
                        ((GridData) sourceSpecimenField.getLayoutData()).exclude =
                            true;
                        sourceSpecimenLabel.setVisible(false);
                        sourceSpecimenField.setVisible(false);
                        isSourceSpcButton.setSelection(true);
                    } else {
                        specimen.setParentSpecimen(new SpecimenWrapper(
                            SessionManager.getAppService(),
                            newParent.specimen));
                        ((GridData) sourceSpecimenLabel.getLayoutData()).exclude =
                            false;
                        ((GridData) sourceSpecimenField.getLayoutData()).exclude =
                            false;
                        sourceSpecimenLabel.setVisible(true);
                        sourceSpecimenField.setVisible(true);
                        sourceSpecimenField.setText(newParent.specimen
                            .getInventoryId());
                        isSourceSpcButton.setSelection(false);
                    }

                    patientField.setText(specimen.getCollectionEvent()
                        .getPatient().getPnumber());
                    ceventText.setText(specimen.getCollectionInfo());
                    parentPEvent =
                        specimen.getParentSpecimen() == null ? null :
                            specimen.getParentSpecimen().getProcessingEvent();
                    if (parentPEvent != null)
                        parentPEventField.setText(new StringBuilder(
                            parentPEvent
                                .getFormattedCreatedAt())
                            .append(" (") //$NON-NLS-1$
                            .append(
                                NLS.bind(
                                    Messages.SpecimenEntryForm_worksheet_string,
                                    parentPEvent.getWorksheet()))
                            .append(")") //$NON-NLS-1$
                            .toString());
                    commentText.setText(wizard.getComment());
                    setDirty(true); // so changes can be saved
                    client.getParent().layout(true, true);
                }
            }
        });

        originCenterLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_origin_center_label);
        setTextValue(originCenterLabel, specimen.getOriginInfo().getCenter()
            .getNameShort());
        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_current_center_label);
        setTextValue(centerLabel, specimen.getCenterString());

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_position_label,
            specimen.getPositionString(true, false));

        boolean isSourceSpc = specimen.getTopSpecimen().equals(specimen);

        isSourceSpcButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE,
            Messages.SpecimenEntryForm_source_specimen_label);
        isSourceSpcButton.setEnabled(false);
        isSourceSpcButton.setSelection(isSourceSpc);

        sourceSpecimenLabel =
            widgetCreator.createLabel(client,
                Messages.SpecimenEntryForm_source_inventoryid_label);
        sourceSpecimenField = createReadOnlyWidget(client, SWT.NONE,
            specimen
                .getTopSpecimen().getInventoryId());

        GridData gds1 = new GridData();
        gds1.exclude = isSourceSpc;
        gds1.horizontalAlignment = SWT.FILL;
        sourceSpecimenLabel.setLayoutData(gds1);

        GridData gds2 = new GridData();
        gds2.exclude = isSourceSpc;
        gds2.horizontalAlignment = SWT.FILL;
        sourceSpecimenField.setLayoutData(gds2);

        sourceSpecimenLabel.setVisible(!isSourceSpc);
        sourceSpecimenField.setVisible(!isSourceSpc);
        ceventText = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_cEvent_label,
            specimen.getCollectionInfo());

        createProcessingEventSection(client);

        createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_children_nber_label,
            String.valueOf(specimen.getChildSpecimenCollection(false).size()));

        activityStatusComboViewer = createComboViewer(client,
            Messages.SpecimenEntryForm_status_label,
            ActivityStatus.valuesList(), specimen.getActivityStatus(),
            Messages.SpecimenEntryForm_status_validation_msg,
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

    private void createProcessingEventSection(Composite client) {

        // create top section
        ProcessingEventWrapper parentPevent =
            specimen.getParentSpecimen() == null ? null :
                specimen.getParentSpecimen().getProcessingEvent();
        widgetCreator.createLabel(client,
            Messages.SpecimenEntryForm_source_pevent);
        String parentPEventString;
        if (parentPevent == null)
            parentPEventString = ""; //$NON-NLS-1$
        else
            parentPEventString =
                new StringBuilder(parentPevent.getFormattedCreatedAt())
                    .append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenEntryForm_worksheet_string,
                            parentPevent.getWorksheet()))
                    .append(")").toString(); //$NON-NLS-1$
        parentPEventField = createReadOnlyWidget(
            client,
            SWT.NONE,
            parentPEventString);

        // create regular pevent section
        ProcessingEventWrapper pevent = specimen.getProcessingEvent();
        widgetCreator.createLabel(client,
            Messages.SpecimenEntryForm_pevent_label);
        String peventString;
        if (pevent == null)
            peventString = ""; //$NON-NLS-1$
        else
            peventString =
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenEntryForm_worksheet_string,
                            pevent.getWorksheet())).append(")").toString(); //$NON-NLS-1$
        createReadOnlyWidget(
            client,
            SWT.NONE,
            peventString);

    }

    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Messages.SpecimenEntryForm_4);
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
                Messages.SpecimenEntryForm_5, null, comment,
                Messages.SpecimenEntryForm_6, null);

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
        GuiUtil.reset(activityStatusComboViewer, specimen.getActivityStatus());
        GuiUtil.reset(specimenTypeComboViewer, specimen.getSpecimenType());
    }

}
