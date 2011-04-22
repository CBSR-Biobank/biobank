package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.data.ShipmentProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.ItemState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Map;

public class DispatchCreateProcess extends ServerProcess {

    public DispatchCreateProcess(WritableApplicationService appService,
        ShipmentProcessData data, User user) {
        super(appService, data, user);
    }

    /**
     * Process of a map of cells
     */
    @Override
    protected ScanProcessResult getScanProcessResult(
        Map<RowColPos, Cell> cells, boolean isRescanMode) throws Exception {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells, createProcess(cells));
        return res;
    }

    /**
     * Process of only one cell
     */
    @Override
    protected CellProcessResult getCellProcessResult(Cell cell)
        throws Exception {
        CellProcessResult res = new CellProcessResult();
        ShipmentProcessData dispatchData = (ShipmentProcessData) data;
        CenterWrapper<?> sender = null;
        if (dispatchData.getSenderId() != null) {
            sender = CenterWrapper.getCenterFromId(appService,
                dispatchData.getSenderId());
        }
        processCellDipatchCreateStatus(cell, sender,
            dispatchData.isErrorIfAlreadyAdded());
        res.setResult(cell);
        return res;
    }

    /**
     * Processing for the create mode
     * 
     * @param cells
     * @return
     * @throws Exception
     */
    private CellStatus createProcess(Map<RowColPos, Cell> cells)
        throws Exception {
        CellStatus currentScanState = CellStatus.EMPTY;
        ShipmentProcessData dispatchData = (ShipmentProcessData) data;
        CenterWrapper<?> sender = null;
        if (dispatchData.getSenderId() != null) {
            sender = CenterWrapper.getCenterFromId(appService,
                dispatchData.getSenderId());
        }
        if (dispatchData.getPallet(appService) == null) {
            for (Cell cell : cells.values()) {
                processCellDipatchCreateStatus(cell, sender, false);
                currentScanState = currentScanState.mergeWith(cell.getStatus());
            }
        } else {
            for (int row = 0; row < dispatchData.getPallet(appService)
                .getRowCapacity(); row++) {
                for (int col = 0; col < dispatchData.getPallet(appService)
                    .getColCapacity(); col++) {
                    RowColPos rcp = new RowColPos(row, col);
                    Cell cell = cells.get(rcp);
                    SpecimenWrapper expectedSpecimen = dispatchData
                        .getPallet(appService).getSpecimens().get(rcp);
                    if (expectedSpecimen != null) {
                        if (cell == null) {
                            cell = new Cell(row, col, null, null);
                            cells.put(rcp, cell);
                        }
                        cell.setExpectedSpecimenId(expectedSpecimen.getId());
                    }
                    if (cell != null) {
                        processCellDipatchCreateStatus(cell, sender, false);
                        currentScanState = currentScanState.mergeWith(cell
                            .getStatus());
                    }
                }
            }
        }
        return currentScanState;
    }

    /**
     * Process one cell for create mode param checkAlreadyAdded if set to true,
     * will set the Cell as error if is already added, otherwise the status will
     * only be 'already added' (this status is used while scanning: the color
     * will be different)
     */
    private CellStatus processCellDipatchCreateStatus(Cell scanCell,
        CenterWrapper<?> sender, boolean checkAlreadyAdded) throws Exception {
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
                "ScanAssign.scanStatus.specimen.missing", //$NON-NLS-1$
                expectedSpecimen.getInventoryId()));
            scanCell.setTitle("?"); //$NON-NLS-1$
        } else {
            SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(
                appService, value, user);
            if (foundSpecimen == null) {
                // not in database
                scanCell.setStatus(CellStatus.ERROR);
                scanCell.setInformation(Messages
                    .getString("ScanAssign.scanStatus.specimen.notlinked")); //$NON-NLS-1$
            } else {
                if (expectedSpecimen != null
                    && !foundSpecimen.equals(expectedSpecimen)) {
                    // Position taken
                    scanCell.setStatus(CellStatus.ERROR);
                    scanCell
                        .setInformation(Messages
                            .getString("ScanAssign.scanStatus.specimen.positionTakenError")); //$NON-NLS-1$
                    scanCell.setTitle("!"); //$NON-NLS-1$
                } else {
                    scanCell.setSpecimenId(foundSpecimen.getId());
                    if (expectedSpecimen != null
                        || ((ShipmentProcessData) data).getPallet(appService) == null) {
                        checkCanAddSpecimen(scanCell, foundSpecimen, sender,
                            checkAlreadyAdded);
                    } else {
                        // should not be there
                        scanCell.setStatus(CellStatus.ERROR);
                        scanCell.setTitle(foundSpecimen.getCollectionEvent()
                            .getPatient().getPnumber());
                        scanCell
                            .setInformation(Messages
                                .getString("DispatchProcess.create.specimen.anotherPallet")); //$NON-NLS-1$
                    }
                }
            }
        }
        return scanCell.getStatus();
    }

    /**
     * Check at creation
     * 
     * @param cell
     * @param specimen
     * @param sender
     * @param checkAlreadyAdded
     * @throws Exception
     */
    private void checkCanAddSpecimen(Cell cell, SpecimenWrapper specimen,
        CenterWrapper<?> sender, boolean checkAlreadyAdded) throws Exception {
        specimen.reload();
        if (specimen.isNew()) {
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation(""); //$NON-NLS-1$
        } else if (!specimen.isActive()) {
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation(Messages.getString(
                "DispatchProcess.create.specimen.status", //$NON-NLS-1$
                specimen.getInventoryId()));
        } else if (!specimen.getCurrentCenter().equals(sender)) {
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation(Messages.getString(
                "DispatchProcess.create.specimen.currentCenter", specimen //$NON-NLS-1$
                    .getInventoryId(), specimen.getCurrentCenter()
                    .getNameShort(), sender.getNameShort()));
        } else {
            Map<Integer, ItemState> currentSpecimenIds = ((ShipmentProcessData) data)
                .getCurrentDispatchSpecimenIds();
            boolean alreadyInShipment = currentSpecimenIds != null
                && currentSpecimenIds.get(specimen.getId()) != null;
            if (checkAlreadyAdded && alreadyInShipment) {
                cell.setStatus(CellStatus.ERROR);
                cell.setInformation(Messages.getString(
                    "DispatchProcess.create.specimen.alreadyAdded", //$NON-NLS-1$
                    specimen.getInventoryId()));
            } else if (specimen.isUsedInDispatch()) {
                cell.setStatus(CellStatus.ERROR);
                cell.setInformation(Messages.getString(
                    "DispatchProcess.create.specime.inNotClosedDispatch", //$NON-NLS-1$
                    specimen.getInventoryId()));
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
