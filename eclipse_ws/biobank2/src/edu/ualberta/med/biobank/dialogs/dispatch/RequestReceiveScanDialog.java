package edu.ualberta.med.biobank.dialogs.dispatch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.scanprocess.data.ProcessData;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.DispatchReceivingEntryForm.SpecimenInfo;
import edu.ualberta.med.biobank.forms.RequestEntryFormBase;
import edu.ualberta.med.biobank.widgets.grids.cell.PalletCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.scannerconfig.dmscanlib.ScanCell;

public class RequestReceiveScanDialog extends
    AbstractScanDialog<RequestWrapper> {

    private static final String TITLE = "Scanning received pallets";

    private int pendingSpecimensNumber = 0;

    private boolean specimensReceived = false;

    private int errors;

    public RequestReceiveScanDialog(Shell parentShell,
        final RequestWrapper currentShipment, CenterWrapper<?> centerWrapper) {
        super(parentShell, currentShipment, centerWrapper);
    }

    @Override
    protected String getTitleAreaMessage() {
        return "Scan one pallet.";
    }

    @Override
    protected String getTitleAreaTitle() {
        return TITLE;
    }

    @Override
    protected String getDialogShellTitle() {
        return TITLE;
    }

    /**
     * set the status of the cell. return the specimen if it is an extra one.
     */
    protected void processCellStatus(PalletCell cell) {
        SpecimenInfo info = RequestEntryFormBase.getInfoForInventoryId(
            currentShipment, cell.getValue());
        if (info.specimen != null) {
            cell.setSpecimen(info.specimen);
            cell.setTitle(info.specimen.getCollectionEvent().getPatient()
                .getPnumber());
        }
        switch (info.type) {
        case RECEIVED:
            cell.setStatus(UICellStatus.IN_SHIPMENT_RECEIVED);
            break;
        case DUPLICATE:
            cell.setStatus(UICellStatus.ERROR);
            cell.setInformation("Found more than one specimen with inventoryId "
                + cell.getValue());
            cell.setTitle("!");
            errors++;
            break;
        case NOT_IN_DB:
            cell.setStatus(UICellStatus.ERROR);
            cell.setInformation("Specimen " + cell.getValue()
                + " not found in database");
            cell.setTitle("!");
            errors++;
            break;
        case NOT_IN_SHIPMENT:
            cell.setStatus(UICellStatus.EXTRA);
            cell.setInformation("Specimen should not be in shipment");
            pendingSpecimensNumber++;
            break;
        case OK:
            cell.setStatus(UICellStatus.IN_SHIPMENT_EXPECTED);
            pendingSpecimensNumber++;
            break;
        case EXTRA:
            cell.setStatus(UICellStatus.EXTRA);
            pendingSpecimensNumber++;
            break;
        }
    }

    @Override
    protected void processScanResult(IProgressMonitor monitor,
        CenterWrapper<?> site) throws Exception {
        Map<RowColPos, PalletCell> cells = getCells();
        if (cells != null) {
            processCells(cells.keySet(), monitor);
        }
    }

    private void processCells(Collection<RowColPos> rcps,
        IProgressMonitor monitor) {
        pendingSpecimensNumber = 0;
        errors = 0;
        Map<RowColPos, PalletCell> cells = getCells();
        if (cells != null) {
            setScanOkValue(false);
            List<SpecimenWrapper> newExtraSpecimens = new ArrayList<SpecimenWrapper>();
            for (RowColPos rcp : rcps) {
                if (monitor != null) {
                    monitor.subTask("Processing position "
                        + ContainerLabelingSchemeWrapper.rowColToSbs(rcp));
                }
                PalletCell cell = cells.get(rcp);
                processCellStatus(cell);
                if (cell.getStatus() == UICellStatus.EXTRA) {
                    newExtraSpecimens.add(cell.getSpecimen());
                }
            }
            addExtraCells(newExtraSpecimens);
            setScanOkValue(errors == 0);
        }
    }

    private void addExtraCells(final List<SpecimenWrapper> extraSpecimens) {
        if (extraSpecimens.size() > 0) {
            Display.getDefault().asyncExec(new Runnable() {
                @Override
                public void run() {
                    BiobankPlugin.openInformation("Extra specimens",
                        "Some of the specimens in this pallet were not supposed"
                            + " to be in this shipment.");
                }
            });
        }
    }

    @Override
    protected String getProceedButtonlabel() {
        return "Accept specimens";
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
        try {
            (currentShipment).receiveSpecimens(specimens);
            redrawPallet();
            pendingSpecimensNumber = 0;
            setOkButtonEnabled(true);
            specimensReceived = true;
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Error receiving specimens", e);
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

    public boolean hasReceivedSpecimens() {
        return specimensReceived;
    }

    @Override
    protected void postprocessScanTubeAlone(PalletCell cell) throws Exception {
        processCells(Arrays.asList(cell.getRowColPos()), null);
        super.postprocessScanTubeAlone(cell);
    }

    @Override
    protected ProcessData getProcessData() {
        // TODO Auto-generated method stub
        return null;
    }
}
