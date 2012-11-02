package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcFileBrowser;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;
import edu.ualberta.med.biobank.widgets.grids.ScanPalletWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletWell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.ScannerConfigPlugin;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;

public class DecodeImageForm extends PlateForm implements
    IBgcFileBrowserListener {
    private static final I18n i18n = I18nFactory
        .getI18n(DecodeImageForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.DecodeImageForm";

    private ScanPalletWidget spw;

    private BgcFileBrowser imageFileSelector;

    private String imageFilename;

    @SuppressWarnings("nls")
    @Override
    protected void init() throws Exception {
        setPartName(
        // tab name.
        i18n.tr("Decode Image"));
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(
            // form title.
            i18n.tr("Decode Image"));
        GridLayout layout = new GridLayout(1, false);
        page.setLayout(layout);
        page.setLayoutData(new GridData(SWT.BEGINNING, SWT.TOP, false, false));

        imageFileSelector = new BgcFileBrowser(page,
            // label.
            i18n.tr("Image File"), SWT.NONE,
            new String[] { "*.bmp" });
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
            @SuppressWarnings("nls")
            @Override
            public void run(IProgressMonitor monitor) {
                monitor.beginTask(
                    // progress monitor message.
                    i18n.tr("Decoding..."),
                    IProgressMonitor.UNKNOWN);
                try {
                    decodeImage();
                } catch (RemoteConnectFailureException exp) {
                    BgcPlugin.openRemoteConnectErrorMessage(exp);
                } catch (Exception e) {
                    BgcPlugin.openAsyncError(
                        // dialog title.
                        i18n.tr("Decoding error"), e);
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
        Set<DecodedWell> decodedCells = ScannerConfigPlugin.decodeImage(imageFilename);
        wells = PalletWell.convertArray(decodedCells);

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
