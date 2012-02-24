package edu.ualberta.med.biobank.forms;

import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetDispatchesAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetDispatchesAction.SpecimenDispatchesInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenViewForm"; //$NON-NLS-1$

    private SpecimenWrapper specimenWrapper;

    private BgcBaseText centerLabel;

    private BgcBaseText originCenterLabel;

    private BgcBaseText sampleTypeLabel;

    private BgcBaseText createdDateLabel;

    private BgcBaseText volumeLabel;

    private BgcBaseText studyLabel;

    private BgcBaseText patientLabel;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText commentLabel;

    private BgcBaseText positionLabel;

    private DispatchInfoTable dispatchInfoTable;

    private BgcBaseText ceventLabel;

    private BgcBaseText sourceInvIdLabel;

    private BgcBaseText sourcePeventLabel;

    private BgcBaseText peventLabel;

    private BgcBaseText childrenLabel;

    private Button isSourceSpcButton;

    private SpecimenBriefInfo specimenBriefInfo;

    private CommentCollectionInfoTable commentTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SpecimenAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        updateSpecimenInfo();

        SessionManager.logLookup(specimenWrapper);
        setPartName(NLS.bind(Messages.SpecimenViewForm_title,
            specimenWrapper.getInventoryId()));
    }

    private void updateSpecimenInfo() throws Exception {
        specimenBriefInfo = SessionManager.getAppService().doAction(
            new SpecimenGetInfoAction(adapter.getId()));
        Specimen specimen = specimenBriefInfo.getSpecimen();
        Assert.isNotNull(specimen);
        specimenWrapper =
            new SpecimenWrapper(SessionManager.getAppService(), specimen);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.SpecimenViewForm_title,
            specimenWrapper.getInventoryId()));
        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createInformationSection();
        createCommentsSection();
        createDispatchSection();
        createContainersSection();
        setValues();
    }

    private void createDispatchSection() throws ApplicationException {
        Section section =
            createSection(Messages.SpecimenViewForm_dispatch_title);
        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        section.setExpanded(false);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        SpecimenDispatchesInfo specimenDispatchesInfo =
            SessionManager.getAppService().doAction(
                new SpecimenGetDispatchesAction(specimenWrapper.getId()));

        dispatchInfoTable =
            new DispatchInfoTable(client,
                specimenDispatchesInfo.getDispatches());
    }

    private void createInformationSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        sampleTypeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_type_label);
        createdDateLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_created_label);
        volumeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_volume_label);
        studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_study_label);
        patientLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_patient_label);
        originCenterLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_origin_center_label);
        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_current_center_label);
        positionLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_position_label);
        isSourceSpcButton = (Button) createLabelledWidget(client, Button.class,
            SWT.NONE, Messages.SpecimenViewForm_source_specimen_label);
        if (!specimenWrapper.getTopSpecimen().equals(specimenWrapper)) {
            sourceInvIdLabel = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SpecimenViewForm_source_specimenid_label);
        }

        ceventLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_cevent_label);

        if (!specimenWrapper.getTopSpecimen().equals(specimenWrapper)) {
            sourcePeventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SpecimenViewForm_source_pevent_label);
        }

        if (specimenWrapper.getProcessingEvent() != null) {
            peventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SpecimenViewForm_pevent_label);
        }
        childrenLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_children_nber_label);
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_status_label);

    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentCollectionInfoTable(client,
                specimenWrapper.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void createContainersSection() {
        if (specimenWrapper.getParentContainer() != null) {
            Section section =
                createSection(Messages.SpecimenViewForm_visualization_title);
            Composite containersComposite = toolkit.createComposite(section);
            section.setClient(containersComposite);
            section.setExpanded(false);
            containersComposite.setLayout(new GridLayout(1, false));
            toolkit.paintBordersFor(containersComposite);

            Stack<Container> parents = specimenBriefInfo.getParents();
            Container container;
            while (!parents.isEmpty()) {
                container = parents.pop();
                RowColPos position;
                if (parents.isEmpty()) {
                    position = specimenWrapper.getPosition();
                } else {
                    position = parents.peek().getPositionAsRowCol();
                }
                Composite containerComposite = toolkit
                    .createComposite(containersComposite);
                GridLayout layout = new GridLayout(1, false);
                layout.horizontalSpacing = 0;
                layout.marginWidth = 0;
                layout.verticalSpacing = 0;
                containerComposite.setLayout(layout);

                StringBuffer sb = new StringBuffer(container.getLabel());
                sb.append(" (");
                sb.append(container.getContainerType().getNameShort());
                sb.append(") ");

                toolkit.createLabel(containerComposite, sb.toString());
                ContainerDisplayWidget containerWidget =
                    new ContainerDisplayWidget(containerComposite);
                containerWidget.setContainer(container);
                containerWidget.setSelection(position);
                toolkit.adapt(containerWidget);
            }
        }
    }

    @Override
    public void setValues() throws Exception {
        setPartName(NLS.bind(Messages.SpecimenViewForm_title,
            specimenWrapper.getInventoryId()));
        form.setText(NLS.bind(Messages.SpecimenViewForm_title,
            specimenWrapper.getInventoryId()));
        dispatchInfoTable.reloadCollection();
        setTextValue(originCenterLabel, specimenWrapper.getOriginInfo()
            .getCenter()
            .getNameShort());
        setTextValue(centerLabel, specimenWrapper.getCurrentCenter()
            .getNameShort());
        setTextValue(sampleTypeLabel, specimenWrapper.getSpecimenType()
            .getName());
        setTextValue(createdDateLabel, specimenWrapper.getFormattedCreatedAt());
        setTextValue(volumeLabel, specimenWrapper.getQuantity() == null ? null
            : specimenWrapper.getQuantity().toString());
        setTextValue(studyLabel, specimenWrapper.getCollectionEvent()
            .getPatient()
            .getStudy().getNameShort());
        setTextValue(patientLabel, specimenWrapper.getCollectionEvent()
            .getPatient()
            .getPnumber());
        setTextValue(positionLabel,
            specimenWrapper.getPositionString(true, false));
        setTextValue(ceventLabel, specimenWrapper.getCollectionInfo());

        boolean isSourceSpc =
            specimenWrapper.getTopSpecimen().equals(specimenWrapper);

        setCheckBoxValue(isSourceSpcButton, isSourceSpc);

        if (!isSourceSpc) {
            setTextValue(sourceInvIdLabel, specimenWrapper.getTopSpecimen()
                .getInventoryId());

            ProcessingEventWrapper topPevent = specimenWrapper.getTopSpecimen()
                .getProcessingEvent();

            setTextValue(
                sourcePeventLabel,
                new StringBuilder(topPevent.getFormattedCreatedAt())
                    .append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenViewForm_worksheet_string,
                            topPevent.getWorksheet())).append(")").toString()); //$NON-NLS-1$
        }

        ProcessingEventWrapper pevent = specimenWrapper.getProcessingEvent();
        if (pevent != null) {
            setTextValue(
                peventLabel,
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenViewForm_worksheet_string,
                            pevent.getWorksheet())).append(")").toString()); //$NON-NLS-1$
        }

        setTextValue(childrenLabel,
            specimenWrapper.getChildSpecimenCollection(false)
                .size());
        setTextValue(activityStatusLabel, specimenWrapper.getActivityStatus());
        setTextValue(commentLabel, specimenWrapper.getCommentCollection(false));

    }

    @Override
    public void setFocus() {
        // LEAVE AS EMPTY METHOD
        //
        // specimens are not present in treeviews, unnecessary reloads can be
        // prevented with this method left empty
    }

}
