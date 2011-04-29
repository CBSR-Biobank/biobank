package edu.ualberta.med.biobank.forms.linkassign;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.widgets.AliquotedSpecimenSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class LinkWithScannerManagement extends AbstractPalletSpecimenManagement {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(LinkWithScannerManagement.class.getName());

    private LinkFormPatientManagement linkFormPatientManagement;

    private ScanPalletWidget palletWidget;

    // choose selection mode - deactivated by default
    private Composite radioComponents;

    // select per row
    private Composite typesSelectionPerRowComposite;
    private List<AliquotedSpecimenSelectionWidget> specimenTypesWidgets;

    // custom selection with mouse
    private Composite typesSelectionCustomComposite;
    private AliquotedSpecimenSelectionWidget customSelectionWidget;

    // should be set to true when all scanned specimens have a type set
    private IObservableValue typesFilledValue = new WritableValue(Boolean.TRUE,
        Boolean.class);

    // button to choose a fake scan - debug only
    private Button fakeScanRandom;

    private Composite fieldsComposite;

    private boolean isFakeScanRandom;

    private ScrolledComposite containersScroll;

    private List<SpecimenHierarchy> preSelections;

    private CenterWrapper<?> currentSelectedCenter;

    private Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
    private List<SpecimenTypeWrapper> palletSpecimenTypes;

    private Composite multipleOptionsFields;

    private GenericLink2EntryForm form;

    protected LinkWithScannerManagement(GenericLink2EntryForm form,
        LinkFormPatientManagement linkFormPatientManagement) throws Exception {
        this.form = form;
        this.linkFormPatientManagement = linkFormPatientManagement;
        setCanLaunchScan(true);
    }

    @Override
    protected void createFakeOptions(Composite fieldsComposite) {
        GridData gd;
        Composite comp = form.getToolkit().createComposite(fieldsComposite);
        comp.setLayout(new GridLayout());
        gd = new GridData();
        gd.horizontalSpan = 3;
        gd.widthHint = 400;
        comp.setLayoutData(gd);
        fakeScanRandom = form.getToolkit().createButton(comp,
            "Get random scan values", //$NON-NLS-1$
            SWT.RADIO);
        fakeScanRandom.setSelection(true);
        form.getToolkit().createButton(comp,
            "Get random and already linked specimens", SWT.RADIO); //$NON-NLS-1$
    }

    @Override
    protected void afterScanAndProcess(final Integer rowOnly) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                typesSelectionPerRowComposite
                    .setEnabled(currentScanState != UICellStatus.ERROR);
                if (typesRows.size() > 0)
                    Display.getDefault().asyncExec(new Runnable() {
                        @Override
                        public void run() {
                            if (rowOnly == null)
                                setCombosLists(typesRows);
                            else {
                                setCountOnSpecimenWidget(typesRows, rowOnly);
                            }
                        }
                    });
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
    }

    @Override
    protected void beforeScanThreadStart() {
        beforeScans(true);
    }

    @Override
    protected void beforeScanTubeAlone() {
        beforeScans(false);
    }

    private void beforeScans(boolean resetTypeRows) {
        isFakeScanRandom = fakeScanRandom != null
            && fakeScanRandom.getSelection();
        currentSelectedCenter = SessionManager.getUser()
            .getCurrentWorkingCenter();
        preSelections = new ArrayList<SpecimenHierarchy>();
        for (AliquotedSpecimenSelectionWidget stw : specimenTypesWidgets) {
            preSelections.add(stw.getSelection());
        }
        if (resetTypeRows)
            typesRows.clear();
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() throws Exception {
        if (isFakeScanRandom) {
            return PalletCell.getRandomScanLink();
        }
        try {
            return PalletCell.getRandomScanLinkWithSpecimensAlreadyLinked(
                appService, currentSelectedCenter.getId());
        } catch (Exception ex) {
            BiobankPlugin.openAsyncError("Fake Scan problem", ex); //$NON-NLS-1$
        }
        return null;
    }

    @Override
    protected ProcessData getProcessData() {
        return new LinkProcessData();
    }

    /**
     * Post process of only one cell after server call has been made
     */
    @Override
    protected void processCellResult(final RowColPos rcp, PalletCell cell) {
        Integer typesRowsCount = typesRows.get(rcp.row);
        if (typesRowsCount == null) {
            typesRowsCount = 0;
            specimenTypesWidgets.get(rcp.row).resetValues(!isRescanMode(),
                true, true);
        }
        SpecimenHierarchy selection = preSelections.get(cell.getRow());
        if (selection != null)
            setHierarchyToCell(cell, selection);
        if (PalletCell.hasValue(cell)) {
            typesRowsCount++;
            typesRows.put(rcp.row, typesRowsCount);
        }
    }

    private AliquotedSpecimenSelectionWidget setCountOnSpecimenWidget(
        Map<Integer, Integer> typesRows, int row) {
        AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets.get(row);
        Integer number = typesRows.get(row);
        if (number == null) {
            number = 0;
            widget.deselectAll();
        }
        widget.setNumber(number);
        return widget;
    }

    /**
     * If the current center is a site, and if this site defines containers of
     * 8*12 size, then get the specimen types these containers can contain
     */
    private void initPalletSpecimenTypes() throws ApplicationException {
        palletSpecimenTypes = null;
        if (SessionManager.getUser().getCurrentWorkingSite() != null) {
            List<SpecimenTypeWrapper> res = SpecimenTypeWrapper
                .getSpecimenTypeForPallet96(appService, SessionManager
                    .getUser().getCurrentWorkingSite());
            if (res.size() != 0)
                palletSpecimenTypes = res;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void saveForm() throws Exception {
        Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) palletWidget
            .getCells();
        StringBuffer sb = new StringBuffer("ALIQUOTED SPECIMENS:\n"); //$NON-NLS-1$
        int nber = 0;
        ActivityStatusWrapper activeStatus = ActivityStatusWrapper
            .getActiveActivityStatus(appService);
        OriginInfoWrapper originInfo = new OriginInfoWrapper(
            SessionManager.getAppService());
        originInfo
            .setCenter(SessionManager.getUser().getCurrentWorkingCenter());
        originInfo.persist();
        // use a set because do not want to add the same parent twice
        Set<SpecimenWrapper> modifiedSources = new HashSet<SpecimenWrapper>();
        for (PalletCell cell : cells.values()) {
            if (PalletCell.hasValue(cell)
                && cell.getStatus() == UICellStatus.TYPE) {
                SpecimenWrapper sourceSpecimen = cell.getSourceSpecimen();
                SpecimenWrapper aliquotedSpecimen = cell.getSpecimen();
                aliquotedSpecimen.setInventoryId(cell.getValue());
                aliquotedSpecimen.setCreatedAt(new Date());
                aliquotedSpecimen.setActivityStatus(activeStatus);
                aliquotedSpecimen.setCurrentCenter(currentSelectedCenter);
                aliquotedSpecimen.setOriginInfo(originInfo);
                aliquotedSpecimen.setParentSpecimen(sourceSpecimen);
                aliquotedSpecimen.setCollectionEvent(sourceSpecimen
                    .getCollectionEvent());
                aliquotedSpecimen.setQuantityFromType();
                sourceSpecimen.addToChildSpecimenCollection(Arrays
                    .asList(aliquotedSpecimen));
                modifiedSources.add(sourceSpecimen);
                // LINKED\: {0} - Type: {1} - Patient\: {2} - Visit\: {3} -
                // Center: {4} \n
                sb.append(Messages.getString(
                    "ScanLink.activitylog.specimen.linked", //$NON-NLS-1$
                    cell.getValue(), cell.getType().getName(), sourceSpecimen
                        .getSpecimenType().getNameShort(), sourceSpecimen
                        .getInventoryId(), sourceSpecimen.getCollectionEvent()
                        .getPatient().getPnumber(), sourceSpecimen
                        .getCollectionEvent().getVisitNumber(),
                    currentSelectedCenter.getNameShort()));
                nber++;
            }
        }
        // persist of parent will automatically persist children
        ModelWrapper.persistBatch(modifiedSources);
        // display logs only if persist succeeds.
        appendLog(sb.toString());

        // SCAN-LINK\: {0} specimens linked to patient {1} on center {2}
        appendLog(Messages.getString("ScanLink.activitylog.save.summary", nber, //$NON-NLS-1$
            linkFormPatientManagement.getCurrentPatient().getPnumber(),
            currentSelectedCenter.getNameShort()));
        setFinished(false);
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
                    for (Entry<RowColPos, PalletCell> entry : cells.entrySet()) {
                        RowColPos rcp = entry.getKey();
                        if (rcp.row == indexRow) {
                            PalletCell cell = entry.getValue();
                            if (PalletCell.hasValue(cell)) {
                                setHierarchyToCell(cell, selection);
                            }
                        }
                    }
                    palletWidget.redraw();
                }
            }
        }
    }

    /**
     * Multiple linking: apply a source and type to a specific cell.
     */
    private void setHierarchyToCell(PalletCell cell, SpecimenHierarchy selection) {
        cell.setSourceSpecimen(selection.getParentSpecimen());
        cell.setSpecimenType(selection.getAliquotedSpecimenType());
        cell.setStatus(UICellStatus.TYPE);
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        setDirty(false);
        fieldsComposite.setEnabled(true);
        setScanValid(true);
        reset(true);
    }

    public void reset(boolean resetAll) {
        linkFormPatientManagement.reset(resetAll);
        cancelConfirmWidget.reset();
        removeRescanMode();
        setScanHasBeenLauched(false);
        if (resetAll) {
            resetPlateToScan();
            palletWidget.setCells(null);
            for (AliquotedSpecimenSelectionWidget stw : specimenTypesWidgets) {
                stw.resetValues(true, true);
            }
        }
        setFocus();
    }

    @Override
    protected String getActivityTitle() {
        return "Scan link activity"; //$NON-NLS-1$
    }

    @Override
    protected void disableFields() {
        fieldsComposite.setEnabled(false);
    }

    @Override
    public BiobankLogger getErrorLogger() {
        return logger;
    }

    @Override
    protected boolean fieldsValid() {
        return isPlateValid() && linkFormPatientManagement.fieldsValid();
    }

    @Override
    public boolean onClose() {
        linkFormPatientManagement.onClose();
        return super.onClose();
    }

    protected void createFields(Composite parent) {
        multipleOptionsFields = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 10;
        layout.marginWidth = 0;
        multipleOptionsFields.setLayout(layout);
        toolkit.paintBordersFor(multipleOptionsFields);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        multipleOptionsFields.setLayoutData(gd);
        // plate selection
        createPlateToScanField(multipleOptionsFields);
        createScanButton(parent);

        // source/type hierarchy widgets
        typesSelectionPerRowComposite = toolkit.createComposite(parent);
        layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        typesSelectionPerRowComposite.setLayout(layout);
        toolkit.paintBordersFor(typesSelectionPerRowComposite);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        typesSelectionPerRowComposite.setLayoutData(gd);

        toolkit.createLabel(typesSelectionPerRowComposite, ""); //$NON-NLS-1$
        toolkit.createLabel(typesSelectionPerRowComposite,
            Messages.getString("GenericLinkEntryForm.source.column.title")); //$NON-NLS-1$
        toolkit.createLabel(typesSelectionPerRowComposite,
            Messages.getString("GenericLinkEntryForm.result.column.title")); //$NON-NLS-1$
        toolkit.createLabel(typesSelectionPerRowComposite, ""); //$NON-NLS-1$

        specimenTypesWidgets = new ArrayList<AliquotedSpecimenSelectionWidget>();
        AliquotedSpecimenSelectionWidget precedent = null;
        for (int i = 0; i < ScanCell.ROW_MAX; i++) {
            final AliquotedSpecimenSelectionWidget typeWidget = new AliquotedSpecimenSelectionWidget(
                typesSelectionPerRowComposite,
                ContainerLabelingSchemeWrapper.SBS_ROW_LABELLING_PATTERN
                    .charAt(i), widgetCreator, true);
            final int indexRow = i;
            typeWidget
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        if (typeWidget.needToSave()) {
                            SpecimenHierarchy selection = typeWidget
                                .getSelection();
                            if (selection != null) {
                                @SuppressWarnings("unchecked")
                                Map<RowColPos, PalletCell> cells = (Map<RowColPos, PalletCell>) palletWidget
                                    .getCells();
                                if (cells != null) {
                                    for (RowColPos rcp : cells.keySet()) {
                                        if (rcp.row == indexRow) {
                                            PalletCell cell = cells.get(rcp);
                                            if (PalletCell.hasValue(cell)) {
                                                setHierarchyToCell(cell,
                                                    selection);
                                            }
                                        }
                                    }
                                    palletWidget.redraw();
                                }
                            }
                        }
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
        AliquotedSpecimenSelectionWidget lastWidget = specimenTypesWidgets
            .get(specimenTypesWidgets.size() - 1);
        lastWidget.setNextWidget(cancelConfirmWidget);

        form.addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            typesFilledValue,
            Messages.getString("ScanLink.sampleType.select.validationMsg")); //$NON-NLS-1$
    }

    protected void createContainersVisualisation(Composite parent) {
        Composite comp = toolkit.createComposite(parent);
        GridLayout layout = new GridLayout(2, false);
        comp.setLayout(layout);
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        comp.setLayoutData(gd);

        palletWidget = new ScanPalletWidget(comp,
            UICellStatus.DEFAULT_PALLET_SCAN_LINK_STATUS_LIST);
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
        // palletWidget.loadProfile(profilesCombo.getCombo().getText());

        createScanTubeAloneButton(comp);
    }

    public void patientFocusLost(
        List<SpecimenTypeWrapper> studiesAliquotedTypes,
        List<SpecimenWrapper> availableSourceSpecimens) {
        if (isFirstSuccessfulScan())
            // for multiple
            for (int row = 0; row < specimenTypesWidgets.size(); row++) {
                AliquotedSpecimenSelectionWidget widget = specimenTypesWidgets
                    .get(row);
                widget.setSourceSpecimens(availableSourceSpecimens);
                widget.setResultTypes(studiesAliquotedTypes);
            }
    }

}
