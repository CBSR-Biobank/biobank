package edu.ualberta.med.biobank.forms;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.model.AliquotCellStatus;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.widgets.PlateSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public class DecodePlateForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.DecodePlateForm";

    private Button scanButton;

    private ScanPalletWidget spw;

    private Map<RowColPos, PalletCell> cells;

    private PlateSelectionWidget plateSelectionWidget;

    Integer plateToScan;

    @Override
    protected void init() throws Exception {
        setPartName(Messages.getString("DecodePlate.tabTitle")); //$NON-NLS-1$
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.getString("DecodePlate.tabTitle"));
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(
            new GridData(SWT.BEGINNING, SWT.TOP, false, false));

        plateSelectionWidget = new PlateSelectionWidget(form.getBody(),
            SWT.NONE);
        plateSelectionWidget.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        plateSelectionWidget.setLayoutData(gd);

        scanButton = toolkit.createButton(form.getBody(),
            "Scan && Decode Plate", SWT.PUSH);
        scanButton
            .setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalScanAndProcessResult();
            }
        });

        spw = new ScanPalletWidget(form.getBody(), false);
        spw.setVisible(true);
        toolkit.adapt(spw);

    }

    @Override
    public void setFocus() {
        scanButton.setFocus();
    }

    @Override
    protected void reload() throws Exception {
        plateSelectionWidget.reload();
    }

    protected void internalScanAndProcessResult() {
        plateToScan = plateSelectionWidget.getSelectedPlate();

        if (plateToScan == null) {
            BioBankPlugin.openAsyncError("Decode Plate Error",
                "No plate selected");
            return;
        }

        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) {
                monitor.beginTask("Scanning and decoding...",
                    IProgressMonitor.UNKNOWN);
                try {
                    scanAndProcessResult(monitor);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError(Messages
                        .getString("DecodePlate.dialog.scanError.title"), //$NON-NLS-1$
                        e);
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

    protected void scanAndProcessResult(IProgressMonitor monitor)
        throws Exception {
        launchScan(monitor);
        monitor.subTask("Decoding...");

        Display.getDefault().asyncExec(new Runnable() {
            public void run() {
                processScanResult();
                spw.setCells(cells);
            }
        });
    }

    /**
     * go through cells retrieved from scan, set status and update the types
     * combos components
     */
    private void processScanResult() {
        Map<Integer, Integer> typesRows = new HashMap<Integer, Integer>();
        for (RowColPos rcp : cells.keySet()) {
            Integer typesRowsCount = typesRows.get(rcp.row);
            if (typesRowsCount == null) {
                typesRowsCount = 0;
            }
            PalletCell cell = null;
            cell = cells.get(rcp);
            processCellStatus(cell);
            if (PalletCell.hasValue(cell)) {
                typesRowsCount++;
                typesRows.put(rcp.row, typesRowsCount);
            }
        }
    }

    protected void launchScan(IProgressMonitor monitor) throws Exception {
        monitor.subTask("Launching scan");
        cells = PalletCell.convertArray(ScannerConfigPlugin.scan(plateToScan));
    }

    /**
     * Process the cell: apply a status and set correct information
     */
    private void processCellStatus(PalletCell cell) {
        if (cell != null) {
            cell.setStatus((cell.getValue() != null) ? AliquotCellStatus.NEW
                : AliquotCellStatus.EMPTY);
        }
    }

}
