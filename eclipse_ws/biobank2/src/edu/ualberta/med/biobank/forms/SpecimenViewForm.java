package edu.ualberta.med.biobank.forms;

import java.util.Stack;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.widgets.Section;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetDispatchesAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetDispatchesAction.SpecimenDispatchesInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.batchop.SpecimenBatchOpViewForm;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.gui.common.widgets.utils.BgcWidgetCreator;
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
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenViewForm.class);

    @SuppressWarnings("nls")
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

    private BgcBaseText parentInvIdLabel;

    private BgcBaseText sourcePeventLabel;

    private BgcBaseText peventLabel;

    private BgcBaseText childrenLabel;

    private BgcBaseText plateErrorsLabel;

    private BgcBaseText sampleErrorsLabel;

    private BgcBaseText batchOpLabel;

    private Button openBatchOpButton;

    private Button isSourceSpcButton;

    private SpecimenBriefInfo specimenBriefInfo;

    private CommentsInfoTable commentTable;

    private SpecimenDispatchesInfo dispatchesInfo;

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof SpecimenAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        updateSpecimenInfo();

        setPartName(i18n.tr("Specimen: {0}",
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

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Specimen: {0}",
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
        @SuppressWarnings("nls")
        Section section =
            createSection(i18n.tr("Dispatch History"));
        Composite client = toolkit.createComposite(section);
        section.setClient(client);
        section.setExpanded(false);
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        dispatchesInfo =
            SessionManager.getAppService().doAction(
                new SpecimenGetDispatchesAction(specimenWrapper.getId()));

        dispatchInfoTable =
            new DispatchInfoTable(client,
                dispatchesInfo.getDispatches());
        dispatchInfoTable
            .addEditItemListener(new IInfoTableEditItemListener<Dispatch>() {

                @Override
                public void editItem(InfoTableEvent<Dispatch> event) {
                    Dispatch d =
                        ((Dispatch) ((InfoTableSelection) event
                            .getSelection()).getObject());
                    new DispatchAdapter(null, new DispatchWrapper(
                        SessionManager.getAppService(), d)).openEntryForm();
                }
            });
    }

    @SuppressWarnings("nls")
    private void createInformationSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);
        sampleTypeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Type"));
        createdDateLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Created"));
        volumeLabel = createReadOnlyLabelledField(client, SWT.NONE,
            AliquotedSpecimen.PropertyName.VOLUME.toString());
        studyLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Study.NAME.singular().toString());
        patientLabel = createReadOnlyLabelledField(client, SWT.NONE,
            Patient.NAME.singular().toString());
        originCenterLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Origin center"));
        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Current center"));
        positionLabel = createReadOnlyLabelledField(client, SWT.NONE,
            AbstractPosition.NAME.singular().toString());
        isSourceSpcButton = (Button) createLabelledWidget(client, Button.class,
            SWT.NONE, SourceSpecimen.NAME.singular().toString());
        if (!specimenWrapper.getTopSpecimen().equals(specimenWrapper)) {
            sourceInvIdLabel = createReadOnlyLabelledField(client, SWT.NONE,
                i18n.tr("Originating Specimen"));
        }

        parentInvIdLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Parent Specimen"));

        ceventLabel = createReadOnlyLabelledField(client, SWT.NONE,
            CollectionEvent.NAME.singular().toString());

        if (!specimenWrapper.getTopSpecimen().equals(specimenWrapper)) {
            sourcePeventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                i18n.tr("Created in Processing Event"));
        }

        if (specimenWrapper.getProcessingEvent() != null) {
            peventLabel = createReadOnlyLabelledField(client, SWT.NONE,
                ProcessingEvent.NAME.singular().toString());
        }
        childrenLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Children #"));
        activityStatusLabel = createReadOnlyLabelledField(client, SWT.NONE,
            ActivityStatus.NAME.singular().toString());

        plateErrorsLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Plate Errors"));
        sampleErrorsLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Sample Errors"));

        createSpecimenImportField(client);
    }

    @SuppressWarnings("nls")
    private void createSpecimenImportField(Composite parent) {
        Label label = widgetCreator.createLabel(parent, "Imported");

        Composite c = new Composite(parent, SWT.NONE);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        c.setLayoutData(gd);
        GridLayout gl = new GridLayout(2, false);
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        c.setLayout(gl);
        label.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING));

        batchOpLabel = (BgcBaseText) widgetCreator
            .createWidget(c, BgcBaseText.class, SWT.READ_ONLY, null);
        batchOpLabel.setBackground(BgcWidgetCreator.READ_ONLY_TEXT_BGR);

        openBatchOpButton = new Button(c, SWT.NONE);
        openBatchOpButton.setText(i18n.tr("View Import"));

        toolkit.adapt(c);

        openBatchOpButton.addListener(SWT.MouseUp, new Listener() {
            @Override
            public void handleEvent(Event event) {
                if (specimenBriefInfo.getBatch() != null) {
                    Integer batchOpId = specimenBriefInfo.getBatch().getId();
                    try {
                        SpecimenBatchOpViewForm.openForm(batchOpId, true);
                    } catch (PartInitException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
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

    @SuppressWarnings("nls")
    private void createContainersSection() {
        if (specimenWrapper.getParentContainer() != null) {
            Section section =
                createSection(i18n.tr("Container Visualization"));
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

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        setPartName(i18n.tr("Specimen: {0}",
            specimenWrapper.getInventoryId()));
        form.setText(i18n.tr("Specimen: {0}",
            specimenWrapper.getInventoryId()));
        dispatchInfoTable.setList(dispatchesInfo.getDispatches());
        setTextValue(originCenterLabel, specimenWrapper.getOriginInfo()
            .getCenter()
            .getNameShort());
        setTextValue(centerLabel, specimenWrapper.getCurrentCenter()
            .getNameShort());
        setTextValue(sampleTypeLabel, specimenWrapper.getSpecimenType()
            .getName());
        setTextValue(createdDateLabel, specimenWrapper.getFormattedCreatedAt());
        setTextValue(volumeLabel, specimenWrapper.getQuantity() == null ? null
            : NumberFormatter.format(specimenWrapper.getQuantity()));
        setTextValue(studyLabel, specimenWrapper.getCollectionEvent()
            .getPatient()
            .getStudy().getNameShort());
        setTextValue(patientLabel, specimenWrapper.getCollectionEvent()
            .getPatient()
            .getPnumber());
        setTextValue(positionLabel,
            specimenWrapper.getPositionString(true, false));
        setTextValue(ceventLabel, specimenWrapper.getCollectionInfo());

        setTextValue(plateErrorsLabel, specimenWrapper.getWrappedObject()
            .getPlateErrors());
        setTextValue(sampleErrorsLabel, specimenWrapper.getWrappedObject()
            .getSampleErrors());

        setTextValue(batchOpLabel, specimenBriefInfo.getBatch() != null
            ? i18n.tr("Yes")
            : i18n.tr("No"));
        openBatchOpButton.setEnabled(specimenBriefInfo.getBatch() != null);

        boolean isSourceSpc =
            (specimenWrapper.getOriginalCollectionEvent() != null);

        setCheckBoxValue(isSourceSpcButton, isSourceSpc);

        if (!isSourceSpc) {
            setTextValue(sourceInvIdLabel, specimenWrapper.getTopSpecimen()
                .getInventoryId());

            setTextValue(parentInvIdLabel, specimenWrapper.getParentSpecimen()
                .getInventoryId());

            ProcessingEventWrapper pevent = specimenWrapper.getParentSpecimen()
                .getProcessingEvent();

            if (pevent != null) {
                setTextValue(sourcePeventLabel,
                    new StringBuilder(pevent.getFormattedCreatedAt())
                        .append(" (").append(i18n.tr("worksheet: {0}",
                            pevent.getWorksheet())).append(")").toString());
            }
        }

        ProcessingEventWrapper pevent = specimenWrapper.getProcessingEvent();
        if (pevent != null) {
            setTextValue(
                peventLabel,
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (")
                    .append(
                        i18n.tr("worksheet: {0}",
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
