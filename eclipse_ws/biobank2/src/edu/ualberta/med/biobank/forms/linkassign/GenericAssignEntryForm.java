package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.scanprocess.data.AssignProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.dialogs.select.SelectParentContainerDialog;
import edu.ualberta.med.biobank.forms.AbstractPalletSpecimenAdminForm;
import edu.ualberta.med.biobank.forms.listener.EnterKeyToNextFieldListener;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.validators.AbstractValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.StringLengthValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;

public class GenericAssignEntryForm extends AbstractPalletSpecimenAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.GenericAssignEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(GenericAssignEntryForm.class.getName());

    private static boolean singleMode = false;

    private StackLayout stackLayout;

    // for single specimen assign
    private Composite singleAssignComposite;
    private SpecimenWrapper specimen;
    private BiobankText inventoryIdText;
    private Label oldCabinetPositionLabel;
    private BiobankText oldCabinetPositionText;
    private Label oldCabinetPositionCheckLabel;
    private AbstractValidator oldCabinetPositionCheckValidator;
    private BiobankText oldCabinetPositionCheckText;
    private Label newCabinetPositionLabel;
    private StringLengthValidator newCabinetPositionValidator;
    private BiobankText newCabinetPositionText;
    private static IObservableValue canLaunchCheck = new WritableValue(
        Boolean.TRUE, Boolean.class);
    private ContainerWrapper firstParent;
    private ContainerWrapper secondParent;
    private ContainerWrapper thirdParent;
    private Label thirdParentLabel;
    private Label secondParentLabel;
    private ContainerDisplayWidget thirdParentWidget;
    private ContainerDisplayWidget secondParentWidget;

    // for multiple specimens assign
    private Composite multipleAssignComposite;
    private ContainerWrapper currentParentContainer = new ContainerWrapper(
        appService);
    private ScanPalletWidget parentContainerWidget;

    private ScrolledComposite visualisationScrollComposite;

    private Composite visualisationMainComposite;

    protected boolean inventoryIdModified;

    protected boolean positionTextModified;

    @Override
    protected void init() throws Exception {
        super.init();
        specimen = new SpecimenWrapper(appService);
    }

    @Override
    protected String getActivityTitle() {
        return "Generic Assign";
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Assign position"); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        page.setLayout(layout);

        createLeftSection();

        createContainersVisualisationSection();

        createCancelConfirmWidget();
    }

    private void createLeftSection() {
        Composite leftComposite = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        leftComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.widthHint = 600;
        gd.verticalAlignment = SWT.TOP;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        leftComposite.setLayoutData(gd);
        toolkit.paintBordersFor(leftComposite);

        BiobankText siteLabel = createReadOnlyLabelledField(leftComposite,
            SWT.NONE, Messages.getString("ScanAssign.site.label")); //$NON-NLS-1$
        siteLabel.setText(SessionManager.getUser().getCurrentWorkingCenter()
            .getNameShort());
        setFirstControl(siteLabel);

        // radio button to choose single or multiple
        final Button radioSingle = toolkit.createButton(leftComposite,
            "Single", SWT.RADIO);
        final Button radioMultiple = toolkit.createButton(leftComposite,
            "Multiple", SWT.RADIO);

        // stackLayout
        final Composite stackComposite = toolkit.createComposite(leftComposite);
        stackLayout = new StackLayout();
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        stackComposite.setLayoutData(gd);
        stackComposite.setLayout(stackLayout);

        createSingleLinkComposite(stackComposite);
        createMultipleLink(stackComposite);
        radioSingle.setSelection(true);
        stackLayout.topControl = singleAssignComposite;

        radioSingle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioSingle.getSelection()) {
                    setStackTopComposite(true);
                }
            }
        });
        radioMultiple.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioMultiple.getSelection()) {
                    setStackTopComposite(false);
                }
            }
        });
    }

    /**
     * Show either single or multiple selection for linking
     */
    private void setStackTopComposite(boolean single) {
        singleMode = single;
        if (single) {
            stackLayout.topControl = singleAssignComposite;
        } else {
            stackLayout.topControl = multipleAssignComposite;
        }
        if (parentContainerWidget != null)
            widgetCreator.showWidget(parentContainerWidget, !single);
        page.layout(true, true);
    }

    private void createSingleLinkComposite(Composite parent) {
        singleAssignComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        singleAssignComposite.setLayout(layout);
        toolkit.paintBordersFor(singleAssignComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleAssignComposite.setLayoutData(gd);

        // inventoryID
        SpecimenWrapper singleSpecimen = new SpecimenWrapper(appService);
        inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            singleAssignComposite, BiobankText.class, SWT.NONE,
            Messages.getString("Cabinet.inventoryId.label"), new String[0], //$NON-NLS-1$
            singleSpecimen, "inventoryId", //$NON-NLS-1$
            new NonEmptyStringValidator("Inventory Id should be selected"));
        inventoryIdText.addKeyListener(textFieldKeyListener);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (inventoryIdModified)
                    try {
                        retrieveSpecimenData();
                    } catch (Exception ex) {
                        BiobankPlugin.openError("Move - specimen error", ex); //$NON-NLS-1$
                        focusControlInError(inventoryIdText);
                    }
                inventoryIdModified = false;
            }
        });
        inventoryIdText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                inventoryIdModified = true;
                positionTextModified = true;
                // resultShownValue.setValue(Boolean.FALSE);
                displayPositions(false);
            }
        });
        createPositionFields(singleAssignComposite);

        Button checkButton = toolkit.createButton(singleAssignComposite,
            Messages.getString("Cabinet.checkButton.text"), //$NON-NLS-1$
            SWT.PUSH);
        gd = new GridData();
        gd.horizontalSpan = 2;
        checkButton.setLayoutData(gd);
        checkButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                checkPositionAndSpecimen();
            }
        });
    }

    /**
     * 
     * Single assign. Search the specimen, if find it, display related
     * information
     */
    protected void retrieveSpecimenData() throws Exception {
        String inventoryId = inventoryIdText.getText();
        if (inventoryId.isEmpty()) {
            return;
        }
        if (inventoryId.length() == 4) {
            // compatibility with old cabinet specimens imported
            // 4 letters specimens are now C+4letters
            inventoryId = "C" + inventoryId; //$NON-NLS-1$
        }
        // resultShownValue.setValue(false);
        reset();
        specimen.setInventoryId(inventoryId);
        inventoryIdText.setText(inventoryId);
        oldCabinetPositionCheckText.setText("?");

        appendLog(Messages.getString("Cabinet.activitylog.gettingInfoId", //$NON-NLS-1$
            specimen.getInventoryId()));
        SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(appService,
            specimen.getInventoryId(), SessionManager.getUser());
        if (foundSpecimen == null) {
            canLaunchCheck.setValue(false);
            throw new Exception("No specimen found with inventoryId " //$NON-NLS-1$
                + specimen.getInventoryId());
        }
        specimen.initObjectWith(foundSpecimen);
        // List<SpecimenTypeWrapper> possibleTypes = getCabinetSpecimenTypes();
        // if (!possibleTypes.contains(specimen.getSpecimenType())) {
        // canLaunchCheck.setValue(false);
        // throw new Exception(
        //                "This specimen is of type " + specimen.getSpecimenType().getNameShort() //$NON-NLS-1$
        // + ": this is not a cabinet type");
        // }
        if (specimen.isUsedInDispatch()) {
            canLaunchCheck.setValue(false);
            throw new Exception(
                "This specimen is currently in transit in a dispatch.");
        }
        canLaunchCheck.setValue(true);
        String positionString = specimen.getPositionString(true, false);
        if (positionString == null) {
            displayOldCabinetFields(false);
            positionString = "none"; //$NON-NLS-1$
        } else {
            displayOldCabinetFields(true);
            oldCabinetPositionCheckText.setText(oldCabinetPositionCheckText
                .getText());
        }
        oldCabinetPositionText.setText(positionString);
        page.layout(true, true);
        appendLog(Messages.getString(
            "Cabinet.activitylog.specimenInfo", specimen.getInventoryId(), //$NON-NLS-1$
            positionString));
    }

    /**
     * Some fields will be displayed only if the specimen has already a position
     */
    private void createPositionFields(Composite fieldsComposite) {
        // for move mode: display old position retrieved from database
        oldCabinetPositionLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("Cabinet.old.position.label"));
        oldCabinetPositionText = (BiobankText) widgetCreator.createBoundWidget(
            fieldsComposite, BiobankText.class, SWT.NONE,
            oldCabinetPositionLabel, new String[0], null, null);
        oldCabinetPositionText.setEnabled(false);
        oldCabinetPositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for move mode: field to enter old position. Check needed to be sure
        // nothing is wrong with the specimen
        oldCabinetPositionCheckLabel = widgetCreator.createLabel(
            fieldsComposite,
            Messages.getString("Cabinet.old.position.check.label"));
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
        oldCabinetPositionCheckText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);

        // for all modes: position to be assigned to the specimen
        newCabinetPositionLabel = widgetCreator.createLabel(fieldsComposite,
            Messages.getString("Cabinet.position.label"));
        newCabinetPositionValidator = new StringLengthValidator(4,
            Messages.getString("Cabinet.position.validationMsg"));
        displayOldCabinetFields(false);
        newCabinetPositionText = (BiobankText) widgetCreator.createBoundWidget(
            fieldsComposite, BiobankText.class, SWT.NONE,
            newCabinetPositionLabel, new String[0], new WritableValue(
                "", String.class), newCabinetPositionValidator); //$NON-NLS-1$
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
                // resultShownValue.setValue(Boolean.FALSE);
                displayPositions(false);
            }
        });
        newCabinetPositionText
            .addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        displayOldCabinetFields(false);

    }

    private void displayOldCabinetFields(boolean displayOld) {
        widgetCreator.showWidget(oldCabinetPositionLabel, displayOld);
        widgetCreator.showWidget(oldCabinetPositionText, displayOld);
        widgetCreator.showWidget(oldCabinetPositionCheckLabel, displayOld);
        widgetCreator.showWidget(oldCabinetPositionCheckText, displayOld);
        if (displayOld) {
            newCabinetPositionLabel.setText(Messages
                .getString("Cabinet.new.position.label") + ":");
        } else {
            newCabinetPositionLabel.setText(Messages
                .getString("Cabinet.position.label") + ":");
            oldCabinetPositionCheckText.setText(oldCabinetPositionText
                .getText());
        }
        page.layout(true, true);
    }

    private void createMultipleLink(Composite parent) {
        multipleAssignComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        multipleAssignComposite.setLayout(layout);
        toolkit.paintBordersFor(multipleAssignComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleAssignComposite.setLayoutData(gd);

        NonEmptyStringValidator productBarcodeValidator = new NonEmptyStringValidator(
            Messages.getString("ScanAssign.productBarcode.validationMsg"));//$NON-NLS-1$
        NonEmptyStringValidator palletLabelValidator = new NonEmptyStringValidator(
            Messages.getString("ScanAssign.palletLabel.validationMsg"));//$NON-NLS-1$

        BiobankText palletproductBarcodeText = (BiobankText) createBoundWidgetWithLabel(
            multipleAssignComposite, BiobankText.class,
            SWT.NONE,
            Messages.getString("ScanAssign.productBarcode.label"), //$NON-NLS-1$
            null, currentParentContainer,
            ContainerPeer.PRODUCT_BARCODE.getName(), productBarcodeValidator);
        palletproductBarcodeText.addKeyListener(textFieldKeyListener);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletproductBarcodeText.setLayoutData(gd);
        setFirstControl(palletproductBarcodeText);

        palletproductBarcodeText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // if (palletproductBarcodeTextModified
                // && productBarcodeValidator.validate(
                // currentPalletWrapper.getProductBarcode()).equals(
                // Status.OK_STATUS)) {
                // validateValues();
                // }
                // palletproductBarcodeTextModified = false;
            }
        });
        palletproductBarcodeText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                // if (!modificationMode) {
                // palletproductBarcodeTextModified = true;
                // validationMade.setValue(false);
                // }
            }
        });

        BiobankText palletPositionText = (BiobankText) createBoundWidgetWithLabel(
            multipleAssignComposite, BiobankText.class, SWT.NONE,
            Messages.getString("ScanAssign.palletLabel.label"), null, //$NON-NLS-1$
            BeansObservables.observeValue(currentParentContainer,
                ContainerPeer.LABEL.getName()), palletLabelValidator);
        palletPositionText.addKeyListener(EnterKeyToNextFieldListener.INSTANCE);
        gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        palletPositionText.setLayoutData(gd);
        palletPositionText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // if (palletPositionTextModified) {
                // validateValues();
                // }
                // palletPositionTextModified = false;
            }
        });
        palletPositionText.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                // if (!modificationMode) {
                // palletPositionTextModified = true;
                // validationMade.setValue(false);
                // }
            }
        });

        createPalletTypesViewer(multipleAssignComposite);

        createPlateToScanField(multipleAssignComposite);
    }

    private void resetParentContainers() {
        thirdParent = null;
        secondParent = null;
        firstParent = null;
        if (thirdParentWidget != null)
            thirdParentWidget.setSelection(null);
        if (secondParentWidget != null)
            secondParentWidget.setSelection(null);
    }

    protected void initContainersFromPosition() {
        resetParentContainers();
        try {
            firstParent = null;
            secondParent = null;
            thirdParent = null;
            SiteWrapper currentSite = SessionManager.getUser()
                .getCurrentWorkingSite();
            String fullLabel = newCabinetPositionText.getText();
            List<ContainerWrapper> foundContainers = new ArrayList<ContainerWrapper>();
            int removeSize = 2; // FIXME we are assuming that the specimen
                                // position will be only of size 2 !
            List<String> labelsTested = new ArrayList<String>();
            while (removeSize < 5) { // we are assuming that the bin
                                     // position won't be bigger than 3 !
                int cutIndex = fullLabel.length() - removeSize;
                String binLabel = fullLabel.substring(0, cutIndex);
                labelsTested.add(binLabel);
                for (ContainerWrapper cont : ContainerWrapper
                    .getContainersInSite(appService, currentSite, binLabel)) {
                    boolean canContainSamples = cont.getContainerType()
                        .getSpecimenTypeCollection() != null
                        && cont.getContainerType().getSpecimenTypeCollection()
                            .size() > 0;
                    if (canContainSamples) {
                        RowColPos rcp = null;
                        try {
                            rcp = ContainerLabelingSchemeWrapper
                                .getRowColFromPositionString(appService,
                                    fullLabel.substring(cutIndex), cont
                                        .getContainerType()
                                        .getChildLabelingSchemeId(), cont
                                        .getContainerType().getRowCapacity(),
                                    cont.getContainerType().getColCapacity());
                        } catch (Exception ex) {
                            // the test failed
                            continue;
                        }
                        if (rcp != null) // the full position string is valid:
                            foundContainers.add(cont);
                    }
                }
                removeSize++;
            }
            if (foundContainers.size() == 1) {
                initContainersParents(foundContainers.get(0));
            } else if (foundContainers.size() == 0) {
                String errorMsg = Messages.getString(
                    "Cabinet.activitylog.checkParent.error.found", //$NON-NLS-1$
                    getBinLabelMessage(fullLabel, labelsTested));
                BiobankPlugin
                    .openError("Check position and specimen", errorMsg); //$NON-NLS-1$
                appendLog(Messages.getString(
                    "Cabinet.activitylog.checkParent.error", errorMsg)); //$NON-NLS-1$
                focusControlInError(newCabinetPositionText);
                return;
            } else {
                SelectParentContainerDialog dlg = new SelectParentContainerDialog(
                    PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                        .getShell(), foundContainers);
                dlg.open();
                if (dlg.getSelectedContainer() == null) {
                    StringBuffer sb = new StringBuffer();
                    for (ContainerWrapper cont : foundContainers) {
                        sb.append(cont.getFullInfoLabel());
                    }
                    BiobankPlugin.openError("Container problem",
                        "More than one container found mathing the position label: "
                            + sb.toString() + " --- should do something");
                    focusControlInError(newCabinetPositionText);
                } else
                    initContainersParents(dlg.getSelectedContainer());
            }
        } catch (Exception ex) {
            BiobankPlugin.openError("Init container from position", ex);
            focusControlInError(newCabinetPositionText);
        }
    }

    private void initContainersParents(ContainerWrapper cabinetContainer) {
        // only one cabinet container has been found
        firstParent = cabinetContainer;
        secondParent = firstParent.getParentContainer();
        thirdParent = secondParent.getParentContainer();
        appendLog(Messages.getString(
            "Cabinet.activitylog.containers.init", //$NON-NLS-1$
            thirdParent.getFullInfoLabel(), secondParent.getFullInfoLabel(),
            firstParent.getFullInfoLabel()));
    }

    private String getBinLabelMessage(String fullLabel,
        List<String> labelsTested) {
        StringBuffer res = new StringBuffer();
        for (int i = 0; i < labelsTested.size(); i++) {
            if (i != 0) {
                res.append(", ");
            }
            String binLabel = labelsTested.get(i);
            res.append(binLabel).append("(")
                .append(fullLabel.replace(binLabel, "")).append(")");
        }
        return res.toString();
    }

    /**
     * Single assign
     */
    protected void checkPositionAndSpecimen() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            @Override
            public void run() {
                try {
                    appendLog("----"); //$NON-NLS-1$
                    String positionString = newCabinetPositionText.getText();
                    if (firstParent == null) {
                        // resultShownValue.setValue(Boolean.FALSE);
                        displayPositions(false);
                        return;
                    }
                    appendLog(Messages.getString(
                        "Cabinet.activitylog.checkingPosition", positionString)); //$NON-NLS-1$
                    specimen.setSpecimenPositionFromString(positionString,
                        firstParent);
                    if (specimen.isPositionFree(firstParent)) {
                        specimen.setParent(firstParent);
                        displayPositions(true);
                        // resultShownValue.setValue(Boolean.TRUE);
                        cancelConfirmWidget.setFocus();
                    } else {
                        BiobankPlugin.openError("Position not free", Messages
                            .getString(
                                "Cabinet.checkStatus.error", positionString, //$NON-NLS-1$
                                firstParent.getLabel()));
                        appendLog(Messages.getString(
                            "Cabinet.activitylog.checkPosition.error", //$NON-NLS-1$
                            positionString, firstParent.getLabel()));
                        focusControlInError(newCabinetPositionText);
                        return;
                    }
                    setDirty(true);
                } catch (RemoteConnectFailureException exp) {
                    BiobankPlugin.openRemoteConnectErrorMessage(exp);
                } catch (BiobankCheckException bce) {
                    BiobankPlugin.openError(
                        "Error while checking position", bce); //$NON-NLS-1$
                    appendLog("ERROR: " + bce.getMessage()); //$NON-NLS-1$
                    // resultShownValue.setValue(Boolean.FALSE);
                    focusControlInError(inventoryIdText);
                } catch (Exception e) {
                    BiobankPlugin.openError("Error while checking position", e); //$NON-NLS-1$
                    focusControlInError(newCabinetPositionText);
                }
            }
        });
    }

    /**
     * single assign
     */
    private void displayPositions(boolean show) {
        // widgetCreator.showWidget(cabinetWidget, show);
        // widgetCreator.showWidget(cabinetLabel, show);
        // widgetCreator.showWidget(drawerWidget, show);
        // widgetCreator.showWidget(drawerLabel, show);
        // if (show) {
        // cabinetWidget.setContainerType(cabinet.getContainerType());
        // cabinetWidget.setSelection(drawer.getPositionAsRowCol());
        //            cabinetLabel.setText("Cabinet " + cabinet.getLabel()); //$NON-NLS-1$
        // drawerWidget.setContainer(drawer);
        // drawerWidget.setSelection(bin.getPositionAsRowCol());
        //            drawerLabel.setText("Drawer " + drawer.getLabel()); //$NON-NLS-1$
        // }
        // page.layout(true, true);
        // book.reflow(true);
        // // FIXME this is working to display the right length of horizontal
        // // scroll bar when the drawer is very large, but doesn't seems a
        // pretty
        // // way to do it...
        // containersScroll.setMinSize(clientInsideGridScroll.computeSize(
        // SWT.DEFAULT, SWT.DEFAULT));
    }

    /**
     * Multiple assign: container visualisation
     */
    private void createContainersVisualisationSection() {
        visualisationScrollComposite = new ScrolledComposite(page, SWT.H_SCROLL);
        visualisationScrollComposite.setExpandHorizontal(true);
        visualisationScrollComposite.setExpandVertical(true);
        visualisationScrollComposite.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        visualisationScrollComposite.setLayoutData(scrollData);
        visualisationMainComposite = toolkit
            .createComposite(visualisationScrollComposite);
        GridLayout layout = getNeutralGridLayout();
        layout.numColumns = 3;
        visualisationMainComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.FILL;
        gd.verticalAlignment = SWT.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.grabExcessVerticalSpace = true;
        visualisationMainComposite.setLayoutData(gd);
        toolkit.paintBordersFor(visualisationMainComposite);

        visualisationScrollComposite.setContent(visualisationMainComposite);

        widgetCreator.showWidget(visualisationMainComposite, false);
    }

    private void createMultipleVisualisation() {
        Composite freezerComposite = toolkit
            .createComposite(visualisationMainComposite);
        freezerComposite.setLayout(getNeutralGridLayout());
        GridData gdFreezer = new GridData();
        gdFreezer.horizontalSpan = 3;
        gdFreezer.horizontalAlignment = SWT.RIGHT;
        freezerComposite.setLayoutData(gdFreezer);
        //        freezerLabel = toolkit.createLabel(freezerComposite, "Freezer"); //$NON-NLS-1$
        // freezerLabel.setLayoutData(new GridData());
        // freezerWidget = new ContainerDisplayWidget(freezerComposite);
        // freezerWidget.initDisplayFromType(true);
        // toolkit.adapt(freezerWidget);
        // freezerWidget.setDisplaySize(ScanPalletDisplay.PALLET_WIDTH, 100);

        Composite hotelComposite = toolkit
            .createComposite(visualisationMainComposite);
        hotelComposite.setLayout(getNeutralGridLayout());
        hotelComposite.setLayoutData(new GridData());
        //        hotelLabel = toolkit.createLabel(hotelComposite, "Hotel"); //$NON-NLS-1$
        // hotelWidget = new ContainerDisplayWidget(hotelComposite);
        // hotelWidget.initDisplayFromType(true);
        // toolkit.adapt(hotelWidget);
        // hotelWidget.setDisplaySize(100,
        // ScanPalletDisplay.PALLET_HEIGHT_AND_LEGEND);

        Composite palletComposite = toolkit
            .createComposite(visualisationMainComposite);
        palletComposite.setLayout(getNeutralGridLayout());
        palletComposite.setLayoutData(new GridData());
        //        palletLabel = toolkit.createLabel(palletComposite, "Pallet"); //$NON-NLS-1$
        parentContainerWidget = new ScanPalletWidget(palletComposite,
            UICellStatus.DEFAULT_PALLET_SCAN_ASSIGN_STATUS_LIST);
        toolkit.adapt(parentContainerWidget);
        parentContainerWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                // manageDoubleClick(e);
            }
        });
        // showOnlyPallet(true);

        visualisationScrollComposite.setMinSize(visualisationMainComposite
            .computeSize(SWT.DEFAULT, SWT.DEFAULT));
        createScanTubeAloneButton(visualisationMainComposite);
    }

    private void createSingleVisualisation() {
        thirdParentLabel = toolkit.createLabel(visualisationMainComposite,
            "Cabinet"); //$NON-NLS-1$
        secondParentLabel = toolkit.createLabel(visualisationMainComposite,
            "Drawer"); //$NON-NLS-1$

        ContainerTypeWrapper cabinetType = null;
        ContainerTypeWrapper drawerType = null;
        // if (cabinetContainerTypes.size() > 0) {
        // cabinetType = cabinetContainerTypes.get(0);
        // List<ContainerTypeWrapper> children = cabinetType
        // .getChildContainerTypeCollection();
        // if (children.size() > 0) {
        // drawerType = children.get(0);
        // }
        // }
        thirdParentWidget = new ContainerDisplayWidget(
            visualisationMainComposite);
        thirdParentWidget.setContainerType(cabinetType, true);
        toolkit.adapt(thirdParentWidget);
        GridData gdDrawer = new GridData();
        gdDrawer.verticalAlignment = SWT.TOP;
        thirdParentWidget.setLayoutData(gdDrawer);

        secondParentWidget = new ContainerDisplayWidget(
            visualisationMainComposite);
        secondParentWidget.setContainerType(drawerType, true);
        toolkit.adapt(secondParentWidget);

        visualisationScrollComposite.setMinSize(visualisationMainComposite
            .computeSize(SWT.DEFAULT, SWT.DEFAULT));
        displayPositions(false);
    }

    private GridLayout getNeutralGridLayout() {
        GridLayout layout = new GridLayout(1, false);
        layout.horizontalSpacing = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        return layout;
    }

    private void createPalletTypesViewer(
        @SuppressWarnings("unused") Composite parent) {
        // ComboViewer palletTypesViewer = createComboViewer(
        // parent,
        //            Messages.getString("ScanAssign.palletType.label"), //$NON-NLS-1$
        // null, null,
        // Messages.getString("ScanAssign.palletType.validationMsg"),
        // new ComboSelectionUpdate() {
        // @Override
        // public void doSelection(Object selectedObject) {
        // if (!modificationMode) {
        // ContainerTypeWrapper oldContainerType =
        // currentPalletWrapper
        // .getContainerType();
        // currentPalletWrapper
        // .setContainerType((ContainerTypeWrapper) selectedObject);
        // if (oldContainerType != null) {
        // validateValues();
        // }
        // palletTypesViewer.getCombo().setFocus();
        // }
        // }
        //            }); //$NON-NLS-1$
        // if (palletContainerTypes.size() == 1) {
        // currentPalletWrapper.setContainerType(palletContainerTypes.get(0));
        // palletTypesViewer.setSelection(new StructuredSelection(
        // palletContainerTypes.get(0)));
        // }
    }

    @Override
    protected void disableFields() {
        // TODO Auto-generated method stub

    }

    @Override
    protected boolean fieldsValid() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected void saveForm() throws Exception {
        if (singleMode)
            saveSingleSpecimen();
        else
            saveMultipleSpecimens();
    }

    private void saveMultipleSpecimens() {
        // TODO Auto-generated method stub

    }

    private void saveSingleSpecimen() {
        // TODO Auto-generated method stub

    }

    @Override
    protected String getOkMessage() {
        return "Assign position to specimens";
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected ProcessData getProcessData() {
        // FIXME
        return new AssignProcessData(null);
    }

    protected void focusControlInError(final Control control) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                control.setFocus();
            }
        });
    }

    @Override
    public void reset() throws Exception {
        specimen.reset(); // reset internal values
        // resetParentContainers();
        // resultShownValue.setValue(Boolean.FALSE);
        // the 2 following lines are needed. The validator won't update if don't
        // do that (why ?)
        inventoryIdText.setText("**"); //$NON-NLS-1$ 
        inventoryIdText.setText(""); //$NON-NLS-1$
        oldCabinetPositionText.setText("");
        oldCabinetPositionCheckText.setText(""); //$NON-NLS-1$
        newCabinetPositionText.setText(""); //$NON-NLS-1$
        displayOldCabinetFields(false);
        setDirty(false);
        setFocus();
    }
}
