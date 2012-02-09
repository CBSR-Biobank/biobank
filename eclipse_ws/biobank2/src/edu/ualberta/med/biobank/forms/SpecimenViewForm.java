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
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.infotables.DispatchInfoTable;

public class SpecimenViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenViewForm"; //$NON-NLS-1$

    private SpecimenWrapper specimen;

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

    private SpecimenInfo specimenInfo;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SpecimenAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        updateSpecimenInfo();

        // REMOVE THIS LINE WHEN USING ACTION
        specimen = (SpecimenWrapper) getModelObject();

        SessionManager.logLookup(specimen);
        setPartName(NLS.bind(Messages.SpecimenViewForm_title,
            specimen.getInventoryId()));
    }

    private void updateSpecimenInfo() throws Exception {
        // specimenInfo = SessionManager.getAppService().doAction(
        // new SpecimenListGetInfoAction(adapter.getId()));
        // specimen =
        // new StudyWrapper(SessionManager.getAppService(),
        // specimenInfo.getSpecimen());
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.SpecimenViewForm_title,
            specimen.getInventoryId()));
        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        createInformationSection();
        createDispatchSection();
        createContainersSection();
        setValues();
    }

    private void createDispatchSection() {
        Section section =
            createSection(Messages.SpecimenViewForm_dispatch_title);
        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        section.setExpanded(false);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        dispatchInfoTable = new DispatchInfoTable(client, specimen);
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
        if (!specimen.getTopSpecimen().equals(specimen)) {
            sourceInvIdLabel = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SpecimenViewForm_source_specimenid_label);
        }

        ceventLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_cevent_label);

        if (!specimen.getTopSpecimen().equals(specimen)) {
            sourcePeventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SpecimenViewForm_source_pevent_label);
        }

        if (specimen.getProcessingEvent() != null) {
            peventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.SpecimenViewForm_pevent_label);
        }
        childrenLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_children_nber_label);
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Messages.SpecimenViewForm_status_label);
        commentLabel = createReadOnlyLabelledField(client,
            SWT.WRAP | SWT.MULTI, Messages.SpecimenViewForm_comments_label);

    }

    private void createContainersSection() {
        if (specimen.getParentContainer() != null) {
            Section section =
                createSection(Messages.SpecimenViewForm_visualization_title);
            Composite containersComposite = toolkit.createComposite(section);
            section.setClient(containersComposite);
            section.setExpanded(false);
            containersComposite.setLayout(new GridLayout(1, false));
            toolkit.paintBordersFor(containersComposite);

            Stack<ContainerWrapper> parents = new Stack<ContainerWrapper>();
            ContainerWrapper container = specimen.getParentContainer();
            while (container != null) {
                parents.push(container);
                container = container.getParentContainer();
            }
            while (!parents.isEmpty()) {
                container = parents.pop();
                RowColPos position;
                if (parents.isEmpty()) {
                    position = specimen.getPosition();
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
                toolkit.createLabel(containerComposite, container.getLabel()
                    + " (" + container.getContainerType().getNameShort() //$NON-NLS-1$
                    + ") "); //$NON-NLS-1$
                ContainerDisplayWidget containerWidget =
                    new ContainerDisplayWidget(
                        containerComposite);
                containerWidget.setContainer(container);
                containerWidget.setSelection(position);
                toolkit.adapt(containerWidget);
            }
        }
    }

    private void setValues() {
        setTextValue(originCenterLabel, specimen.getOriginInfo().getCenter()
            .getNameShort());
        setTextValue(centerLabel, specimen.getCurrentCenter().getNameShort());
        setTextValue(sampleTypeLabel, specimen.getSpecimenType().getName());
        setTextValue(createdDateLabel, specimen.getFormattedCreatedAt());
        setTextValue(volumeLabel, specimen.getQuantity() == null ? null
            : specimen.getQuantity().toString());
        setTextValue(studyLabel, specimen.getCollectionEvent().getPatient()
            .getStudy().getNameShort());
        setTextValue(patientLabel, specimen.getCollectionEvent().getPatient()
            .getPnumber());
        setTextValue(positionLabel, specimen.getPositionString(true, false));
        setTextValue(ceventLabel, specimen.getCollectionInfo());

        boolean isSourceSpc = specimen.getTopSpecimen().equals(specimen);

        setCheckBoxValue(isSourceSpcButton, isSourceSpc);

        if (!isSourceSpc) {
            setTextValue(sourceInvIdLabel, specimen.getTopSpecimen()
                .getInventoryId());

            ProcessingEventWrapper topPevent = specimen.getTopSpecimen()
                .getProcessingEvent();

            setTextValue(
                sourcePeventLabel,
                new StringBuilder(topPevent.getFormattedCreatedAt())
                    .append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenViewForm_worksheet_string,
                            topPevent.getWorksheet())).append(")").toString()); //$NON-NLS-1$
        }

        ProcessingEventWrapper pevent = specimen.getProcessingEvent();
        if (pevent != null) {
            setTextValue(
                peventLabel,
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (") //$NON-NLS-1$
                    .append(
                        NLS.bind(Messages.SpecimenViewForm_worksheet_string,
                            pevent.getWorksheet())).append(")").toString()); //$NON-NLS-1$
        }

        setTextValue(childrenLabel, specimen.getChildSpecimenCollection(false)
            .size());
        setTextValue(activityStatusLabel, specimen.getActivityStatus());
        setTextValue(commentLabel, specimen.getCommentCollection(false));

    }

    @Override
    public void setFocus() {
        // specimens are not present in treeviews, unnecessary reloads can be
        // prevented with this method
    }

    @Override
    public void reload() throws Exception {
        specimen.reload();
        setValues();
        setPartName(NLS.bind(Messages.SpecimenViewForm_title,
            specimen.getInventoryId()));
        form.setText(NLS.bind(Messages.SpecimenViewForm_title,
            specimen.getInventoryId()));
        dispatchInfoTable.reloadCollection();
    }

}
