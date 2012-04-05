package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.ShipmentReceiveProcessAction;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ProcessResult;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.util.RowColPos;
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
    protected Action<ProcessResult> getCellProcessAction(Integer centerId,
        CellInfo cell, Locale locale) {
        return new ShipmentReceiveProcessAction(getProcessData(), centerId, cell,
            locale);
    }

    @Override
    protected Action<ProcessResult> getPalletProcessAction(
        Integer centerId, Map<RowColPos, CellInfo> cells, boolean isRescanMode,
        Locale locale) {
        return new ShipmentReceiveProcessAction(getProcessData(), centerId, cells,
            isRescanMode, locale);
    }

    protected ShipmentProcessInfo getProcessData() {
        return new ShipmentProcessInfo(null, currentShipment, false);
    }

    @Override
    protected void addExtraCells() {
        if (extras != null && extras.size() > 0) {
            BgcPlugin.openAsyncInformation(
                "Specimens not in dispatch",
                "Some of the specimens in this pallet were not supposed  to be in this shipment. They will be added to the extra-pending list.");
            try {
                currentShipment.addSpecimens(extras,
                    DispatchSpecimenState.EXTRA);
            } catch (Exception e) {
                BgcPlugin.openAsyncError(
                    "Error flagging specimens", e);
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
        if (currentShipment.getDispatchSpecimenCollection(false).size() > 0) {
            int i = 0;
            do {
                DispatchSpecimenWrapper dsa = currentShipment
                    .getDispatchSpecimenCollection(false).get(i);
                int row = i / 12;
                int col = i % 12;
                if (!DispatchSpecimenState.MISSING.isEquals(dsa.getState()))
                    palletScanned.put(new RowColPos(row, col), new PalletCell(
                        new ScanCell(row, col, dsa.getSpecimen()
                            .getInventoryId())));
                i++;
            } while (i < (8 * 12 - 1)
                && i < currentShipment.getDispatchSpecimenCollection(false)
                    .size());

            palletScanned.put(new RowColPos(6, 6), new PalletCell(new ScanCell(
                6, 6, "aaah")));
        }
        return palletScanned;
    }
}
