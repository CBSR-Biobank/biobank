package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.scanprocess.SpecimenHierarchy;
import edu.ualberta.med.biobank.common.scanprocess.data.LinkProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.LinkFormPatientManagement.PatientTextCallback;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.UICellStatus;
import edu.ualberta.med.biobank.validators.CabinetInventoryIDValidator;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class GenericLinkEntryForm extends AbstractPalletSpecimenAdminForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.GenericLinkEntryForm"; //$NON-NLS-1$

    private static BiobankLogger logger = BiobankLogger
        .getLogger(GenericLinkEntryForm.class.getName());

    private LinkFormPatientManagement linkFormPatientManagement;

    private Composite singleLinkComposite;

    private Composite multipleLinkComposite;

    private List<AliquotedSpecimenSelectionWidget> specimenTypesWidgets;

    private ScanPalletWidget palletWidget;

    private BiobankText inventoryIdText;

    private SpecimenWrapper singleSpecimen;

    private CabinetInventoryIDValidator inventoryIDValidator;

    private AliquotedSpecimenSelectionWidget singleTypesWidget;

    @Override
    protected void init() throws Exception {
        super.init();
        setPartName("Linking specimens");
        linkFormPatientManagement = new LinkFormPatientManagement(
            widgetCreator, this);
        singleSpecimen = new SpecimenWrapper(appService);
    }

    @Override
    protected String getActivityTitle() {
        return "Generic Link";
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected void saveForm() throws Exception {
        BiobankPlugin.openInformation("TODO", "Not yet implemented");
    }

    @Override
    protected String getOkMessage() {
        return "Link specimens to their source specimens";
    }

    @Override
    public String getNextOpenedFormID() {
        return ID;
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Linking specimen"); //$NON-NLS-1$
        GridLayout layout = new GridLayout(2, false);
        page.setLayout(layout);

        createLeftSection();

        createPalletSection();
        palletWidget.setVisible(false);

        new CancelConfirmWidget(page, this, true);
        setCanLaunchScan(true);
    }

    private void createLeftSection() {
        Composite leftComposite = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(1, false);
        leftComposite.setLayout(layout);
        toolkit.paintBordersFor(leftComposite);
        GridData gd = new GridData();
        gd.widthHint = 600;
        gd.verticalAlignment = SWT.TOP;
        leftComposite.setLayoutData(gd);

        Composite commonFieldsComposite = toolkit
            .createComposite(leftComposite);
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        commonFieldsComposite.setLayout(layout);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commonFieldsComposite.setLayoutData(gd);
        toolkit.paintBordersFor(commonFieldsComposite);

        // Patient number + visits list
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

        linkFormPatientManagement
            .createCollectionEventWidgets(commonFieldsComposite);

        createLinkingSection(leftComposite);
    }

    private void createLinkingSection(Composite leftComposite) {
        Composite linkComposite = toolkit.createComposite(leftComposite);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        linkComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        linkComposite.setLayoutData(gd);
        toolkit.paintBordersFor(linkComposite);

        // radio button to choose single or multiple
        final Button radioSingle = toolkit.createButton(linkComposite,
            "Single", SWT.RADIO);
        final Button radioMultiple = toolkit.createButton(linkComposite,
            "Multiple", SWT.RADIO);

        // stackLayout
        final Composite selectionComp = toolkit.createComposite(linkComposite);
        final StackLayout selectionStackLayout = new StackLayout();
        gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        selectionComp.setLayoutData(gd);
        selectionComp.setLayout(selectionStackLayout);

        createSingleLinkComposite(selectionComp);
        createMultipleLink(selectionComp);
        radioSingle.setSelection(true);
        selectionStackLayout.topControl = singleLinkComposite;

        radioSingle.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioSingle.getSelection()) {
                    selectionStackLayout.topControl = singleLinkComposite;
                    page.layout(true, true);
                    palletWidget.setVisible(false);
                }
            }
        });
        radioMultiple.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioMultiple.getSelection()) {
                    selectionStackLayout.topControl = multipleLinkComposite;
                    page.layout(true, true);
                    palletWidget.setVisible(true);
                }
            }
        });
    }

    private void createMultipleLink(Composite parent) {
        multipleLinkComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        multipleLinkComposite.setLayout(layout);
        toolkit.paintBordersFor(multipleLinkComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleLinkComposite.setLayoutData(gd);

        createPlateToScanField(multipleLinkComposite);
        createScanButton(multipleLinkComposite);

        toolkit.createLabel(multipleLinkComposite, ""); //$NON-NLS-1$
        toolkit.createLabel(multipleLinkComposite,
            Messages.getString("ScanLink.source.column.title")); //$NON-NLS-1$
        toolkit.createLabel(multipleLinkComposite,
            Messages.getString("ScanLink.result.column.title")); //$NON-NLS-1$
        toolkit.createLabel(multipleLinkComposite, ""); //$NON-NLS-1$

        specimenTypesWidgets = new ArrayList<AliquotedSpecimenSelectionWidget>();
        AliquotedSpecimenSelectionWidget precedent = null;
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            final AliquotedSpecimenSelectionWidget typeWidget = new AliquotedSpecimenSelectionWidget(
                multipleLinkComposite,
                ContainerLabelingSchemeWrapper.SBS_ROW_LABELLING_PATTERN
                    .charAt(i), widgetCreator, true);
            final int indexRow = i;
            typeWidget
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        updateRowType(typeWidget, indexRow);
                        if (palletWidget.isEverythingTyped()) {
                            setDirty(true);
                        }
                    }

                });
            typeWidget.addBindings();
            specimenTypesWidgets.add(typeWidget);
            if (precedent != null) {
                precedent.setNextWidget(typeWidget);
            }
            precedent = typeWidget;
            typeWidget.setEnabled(true);
        }
    }

    @Override
    protected void createScanButton(Composite parent) {
        super.createScanButton(parent);
        scanButton.setEnabled(true);
    }

    /**
     * update types of specimens of one given row
     */
    @SuppressWarnings("unchecked")
    private void updateRowType(AliquotedSpecimenSelectionWidget typeWidget,
        int indexRow) {
        if (typeWidget.needToSave()) {
            SpecimenHierarchy selection = typeWidget.getSelection();
            if (selection != null) {
                Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) palletWidget
                    .getCells();
                if (cells != null) {
                    for (RowColPos rcp : cells.keySet()) {
                        if (rcp.row == indexRow) {
                            PalletCell cell = cells.get(rcp);
                            if (PalletCell.hasValue(cell)) {
                                setTypeToCell(cell, selection);
                            }
                        }
                    }
                    palletWidget.redraw();
                }
            }
        }
    }

    private void setTypeToCell(PalletCell cell, SpecimenHierarchy selection) {
        cell.setSourceSpecimen(selection.getParentSpecimen());
        cell.setSpecimenType(selection.getAliquotedSpecimenType());
        cell.setStatus(UICellStatus.TYPE);
    }

    private void createSingleLinkComposite(Composite parent) {
        singleLinkComposite = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        singleLinkComposite.setLayout(layout);
        toolkit.paintBordersFor(singleLinkComposite);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        singleLinkComposite.setLayoutData(gd);

        // inventoryID
        inventoryIDValidator = new CabinetInventoryIDValidator();
        inventoryIdText = (BiobankText) createBoundWidgetWithLabel(
            singleLinkComposite, BiobankText.class, SWT.NONE,
            Messages.getString("Cabinet.inventoryId.label"), new String[0], //$NON-NLS-1$
            singleSpecimen, "inventoryId", //$NON-NLS-1$
            inventoryIDValidator);
        inventoryIdText.addKeyListener(textFieldKeyListener);
        inventoryIdText.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                // if (inventoryIdModified && !radioNew.getSelection()) {
                // // Move Mode only
                // try {
                // retrieveAliquotDataForMoving();
                // } catch (Exception ex) {
                //                        BiobankPlugin.openError("Move - aliquot error", ex); //$NON-NLS-1$
                // focusControlInError(inventoryIdText);
                // }
                // }
                // inventoryIdModified = false;
            }
        });
        // inventoryIdText.addModifyListener(new ModifyListener() {
        // @Override
        // public void modifyText(ModifyEvent e) {
        // inventoryIdModified = true;
        // positionTextModified = true;
        // resultShownValue.setValue(Boolean.FALSE);
        // displayPositions(false);
        // }
        // });

        singleTypesWidget = new AliquotedSpecimenSelectionWidget(
            singleLinkComposite, null, widgetCreator, false);
        singleTypesWidget.addBindings();

        widgetCreator.createLabel(singleLinkComposite,
            "Go to assign after linking");
        toolkit.createButton(singleLinkComposite, "", SWT.CHECK);
    }

    /**
     * Get types only defined in the patient's study. Then set these types to
     * the types combos
     */
    private void setTypeCombos() {
        List<SpecimenTypeWrapper> studiesAliquotedTypes = linkFormPatientManagement
            .getStudyAliquotedTypes(null, null);
        List<SpecimenWrapper> availableSourceSpecimens = linkFormPatientManagement
            .getSpecimenInCollectionEvent();
        for (int row = 0; row < specimenTypesWidgets.size(); row++) {
            AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                .get(row);
            // if (isFirstSuccessfulScan()) {
            widget.setSourceSpecimens(availableSourceSpecimens);
            widget.setResultTypes(studiesAliquotedTypes);
            // }
        }
        singleTypesWidget.setSourceSpecimens(availableSourceSpecimens);
        singleTypesWidget.setResultTypes(studiesAliquotedTypes);
    }

    private void setCombosListsNumber(Map<Integer, Integer> typesRows) {
        for (int row = 0; row < specimenTypesWidgets.size(); row++) {
            AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                .get(row);
            Integer number = typesRows.get(row);
            if (number != null)
                widget.setNumber(number);
        }
    }

    /**
     * Pallet visualisation
     */
    private void createPalletSection() {
        ScrolledComposite containersScroll = new ScrolledComposite(page,
            SWT.H_SCROLL);
        containersScroll.setExpandHorizontal(true);
        containersScroll.setExpandVertical(true);
        containersScroll.setLayout(new FillLayout());
        GridData scrollData = new GridData();
        scrollData.horizontalAlignment = SWT.FILL;
        scrollData.grabExcessHorizontalSpace = true;
        containersScroll.setLayoutData(scrollData);
        Composite client = toolkit.createComposite(containersScroll);
        GridLayout layout = new GridLayout(2, false);
        client.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        client.setLayoutData(gd);
        containersScroll.setContent(client);

        palletWidget = new ScanPalletWidget(client,
            UICellStatus.DEFAULT_PALLET_SCAN_LINK_STATUS_LIST);
        palletWidget.setVisible(true);
        toolkit.adapt(palletWidget);
        palletWidget.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, true,
            false));

        palletWidget.getMultiSelectionManager().addMultiSelectionListener(
            new MultiSelectionListener() {
                @Override
                public void selectionChanged(MultiSelectionEvent mse) {
                    // customSelectionWidget.setNumber(mse.selections);
                }
            });
        palletWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                scanTubeAlone(e);
            }
        });
        // spw.loadProfile(profilesCombo.getCombo().getText());

        createScanTubeAloneButton(client);

        containersScroll.setMinSize(client
            .computeSize(SWT.DEFAULT, SWT.DEFAULT));

    }

    @Override
    protected void disableFields() {

    }

    @Override
    protected boolean fieldsValid() {
        return true;
    }

    @Override
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        UICellStatus status = processCellStatus(cell, true);
        boolean ok = isScanValid() && (status != UICellStatus.ERROR);
        setScanValid(ok);
        // typesSelectionPerRowComposite.setEnabled(ok);
        palletWidget.redraw();
        form.layout();
    }

    /**
     * Process the cell: apply a status and set correct information
     * 
     * @throws Exception
     */
    private UICellStatus processCellStatus(PalletCell cell,
        boolean independantProcess) throws Exception {
        if (cell == null) {
            return UICellStatus.EMPTY;
        } else {
            String value = cell.getValue();
            if (value != null) {
                SpecimenWrapper foundAliquot = SpecimenWrapper.getSpecimen(
                    appService, value, SessionManager.getUser());
                if (foundAliquot != null) {
                    cell.setStatus(UICellStatus.ERROR);
                    cell.setInformation(Messages
                        .getString("ScanLink.scanStatus.aliquot.alreadyExists")); //$NON-NLS-1$
                    String palletPosition = ContainerLabelingSchemeWrapper
                        .rowColToSbs(new RowColPos(cell.getRow(), cell.getCol()));
                    appendLogNLS("ScanLink.activitylog.aliquot.existsError",
                        palletPosition, value, foundAliquot
                            .getCollectionEvent().getVisitNumber(),
                        foundAliquot.getCollectionEvent().getPatient()
                            .getPnumber(), foundAliquot.getCurrentCenter()
                            .getNameShort());
                } else {
                    cell.setStatus(UICellStatus.NO_TYPE);
                    if (independantProcess) {
                        AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                            .get(cell.getRow());
                        widget.increaseNumber();
                    }
                    // ModelWrapper<?>[] selection = preSelections.get(cell
                    // .getRow());
                    // if (selection != null)
                    // setTypeToCell(cell, selection);
                }
            } else {
                cell.setStatus(UICellStatus.EMPTY);
            }
            return cell.getStatus();
        }
    }

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    @Override
    protected void processScanResult(IProgressMonitor monitor) throws Exception {
        // processScanResult = false;
        boolean everythingOk = true;
        Map<RowColPos, PalletCell> cells = getCells();
        if (cells != null) {
            final Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
            for (RowColPos rcp : cells.keySet()) {
                monitor.subTask(Messages.getString(
                    "ScanLink.scan.monitor.position", //$NON-NLS-1$
                    ContainerLabelingSchemeWrapper.rowColToSbs(rcp)));
                Integer typesRowsCount = typesRows.get(rcp.row);
                if (typesRowsCount == null) {
                    typesRowsCount = 0;
                    specimenTypesWidgets.get(rcp.row).resetValues(
                        !isRescanMode(), true, true);
                }
                PalletCell cell = null;
                cell = cells.get(rcp);
                if (!isRescanMode()
                    || (cell != null && cell.getStatus() != UICellStatus.TYPE && cell
                        .getStatus() != UICellStatus.NO_TYPE)) {
                    // processCellStatus(cell, false);
                }
                everythingOk = cell.getStatus() != UICellStatus.ERROR
                    && everythingOk;
                if (PalletCell.hasValue(cell)) {
                    typesRowsCount++;
                    typesRows.put(rcp.row, typesRowsCount);
                }
            }
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setCombosListsNumber(typesRows);
                }
            });
            // processScanResult = everythingOk;
        }
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        return PalletCell.getRandomScanLink();
    }

    @Override
    protected void afterScanAndProcess(Integer rowOnly) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                // typesSelectionPerRowComposite.setEnabled(processScanResult);
                for (AliquotedSpecimenSelectionWidget typeWidget : specimenTypesWidgets) {
                    if (typeWidget.canFocus()) {
                        typeWidget.setFocus();
                        break;
                    }
                }
                // Show result in grid
                palletWidget.setCells(getCells());
                setRescanMode();
                // not needed on windows. This was if the textfield number
                // go after 9, needed to resize on linux : need to check that
                // again form.layout(true, true);
            }
        });
        setScanValid(true);
    }

    @Override
    protected ProcessData getProcessData() {
        return new LinkProcessData();
    }

}
