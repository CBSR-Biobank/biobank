package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.DispatchProcessData;
import edu.ualberta.med.biobank.common.scanprocess.ReceiveDispatchResult;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DispatchProcess extends ServerProcess {

    /**
     * used when receiving
     */
    private int pendingSpecimenNumber = 0;

    /**
     * used when receiving
     */
    List<Integer> extraSpecimens = null;

    public DispatchProcess(WritableApplicationService appService,
        DispatchProcessData data, User user) {
        super(appService, data, user);
    }

    @Override
    protected ScanProcessResult getScanProcessResult(
        Map<RowColPos, Cell> cells, boolean isRescanMode) throws Exception {
        ReceiveDispatchResult res = new ReceiveDispatchResult();
        res.setResult(cells, internalProcessScanResult(cells), extraSpecimens);
        return res;
    }

    private CellStatus internalProcessScanResult(Map<RowColPos, Cell> cells)
        throws Exception {
        DispatchProcessData dispatchData = (DispatchProcessData) data;
        if (dispatchData.isCreation())
            return createProcess(cells);
        else
            return receiveProcess(cells);
    }

    private CellStatus receiveProcess(Map<RowColPos, Cell> cells)
        throws Exception {
        pendingSpecimenNumber = 0;
        CellStatus currentScanState = CellStatus.EMPTY;
        if (cells != null) {
            extraSpecimens = new ArrayList<Integer>();
            for (Cell cell : cells.values()) {
                processCellDipatchReceiveStatus(cell);
                currentScanState = currentScanState.mergeWith(cell.getStatus());
            }
        }
        return currentScanState;
    }

    private void updateCellWithSpecimen(Cell cell, SpecimenWrapper specimen) {
        cell.setSpecimenId(specimen.getId());
        cell.setTitle(specimen.getCollectionEvent().getPatient().getPnumber());
    }

    protected void processCellDipatchReceiveStatus(Cell cell) throws Exception {
        SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(appService,
            cell.getValue(), user);
        DispatchSpecimenState state = ((DispatchProcessData) data)
            .getCurrentDispatchSpecimenIds().get(foundSpecimen.getId());
        if (state == null) {
            // specimen not in shipment. Check if exists in DB:
            SpecimenWrapper specimen = null;
            specimen = SpecimenWrapper.getSpecimen(appService, cell.getValue(),
                user);
            if (specimen == null) {
                cell.setStatus(CellStatus.ERROR);
                cell.setInformation(Messages
                    .getString(
                        "DispatchReceiveScanDialog.cell.notInDb.msg", cell.getValue())); //$NON-NLS-1$
                cell.setTitle("!"); //$NON-NLS-1$
            } else {
                cell.setStatus(CellStatus.EXTRA);
                cell.setInformation(Messages
                    .getString("DispatchReceiveScanDialog.cell.notInShipment.msg")); //$NON-NLS-1$
                pendingSpecimenNumber++;
            }
        } else {
            if (DispatchSpecimenState.RECEIVED == state) {
                updateCellWithSpecimen(cell, foundSpecimen);
                cell.setStatus(CellStatus.IN_SHIPMENT_RECEIVED);
            } else if (DispatchSpecimenState.EXTRA == state) {
                updateCellWithSpecimen(cell, foundSpecimen);
                extraSpecimens.add(foundSpecimen.getId());
                cell.setStatus(CellStatus.EXTRA);
            } else {
                updateCellWithSpecimen(cell, foundSpecimen);
                cell.setStatus(CellStatus.IN_SHIPMENT_EXPECTED);
            }
        }
    }

    private CellStatus createProcess(Map<RowColPos, Cell> cells)
        throws Exception {
        CellStatus currentScanState = CellStatus.EMPTY;
        DispatchProcessData dispatchData = (DispatchProcessData) data;
        CenterWrapper<?> sender = null;
        if (dispatchData.getSenderId() != null) {
            sender = CenterWrapper.getCenterFromId(appService,
                dispatchData.getSenderId());
        }
        if (dispatchData.getPalletId() == null) {
            for (Cell cell : cells.values()) {
                processCellDipatchCreateStatus(cell, sender);
                currentScanState = currentScanState.mergeWith(cell.getStatus());
            }
        } else {
            for (int row = 0; row < dispatchData.getPalletRowCapacity(); row++) {
                for (int col = 0; col < dispatchData.getPalletColCapacity(); col++) {
                    RowColPos rcp = new RowColPos(row, col);
                    Cell cell = cells.get(rcp);
                    Integer expectedSpecimenId = dispatchData
                        .getExpectedSpecimens().get(rcp);
                    if (expectedSpecimenId != null) {
                        if (cell == null) {
                            cell = new Cell(row, col, null, null);
                            cells.put(rcp, cell);
                        }
                        cell.setExpectedSpecimenId(expectedSpecimenId);
                    }
                    processCellDipatchCreateStatus(cell, sender);
                    if (cell != null)
                        currentScanState = currentScanState.mergeWith(cell
                            .getStatus());
                }
            }
        }
        return currentScanState;
    }

    @Override
    protected CellProcessResult getCellProcessResult(Cell cell)
        throws Exception {
        CellProcessResult res = new CellProcessResult();
        DispatchProcessData dispatchData = (DispatchProcessData) data;
        CenterWrapper<?> sender = null;
        if (dispatchData.getSenderId() != null) {
            sender = CenterWrapper.getCenterFromId(appService,
                dispatchData.getSenderId());
        }
        processCellDipatchCreateStatus(cell, sender);
        res.setResult(cell);
        return res;
    }

    protected CellStatus processCellDipatchCreateStatus(Cell scanCell,
        CenterWrapper<?> sender) throws Exception {
        SpecimenWrapper expectedSpecimen = null;
        if (scanCell.getExpectedSpecimenId() != null) {
            expectedSpecimen = new SpecimenWrapper(appService);
            expectedSpecimen.getWrappedObject().setId(
                scanCell.getExpectedSpecimenId());
            expectedSpecimen.reload();
        }
        String value = scanCell.getValue();
        if (value == null) { // no specimen scanned
            scanCell.setStatus(CellStatus.MISSING);
            scanCell.setInformation(Messages.getString(
                "ScanAssign.scanStatus.aliquot.missing", //$NON-NLS-1$
                expectedSpecimen.getInventoryId()));
            scanCell.setTitle("?"); //$NON-NLS-1$
        } else {
            SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(
                appService, value, user);
            if (foundSpecimen == null) {
                // not in database
                scanCell.setStatus(CellStatus.ERROR);
                scanCell.setInformation(Messages
                    .getString("ScanAssign.scanStatus.aliquot.notlinked")); //$NON-NLS-1$
            } else {
                if (expectedSpecimen != null
                    && !foundSpecimen.equals(expectedSpecimen)) {
                    // Position taken
                    scanCell.setStatus(CellStatus.ERROR);
                    scanCell
                        .setInformation(Messages
                            .getString("ScanAssign.scanStatus.aliquot.positionTakenError")); //$NON-NLS-1$
                    scanCell.setTitle("!"); //$NON-NLS-1$
                } else {
                    scanCell.setSpecimenId(foundSpecimen.getId());
                    if (expectedSpecimen != null
                        || ((DispatchProcessData) data).getPalletId() == null) {
                        checkCanAddSpecimen(scanCell, foundSpecimen, sender,
                            false);
                    } else {
                        // should not be there
                        scanCell.setStatus(CellStatus.ERROR);
                        scanCell.setTitle(foundSpecimen.getCollectionEvent()
                            .getPatient().getPnumber());
                        scanCell
                            .setInformation("This aliquot should be on another pallet"); //$NON-NLS-1$
                    }
                }
            }
        }
        return scanCell.getStatus();
    }

    private void checkCanAddSpecimen(Cell cell, SpecimenWrapper specimen,
        CenterWrapper<?> sender, boolean checkAlreadyAdded) {
        if (specimen.isNew()) {
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation("Cannot add specimen "
                + specimen.getInventoryId() + ": it has not already been saved");
        } else if (!specimen.isActive()) {
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation("Activity status of "
                + specimen.getInventoryId() + " is not 'Active'."
                + " Check comments on this aliquot for more information.");
        } else if (!specimen.getCurrentCenter().equals(sender)) {
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation("Specimen " + specimen.getInventoryId()
                + " is currently assigned to site "
                + specimen.getCurrentCenter().getNameShort()
                + ". It should be first assigned to " + sender.getNameShort()
                + " site.");
        } else {
            Map<Integer, DispatchSpecimenState> currentSpecimenIds = ((DispatchProcessData) data)
                .getCurrentDispatchSpecimenIds();
            boolean alreadyInShipment = currentSpecimenIds != null
                && currentSpecimenIds.get(specimen.getId()) != null;
            if (checkAlreadyAdded && alreadyInShipment) {
                cell.setStatus(CellStatus.ERROR);
                cell.setInformation(specimen.getInventoryId()
                    + " is already in this Dispatch.");
            } else if (specimen.isUsedInDispatch()) {
                cell.setStatus(CellStatus.ERROR);
                cell.setInformation(specimen.getInventoryId()
                    + " is already in a Dispatch in-transit or in creation.");
            } else {
                if (alreadyInShipment)
                    cell.setStatus(CellStatus.IN_SHIPMENT_ADDED);
                else
                    cell.setStatus(CellStatus.FILLED);
                cell.setTitle(specimen.getCollectionEvent().getPatient()
                    .getPnumber());
                cell.setSpecimenId(specimen.getId());
            }
        }
    }

}
