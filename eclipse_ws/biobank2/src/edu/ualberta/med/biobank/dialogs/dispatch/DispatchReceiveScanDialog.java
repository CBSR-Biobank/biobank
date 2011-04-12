package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.scanprocess.data.DispatchProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.model.PalletCell;
import edu.ualberta.med.biobank.model.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchReceiveScanDialog extends
    AbstractScanDialog<DispatchWrapper> {

    private static final String TITLE = Messages
        .getString("DispatchReceiveScanDialog.title"); //$NON-NLS-1$

    private int pendingAliquotsNumber = 0;

    private boolean aliquotsReceived = false;

    public DispatchReceiveScanDialog(Shell parentShell,
        final DispatchWrapper currentShipment, CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.getString("DispatchReceiveScanDialog.description"); //$NON-NLS-1$
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    @Override
    protected ScanProcessResult internalProcessScanResult(
        IProgressMonitor monitor,
        Map<RowColPos, edu.ualberta.med.biobank.common.scanprocess.Cell> serverCells,
        CenterWrapper<?> site) throws Exception {
        // server side call
        ScanProcessResult res = SessionManager.getAppService()
            .processScanResult(serverCells, getProcessData(), isRescanMode(),
                SessionManager.getUser());
        return res;
    }

    @Override
    protected ProcessData getProcessData() {
        return new DispatchProcessData(null, currentShipment, false, false);
    }

    @Override
    protected String getProceedButtonlabel() {
        return Messages
            .getString("DispatchReceiveScanDialog.proceed.button.label"); //$NON-NLS-1$
    }

    @Override
    protected boolean canActivateProceedButton() {
        return pendingAliquotsNumber != 0;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return pendingAliquotsNumber == 0;
    }

    @Override
    protected void doProceed() {
        List<SpecimenWrapper> aliquots = new ArrayList<SpecimenWrapper>();
        for (PalletCell cell : getCells().values()) {
            if (cell.getStatus() == UICellStatus.IN_SHIPMENT_EXPECTED) {
                aliquots.add(cell.getSpecimen());
                cell.setStatus(UICellStatus.IN_SHIPMENT_RECEIVED);
            }
        }
        try {
            (currentShipment).receiveSpecimens(aliquots);
            redrawPallet();
            pendingAliquotsNumber = 0;
            setOkButtonEnabled(true);
            aliquotsReceived = true;
        } catch (Exception e) {
            BiobankPlugin
                .openAsyncError(
                    Messages
                        .getString("DispatchReceiveScanDialog.receiveing.error.title"), e); //$NON-NLS-1$
        }
        Button cancelButton = getButton(IDialogConstants.CANCEL_ID);
        cancelButton.setEnabled(false);
    }

    @Override
    protected void startNewPallet() {
        setRescanMode(false);
        super.startNewPallet();
    }

    @Override
    protected List<UICellStatus> getPalletCellStatus() {
        return UICellStatus.DEFAULT_PALLET_DISPATCH_RECEIVE_STATUS_LIST;
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        if (currentShipment.getSpecimenCollection(false).size() > 0) {
            int i = 0;
            for (DispatchSpecimenWrapper dsa : (currentShipment)
                .getDispatchSpecimenCollection(false)) {
                int row = i / 12;
                int col = i % 12;
                if (!DispatchSpecimenState.MISSING.isEquals(dsa.getState()))
                    palletScanned.put(new RowColPos(row, col), new PalletCell(
                        new ScanCell(row, col, dsa.getSpecimen()
                            .getInventoryId())));
                i++;
            }
        }
        return palletScanned;
    }

    public boolean hasReceivedSpecimens() {
        return aliquotsReceived;
    }

}
