package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.LinkFormPatientManagement.PatientTextCallback;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.preferences.PreferenceConstants;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.CabinetInventoryIDValidator;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class CabinetLinkAssignEntryForm extends AbstractAliquotAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CabinetLinkAssignEntryForm.class.getName());

    private enum AliquotMode {
        NEW_ALIQUOT, MOVE_ALIQUOT
    };

    private LinkFormPatientManagement linkFormPatientManagement;

    private Label cabinetLabel;
    private Label drawerLabel;
    private ContainerDisplayWidget cabinetWidget;
    private ContainerDisplayWidget drawerWidget;

    private BiobankText inventoryIdText;
    private Label oldCabinetPositionLabel;
    private BiobankText oldCabinetPositionText;
    private Label oldCabinetPositionCheckLabel;
    private BiobankText oldCabinetPositionCheckText;
    private Label newCabinetPositionLabel;
    private BiobankText newCabinetPositionText;
    private Label sampleTypeComboLabel;
    private ComboViewer viewerSampleTypes;
    private Label sampleTypeTextLabel;
    private BiobankText sampleTypeText;
    private Button checkButton;

    private CancelConfirmWidget cancelConfirmWidget;

    private IObservableValue resultShownValue = new WritableValue(
        Boolean.FALSE, Boolean.class);
    private static IObservableValue canLaunchCheck = new WritableValue(
        Boolean.TRUE, Boolean.class);

    private SpecimenWrapper aliquot;
    private ContainerWrapper cabinet;
    private ContainerWrapper drawer;
    private ContainerWrapper bin;

    private String cabinetNameContains = ""; //$NON-NLS-1$

    private Button radioNew;

    private CabinetInventoryIDValidator inventoryIDValidator;

    private List<ContainerTypeWrapper> cabinetContainerTypes;

    protected boolean positionTextModified;

    private StringLengthValidator newCabinetPositionValidator;

    protected boolean inventoryIdModified;

    protected boolean oldPositionCheckModified;

    private AbstractValidator oldCabinetPositionCheckValidator;

    private List<SpecimenTypeWrapper> cabinetSpecimenTypes;

    private static final String SAMPLE_TYPE_LIST_BINDING = "sample-type-list-binding";

    private AliquotMode aliquotMode;

    private ScrolledComposite containersScroll;

    protected boolean newAliquotCreation = true;

    private Composite clientInsideGridScroll;

    @Override
    protected void init() throws Exception {
        super.init();
        aliquotMode = AliquotMode.NEW_ALIQUOT;
        setPartName(Messages.getString("Cabinet.tabTitle")); //$NON-NLS-1$
        aliquot = new SpecimenWrapper(appService);
        IPreferenceStore store = BioBankPlugin.getDefault()
            .getPreferenceStore();
        cabinetNameContains = store
            .getString(PreferenceConstants.CABINET_CONTAINER_NAME_CONTAINS);
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);

    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("Cabinet.formTitle")); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        page.setLayout(layout);

        createFieldsSection();
        createLocationSection();

        linkFormPatientManagement.setSite(SessionManager.getUser()
            .getCurrentWorkingCentre());
        if (viewerSampleTypes != null) {
            initCabinetContainerTypesList();
            viewerSampleTypes.setInput(null);
            aliquot.setSpecimenType(null);
            if (newCabinetPositionValidator.validate(
                newCabinetPositionText.getText()).equals(Status.OK_STATUS)) {
                initContainersFromPosition();
                setTypeCombosLists();
            }
        }

        cancelConfirmWidget = new CancelConfirmWidget(page, this, true);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            resultShownValue,
            Messages.getString("Cabinet.checkButton.validationMsg"));

        radioNew.setSelection(true);
        setSpecimenMode(AliquotMode.NEW_ALIQUOT);
    }

    private void createLocationSection() {
        containersScroll = new ScrolledComposite(page, SWT.H_SCROLL);
        containersScroll.setExpandHorizontal(true);
        containersScroll.setExpandVertical(true);
        containersScroll.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        containersScroll.setLayoutData(scrollData);
        clientInsideGridScroll = toolkit.createComposite(containersScroll);
        GridLayout layout = new GridLayout(2, false);
        clientInsideGridScroll.setLayout(layout);
        toolkit.paintBordersFor(clientInsideGridScroll);
        containersScroll.setContent(clientInsideGridScroll);
        cabinetLabel = toolkit.createLabel(clientInsideGridScroll, "Cabinet"); //$NON-NLS-1$
        drawerLabel = toolkit.createLabel(clientInsideGridScroll, "Drawer"); //$NON-NLS-1$

        ContainerTypeWrapper cabinetType = null;
        ContainerTypeWrapper drawerType = null;
        if (cabinetContainerTypes.size() > 0) {
            cabinetType = cabinetContainerTypes.get(0);
            List<ContainerTypeWrapper> children = cabinetType
                .getChildContainerTypeCollection();
            if (children.size() > 0) {
                drawerType = children.get(0);
            }
        }
        cabinetWidget = new ContainerDisplayWidget(clientInsideGridScroll);
        cabinetWidget.setContainerType(cabinetType, true);
        toolkit.adapt(cabinetWidget);
        GridData gdDrawer = new GridData();
        gdDrawer.verticalAlignment = SWT.TOP;
        cabinetWidget.setLayoutData(gdDrawer);

        drawerWidget = new ContainerDisplayWidget(clientInsideGridScroll);
        drawerWidget.setContainerType(drawerType, true);
        toolkit.adapt(drawerWidget);

        containersScroll.setMinSize(clientInsideGridScroll.computeSize(
            SWT.DEFAULT, SWT.DEFAULT));
        displayPositions(false);
    }

    private void createFieldsSection() throws ApplicationException {
        Composite fieldsComposite = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        fieldsComposite.setLayout(layout);
        toolkit.paintBordersFor(fieldsComposite);
        GridData gd = new GridData();
        gd.widthHint = 500;
        gd.verticalAlignment = SWT.TOP;
        fieldsComposite.setLayoutData(gd);

        // radio button to choose new or move
        radioNew = toolkit.createButton(fieldsComposite,
            Messages.getString("Cabinet.button.new.text"), //$NON-NLS-1$
            SWT.RADIO);
        radioNew.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                newAliquotCreation = radioNew.getSelection();
                if (radioNew.getSelection()) {
                    setSpecimenMode(AliquotMode.NEW_ALIQUOT);
                }
            }
        });
        Button radioMove = toolkit.createButton(fieldsComposite,
            Messages.getString("Cabinet.button.move.text"), SWT.RADIO); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalSpan = 2;
        radioMove.setLayoutData(gd);
        radioMove.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                newAliquotCreation = radioNew.getSelection();
                if (!radioNew.getSelection()) {
                    setSpecimenMode(AliquotMode.MOVE_ALIQUOT);
                }
            }
        });

        // Patient number + visits list
        linkFormPatientManagement.createPatientNumberText(fieldsComposite);
        linkFormPatientManagement
            .setPatientTextCallback(new PatientTextCallback() {
                @Override
                public void focusLost() {
                    setTypeCombosLists();
                }

                @Override
                public void textModified() {
                    viewerSampleTypes.setInput(null);
                    positionTextModified = true;
                    resultShownValue.setValue(Boolean.FALSE);
                    displayPositions(false);
                }
            });

        linkFormPatientManagement.createCollectionEventWidgets(fieldsComposite);

        // inventoryID
        inventoryIDValidator = new CabinetInventoryIDValidator();
        inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            fieldsComposite, BiobankText.class, SWT.NONE,
            Messages.getString("Cabinet.inventoryId.label"), new String[0], //$NON-NLS-1$
            aliquot, "inventoryId", //$NON-NLS-1$
            inventoryIDValidator);
        gd = (GridData) inventoryIdText.getLayoutData();
        gd.horizontalSpan = 2;
        inventoryIdText.addKeyListener(textFieldKeyListener);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (inventoryIdModified && !radioNew.getSelection()) {
                    // Move Mode only
                    try {
                        retrieveAliquotDataForMoving();
                    } catch (Exception ex) {
                        BioBankPlugin.openError("Move - aliquot error", ex); //$NON-NLS-1$
                        focusControlInError(inventoryIdText);
                    }
                }
                inventoryIdModified = false;
            }
        });
        inventoryIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                inventoryIdModified = true;
                positionTextModified = true;
                resultShownValue.setValue(Boolean.FALSE);
                displayPositions(false);
            }
        });

        createPositionFields(fieldsComposite);

        createTypeCombo(fieldsComposite);

        checkButton = toolkit.createButton(fieldsComposite,
            Messages.getString("Cabinet.checkButton.text"), //$NON-NLS-1$
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 3;
        checkButton.setLayoutData(gd);
        checkButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkPositionAndAliquot();
            }
        });
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            canLaunchCheck, Messages.getString("Cabinet.canLaunchCheck.msg"));
    }

    /**
     * Only one in creation mode. 3 fields in move mode
     */
    private void createPositionFields(Composite fieldsComposite) {
        GridData gd;
        // for move mode: display old position retrieved from database
        oldCabinetPositionLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("Cabinet.old.position.label"));
        oldCabinetPositionLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        oldCabinetPositionText = (BiobankText) widgetCreator.createBoundWidget(
            fieldsComposite, BiobankText.class, SWT.NONE,
            oldCabinetPositionLabel, new String[0], null, null);
        gd = (GridData) oldCabinetPositionText.getLayoutData();
        gd.horizontalSpan = 2;
        oldCabinetPositionText.setEnabled(false);
        oldCabinetPositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for move mode: field to enter old position. Check needed to be sure
        // nothing is wrong with the aliquot
        oldCabinetPositionCheckLabel = widgetCreator.createLabel(
            fieldsComposite,
            Messages.getString("Cabinet.old.position.check.label"));
        oldCabinetPositionCheckLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        oldCabinetPositionCheckValidator = new AbstractValidator(
            "Enter correct old position") {
            @Override
            public IStatus validate(Object value) {
                if (value != null && !(value instanceof String)) {
                    throw new RuntimeException(
                        "Not supposed to be called for non-strings.");
                }

                if (value != null) {
                    String s = (String) value;
                    if (s.equals(oldCabinetPositionText.getText())) {
                        hideDecoration();
                        return Status.OK_STATUS;
                    }
                }
                showDecoration();
                return ValidationStatus.error(errorMessage);
            }
        };
        oldCabinetPositionCheckText = (BiobankText) widgetCreator
            .createBoundWidget(fieldsComposite, BiobankText.class, SWT.NONE,
                oldCabinetPositionCheckLabel, new String[0], new WritableValue(
                    "", String.class), oldCabinetPositionCheckValidator);
        gd = (GridData) oldCabinetPositionCheckText.getLayoutData();
        gd.horizontalSpan = 2;
        oldCabinetPositionCheckText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for all modes: position to be assigned to the aliquot
        newCabinetPositionLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("Cabinet.position.label"));
        newCabinetPositionLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        newCabinetPositionValidator = new StringLengthValidator(4,
            Messages.getString("Cabinet.position.validationMsg"));
        displayOldCabinetFields(false);
        newCabinetPositionText = (BiobankText) widgetCreator.createBoundWidget(
            fieldsComposite, BiobankText.class, SWT.NONE,
            newCabinetPositionLabel, new String[0], new WritableValue(
                "", String.class), newCabinetPositionValidator); //$NON-NLS-1$
        gd = (GridData) newCabinetPositionText.getLayoutData();
        gd.horizontalSpan = 2;
        newCabinetPositionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (positionTextModified
                    && newCabinetPositionValidator
                        .validate(newCabinetPositionText.getText()) == Status.OK_STATUS) {
                    BusyIndicator.showWhile(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getShell().getDisplay(),
                        new Runnable() {
                            @Override
                            public void run() {
                                initContainersFromPosition();
                                int typeListSize = setTypeCombosLists();
                                if (typeListSize == 0) {
                                    newCabinetPositionText.setFocus();
                                } else {
                                    viewerSampleTypes.getCombo().setFocus();
                                }
                            }
                        });
                }
                positionTextModified = false;
            }
        });
        newCabinetPositionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                positionTextModified = true;
                if (radioNew.getSelection()) {
                    viewerSampleTypes.setInput(null);
                }
                resultShownValue.setValue(Boolean.FALSE);
                displayPositions(false);
            }
        });
        newCabinetPositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        displayOldCabinetFields(false);
    }

    private void displayOldCabinetFields(boolean displayOld) {
        oldCabinetPositionLabel.setVisible(displayOld);
        ((GridData) oldCabinetPositionLabel.getLayoutData()).exclude = !displayOld;
        oldCabinetPositionText.setVisible(displayOld);
        ((GridData) oldCabinetPositionText.getLayoutData()).exclude = !displayOld;
        oldCabinetPositionCheckLabel.setVisible(displayOld);
        ((GridData) oldCabinetPositionCheckLabel.getLayoutData()).exclude = !displayOld;
        oldCabinetPositionCheckText.setVisible(displayOld);
        ((GridData) oldCabinetPositionCheckText.getLayoutData()).exclude = !displayOld;
        if (displayOld) {
            newCabinetPositionLabel.setText(Messages
                .getString("Cabinet.new.position.label"));
            oldCabinetPositionCheckText.setText("");
        } else {
            newCabinetPositionLabel.setText(Messages
                .getString("Cabinet.position.label"));
            oldCabinetPositionCheckText.setText(oldCabinetPositionText
                .getText());
        }
        page.layout(true, true);
    }

    protected void initContainersFromPosition() {
        try {
            bin = null;
            drawer = null;
            cabinet = null;
            SiteWrapper currentSite = SessionManager.getUser()
                .getCurrentWorkingCentre();
            String fullLabel = newCabinetPositionText.getText();
            List<ContainerWrapper> foundContainers = new ArrayList<ContainerWrapper>();
            int removeSize = 2;
            List<String> labelsTested = new ArrayList<String>();
            while (removeSize < 5) {
                String binLabel = fullLabel.substring(0, fullLabel.length()
                    - removeSize);
                labelsTested.add(binLabel);
                for (ContainerWrapper cont : ContainerWrapper
                    .getContainersInSite(appService, currentSite, binLabel)) {
                    boolean canContainSamples = cont.getContainerType()
                        .getSpecimenTypeCollection() != null
                        && cont.getContainerType().getSpecimenTypeCollection()
                            .size() > 0;
                    if (canContainSamples) {
                        foundContainers.add(cont);
                    }
                }
                removeSize++;
            }
            List<ContainerWrapper> cabinetContainers = new ArrayList<ContainerWrapper>();
            for (ContainerWrapper container : foundContainers) {
                ContainerWrapper cont = container;
                while (cont.getParent() != null) {
                    cont = cont.getParent();
                }
                if (cabinetContainerTypes.contains(cont.getContainerType())) {
                    cabinetContainers.add(container);
                }
            }
            if (cabinetContainers.size() == 1) {
                bin = cabinetContainers.get(0);
                drawer = bin.getParent();
                cabinet = drawer.getParent();
            } else if (cabinetContainers.size() == 0) {
                String errorMsg = Messages
                    .getString(
                        "Cabinet.activitylog.checkParent.error.found", getBinLabelMessage(labelsTested), currentSite.getNameShort()); //$NON-NLS-1$
                BioBankPlugin.openError("Check position and aliquot", errorMsg); //$NON-NLS-1$
                appendLogNLS("Cabinet.activitylog.checkParent.error", errorMsg); //$NON-NLS-1$
                viewerSampleTypes.getCombo().setEnabled(false);
                focusControlInError(newCabinetPositionText);
                return;
            } else {
                BioBankPlugin
                    .openError(
                        "Container problem",
                        "More than one container found for " + getBinLabelMessage(labelsTested) //$NON-NLS-1$
                            + " --- should do something"); //$NON-NLS-1$
                viewerSampleTypes.getCombo().setEnabled(false);
                focusControlInError(newCabinetPositionText);
                return;
            }
        } catch (Exception ex) {
            BioBankPlugin.openError("Init container from position", ex);
            focusControlInError(newCabinetPositionText);
        }
    }

    private String getBinLabelMessage(List<String> labelsTested) {
        String res = "";
        for (int i = 0; i < labelsTested.size(); i++) {
            if (i != 0) {
                res += " or ";
            }
            res += labelsTested.get(i);
        }
        return res;
    }

    protected void setSpecimenMode(AliquotMode mode) {
        try {
            aliquotMode = mode;
            reset();

            boolean enabled = (aliquotMode == AliquotMode.NEW_ALIQUOT);

            linkFormPatientManagement.enabledPatientText(enabled);
            linkFormPatientManagement.enabledVisitsList(enabled);
            linkFormPatientManagement.enableValidators(enabled);
            inventoryIDValidator.setManageOldInventoryIDs(!enabled);
            // Validator has change: we need to re-validate
            inventoryIDValidator.validate("");
            displayOldCabinetFields(!enabled);
            enableAndShowSampleTypeCombo();
            canLaunchCheck.setValue(true);
            if (enabled) {
                linkFormPatientManagement.setFirstControl();
                setFocus();
            } else {
                setFirstControl(inventoryIdText);
                setFocus();
            }
            page.layout(true, true);
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError(
                "Error setting move mode " + aliquotMode, //$NON-NLS-1$
                ex);
        }
    }

    private void enableAndShowSampleTypeCombo() {
        boolean enabled = (aliquotMode == AliquotMode.NEW_ALIQUOT);
        if (enabled) {
            widgetCreator.addBinding(SAMPLE_TYPE_LIST_BINDING);
        } else {
            widgetCreator.removeBinding(SAMPLE_TYPE_LIST_BINDING);
        }
        viewerSampleTypes.getCombo().setEnabled(enabled);
        widgetCreator.showWidget(sampleTypeComboLabel, enabled);
        widgetCreator.showWidget(viewerSampleTypes.getCombo(), enabled);
        widgetCreator.showWidget(sampleTypeTextLabel, !enabled);
        widgetCreator.showWidget(sampleTypeText, !enabled);
    }

    private void createTypeCombo(Composite fieldsComposite)
        throws ApplicationException {
        initCabinetContainerTypesList();
        sampleTypeComboLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("Cabinet.sampleType.label"));
        viewerSampleTypes = widgetCreator.createComboViewer(fieldsComposite,
            sampleTypeComboLabel, null, null,
            Messages.getString("Cabinet.sampleType.validationMsg"), true,
            SAMPLE_TYPE_LIST_BINDING, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    if (aliquotMode == AliquotMode.MOVE_ALIQUOT)
                        return;
                    aliquot
                        .setSpecimenType((SpecimenTypeWrapper) selectedObject);

                }
            }); //$NON-NLS-1$
        GridData gd = (GridData) viewerSampleTypes.getCombo().getLayoutData();
        gd.horizontalSpan = 2;

        // for move mode
        sampleTypeTextLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("Cabinet.sampleType.label"));
        sampleTypeTextLabel.setLayoutData(new GridData(
            GridData.VERTICAL_ALIGN_BEGINNING));
        sampleTypeText = (BiobankText) widgetCreator.createBoundWidget(
            fieldsComposite, BiobankText.class, SWT.NONE, sampleTypeTextLabel,
            new String[0], null, null);
        ((GridData) sampleTypeText.getLayoutData()).horizontalSpan = 2;
        sampleTypeText.setEnabled(false);
    }

    private void initCabinetContainerTypesList() throws ApplicationException {
        if (SessionManager.getUser().getCurrentWorkingCentre() == null)
            cabinetContainerTypes = new ArrayList<ContainerTypeWrapper>();
        else {
            cabinetContainerTypes = ContainerTypeWrapper
                .getContainerTypesInSite(appService, SessionManager.getUser()
                    .getCurrentWorkingCentre(), cabinetNameContains, false);
            if (cabinetContainerTypes.size() == 0)
                BioBankPlugin.openAsyncError(
                    Messages.getString("Cabinet.dialog.noType.error.title"), //$NON-NLS-1$
                    Messages.getString("Cabinet.dialog.notType.error.msg", //$NON-NLS-1$
                        cabinetNameContains));
        }
    }

    private List<SpecimenTypeWrapper> getCabinetSpecimenTypes()
        throws ApplicationException {
        if (cabinetSpecimenTypes == null) {
            cabinetSpecimenTypes = SpecimenTypeWrapper
                .getSpecimenTypeForContainerTypes(appService, SessionManager
                    .getUser().getCurrentWorkingCentre(), cabinetNameContains);
        }
        return cabinetSpecimenTypes;
    }

    protected void checkPositionAndAliquot() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    appendLog("----"); //$NON-NLS-1$
                    CollectionEventWrapper ce = linkFormPatientManagement
                        .getSelectedCollectionEvent();
                    aliquot.setCollectionEvent(ce);
                    if (radioNew.getSelection()) {
                        appendLogNLS("Cabinet.activitylog.checkingId", //$NON-NLS-1$
                            aliquot.getInventoryId());
                        aliquot.checkInventoryIdUnique();
                    }
                    String positionString = newCabinetPositionText.getText();
                    if (bin == null) {
                        resultShownValue.setValue(Boolean.FALSE);
                        displayPositions(false);
                        return;
                    }
                    appendLogNLS(
                        "Cabinet.activitylog.checkingPosition", positionString); //$NON-NLS-1$
                    aliquot.setSpecimenPositionFromString(positionString, bin);
                    if (aliquot.isPositionFree(bin)) {
                        aliquot.setParent(bin);
                        displayPositions(true);
                        resultShownValue.setValue(Boolean.TRUE);
                        cancelConfirmWidget.setFocus();
                    } else {
                        BioBankPlugin.openError("Position not free", Messages
                            .getString(
                                "Cabinet.checkStatus.error", positionString, //$NON-NLS-1$
                                bin.getLabel()));
                        appendLogNLS(
                            "Cabinet.activitylog.checkPosition.error", positionString, bin.getLabel()); //$NON-NLS-1$
                        focusControlInError(newCabinetPositionText);
                        return;
                    }
                    setDirty(true);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage(exp);
                } catch (BiobankCheckException bce) {
                    BioBankPlugin.openError(
                        "Error while checking position", bce); //$NON-NLS-1$
                    appendLog("ERROR: " + bce.getMessage()); //$NON-NLS-1$
                    resultShownValue.setValue(Boolean.FALSE);
                    focusControlInError(inventoryIdText);
                } catch (Exception e) {
                    BioBankPlugin.openError("Error while checking position", e); //$NON-NLS-1$
                    focusControlInError(newCabinetPositionText);
                }
            }

        });
    }

    protected void focusControlInError(final Control control) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                control.setFocus();
            }
        });
    }

    /**
     * Get sample types only defined in the patient's study and available in
     * current selected bin. Then set these types to the types combo
     * 
     * @return the size of type combo list
     * @throws Exception
     */
    private int setTypeCombosLists() {
        if (aliquotMode == AliquotMode.MOVE_ALIQUOT)
            return -1;

        viewerSampleTypes.getCombo().setEnabled(true);
        List<SpecimenTypeWrapper> studiesSampleTypes = new ArrayList<SpecimenTypeWrapper>();
        if (linkFormPatientManagement.getCurrentPatient() != null
            && bin != null) {
            List<SpecimenTypeWrapper> binTypes = bin.getContainerType()
                .getSpecimenTypeCollection();
            StudyWrapper study = linkFormPatientManagement.getCurrentPatient()
                .getStudy();
            try {
                // need to reload study to avoid performance problem when using
                // the same lots of time (like is try differents positions for
                // same patient)
                study.reload();
            } catch (Exception e) {
                BioBankPlugin.openAsyncError("Problem reloading study", e);
            }
            for (AliquotedSpecimenWrapper ss : study
                .getAliquotedSpecimenCollection(true)) {
                if (ss.getActivityStatus().isActive()) {
                    SpecimenTypeWrapper type = ss.getSpecimenType();
                    if (binTypes.contains(type)) {
                        studiesSampleTypes.add(type);
                    }
                }
            }
            if (studiesSampleTypes.size() == 0) {
                String studyText = "unknown";
                if (linkFormPatientManagement.getCurrentPatient() != null) {
                    studyText = linkFormPatientManagement.getCurrentPatient()
                        .getStudy().getNameShort();
                }
                BioBankPlugin.openError(
                    "No Sample Types",
                    "There are no sample types that "
                        + "are defined for current patient study (" + studyText
                        + ") and that are defined as possible for bin "
                        + bin.getLabel());
            }
            if (!radioNew.getSelection()) {
                // Move
                SpecimenTypeWrapper type = aliquot.getSpecimenType();
                if (!studiesSampleTypes.contains(type)
                    && binTypes.contains(type)) {
                    // in move mode, the sample source could be deactivate
                    studiesSampleTypes.add(type);
                }
            }
        }
        viewerSampleTypes.setInput(studiesSampleTypes);
        viewerSampleTypes.getCombo().setEnabled(true);
        if (studiesSampleTypes.size() == 1) {
            viewerSampleTypes.getCombo().select(0);
            aliquot.setSpecimenType(studiesSampleTypes.get(0));
        } else {
            viewerSampleTypes.getCombo().deselectAll();
            aliquot.setSpecimenType(null);
        }
        return studiesSampleTypes.size();
    }

    /**
     * In move mode, get informations from the existing aliquot
     * 
     * @throws Exception
     */
    protected void retrieveAliquotDataForMoving() throws Exception {
        String inventoryId = inventoryIdText.getText();
        if (inventoryId.isEmpty()) {
            return;
        }
        if (inventoryId.length() == 4) {
            // compatibility with old aliquots imported
            // 4 letters aliquots are now C+4letters
            inventoryId = "C" + inventoryId; //$NON-NLS-1$
        }
        resultShownValue.setValue(false);
        reset();
        aliquot.setInventoryId(inventoryId);
        inventoryIdText.setText(inventoryId);
        oldCabinetPositionCheckText.setText("?");

        appendLogNLS("Cabinet.activitylog.gettingInfoId", //$NON-NLS-1$
            aliquot.getInventoryId());
        SpecimenWrapper foundAliquot = SpecimenWrapper.getSpecimen(appService,
            aliquot.getInventoryId(), SessionManager.getUser());
        if (foundAliquot == null) {
            canLaunchCheck.setValue(false);
            throw new Exception("No aliquot found with inventoryId " //$NON-NLS-1$
                + aliquot.getInventoryId());
        }
        aliquot.initObjectWith(foundAliquot);
        List<SpecimenTypeWrapper> possibleTypes = getCabinetSpecimenTypes();
        if (!possibleTypes.contains(aliquot.getSpecimenType())) {
            canLaunchCheck.setValue(false);
            throw new Exception(
                "This aliquot is of type " + aliquot.getSpecimenType().getNameShort() //$NON-NLS-1$
                    + ": this is not a cabinet type");
        }
        if (aliquot.isUsedInDispatch()) {
            canLaunchCheck.setValue(false);
            throw new Exception(
                "This aliquot is currently in transit in a dispatch.");
        }
        canLaunchCheck.setValue(true);
        PatientWrapper patient = aliquot.getCollectionEvent().getPatient();
        linkFormPatientManagement.setCurrentPatientAndVisit(patient,
            aliquot.getProcessingEvent());
        String positionString = aliquot.getPositionString(true, false);
        if (positionString == null) {
            widgetCreator.hideWidget(oldCabinetPositionCheckLabel);
            widgetCreator.hideWidget(oldCabinetPositionCheckText);
            oldCabinetPositionCheckText.setText("");
            positionString = "none"; //$NON-NLS-1$
        } else {
            widgetCreator.showWidget(oldCabinetPositionCheckLabel);
            widgetCreator.showWidget(oldCabinetPositionCheckText);
            oldCabinetPositionCheckText.setText(oldCabinetPositionCheckText
                .getText());
        }
        oldCabinetPositionText.setText(positionString);
        sampleTypeText.setText(aliquot.getSpecimenType().getName());
        page.layout(true, true);
        appendLogNLS(
            "Cabinet.activitylog.aliquotInfo", aliquot.getInventoryId(), //$NON-NLS-1$
            positionString);
    }

    private void displayPositions(boolean show) {
        widgetCreator.showWidget(cabinetWidget, show);
        widgetCreator.showWidget(cabinetLabel, show);
        widgetCreator.showWidget(drawerWidget, show);
        widgetCreator.showWidget(drawerLabel, show);
        if (show) {
            cabinetWidget.setContainerType(cabinet.getContainerType());
            cabinetWidget.setSelection(drawer.getPositionAsRowCol());
            cabinetLabel.setText("Cabinet " + cabinet.getLabel()); //$NON-NLS-1$
            drawerWidget.setContainer(drawer);
            drawerWidget.setSelection(bin.getPositionAsRowCol());
            drawerLabel.setText("Drawer " + drawer.getLabel()); //$NON-NLS-1$
        }
        page.layout(true, true);
        book.reflow(true);
        // FIXME this is working to display the right length of horizontal
        // scroll bar when the drawer is very large, but doesn't seems a pretty
        // way to do it...
        containersScroll.setMinSize(clientInsideGridScroll.computeSize(
            SWT.DEFAULT, SWT.DEFAULT));
    }

    @Override
    public void reset() throws Exception {
        aliquot.reset(); // reset internal values
        cabinet = null;
        drawer = null;
        bin = null;
        cabinetWidget.setSelection(null);
        drawerWidget.setSelection(null);
        resultShownValue.setValue(Boolean.FALSE);
        linkFormPatientManagement.reset(true);
        // the 2 following lines are needed. The validator won't update if don't
        // do that (why ?)
        inventoryIdText.setText("**"); //$NON-NLS-1$ 
        inventoryIdText.setText(""); //$NON-NLS-1$
        oldCabinetPositionText.setText("");
        oldCabinetPositionCheckText.setText(""); //$NON-NLS-1$
        newCabinetPositionText.setText(""); //$NON-NLS-1$
        sampleTypeText.setText("");
        if (viewerSampleTypes.getCombo().getItemCount() > 1) {
            viewerSampleTypes.getCombo().deselectAll();
        }
        setDirty(false);
        setFocus();
    }

    @Override
    protected void saveForm() throws Exception {
        if (newAliquotCreation) {
            aliquot.setCreatedAt(new Date());
            aliquot.setQuantityFromType();
            aliquot.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        }
        aliquot.persist();
        String posStr = aliquot.getPositionString(true, false);
        if (posStr == null) {
            posStr = "none"; //$NON-NLS-1$
        }
        String msgString = "";
        if (newAliquotCreation) {
            msgString = "Cabinet.activitylog.aliquot.saveNew"; //$NON-NLS-1$
        } else {
            msgString = "Cabinet.activitylog.aliquot.saveMove"; //$NON-NLS-1$
        }
        appendLogNLS(msgString, posStr, aliquot.getInventoryId(), aliquot
            .getSpecimenType().getName(), linkFormPatientManagement
            .getCurrentPatient().getPnumber(), aliquot.getProcessingEvent()
            .getFormattedCreatedAt(), aliquot.getCurrentCenter().getName());
        setFinished(false);
    }

    @Override
    protected String getOkMessage() {
        return "Add cabinet aliquots."; //$NON-NLS-1$
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            form.setMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            setConfirmEnabled(true);
            checkButton.setEnabled(true);
        } else {
            form.setMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
            checkButton.setEnabled(canLaunchCheck.getValue().equals(true));
            if (status.getMessage() != null
                && status.getMessage().contentEquals(
                    Messages.getString("Cabinet.checkButton.validationMsg"))) {
                checkButton.setEnabled(true);
            } else {
                checkButton.setEnabled(false);
            }
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected String getActivityTitle() {
        return "Cabinet link/assign activity"; //$NON-NLS-1$
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    public boolean onClose() {
        linkFormPatientManagement.onClose();
        return super.onClose();
    }

}
