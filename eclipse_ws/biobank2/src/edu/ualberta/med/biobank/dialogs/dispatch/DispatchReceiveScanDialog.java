package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ShipmentProcessData;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class DispatchReceiveScanDialog extends
    ReceiveScanDialog<DispatchWrapper> {

    public DispatchReceiveScanDialog(Shell parentShell,
        final DispatchWrapper currentShipment, CenterWrapper<?> currentSite) {
        super(parentShell, currentShipment, currentSite);
    }

    @Override
    protected ProcessData getProcessData() {
        return new ShipmentProcessData(null, currentShipment, false, false);
    }

    @Override
    protected void addExtraCells() {
        if (extras != null && extras.size() > 0) {
            BiobankGuiCommonPlugin
                .openAsyncInformation(
                    Messages
                        .getString("DispatchReceiveScanDialog.notInDispatch.error.title"), //$NON-NLS-1$
                    Messages
                        .getString("DispatchReceiveScanDialog.notInDispatch.error.msg")); //$NON-NLS-1$
            try {
                currentShipment.addSpecimens(extras,
                    DispatchSpecimenState.EXTRA);
            } catch (Exception e) {
                BiobankGuiCommonPlugin
                    .openAsyncError(
                        Messages
                            .getString("DispatchReceiveScanDialog.flagging.error.title"), //$NON-NLS-1$
                        e);
            }
        }
    }

    @Override
    protected void receiveSpecimens(List<SpecimenWrapper> specimens) {
        currentShipment.receiveSpecimens(specimens);
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

}
