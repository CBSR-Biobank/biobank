package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.scanprocess.data.DispatchProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchReceiveScanDialog extends
    AbstractScanDialog<DispatchWrapper> {

    private int pendingSpecimensNumber = 0;

    private boolean specimensReceived = false;

    private List<SpecimenWrapper> extras = new ArrayList<SpecimenWrapper>();

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
    protected void specificScanPosProcess(PalletCell palletCell) {
        if (palletCell.getStatus() == UICellStatus.EXTRA)
            extras.add(palletCell.getSpecimen());
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
    protected void buttonPressed(int buttonId) {
        if (IDialogConstants.PROCEED_ID == buttonId
            || IDialogConstants.FINISH_ID == buttonId
            || IDialogConstants.NEXT_ID == buttonId) {
            addExtraCells();
        }
        if (IDialogConstants.NEXT_ID == buttonId)
            extras.clear();
        super.buttonPressed(buttonId);
    }

    private void addExtraCells() {
        if (extras != null && extras.size() > 0) {
            BiobankPlugin
                .openAsyncInformation(
                    Messages
                        .getString("DispatchReceiveScanDialog.notInDispatch.error.title"), //$NON-NLS-1$
                    Messages
                        .getString("DispatchReceiveScanDialog.notInDispatch.error.msg")); //$NON-NLS-1$
            try {
                currentShipment.addSpecimens(extras,
                    DispatchSpecimenState.EXTRA);
            } catch (Exception e) {
                BiobankPlugin
                    .openAsyncError(
                        Messages
                            .getString("DispatchReceiveScanDialog.flagging.error.title"), //$NON-NLS-1$
                        e);
            }
        }
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
            palletScanned.put(new RowColPos(6, 6), new PalletCell(new ScanCell(
                6, 6, "aaar")));
        }
        return palletScanned;
    }

    public boolean hasReceivedSpecimens() {
        return specimensReceived;
    }

}
