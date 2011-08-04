package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.FileBrowser;
import edu.ualberta.med.biobank.widgets.IFileBrowserListener;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCellPos;
import edu.ualberta.med.scannerconfig.preferences.scanner.profiles.ProfileManager;

public class DecodeImageForm extends PlateForm implements IFileBrowserListener {

    public static final String ID = "edu.ualberta.med.biobank.forms.DecodeImageForm"; //$NON-NLS-1$

    private ScanPalletWidget spw;

    private FileBrowser imageFileSelector;

    private String imageFilename;

    @Override
    protected void init() throws Exception {
        setPartName(Messages.DecodeImage_tabTitle);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.DecodeImage_tabTitle);
        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

        imageFileSelector = new FileBrowser(page,
            Messages.DecodeImage_browse_label, SWT.NONE,
            new String[] { "*.bmp" }); //$NON-NLS-1$
        imageFileSelector.addFileSelectedListener(this);
        imageFileSelector.adaptToToolkit(toolkit, true);

        spw = new ScanPalletWidget(page, Arrays.asList(UICellStatus.EMPTY,
            UICellStatus.FILLED));
        spw.setVisible(true);
        toolkit.adapt(spw);
    }

    @Override
    public void fileSelected(String filename) {
        imageFilename = filename;
        IRunnableWithProgress op = new IRunnableWithProgress() {
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(Messages.DecodeImageForm_decoding,
                    IProgressMonitor.UNKNOWN);
                try {
                    decodeImage();
                } catch (RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        Messages.DecodeImage_dialog_scanError_title, e);
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

    protected void decodeImage() throws Exception {
        Map<ScanCellPos, ScanCell> decodedCells = ScannerConfigPlugin
            .decodeImage(1, ProfileManager.ALL_PROFILE_NAME, imageFilename);
        cells = PalletCell.convertArray(decodedCells);

        Display.getDefault().asyncExec(new Runnable() {
            @Override
            public void run() {
                processScanResult();
                spw.setCells(cells);
            }
        });
    }

    @Override
    public void reload() throws Exception {
        // do nothing
    }

}
