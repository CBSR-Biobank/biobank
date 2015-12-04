package edu.ualberta.med.biobank.forms.linkassign;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenSetGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement.ScanManualOption;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.CancelConfirmWidget;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.PalletDimensions;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import gov.nih.nci.system.applicationservice.ApplicationException;

public abstract class AbstractPalletSpecimenAdminForm extends AbstractSpecimenAdminForm
    implements IDecodePalletManagement, ModifyListener {

    private static final I18n i18n = I18nFactory.getI18n(AbstractPalletSpecimenAdminForm.class);

    private static Logger log = LoggerFactory.getLogger(AbstractPalletSpecimenAdminForm.class);

    protected Button scanButton;

    protected CancelConfirmWidget cancelConfirmWidget;

    @SuppressWarnings("nls")
    // TR: button label
    private static final String DECODE_PALLET_BUTTON_LABEL = i18n.tr("Decode pallet");

    private final IObservableValue canLaunchScanValue =
        new WritableValue(Boolean.TRUE, Boolean.class);

    private final IObservableValue scanHasBeenLaunchedValue =
        new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue scanValidValue =
        new WritableValue(Boolean.TRUE, Boolean.class);

    protected PalletScanManagement palletScanManagement;

    protected ComboViewer profilesCombo;

    protected boolean plateMismatchErrorReported = false;

    protected final Set<ContainerType> palletContainerTypes = new HashSet<ContainerType>();

    // global state of the pallet process
    protected UICellStatus currentScanState = UICellStatus.NOT_INITIALIZED;

    @Override
    protected void init() throws Exception {
        super.init();
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
    }

    @Override
    public void beforeProcessingThreadStart() {
    }

    /**
     * go through cells retrieved from scan, set status and update the types combos components
     * 
     * @throws Exception
     */
    @Override
    @SuppressWarnings("nls")
    public void processDecodeResult() throws Exception {
        CenterWrapper<?> currentWorkingCenter = SessionManager.getUser().getCurrentWorkingCenter();
        if (currentWorkingCenter == null) {
            throw new IllegalStateException("current working center is null");
        }

        setScanHasBeenLaunched(false, true);

        Map<RowColPos, SpecimenCell> cells = getCells();
        // conversion for server side call
        Map<RowColPos, CellInfo> serverCells = null;
        if (cells != null) {
            serverCells = AbstractPalletSpecimenAdminForm.getServerSpecimenData(cells.values());
        }
        // server side call
        ScanProcessResult res = (ScanProcessResult) SessionManager.getAppService().doAction(
            getPalletProcessAction(
                currentWorkingCenter.getId(),
                serverCells,
                Locale.getDefault()));

        // print result logs
        appendLogs(res.getLogs());

        Map<String, SpecimenBriefInfo> specimenDataMap =
            AbstractPalletSpecimenAdminForm.getSpecimenData(
                currentWorkingCenter, new HashSet<SpecimenCell>(cells.values()));

        // for each cell, convert into a client side cell
        for (Entry<RowColPos, CellInfo> entry : res.getCells().entrySet()) {
            RowColPos pos = entry.getKey();
            SpecimenCell palletCell = cells.get(entry.getKey());
            CellInfo servercell = entry.getValue();
            if (palletCell == null) {
                // can happen if missing no tube in this cell
                palletCell = new SpecimenCell(
                    pos.getRow(),
                    pos.getCol(),
                    new DecodedWell(
                        servercell.getRow(),
                        servercell.getCol(),
                        servercell.getValue()));
                cells.put(pos, palletCell);
                log.debug("processScanResult: palletCell is null: pos ({}, {})",
                    pos.getRow(), pos.getCol());
            }
            palletCell.merge(specimenDataMap.get(palletCell.getValue()), servercell);
            // additional cell specific client conversion if needed
            processCellResult(pos, palletCell);
        }

        currentScanState = UICellStatus.valueOf(res.getProcessStatus().name());
        setScanValid(getCells() != null && !getCells().isEmpty()
            && currentScanState != UICellStatus.ERROR);

        afterScanAndProcess(null);

        setScanHasBeenLaunched(true, true);
        appendLog(NLS.bind("Scan completed - {0} specimens found", cells.keySet().size()));
    }

    @SuppressWarnings("unused")
    protected void afterScanAndProcess(Integer rowToProcess) {
        // default does nothing
    }

    @Override
    public void decodeAndProcessError(String errorMsg) {
        setScanValid(false);
        if (errorMsg != null && !errorMsg.isEmpty()) {
            appendLog(errorMsg);
        }
    }

    @SuppressWarnings("nls")
    @Override
    public void postProcessDecodeTubesManually(Set<SpecimenCell> cells) throws Exception {
        CenterWrapper<?> currentWorkingCenter = SessionManager.getUser().getCurrentWorkingCenter();
        if (currentWorkingCenter == null) {
            throw new IllegalStateException("current working center is null");
        }

        Map<RowColPos, SpecimenCell> mergedCells = getCells();
        for (SpecimenCell cell : cells) {
            mergedCells.put(cell.getRowColPos(), cell);
        }

        Map<RowColPos, CellInfo> serverCells = getServerSpecimenData(mergedCells.values());

        ScanProcessResult res = (ScanProcessResult) SessionManager.getAppService().doAction(
            getPalletProcessAction(
                currentWorkingCenter.getId(),
                serverCells,
                Locale.getDefault()));

        Map<String, SpecimenBriefInfo> specimenDataMap =
            AbstractPalletSpecimenAdminForm.getSpecimenData(currentWorkingCenter, cells);

        for (SpecimenCell palletCell : cells) {
            CellInfo cellServerInfo = res.getCells().get(palletCell.getRowColPos());
            appendLog(NLS.bind("Tube {0} scanned and set to position {1}", palletCell.getValue(),
                palletScanManagement.getContainerType().getPositionString(palletCell.getRowColPos())));
            beforeScanTubeAlone();
            palletCell.merge(specimenDataMap.get(palletCell.getValue()), cellServerInfo);
            appendLogs(res.getLogs());
            processCellResult(palletCell.getRowColPos(), palletCell);
            currentScanState = currentScanState.mergeWith(palletCell.getStatus());
            setScanValid((getCells() != null)
                && !getCells().isEmpty()
                && (currentScanState != UICellStatus.ERROR));
            afterScanAndProcess(palletCell.getRow());
            setScanHasBeenLaunched(true);
        }
    }

    @Override
    public boolean canDecodeTubesManually(SpecimenCell cell) {
        return ((cell == null) || (cell.getStatus() == UICellStatus.EMPTY));
    }

    protected abstract void enableFields(boolean enable);

    @SuppressWarnings("nls")
    protected void createScanButton(Composite parent) {
        scanButton = toolkit.createButton(parent, DECODE_PALLET_BUTTON_LABEL, SWT.PUSH);
        GridData gd = new GridData();
        gd.widthHint = 100;
        scanButton.setLayoutData(gd);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                launchScanAndProcessResult();
            }
        });
        scanButton.setEnabled(false);

        addBooleanBinding(new WritableValue(Boolean.FALSE, Boolean.class),
            canLaunchScanValue,
            // TR: validation error message
            i18n.tr("Errors were detected. Cannot decode pallet yet."));
        addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            scanHasBeenLaunchedValue,
            // TR: validation error message
            i18n.tr("Decode a pallet or enter inventory IDs manually by double clicking cells on the grid"));
        addBooleanBinding(new WritableValue(Boolean.TRUE, Boolean.class),
            scanValidValue,
            // TR: validation error message
            i18n.tr("Errors found with inventory IDs"));
    }

    protected void launchScanAndProcessResult() {
        palletScanManagement.decodeAndProcessResult();
        refreshPalletDisplay();
    }

    protected abstract void refreshPalletDisplay();

    @Override
    public void modifyText(ModifyEvent e) {
        if (scanButton != null) {
            scanButton.setEnabled((Boolean) canLaunchScanValue.getValue() && fieldsValid());
        }
    }

    protected void createCancelConfirmWidget(Composite parent) {
        cancelConfirmWidget = new CancelConfirmWidget(parent, this, true);
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        if (status.getSeverity() == IStatus.OK) {
            setFormHeaderErrorMessage(getOkMessage(), IMessageProvider.NONE);
            cancelConfirmWidget.setConfirmEnabled(true);
            setConfirmEnabled(true);
            setDirty(true);
        } else {
            setFormHeaderErrorMessage(status.getMessage(), IMessageProvider.ERROR);
            cancelConfirmWidget.setConfirmEnabled(false);
            setConfirmEnabled(false);
        }
        scanButton.setEnabled((Boolean) canLaunchScanValue.getValue());
    }

    protected abstract boolean fieldsValid();

    protected void setScanValid(final boolean valid) {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanValidValue.setValue(valid);
            }
        });
    }

    protected boolean isScanValid() {
        return scanValidValue.getValue().equals(Boolean.TRUE);
    }

    protected void setScanHasBeenLaunched(boolean launched) {
        scanHasBeenLaunchedValue.setValue(launched);
    }

    protected boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected void setScanHasBeenLaunched(final boolean launched, boolean async) {
        if (async) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    setScanHasBeenLaunched(launched);
                }
            });
        } else {
            setScanHasBeenLaunched(launched);
        }
    }

    protected void setBindings(boolean isSingleMode) {
        setScanHasBeenLaunched(isSingleMode);
    }

    protected boolean needPlate() {
        return true;
    }

    protected void setCanLaunchScan(boolean canLauch) {
        canLaunchScanValue.setValue(canLauch);
    }

    protected void scanTubesManually(MouseEvent e) {
        palletScanManagement.scanTubesManually(e, ScanManualOption.NO_DUPLICATES);
    }

    protected abstract Action<ProcessResult> getCellProcessAction(
        Integer centerId, CellInfo cell, Locale locale);

    protected abstract Action<ProcessResult> getPalletProcessAction(
        Integer centerId,
        Map<RowColPos, CellInfo> cells,
        Locale locale);

    protected void beforeScanTubeAlone() {
        // default does nothing
    }

    protected Map<RowColPos, SpecimenCell> getCells() {
        return palletScanManagement.getCells();
    }

    @Override
    public void setValues() throws Exception {
        scanValidValue.setValue(true);
        palletScanManagement.onReset();
        enableFields(true);
    }

    protected void setUseScanner(@SuppressWarnings("unused") boolean useScanner) {
        currentScanState = UICellStatus.EMPTY;
    }

    @SuppressWarnings("unused")
    protected void processCellResult(RowColPos rcp, SpecimenCell palletCell) {
        // nothing done by default
    }

    protected boolean isAtLeastOneScanLaunched() {
        return palletScanManagement.getScansCount() > 0;
    }

    protected void initCellsWithContainer(ContainerWrapper currentMultipleContainer) {
        if (currentMultipleContainer != null) {
            palletScanManagement.initCellsWithContainer(currentMultipleContainer);
        }
    }

    protected ContainerType getContainerType() {
        return palletScanManagement.getContainerType();
    }

    protected void setContainerType(ContainerType type) {
        palletScanManagement.setContainerType(type);
    }

    protected void setFakeContainerType(int rows, int cols) {
        palletScanManagement.setFakeContainerType(rows, cols);
    }

    protected void initPalletContainerTypes() throws ApplicationException {
        palletContainerTypes.addAll(AbstractLinkAssignEntryForm.getPalletContainerTypes());
    }

    /**
     * Returns the information stored on the server for each specimen inventory ID passed in. If the
     * inventory ID is not in the database then nothing is returned.
     * 
     * @param currentWorkingCenter
     * @param cells
     * @return
     * @throws ApplicationException
     */
    @SuppressWarnings("nls")
    public static Map<String, SpecimenBriefInfo> getSpecimenData(
        CenterWrapper<?> currentWorkingCenter,
        Set<SpecimenCell> cells)
        throws ApplicationException {
        Set<String> inventoryIds = new HashSet<String>();

        for (SpecimenCell cell : cells) {
            String inventoryId = cell.getValue();
            if (inventoryId == null) {
                throw new IllegalStateException("cell has no inventory id");
            }
            inventoryIds.add(inventoryId);
        }

        List<SpecimenBriefInfo> specimenData = SessionManager.getAppService().doAction(
            new SpecimenSetGetInfoAction(
                currentWorkingCenter.getWrappedObject(),
                inventoryIds)).getList();

        return SpecimenSetGetInfoAction.toMap(specimenData);
    }

    public PalletDimensions getCurrentPlateDimensions() {
        ContainerType containerType = getContainerType();
        return PalletScanManagement.capacityToPlateDimensions(containerType.getCapacity());

    }

    /**
     * Conversion for server side call.
     * 
     * @param cells
     * @return
     */
    @SuppressWarnings("nls")
    public static Map<RowColPos, CellInfo> getServerSpecimenData(Collection<SpecimenCell> cells) {
        if (cells == null) {
            throw new IllegalArgumentException("cells is null");
        }

        Map<RowColPos, CellInfo> serverCells = new HashMap<RowColPos, CellInfo>();
        for (SpecimenCell cell : cells) {
            serverCells.put(cell.getRowColPos(), cell.transformIntoServerCell());
        }

        return serverCells;

    }

}
