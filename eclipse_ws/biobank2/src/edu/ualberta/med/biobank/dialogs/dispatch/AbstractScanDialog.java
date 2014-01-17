package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.linkassign.AbstractPalletSpecimenAdminForm;
import edu.ualberta.med.biobank.forms.linkassign.IDecodePalletManagement;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement.ScanManualOption;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.SbsLabeling;
import edu.ualberta.med.biobank.widgets.grids.PalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;

public abstract class AbstractScanDialog<T extends ModelWrapper<?>>
    extends BgcBaseDialog
    implements IDecodePalletManagement {

    private static final I18n i18n = I18nFactory.getI18n(AbstractScanDialog.class);

    private static Logger log = LoggerFactory.getLogger(AbstractScanDialog.class);

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Scanning specimens");

    @SuppressWarnings("nls")
    // TR: button label
    private static final String DECODE_PALLET_BUTTON_LABEL = i18n.tr("Decode pallet");

    private final PalletScanManagement palletScanManagement;

    protected PalletWidget palletWidget;

    protected T currentShipment;

    private final IObservableValue scanHasBeenLaunchedValue =
        new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue hasValues = new WritableValue(Boolean.FALSE, Boolean.class);

    /** should only be assigned by using {@link setScanOkValue} */
    private boolean scanStatus = false;

    /** Holds the value stored in {@link scanStatus} */
    private final IObservableValue scanStatusObservable =
        new WritableValue(Boolean.TRUE, Boolean.class);

    private Button decodeButton;

    protected CenterWrapper<?> currentCenter;

    protected RowColPos currentGridDimensions =
        new RowColPos(RowColPos.ROWS_DEFAULT, RowColPos.COLS_DEFAULT);

    public AbstractScanDialog(
        Shell parentShell,
        final T currentShipment,
        CenterWrapper<?> currentSite) {
        super(parentShell);
        this.currentShipment = currentShipment;
        this.currentCenter = currentSite;
        palletScanManagement = new PalletScanManagement(this);
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @SuppressWarnings("unused")
    protected void specificScanPosProcess(SpecimenCell palletCell) {
        // default do nothing
    }

    @SuppressWarnings("unused")
    protected boolean checkBeforeProcessing(CenterWrapper<?> currentCenter) throws Exception {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        final Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createCustomDialogPreContents(contents);

        decodeButton = new Button(contents, SWT.PUSH);
        decodeButton.setText(DECODE_PALLET_BUTTON_LABEL);
        decodeButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                decodeButtonSelected();
            }
        });
        decodeButton.setEnabled(false);
        GridData gd = new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false);
        decodeButton.setLayoutData(gd);

        palletWidget = createScanPalletWidget(contents);

        scanStatus = false;
        widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            scanStatusObservable,
            i18n.tr("Error in scan result. Please, only keep specimens without errors."),
            IStatus.ERROR);
        widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            scanHasBeenLaunchedValue,
            i18n.tr("Scan should be launched"),
            IStatus.ERROR);
        widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            hasValues,
            i18n.tr("No values scanned"),
            IStatus.ERROR);
    }

    protected void decodeButtonSelected() {
        setContainerType(null);
        decodeAndProcessResult();
    }

    protected void decodeAndProcessResult() {
        setDecodeOkValue(false);
        palletScanManagement.decodeAndProcessResult();
        palletWidget.setVisible(true);
    }

    @SuppressWarnings("unused")
    protected void createCustomDialogPreContents(Composite parent) {
        // default does nothing
    }

    protected abstract List<UICellStatus> getPalletCellStatus();

    @SuppressWarnings("nls")
    protected void startNewPallet() {
        log.debug("startNewPallet");
        palletWidget.setCells(null);
        scanHasBeenLaunchedValue.setValue(false);
    }

    @SuppressWarnings("nls")
    protected void setDecodeOkValue(final boolean value) {
        log.debug("setDecodeOkValue: value: {}", value);
        scanStatus = value;
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                scanStatusObservable.setValue(scanStatus);
            }
        });
    }

    protected void setHasValues() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                hasValues.setValue(getCells().size() > 0);
            }
        });
    }

    @SuppressWarnings("nls")
    private void setScanHasBeenLaunched(final boolean launched) {
        log.debug("setScanHasBeenLaunched: start: launched: {}", launched);
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                log.debug("setScanHasBeenLaunched: run: start");
                scanHasBeenLaunchedValue.setValue(launched);
                log.debug("setScanHasBeenLaunched: run: end");
            }
        });
        log.debug("setScanHasBeenLaunched: end");
    }

    private boolean isScanHasBeenLaunched() {
        return scanHasBeenLaunchedValue.getValue().equals(true);
    }

    protected Map<RowColPos, SpecimenCell> getCells() {
        return palletScanManagement.getCells();
    }

    protected void redrawPallet() {
        palletWidget.redraw();
    }

    @SuppressWarnings("nls")
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.CANCEL_ID, i18n.tr("Cancel"), false);
        createButton(parent, IDialogConstants.PROCEED_ID, getProceedButtonlabel(), false);
        createButton(parent, IDialogConstants.NEXT_ID, i18n.tr("Start next Pallet"), false);
        createButton(parent, IDialogConstants.FINISH_ID, IDialogConstants.FINISH_LABEL, false);
    }

    protected abstract String getProceedButtonlabel();

    @SuppressWarnings("nls")
    @Override
    protected void setOkButtonEnabled(boolean enabled) {
        Button proceedButton = getButton(IDialogConstants.PROCEED_ID);
        Button finishButton = getButton(IDialogConstants.FINISH_ID);
        Button nextButton = getButton(IDialogConstants.NEXT_ID);
        if ((finishButton != null) && !finishButton.isDisposed()) {
            if (canActivateProceedButton())
                proceedButton.setEnabled(enabled);
            else
                proceedButton.setEnabled(false);
            if (canActivateNextAndFinishButton()) {
                finishButton.setEnabled(enabled);
                nextButton.setEnabled(enabled);
            } else {
                finishButton.setEnabled(false);
                nextButton.setEnabled(false);
            }

        } else {
            okButtonEnabled = enabled;
        }
        log.debug("setOkButtonEnabled");
    }

    protected boolean canActivateNextAndFinishButton() {
        return true;
    }

    protected boolean canActivateProceedButton() {
        return true;
    }

    @Override
    protected void handleStatusChanged(IStatus status) {
        super.handleStatusChanged(status);
        decodeButton.setEnabled(fieldsValid());
    }

    protected boolean fieldsValid() {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.CANCEL_ID == buttonId)
            super.buttonPressed(buttonId);
        else if (IDialogConstants.PROCEED_ID == buttonId) {
            try {
                doProceed();
            } catch (Exception e) {
                BgcPlugin.openAsyncError(i18n.tr("Error"), e);
            }
        } else if (IDialogConstants.FINISH_ID == buttonId) {
            setReturnCode(OK);
            close();
        } else if (IDialogConstants.NEXT_ID == buttonId) {
            startNewPallet();
        }
    }

    protected abstract void doProceed() throws Exception;

    // protected abstract Action<ProcessResult> getCellProcessAction(
    // Integer centerId, CellInfo cell, Locale locale);

    protected abstract Action<ProcessResult> getPalletProcessAction(
        Integer centerId, Map<RowColPos, CellInfo> cells, Locale locale);

    protected void resetScan() {
        if (palletWidget != null)
            palletWidget.setCells(null);
        setScanHasBeenLaunched(false);
        palletScanManagement.onReset();
    }

    private PalletWidget createScanPalletWidget(Composite contents) {
        PalletWidget palletWidget = new PalletWidget(
            contents,
            getPalletCellStatus(),
            SbsLabeling.ROW_DEFAULT,
            SbsLabeling.COL_DEFAULT);

        palletWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (isScanHasBeenLaunched())
                    palletScanManagement.scanTubesManually(e, ScanManualOption.NO_DUPLICATES);
            }
        });
        palletWidget.setVisible(false);
        return palletWidget;
    }

    @Override
    public void beforeProcessingThreadStart() {
        // do nothing
    }

    @Override
    @SuppressWarnings("nls")
    public void processDecodeResult() throws Exception {
        log.debug("processScanResult: start");

        Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
                Capacity capacity = palletScanManagement.getContainerType().getCapacity();
                palletWidget.setStorageSize(capacity.getRowCapacity(), capacity.getColCapacity());
            }
        });

        if (currentCenter == null) {
            throw new IllegalStateException("current working center is null");
        }

        if (checkBeforeProcessing(currentCenter)) {
            Map<RowColPos, SpecimenCell> cells = getCells();

            // conversion for server side call
            Map<RowColPos, CellInfo> serverCells = null;
            if (cells != null) {
                serverCells = new HashMap<RowColPos, CellInfo>();
                for (Entry<RowColPos, SpecimenCell> entry : cells.entrySet()) {
                    serverCells.put(entry.getKey(), entry.getValue().transformIntoServerCell());
                }
            }
            // server side call
            ScanProcessResult res = (ScanProcessResult) SessionManager.getAppService().doAction(
                getPalletProcessAction(
                    currentCenter.getId(),
                    serverCells,
                    Locale.getDefault()));

            Map<String, SpecimenBriefInfo> specimenDataMap =
                AbstractPalletSpecimenAdminForm.getSpecimenData(
                    currentCenter, new HashSet<SpecimenCell>(cells.values()));

            for (Entry<RowColPos, CellInfo> entry : res.getCells().entrySet()) {
                RowColPos pos = entry.getKey();
                SpecimenCell palletWell = cells.get(entry.getKey());
                CellInfo servercell = entry.getValue();
                if (palletWell == null) {
                    // can happen if missing
                    palletWell = new SpecimenCell(pos.getRow(), pos.getCol(), new DecodedWell(
                        servercell.getRow(), servercell.getCol(), servercell.getValue()));
                    cells.put(pos, palletWell);
                }
                palletWell.merge(specimenDataMap.get(palletWell.getValue()), servercell);
                specificScanPosProcess(palletWell);
            }
            setDecodeOkValue(res.getProcessStatus() != CellInfoStatus.ERROR);
        } else {
            setDecodeOkValue(false);
        }
        setHasValues();

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                palletWidget.setCells(getCells());
                setScanHasBeenLaunched(true);
            }
        });

        log.debug("processScanResult: end");
    }

    @Override
    public void decodeAndProcessError(String errorMsg) {
        // do nothing
    }

    @Override
    @SuppressWarnings("nls")
    public void postProcessDecodeTubesManually(Set<SpecimenCell> cells) throws Exception {
        log.debug("postprocessScanTubesManually: start");
        boolean errorFound = false;

        if (cells == null) {
            throw new IllegalArgumentException("cells is null");
        }

        CenterWrapper<?> currentWorkingCenter = SessionManager.getUser().getCurrentWorkingCenter();
        if (currentWorkingCenter == null) {
            throw new IllegalStateException("current working center is null");
        }

        // conversion for server side call
        Map<RowColPos, CellInfo> serverCells =
            AbstractPalletSpecimenAdminForm.getServerSpecimenData(cells);

        // server side call
        ScanProcessResult res = (ScanProcessResult) SessionManager.getAppService().doAction(
            getPalletProcessAction(
                currentWorkingCenter.getId(),
                serverCells,
                Locale.getDefault()));

        Map<String, SpecimenBriefInfo> specimenDataMap =
            AbstractPalletSpecimenAdminForm.getSpecimenData(currentWorkingCenter, cells);

        for (SpecimenCell cell : cells) {
            CellInfo cellServerInfo = res.getCells().get(cell.getRowColPos());
            cell.merge(specimenDataMap.get(cell.getValue()), cellServerInfo);
            if (res.getProcessStatus() == CellInfoStatus.ERROR) {
                Button okButton = getButton(IDialogConstants.PROCEED_ID);
                okButton.setEnabled(false);
                errorFound = true;
            }
            specificScanPosProcess(cell);
        }
        palletWidget.redraw();
        setDecodeOkValue(scanStatus && !errorFound);
        setHasValues();
        log.debug("postprocessScanTubesManually: end");
    }

    @Override
    @SuppressWarnings("nls")
    public boolean canDecodeTubesManually(SpecimenCell cell) {
        boolean result = ((cell == null) || (cell.getStatus() == UICellStatus.EMPTY)
            || (cell.getStatus() == UICellStatus.ERROR)
            || (cell.getStatus() == UICellStatus.MISSING));
        log.debug("canScanTubeAlone: result: {}", result);
        return result;
    }

    public void setContainerType(ContainerType containerType) {
        palletScanManagement.setContainerType(containerType);
    }

}
