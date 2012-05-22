package edu.ualberta.med.biobank.forms;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

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
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
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
import edu.ualberta.med.biobank.model.AbstractBiobankModel;
import edu.ualberta.med.biobank.model.AbstractPosition;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import edu.ualberta.med.biobank.wizards.ReparentingWizard;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class SpecimenEntryForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.SpecimenEntryForm";

    @SuppressWarnings("nls")
    public static final String OK_MESSAGE = i18n.tr("Edit specimen");

    private final SpecimenWrapper specimen = new SpecimenWrapper(
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

    private List<AbstractBiobankModel> specTypes;

    private Label sourceSpecimenLabel;

    protected ReparentingWizard wizard;

    private BgcBaseText parentPEventField;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        updateSpecimenInfo(adapter.getId());
        setPartName(i18n.tr("Specimen Entry"));
    }

    private void updateSpecimenInfo(Integer id) throws ApplicationException {
        if (id != null) {
            specimenInfo = SessionManager.getAppService().doAction(
                new SpecimenGetInfoAction(id));
            specTypes =
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

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Specimen {0} Information",
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
        for (AbstractBiobankModel a : specTypes)
            if (a instanceof AliquotedSpecimen)
                specimenTypes.add(((AliquotedSpecimen) a).getSpecimenType());
            else
                specimenTypes.add(((SourceSpecimen) a).getSpecimenType());
        if (specimenInfo.getSpecimen().getSpecimenType() != null
            && !specimenTypes.contains(specimenInfo.getSpecimen()
                .getSpecimenType())) {
            specimenTypes
                .add(specimenInfo.getSpecimen().getSpecimenType());
        }

        specimenTypeComboViewer = createComboViewer(client,
            i18n.tr("Type"),
            ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(),
                specimenTypes,
                SpecimenTypeWrapper.class),
            specimen.getSpecimenType(),
            i18n.tr("Specimen must have a type"),
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
                            volumeField.setText(StringUtil.EMPTY_STRING);
                        } else {
                            volumeField.setText(volume.toString());
                        }
                    }
                }

                private BigDecimal setQuantityFromType(SpecimenType specimenType) {
                    for (AbstractBiobankModel as : specTypes) {
                        if (as instanceof AliquotedSpecimen) {
                            if (specimenType
                                .equals(((AliquotedSpecimen) as)
                                    .getSpecimenType())) {
                                return ((AliquotedSpecimen) as).getVolume();
                            }
                        }
                    }
                    return null;
                }
            });

        createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Created"),
            specimen.getFormattedCreatedAt());

        volumeField = createReadOnlyLabelledField(client, SWT.NONE,
            AliquotedSpecimen.PropertyName.VOLUME.toString(),
            specimen.getQuantity() == null ? null : specimen.getQuantity()
                .toString());

        createReadOnlyLabelledField(client, SWT.NONE,
            Study.NAME.singular().toString(), specimen
                .getCollectionEvent().getPatient().getStudy().getNameShort());

        Label label = widgetCreator.createLabel(client,
            Patient.NAME.singular().toString());

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
            .setText(i18n.tr("Change Source"));

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
                            .append(" (")
                            .append(
                                i18n.tr("worksheet: {0}",
                                    parentPEvent.getWorksheet()))
                            .append(")")
                            .toString());
                    commentText.setText(wizard.getComment());
                    setDirty(true); // so changes can be saved
                    client.getParent().layout(true, true);
                }
            }
        });

        originCenterLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Origin center"));
        setTextValue(originCenterLabel, specimen.getOriginInfo().getCenter()
            .getNameShort());
        centerLabel = createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Current center"));
        setTextValue(centerLabel, getCenterString(specimen));

        createReadOnlyLabelledField(client, SWT.NONE,
            AbstractPosition.NAME.singular().toString(),
            specimen.getPositionString(true, false));

        boolean isSourceSpc = specimen.getTopSpecimen().equals(specimen);

        isSourceSpcButton = (Button) createLabelledWidget(client,
            Button.class, SWT.NONE,
            SourceSpecimen.NAME.singular().toString());
        isSourceSpcButton.setEnabled(false);
        isSourceSpcButton.setSelection(isSourceSpc);

        sourceSpecimenLabel =
            widgetCreator.createLabel(client,
                i18n.tr("Source Inventory ID"));
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
            CollectionEvent.NAME.singular().toString(),
            specimen.getCollectionInfo());

        createProcessingEventSection(client);

        createReadOnlyLabelledField(client, SWT.NONE,
            i18n.tr("Children #"),
            String.valueOf(specimen.getChildSpecimenCollection(false).size()));

        activityStatusComboViewer = createComboViewer(client,
            ActivityStatus.NAME.singular().toString(),
            ActivityStatus.valuesList(), specimen.getActivityStatus(),
            i18n.tr("Specimen must have an activity status"),
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

    @SuppressWarnings("nls")
    private static String getCenterString(SpecimenWrapper specimen) {
        CenterWrapper<?> center = specimen.getCurrentCenter();
        if (center != null) {
            return center.getNameShort();
        }
        // TODO should never see that ? should never retrieve a Specimen which
        // site cannot be displayed ?
        return i18n.tr("CANNOT DISPLAY INFORMATION");
    }

    @SuppressWarnings({ "nls" })
    private void createProcessingEventSection(Composite client) {

        // create top section
        ProcessingEventWrapper parentPevent =
            specimen.getParentSpecimen() == null ? null :
                specimen.getParentSpecimen().getProcessingEvent();
        widgetCreator.createLabel(client,
            i18n.tr("Source Processing Event"));
        String parentPEventString;
        if (parentPevent == null)
            parentPEventString = StringUtil.EMPTY_STRING;
        else
            parentPEventString =
                new StringBuilder(parentPevent.getFormattedCreatedAt())
                    .append(" (")
                    .append(
                        i18n.tr("worksheet: {0}", parentPevent.getWorksheet()))
                    .append(")").toString();
        parentPEventField =
            createReadOnlyWidget(client, SWT.NONE, parentPEventString);

        // create regular pevent section
        ProcessingEventWrapper pevent = specimen.getProcessingEvent();
        widgetCreator.createLabel(client,
            ProcessingEvent.NAME.singular().toString());
        String peventString;
        if (pevent == null)
            peventString = StringUtil.EMPTY_STRING;
        else
            peventString =
                new StringBuilder(pevent.getFormattedCreatedAt()).append(" (")
                    .append(i18n.tr("worksheet: {0}",
                        pevent.getWorksheet())).append(")").toString();
        createReadOnlyWidget(client, SWT.NONE, peventString);

    }

    @SuppressWarnings("nls")
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
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
                i18n.tr("Add a comment"), null, comment,
                "message", null);

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
