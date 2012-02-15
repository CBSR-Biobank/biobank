package edu.ualberta.med.biobank.forms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
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
import edu.ualberta.med.biobank.common.action.specimen.SpecimenUpdateAction;
import edu.ualberta.med.biobank.common.peer.CollectionEventPeer;
import edu.ualberta.med.biobank.common.peer.PatientPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.util.Holder;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.dialogs.BiobankWizardDialog;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import edu.ualberta.med.biobank.wizards.SelectCollectionEventWizard;

public class SpecimenEntryForm extends BiobankEntryForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenEntryForm"; //$NON-NLS-1$

    public static final String OK_MESSAGE = Messages.SpecimenEntryForm_ok_msg;

    private SpecimenWrapper specimen;

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

    private CommentCollectionInfoTable commentEntryTable;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    @Override
    protected void init() throws Exception {
        specimen = (SpecimenWrapper) getModelObject();
        SessionManager.logEdit(specimen);
        setPartName(Messages.SpecimenEntryForm_title);
        allchildren = new ArrayList<SpecimenWrapper>();
        origchildren = new ArrayList<SpecimenWrapper>();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.SpecimenEntryForm_form_title,
            specimen.getInventoryId()));
        page.setLayout(new GridLayout(1, false));
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, true,
            false));

        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        StudyWrapper study = specimen.getCollectionEvent().getPatient()
            .getStudy();
        study.reload();

        List<AliquotedSpecimenWrapper> allowedAliquotedSpecimen = study
            .getAliquotedSpecimenCollection(true);

        List<SpecimenTypeWrapper> containerSpecimenTypeList = null;
        if (specimen.hasParent()) {
            ContainerTypeWrapper ct = specimen.getParentContainer()
                .getContainerType();
            ct.reload();
            containerSpecimenTypeList = ct.getSpecimenTypeCollection();
        }

        List<SpecimenTypeWrapper> specimenTypes =
            new ArrayList<SpecimenTypeWrapper>();
        for (AliquotedSpecimenWrapper ss : allowedAliquotedSpecimen) {
            SpecimenTypeWrapper sst = ss.getSpecimenType();
            if (containerSpecimenTypeList == null) {
                specimenTypes.add(sst);
            } else {
                for (SpecimenTypeWrapper st : containerSpecimenTypeList) {
                    if (sst.equals(st))
                        specimenTypes.add(st);
                }
            }
        }
        if (specimen.getSpecimenType() != null
            && !specimenTypes.contains(specimen.getSpecimenType())) {
            specimenTypes.add(specimen.getSpecimenType());
        }

        specimenTypeComboViewer = createComboViewer(client,
            Messages.SpecimenEntryForm_type_label, specimenTypes,
            specimen.getSpecimenType(),
            Messages.SpecimenEntryForm_type_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    specimen
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);
                    specimen.setQuantityFromType();
                    BigDecimal volume = specimen.getQuantity();
                    if (volumeField != null) {
                        if (volume == null) {
                            volumeField.setText(""); //$NON-NLS-1$
                        } else {
                            volumeField.setText(volume.toString());
                        }
                    }
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

        Button editPatientButton = new Button(c, SWT.NONE);
        editPatientButton
            .setText(Messages.SpecimenEntryForm_change_button_label);

        toolkit.adapt(c);

        editPatientButton.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                SelectCollectionEventWizard wizard =
                    new SelectCollectionEventWizard(
                        SessionManager.getAppService());
                WizardDialog dialog = new BiobankWizardDialog(page.getShell(),
                    wizard);
                int res = dialog.open();
                if (res == Status.OK) {
                    newCollectionEvent = wizard.getCollectionEvent();
                    transferSpecimen(specimen, newCollectionEvent,
                        wizard.getComment());
                    patientField.setText(specimen.getCollectionEvent()
                        .getPatient().getPnumber());
                    ceventText.setText(specimen.getCollectionInfo());
                    commentText.setText(wizard.getComment());
                    setDirty(true); // so changes can be saved
                }
            }
        });
        editPatientButton
            .setEnabled(specimen.getTopSpecimen().equals(specimen));

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

        Button isSourceSpcButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE,
            Messages.SpecimenEntryForm_source_specimen_label);
        isSourceSpcButton.setEnabled(false);
        isSourceSpcButton.setSelection(isSourceSpc);

        if (!isSourceSpc) {
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SpecimenEntryForm_source_inventoryid_label, specimen
                    .getTopSpecimen().getInventoryId());
        }

        ceventText = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenEntryForm_cEvent_label,
            specimen.getCollectionInfo());

        if (!isSourceSpc) {
            ProcessingEventWrapper topPevent = specimen.getTopSpecimen()
                .getProcessingEvent();
            createReadOnlyLabelledField(
                client,
                SWT.NONE,
                Messages.SpecimenEntryForm_source_pevent,
                new StringBuilder(topPevent.getFormattedCreatedAt())
                    .append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenEntryForm_worksheet_string,
                            topPevent.getWorksheet())).append(")").toString()); //$NON-NLS-1$
        }

        ProcessingEventWrapper pevent = specimen.getProcessingEvent();
        if (pevent != null) {
            createReadOnlyLabelledField(
                client,
                SWT.NONE,
                Messages.SpecimenEntryForm_pevent_label,
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenEntryForm_worksheet_string,
                            pevent.getWorksheet())).append(")").toString()); //$NON-NLS-1$
        }

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

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentCollectionInfoTable(client,
            specimen.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add);

    }

    protected void transferSpecimen(SpecimenWrapper specimen2,
        CollectionEventWrapper collectionEvent, String wcomment) {
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
        newComment.setMessage(Messages.SpecimenEntryForm_cevent_modification
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
    public String getNextOpenedFormID() {
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
