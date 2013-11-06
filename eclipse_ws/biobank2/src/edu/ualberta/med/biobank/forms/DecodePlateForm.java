package edu.ualberta.med.biobank.forms;

import java.io.File;
import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.PlateSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.scannerconfig.PlateDimensions;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanLibResult;

public class DecodePlateForm extends PlateForm {
    private static final I18n i18n = I18nFactory
        .getI18n(DecodeImageForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.DecodePlateForm";

    private static final String FAKE_PLATE_IMAGE_FILE_NAME = "fakePlateImage.bmp";

    private ScanPalletWidget spw;

    private PlateSelectionWidget plateSelectionWidget;

    Integer plateToScan;

    // used for debugging in Linux
    private final boolean haveFakePlateImage;

    @SuppressWarnings("nls")
    public DecodePlateForm() {
        super();
        // For Linux set debugMode to true if a fake flatbed image exits
        if (!System.getProperty("os.name").startsWith("Windows")) {
            File platesFile = new File(FAKE_PLATE_IMAGE_FILE_NAME);
            haveFakePlateImage = platesFile.exists();
        } else {
            haveFakePlateImage = false;
        }

    }

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        setPartName(i18n.tr("Decode Plate"));
    }

    @Override
    public void dispose() {
        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .removePropertyChangeListener(propertyListener);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Decode Plate"));
        GridLayout layout = new GridLayout(2, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

        plateSelectionWidget = new PlateSelectionWidget(page, SWT.NONE);
        plateSelectionWidget.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        plateSelectionWidget.setLayoutData(gd);
        plateSelectionWidget.addPlateSelectionListener(new SelectionListener() {

            @Override
            public void widgetSelected(SelectionEvent e) {
                int plateNumber = plateSelectionWidget.getSelectedPlate();
                PlateDimensions gridDimensions = ScannerConfigPlugin.getPlateGridDimensions(plateNumber);
                int rows = gridDimensions.getRows();
                int cols = gridDimensions.getCols();
                spw.dispose();
                spw = new ScanPalletWidget(
                    page, Arrays.asList(UICellStatus.EMPTY, UICellStatus.FILLED), rows, cols);
                spw.setVisible(true);
                toolkit.adapt(spw);
                page.layout(true, true);
                book.reflow(true);
            }

            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                widgetSelected(e);
            }
        });

        scanButton = toolkit.createButton(page, i18n.tr("Scan"), SWT.PUSH);
        scanButton.setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scanAndProcessResult();
            }
        });

        int rows;
        int cols;
        Integer plateNumber = plateSelectionWidget.getSelectedPlate();
        if (plateNumber == null) {
            rows = RowColPos.ROWS_DEFAULT;
            cols = RowColPos.COLS_DEFAULT;
        }
        else {
            PlateDimensions gridDimensions = ScannerConfigPlugin.getPlateGridDimensions(plateNumber);
            rows = gridDimensions.getRows();
            cols = gridDimensions.getCols();
        }

        spw = new ScanPalletWidget(page, Arrays.asList(UICellStatus.EMPTY,
            UICellStatus.FILLED), rows, cols);
        spw.setVisible(true);
        toolkit.adapt(spw);

        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .addPropertyChangeListener(propertyListener);
    }

    @Override
    public void setFocus() {
        scanButton.setFocus();
    }

    @SuppressWarnings("nls")
    protected void scanAndProcessResult() {
        plateToScan = plateSelectionWidget.getSelectedPlate();

        if (plateToScan == null) {
            BgcPlugin.openAsyncError(
                // dialog title.
                i18n.tr("Decode Plate Error"),
                // dialog message.
                i18n.tr("No plate selected"));
            return;
        }

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(
                    // progress monitor message.
                    i18n.tr("Scanning and decoding..."),
                    IProgressMonitor.UNKNOWN);
                try {
                    launchScan(monitor);
                } catch (RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        // dialog title.
                        i18n.tr("Scan & Decode Error"), e);
                }
                monitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(true, true, op);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("nls")
    protected void launchScan(IProgressMonitor monitor) throws Exception {
        monitor.subTask(
            // progress monitor message.
            i18n.tr("Launching scan"));

        String filename = String.format("plate_scan_%d.bmp", plateToScan);
        ScanLibResult.Result result = ScannerConfigPlugin.scanPlate(plateToScan, filename);

        if ((result == ScanLibResult.Result.SUCCESS)
            || ((result == ScanLibResult.Result.FAIL) && (haveFakePlateImage))) {
        }

        // Set<DecodedWell> decodedCells = ScannerConfigPlugin.decodePlate(plateToScan);
        // wells = PalletWell.convertArray(decodedCells);
        processResult(monitor);
    }

    @SuppressWarnings("nls")
    protected void processResult(IProgressMonitor monitor) throws Exception {
        monitor.subTask(
            // progress monitor message.
            i18n.tr("Decoding..."));

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                processScanResult();
                spw.setCells(wells);
            }
        });
    }

    @Override
    public void setValues() throws Exception {
        // TODO Auto-generated method stub

    }
}
