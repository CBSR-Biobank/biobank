package edu.ualberta.med.biobank.forms;

import java.util.Arrays;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.PlateSelectionWidget;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;

public class DecodePlateForm extends PlateForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.DecodePlateForm"; //$NON-NLS-1$

    private ScanPalletWidget spw;

    private PlateSelectionWidget plateSelectionWidget;

    Integer plateToScan;

    @Override
    protected void init() throws Exception {
        setPartName(Messages.DecodePlate_tabTitle);
    }

    @Override
    public void dispose() {
        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .removePropertyChangeListener(propertyListener);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.DecodePlate_tabTitle);
        GridLayout layout = new GridLayout(2, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

        plateSelectionWidget = new PlateSelectionWidget(page, SWT.NONE);
        plateSelectionWidget.adaptToToolkit(toolkit, true);
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        plateSelectionWidget.setLayoutData(gd);

        scanButton = toolkit.createButton(page,
            Messages.DecodePlateForm_button_scan_decode_label, SWT.PUSH);
        scanButton
            .setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scanAndProcessResult();
            }
        });

        spw = new ScanPalletWidget(page, Arrays.asList(UICellStatus.EMPTY,
            UICellStatus.FILLED));
        spw.setVisible(true);
        toolkit.adapt(spw);

        ScannerConfigPlugin.getDefault().getPreferenceStore()
            .addPropertyChangeListener(propertyListener);
    }

    @Override
    public void setFocus() {
        scanButton.setFocus();
    }

    @Override
    public void reload() throws Exception {
        //
    }

    protected void scanAndProcessResult() {
        plateToScan = plateSelectionWidget.getSelectedPlate();

        if (plateToScan == null) {
            BgcPlugin.openAsyncError(Messages.DecodePlateForm_error_title,
                Messages.DecodePlateForm_noplate_error_msg);
            return;
        }

        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(Messages.DecodePlateForm_scanning_decoding,
                    IProgressMonitor.UNKNOWN);
                try {
                    scanAndProcessResult(monitor);
                } catch (RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.DecodePlate_dialog_scanError_title, e);
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
        monitor.subTask(Messages.DecodePlateForm_decoding);

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                processScanResult();
                spw.setCells(cells);
            }
        });
    }

    protected void launchScan(IProgressMonitor monitor) throws Exception {
        monitor.subTask(Messages.DecodePlateForm_launching);

        ScanCell[][] decodedCells = null;
        decodedCells = ScannerConfigPlugin.decodePlate(plateToScan,
            ProfileManager.ALL_PROFILE_NAME);
        cells = PalletCell.convertArray(decodedCells);
    }

}
