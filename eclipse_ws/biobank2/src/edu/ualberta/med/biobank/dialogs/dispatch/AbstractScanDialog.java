package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.runtime.Assert;
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
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.linkassign.IPalletScanManagement;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement;
import edu.ualberta.med.biobank.forms.utils.PalletScanManagement.ScanManualOption;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.dialogs.BgcBaseDialog;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.SbsLabeling;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;

public abstract class AbstractScanDialog<T extends ModelWrapper<?>>
    extends BgcBaseDialog
    implements IPalletScanManagement {

    private static final I18n i18n = I18nFactory.getI18n(AbstractScanDialog.class);

    private static Logger log = LoggerFactory.getLogger(AbstractScanDialog.class);

    @SuppressWarnings("nls")
    private static final String TITLE = i18n.tr("Scanning specimens");

    @SuppressWarnings("nls")
    // TR: button label
    private static final String FLATBED_SCAN_BUTTON_LABEL = i18n.tr("Flatbed Scan");

    protected final PalletScanManagement palletScanManagement;

    protected ScanPalletWidget spw;

    protected T currentShipment;

    private final IObservableValue scanHasBeenLaunchedValue =
        new WritableValue(Boolean.FALSE, Boolean.class);

    private final IObservableValue hasValues = new WritableValue(Boolean.FALSE, Boolean.class);

    /** should only be assigned by using {@link setScanOkValue} */
    private boolean scanStatus = false;

    /** Holds the value stored in {@link scanStatus} */
    private final IObservableValue scanStatusObservable =
        new WritableValue(Boolean.TRUE, Boolean.class);

    private Button scanButton;

    protected CenterWrapper<?> currentSite;

    protected RowColPos currentGridDimensions =
        new RowColPos(RowColPos.ROWS_DEFAULT, RowColPos.COLS_DEFAULT);

    public AbstractScanDialog(Shell parentShell, final T currentShipment,
        CenterWrapper<?> currentSite) {
        super(parentShell);
        this.currentShipment = currentShipment;
        this.currentSite = currentSite;
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
    protected void specificScanPosProcess(PalletWell palletCell) {
        // default do nothing
    }

    @SuppressWarnings("unused")
    protected boolean checkBeforeProcessing(CenterWrapper<?> currentCenter)
        throws Exception {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        final Composite contents = new Composite(parent, SWT.NONE);
        contents.setLayout(new GridLayout(2, false));
        contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        createCustomDialogPreContents(contents);

        scanButton = new Button(contents, SWT.PUSH);
        scanButton.setText(FLATBED_SCAN_BUTTON_LABEL);
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                launchScan();
            }
        });
        scanButton.setEnabled(false);

        createScanPalletWidget(contents, SbsLabeling.ROW_DEFAULT, SbsLabeling.COL_DEFAULT);

        scanStatus = false;
        widgetCreator.addBooleanBinding(
            new WritableValue(Boolean.FALSE, Boolean.class),
            scanStatusObservable,
            i18n.tr("Error in scan result. Please keep only specimens with no errors."),
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

    @SuppressWarnings("unused")
    protected void createCustomDialogPreContents(Composite parent) {
        // default does nothing
    }

    protected abstract List<UICellStatus> getPalletCellStatus();

    @SuppressWarnings("nls")
    private void launchScan() {
        log.debug("launchScan");
        setScanOkValue(false);
        palletScanManagement.launchScanAndProcessResult();
    }

    @SuppressWarnings("nls")
    protected void startNewPallet() {
        log.debug("startNewPallet");
        spw.setCells(null);
        scanHasBeenLaunchedValue.setValue(false);
    }

    @SuppressWarnings("nls")
    protected void setScanOkValue(final boolean value) {
        log.debug("setScanOkValue: value: {}", value);
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

    protected Map<RowColPos, PalletWell> getCells() {
        return palletScanManagement.getCells();
    }

    protected void redrawPallet() {
        spw.redraw();
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
        scanButton.setEnabled(fieldsValid());
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

    protected abstract Action<ProcessResult> getCellProcessAction(
        Integer centerId, CellInfo cell, Locale locale);

    protected abstract Action<ProcessResult> getPalletProcessAction(
        Integer centerId, Map<RowColPos, CellInfo> cells, Locale locale);

    protected void resetScan() {
        if (spw != null)
            spw.setCells(null);
        setScanHasBeenLaunched(false);
        palletScanManagement.onReset();
    }

    private void createScanPalletWidget(Composite contents, int rows, int cols) {
        spw = new ScanPalletWidget(contents, getPalletCellStatus(), rows, cols);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        spw.setLayoutData(gd);

        spw.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                if (isScanHasBeenLaunched())
                    palletScanManagement.scanTubesManually(e, ScanManualOption.NO_DUPLICATES);
            }
        });
    }

    @Override
    public void beforeScanThreadStart() {
        // do nothing
    }

    @Override
    public void beforeScan() {
        // do nothing
    }

    @Override
    @SuppressWarnings("nls")
    public void processScanResult() throws Exception {
        log.debug("processScanResult: start");
        Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());

        if (checkBeforeProcessing(currentSite)) {
            Map<RowColPos, PalletWell> cells = getCells();
            // conversion for server side call
            Map<RowColPos, CellInfo> serverCells = null;
            if (cells != null) {
                serverCells = new HashMap<RowColPos, CellInfo>();
                for (Entry<RowColPos, PalletWell> entry : cells.entrySet()) {
                    serverCells.put(entry.getKey(), entry.getValue().transformIntoServerCell());
                }
            }
            // server side call
            ScanProcessResult res = (ScanProcessResult) SessionManager.getAppService().doAction(
                getPalletProcessAction(
                    SessionManager.getUser().getCurrentWorkingCenter().getId(),
                    serverCells,
                    Locale.getDefault()));

            if (cells != null) {
                // for each cell, convert into a client side cell
                for (Entry<RowColPos, CellInfo> entry : res.getCells().entrySet()) {
                    RowColPos pos = entry.getKey();
                    PalletWell palletWell = cells.get(entry.getKey());
                    CellInfo servercell = entry.getValue();
                    if (palletWell == null) {
                        // can happen if missing
                        palletWell = new PalletWell(pos.getRow(), pos.getCol(), new DecodedWell(
                            servercell.getRow(), servercell.getCol(), servercell.getValue()));
                        cells.put(pos, palletWell);
                    }
                    palletWell.merge(SessionManager.getAppService(), servercell);
                    specificScanPosProcess(palletWell);
                }
            }
            setScanOkValue(res.getProcessStatus() != CellInfoStatus.ERROR);
        } else {
            setScanOkValue(false);
        }
        setHasValues();
        log.debug("processScanResult: end");
    }

    @Override
    public void afterScanBeforeMerge() {
        // do nothing
    }

    @Override
    public void afterSuccessfulScan(Map<RowColPos, PalletWell> wells) {
        // do nothing
    }

    @Override
    public void afterScanAndProcess() {
        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                spw.setCells(getCells());
                setScanHasBeenLaunched(true);
            }
        });
    }

    @Override
    public void scanAndProcessError(String errorMsg) {
        // do nothing
    }

    @Override
    @SuppressWarnings("nls")
    public void postprocessScanTubesManually(Set<PalletWell> cells) throws Exception {
        log.debug("postprocessScanTubesManually: start");
        boolean errorFound = false;
        for (PalletWell cell : cells) {
            Assert.isNotNull(SessionManager.getUser().getCurrentWorkingCenter());
            CellProcessResult res = (CellProcessResult) SessionManager.getAppService().doAction(
                getCellProcessAction(SessionManager.getUser().getCurrentWorkingCenter().getId(),
                    cell.transformIntoServerCell(),
                    Locale.getDefault()));
            cell.merge(SessionManager.getAppService(), res.getCell());
            if (res.getProcessStatus() == CellInfoStatus.ERROR) {
                Button okButton = getButton(IDialogConstants.PROCEED_ID);
                okButton.setEnabled(false);
                errorFound = true;
            }
            specificScanPosProcess(cell);
        }
        spw.redraw();
        setScanOkValue(scanStatus && !errorFound);
        setHasValues();
        log.debug("postprocessScanTubesManually: end");
    }

    @Override
    @SuppressWarnings("nls")
    public boolean canScanTubesManually(PalletWell cell) {
        boolean result = ((cell == null) || (cell.getStatus() == UICellStatus.EMPTY)
            || (cell.getStatus() == UICellStatus.ERROR)
            || (cell.getStatus() == UICellStatus.MISSING));
        log.debug("canScanTubeAlone: result: {}", result);
        return result;
    }
}
