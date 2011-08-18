package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.scanprocess.data.ShipmentProcessData;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class RequestReceiveScanDialog extends ReceiveScanDialog<RequestWrapper> {

    private List<SpecimenWrapper> dispatchSpecimens;

    public RequestReceiveScanDialog(Shell parentShell,
        final RequestWrapper currentShipment, CenterWrapper<?> centerWrapper) {
        super(parentShell, currentShipment, centerWrapper);
        dispatchSpecimens = new ArrayList<SpecimenWrapper>();
    }

    @Override
    protected ProcessData getProcessData() {
        return new ShipmentProcessData(null, currentShipment, false, false);
    }

    @Override
    protected void addExtraCells() {
        if (extras != null && extras.size() > 0) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    BgcPlugin.openInformation(
                        Messages.RequestReceiveScanDialog_extra_title,
                        Messages.RequestReceiveScanDialog_extra_msg);
                }
            });
        }
    }

    @Override
    protected void receiveSpecimens(List<SpecimenWrapper> specimens) {
        try {
            dispatchSpecimens.addAll(specimens);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.RequestReceiveScanDialog_receiveError_title, e);
        }
    }

    @Override
    protected List<UICellStatus> getPalletCellStatus() {
        return UICellStatus.REQUEST_PALLET_STATUS_LIST;
    }

    @Override
    protected Map<RowColPos, PalletCell> getFakeScanCells() {
        Map<RowColPos, PalletCell> palletScanned = new TreeMap<RowColPos, PalletCell>();
        if ((currentShipment).getRequestSpecimenCollection(false).size() > 0) {
            int i = 0;
            for (RequestSpecimenWrapper dsa : (currentShipment)
                .getRequestSpecimenCollection(false)) {
                int row = i / 12;
                int col = i % 12;
                if (row > 7)
                    break;
                if (!RequestSpecimenState.UNAVAILABLE_STATE.isEquals(dsa
                    .getState()))
                    palletScanned.put(new RowColPos(row, col), new PalletCell(
                        new ScanCell(row, col, dsa.getSpecimen()
                            .getInventoryId())));
                i++;
            }
        }
        return palletScanned;
    }

    public List<SpecimenWrapper> getSpecimens() {
        return dispatchSpecimens;
    }

}
