package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.scanprocess.data.DispatchProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
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

    private int pendingSpecimensNumber = 0;

    private boolean specimensReceived = false;

    public DispatchReceiveScanDialog(Shell parentShell,
        final DispatchWrapper currentShipment, CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @Override
    protected String getTitleAreaMessage() {
        return Messages.getString("DispatchReceiveScanDialog.description"); //$NON-NLS-1$
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
        return pendingSpecimensNumber != 0;
    }

    @Override
    protected boolean canActivateNextAndFinishButton() {
        return pendingSpecimensNumber == 0;
    }

    @Override
    protected void doProceed() {
        List<SpecimenWrapper> specimens = new ArrayList<SpecimenWrapper>();
        for (PalletCell cell : getCells().values()) {
            if (cell.getStatus() == UICellStatus.IN_SHIPMENT_EXPECTED) {
                specimens.add(cell.getSpecimen());
                cell.setStatus(UICellStatus.IN_SHIPMENT_RECEIVED);
            }
        }
        currentShipment.receiveSpecimens(specimens);
        redrawPallet();
        pendingSpecimensNumber = 0;
        setOkButtonEnabled(true);
        specimensReceived = true;
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
        return specimensReceived;
    }

}
