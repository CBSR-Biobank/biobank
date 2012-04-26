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
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.dispatch.DispatchAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.DispatchInfoTable;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenViewForm";

    private final SpecimenWrapper specimenWrapper =
        new SpecimenWrapper(SessionManager.getAppService());

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

    private CommentsInfoTable commentTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SpecimenAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        updateSpecimenInfo();

        setPartName(NLS.bind("Specimen: {0}",
            specimenWrapper.getInventoryId()));
    }

    private void updateSpecimenInfo() throws Exception {
        specimenBriefInfo = SessionManager.getAppService().doAction(
            new SpecimenGetInfoAction(adapter.getId()));
        Specimen specimen = specimenBriefInfo.getSpecimen();
        Assert.isNotNull(specimen);
        specimenWrapper.setWrappedObject(specimen);
        SessionManager.logLookup(specimen);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind("Specimen: {0}",
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
            createSection("Dispatch History");
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
        dispatchInfoTable
            .addEditItemListener(new IInfoTableEditItemListener<DispatchWrapper>() {

                @Override
                public void editItem(InfoTableEvent<DispatchWrapper> event) {
                    Dispatch d =
                        ((Dispatch) ((InfoTableSelection) event
                            .getSelection()).getObject());
                    new DispatchAdapter(null, new DispatchWrapper(
                        SessionManager.getAppService(), d)).openEntryForm();
                }
            });
    }

    private void createInformationSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        sampleTypeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Type");
        createdDateLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Created");
        volumeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            AliquotedSpecimen.PropertyName.VOLUME.toString());
        studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Study.NAME.singular().toString());
        patientLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Patient.NAME.singular().toString());
        originCenterLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Origin center");
        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Current center");
        positionLabel = createReadOnlyLabelledField(client, SWT.NONE,
            AbstractPosition.NAME.singular().toString());
        isSourceSpcButton = (Button) createLabelledWidget(client, Button.class,
            SWT.NONE, SourceSpecimen.NAME.singular().toString());
        if (!specimenWrapper.getTopSpecimen().equals(specimenWrapper)) {
            sourceInvIdLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Source Inventory ID");
        }

        ceventLabel = createReadOnlyLabelledField(client, SWT.NONE,
            CollectionEvent.NAME.singular().toString());

        if (!specimenWrapper.getTopSpecimen().equals(specimenWrapper)) {
            sourcePeventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                "Source Processing Event");
        }

        if (specimenWrapper.getProcessingEvent() != null) {
            peventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                ProcessingEvent.NAME.singular().toString());
        }
        childrenLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Children #");
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            ActivityStatus.NAME.singular().toString());

    }

    private void createCommentsSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        commentTable =
            new CommentsInfoTable(client,
                specimenWrapper.getCommentCollection(false));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void createContainersSection() {
        if (specimenWrapper.getParentContainer() != null) {
            Section section =
                createSection("Container Visualization");
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
        setPartName(NLS.bind("Specimen: {0}",
            specimenWrapper.getInventoryId()));
        form.setText(NLS.bind("Specimen: {0}",
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
                    .append(" (")
                    .append(
                        NLS.bind("worksheet: {0}",
                            topPevent.getWorksheet())).append(")").toString());
        }

        ProcessingEventWrapper pevent = specimenWrapper.getProcessingEvent();
        if (pevent != null) {
            setTextValue(
                peventLabel,
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (")
                    .append(
                        NLS.bind("worksheet: {0}",
                            pevent.getWorksheet())).append(")").toString());
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
