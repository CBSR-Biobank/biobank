package edu.ualberta.med.biobank.forms.linkassign;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.scanprocess.SpecimenHierarchy;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.linkassign.LinkFormPatientManagement.CEventComboCallback;
import edu.ualberta.med.biobank.forms.linkassign.LinkFormPatientManagement.PatientTextCallback;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.BiobankText;

// FIXME the custom selection is not done in this version. 
public class GenericLink2EntryForm extends AbstractLinkAssignEntryForm2 {

    public static final String ID = "edu.ualberta.med.biobank.forms.GenericLinkEntryForm"; //$NON-NLS-1$

    private static final String INVENTORY_ID_BINDING = "inventoryId-binding";

    private static BiobankLogger logger = BiobankLogger
        .getLogger(GenericLink2EntryForm.class.getName());

    private static boolean singleMode = false;

    // TODO do not need a composite class anymore if only one link form is left
    private LinkFormPatientManagement linkFormPatientManagement;

    // single linking
    // source specimen / type relation when only one specimen
    private AliquotedSpecimenSelectionWidget singleTypesWidget;

    private LinkWithScannerManagement scanLinkForm;

    @Override
    protected void init() throws Exception {
        super.init();
        setPartName(Messages.getString("GenericLinkEntryForm.tab.title")); //$NON-NLS-1$
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
        scanLinkForm = new LinkWithScannerManagement(this,
            linkFormPatientManagement);
    }

    @Override
    protected String getFormTitle() {
        return Messages.getString("GenericLinkEntryForm.form.title");
    }

    @Override
    protected boolean isSingleMode() {
        return singleMode;
    }

    @Override
    protected void setSingleMode(boolean single) {
        singleMode = single;
    }

    @Override
    protected String getActivityTitle() {
        return Messages.getString("GenericLinkEntryForm.activity.title"); //$NON-NLS-1$
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected String getOkMessage() {
        return Messages.getString("GenericLinkEntryForm.description.ok"); //$NON-NLS-1$
    }

    @Override
    public String getNextOpenedFormID() {
        // FIXME if checkbox to open assign form, should be assign form instead
        return ID;
    }

    @Override
    protected void createCommonFields(Composite commonFieldsComposite) {
        // Patient number
        linkFormPatientManagement
            .createPatientNumberText(commonFieldsComposite);
        linkFormPatientManagement
            .setPatientTextCallback(new PatientTextCallback() {
                @Override
                public void focusLost() {
                    setTypeCombos();
                }

                @Override
                public void textModified() {
                }
            });
        // Processing event and Collection events lists
        linkFormPatientManagement.createEventsWidgets(commonFieldsComposite);
        linkFormPatientManagement
            .setCEventComboCallback(new CEventComboCallback() {
                @Override
                public void selectionChanged() {
                    setTypeCombos();
                }
            });
    }

    /**
     * Get types only defined in the patient's study. Then set these types to
     * the types combos
     */
    private void setTypeCombos() {
        List<SpecimenTypeWrapper> studiesAliquotedTypes = linkFormPatientManagement
            .getStudyAliquotedTypes(null, null);
        List<SpecimenWrapper> availableSourceSpecimens = linkFormPatientManagement
            .getParentSpecimenForPEventAndCEvent();
        scanLinkForm.patientFocusLost(studiesAliquotedTypes,
            availableSourceSpecimens);
        // for single
        singleTypesWidget.setSourceSpecimens(availableSourceSpecimens);
        singleTypesWidget.setResultTypes(studiesAliquotedTypes);
    }

    @Override
    protected void createMultipleFields(Composite parent) {
        scanLinkForm.createFields(parent);
    }

    @Override
    protected void createContainersVisualisation(Composite parent) {
        scanLinkForm.createContainersVisualisation(parent);
    }

    @Override
    protected void createSingleFields(Composite parent) {
        Composite fieldsComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        fieldsComposite.setLayoutData(gd);

        // inventoryID
        BiobankText inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            fieldsComposite,
            BiobankText.class,
            SWT.NONE,
            Messages.getString("GenericLinkEntryForm.inventoryId.label"), //$NON-NLS-1$
            new String[0],
            singleSpecimen,
            SpecimenPeer.INVENTORY_ID.getName(),
            new NonEmptyStringValidator(Messages
                .getString("GenericLinkEntryForm.inventoryId.validator.msg")), //$NON-NLS-1$
            INVENTORY_ID_BINDING);
        inventoryIdText.addKeyListener(textFieldKeyListener);

        // widget to select the source and the type
        singleTypesWidget = new AliquotedSpecimenSelectionWidget(
            fieldsComposite, null, widgetCreator, false);
        singleTypesWidget.addBindings();

        widgetCreator.createLabel(fieldsComposite,
            Messages.getString("GenericLinkEntryForm.checkbox.assign")); //$NON-NLS-1$
        toolkit.createButton(fieldsComposite, "", SWT.CHECK); //$NON-NLS-1$
    }

    @Override
    protected void setBindings(boolean isSingleMode) {
        scanLinkForm.setBindings(isSingleMode);
        if (isSingleMode) {
            widgetCreator.addBinding(INVENTORY_ID_BINDING);
            singleTypesWidget.addBindings();
        } else {
            widgetCreator.removeBinding(INVENTORY_ID_BINDING);
            singleTypesWidget.removeBindings();
        }
    }

    // protected boolean fieldsValid() {
    // return (singleMode ? true : isPlateValid())
    // && linkFormPatientManagement.fieldsValid();
    // }

    @Override
    protected void doBeforeSave() throws Exception {
        // can't access the combos in another thread, so do it now
        if (singleMode) {
            SpecimenHierarchy selection = singleTypesWidget.getSelection();
            singleSpecimen.setParentSpecimen(selection.getParentSpecimen());
            singleSpecimen
                .setSpecimenType(selection.getAliquotedSpecimenType());
            singleSpecimen.setCollectionEvent(linkFormPatientManagement
                .getSelectedCollectionEvent());
        }
    }

    @Override
    protected void saveForm() throws Exception {
        OriginInfoWrapper originInfo = new OriginInfoWrapper(
            SessionManager.getAppService());
        originInfo
            .setCenter(SessionManager.getUser().getCurrentWorkingCenter());
        originInfo.persist();
        if (singleMode)
            saveSingleSpecimen(originInfo);
        else
            saveMultipleSpecimens();
        setFinished(false);
    }

    private void saveMultipleSpecimens() throws Exception {
        scanLinkForm.saveForm();
    }

    private void saveSingleSpecimen(OriginInfoWrapper originInfo)
        throws Exception {
        singleSpecimen.setCreatedAt(new Date());
        singleSpecimen.setQuantityFromType();
        singleSpecimen.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));
        singleSpecimen.setCurrentCenter(SessionManager.getUser()
            .getCurrentWorkingCenter());

        singleSpecimen.setOriginInfo(originInfo);
        singleSpecimen.persist();
        String posStr = singleSpecimen.getPositionString(true, false);
        if (posStr == null) {
            posStr = Messages
                .getString("GenericLinkEntryForm.position.label.none"); //$NON-NLS-1$
        }
        // LINKED\: specimen {0} of type\: {1} to source\: {2} ({3}) -
        // Patient\: {4} - Visit\: {5} - Center\: {6} \n
        appendLog(Messages.getString(
            "GenericLinkEntryForm.activitylog.specimen.linked", singleSpecimen //$NON-NLS-1$
                .getInventoryId(), singleSpecimen.getSpecimenType().getName(),
            singleSpecimen.getParentSpecimen().getInventoryId(), singleSpecimen
                .getParentSpecimen().getSpecimenType().getNameShort(),
            linkFormPatientManagement.getCurrentPatient().getPnumber(),
            singleSpecimen.getCollectionEvent().getVisitNumber(),
            singleSpecimen.getCurrentCenter().getNameShort()));
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        linkFormPatientManagement.reset(true);
        singleTypesWidget.deselectAll();
    }

    @Override
    public void reset(boolean resetAll) {
        linkFormPatientManagement.reset(resetAll);
        scanLinkForm.reset(resetAll);
        super.reset(resetAll);
    }

    @Override
    public boolean onClose() {
        linkFormPatientManagement.onClose();
        return super.onClose();
    }

}
