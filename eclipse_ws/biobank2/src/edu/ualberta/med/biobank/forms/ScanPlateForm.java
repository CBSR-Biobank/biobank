package edu.ualberta.med.biobank.forms;

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
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;

public class ScanPlateForm extends BiobankViewForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ScanPlateForm";

    private Integer plateId;

    private Button scanButton;

    private ScanPalletWidget spw;

    protected Map<RowColPos, PalletCell> cells;

    @Override
    protected void init() throws Exception {
        FormInput input = (FormInput) getEditorInput();
        plateId = (Integer) input.getAdapter(Integer.class);
        setPartName(Messages.getFormattedString("ScanPlate.tabTitle", plateId)); //$NON-NLS-1$
    }

    @Override
    protected void createFormContent() throws Exception {
        form
            .setText(Messages.getFormattedString("ScanPlate.tabTitle", plateId));
        GridLayout layout = new GridLayout(2, false);
        form.getBody().setLayout(layout);
        form.getBody().setLayoutData(
            new GridData(SWT.BEGINNING, SWT.TOP, false, false));

        scanButton = toolkit.createButton(form.getBody(), "Scan Plate",
            SWT.PUSH);
        scanButton
            .setLayoutData(new GridData(SWT.CENTER, SWT.TOP, false, false));
        scanButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                scanPlate();
            }
        });

    }

    @Override
    public void setFocus() {
        scanButton.setFocus();
    }

    @Override
    protected void reload() throws Exception {
    }

    protected void scanPlate() {
        IRunnableWithProgress op = new IRunnableWithProgress() {
            public void run(IProgressMonitor monitor) {
                monitor.beginTask("Scanning...", IProgressMonitor.UNKNOWN);
                try {
                    launchScan(monitor);
                } catch (RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError(Messages
                        .getString("ScanPlate.dialog.scanError.title"), //$NON-NLS-1$
                        e);
                }
                monitor.done();
            }
        };
        try {
            new ProgressMonitorDialog(PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow().getShell()).run(false, false, op);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void launchScan(IProgressMonitor monitor) throws Exception {
        monitor.subTask("Launching scan");
        ScannerConfigPlugin.scanPlate(plateId, "scan.bmp");
    }

}
