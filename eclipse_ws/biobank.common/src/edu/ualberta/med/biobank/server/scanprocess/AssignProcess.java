package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.data.AssignProcessData;
import edu.ualberta.med.biobank.common.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.HashMap;
import java.util.Map;

public class AssignProcess extends ServerProcess {

    public AssignProcess(WritableApplicationService appService,
        AssignProcessData data, User user) {
        super(appService, data, user);
    }

    @Override
    protected ScanProcessResult getScanProcessResult(
        Map<RowColPos, Cell> cells, boolean isRescanMode) throws Exception {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells, internalProcessScanResult(cells, isRescanMode));
        return res;
    }

    protected CellStatus internalProcessScanResult(Map<RowColPos, Cell> cells,
        boolean rescanMode) throws Exception {
        AssignProcessData assignData = (AssignProcessData) data;
        CellStatus currentScanState = CellStatus.EMPTY;
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet = new HashMap<RowColPos, Boolean>();
        for (int row = 0; row < assignData.getPalletRowCapacity(appService); row++) {
            for (int col = 0; col < assignData.getPalletColCapacity(appService); col++) {
                RowColPos rcp = new RowColPos(row, col);
                Cell cell = cells.get(rcp);
                if (!rescanMode || cell == null || cell.getStatus() == null
                    || cell.getStatus() == CellStatus.EMPTY
                    || cell.getStatus() == CellStatus.ERROR
                    || cell.getStatus() == CellStatus.MISSING) {
                    SpecimenWrapper expectedSpecimen = assignData
                        .getExpectedSpecimen(appService, row, col);
                    if (expectedSpecimen != null) {
                        if (cell == null) {
                            cell = new Cell(rcp.row, rcp.col, null, null);
                            cells.put(rcp, cell);
                        }
                        cell.setExpectedSpecimenId(expectedSpecimen.getId());
                    }
                    if (cell != null) {
                        internalProcessCellAssignStatus(cell,
                            movedAndMissingSpecimensFromPallet);
                    }
                }
                CellStatus newStatus = CellStatus.EMPTY;
                if (cell != null) {
                    newStatus = cell.getStatus();
                }
                currentScanState = currentScanState.mergeWith(newStatus);
            }
        }
        return currentScanState;
    }

    @Override
    protected CellProcessResult getCellProcessResult(Cell cell)
        throws Exception {
        CellProcessResult res = new CellProcessResult();
        internalProcessCellAssignStatus(cell, null);
        res.setResult(cell);
        return res;
    }

    /**
     * set the status of the cell
     */
    protected CellStatus internalProcessCellAssignStatus(Cell scanCell,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet)
        throws Exception {
        SpecimenWrapper expectedSpecimen = null;
        if (scanCell.getExpectedSpecimenId() != null) {
            expectedSpecimen = new SpecimenWrapper(appService);
            expectedSpecimen.getWrappedObject().setId(
                scanCell.getExpectedSpecimenId());
            expectedSpecimen.reload();
        }

        String value = scanCell.getValue();
        String positionString = ((AssignProcessData) data)
            .getPalletLabel(appService)
            + ContainerLabelingSchemeWrapper.rowColToSbs(new RowColPos(scanCell
                .getRow(), scanCell.getCol()));
        if (value == null) { // no specimen scanned
            updateCellAsMissing(positionString, scanCell, expectedSpecimen,
                movedAndMissingSpecimensFromPallet);
        } else {
            // FIXME test what happen if don't have read rights on the site
            SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(
                appService, value, user);
            if (foundSpecimen == null) {
                updateCellAsNotLinked(positionString, scanCell);
            } else if (!foundSpecimen.getCurrentCenter().equals(
                user.getCurrentWorkingCenter())) {
                updateCellAsInOtherSite(positionString, scanCell, foundSpecimen);
            } else if (expectedSpecimen != null
                && !foundSpecimen.equals(expectedSpecimen)) {
                updateCellAsPositionAlreadyTaken(positionString, scanCell,
                    expectedSpecimen, foundSpecimen);
            } else {
                scanCell.setSpecimenId(foundSpecimen.getId());
                if (expectedSpecimen != null) {
                    // specimen scanned is already registered at this
                    // position (everything is ok !)
                    scanCell.setStatus(CellStatus.FILLED);
                    scanCell.setTitle(foundSpecimen.getCollectionEvent()
                        .getPatient().getPnumber());
                    scanCell.setSpecimenId(expectedSpecimen.getId());
                } else {
                    ContainerTypeWrapper cType = ((AssignProcessData) data)
                        .getContainerType(appService);
                    if (cType.getSpecimenTypeCollection().contains(
                        foundSpecimen.getSpecimenType())) {
                        if (foundSpecimen.hasParent()) { // moved ?
                            processCellWithPreviousPosition(scanCell,
                                positionString, foundSpecimen,
                                movedAndMissingSpecimensFromPallet);
                        } else { // new in pallet
                            if (foundSpecimen.isUsedInDispatch()) {
                                updateCellAsDispatchedError(positionString,
                                    scanCell, foundSpecimen);
                            } else {
                                scanCell.setStatus(CellStatus.NEW);
                                scanCell.setTitle(foundSpecimen
                                    .getCollectionEvent().getPatient()
                                    .getPnumber());
                            }
                        }
                    } else {
                        // pallet can't hold this specimen type
                        updateCellAsTypeError(positionString, scanCell,
                            foundSpecimen, cType);
                    }
                }
            }
        }
        return scanCell.getStatus();
    }

    /**
     * specimen missing
     */
    private void updateCellAsMissing(String position, Cell scanCell,
        SpecimenWrapper missingSpecimen,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet) {
        RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
        Boolean posHasMovedSpecimen = movedAndMissingSpecimensFromPallet
            .get(rcp);
        if (!Boolean.TRUE.equals(posHasMovedSpecimen)) {
            scanCell.setStatus(CellStatus.MISSING);
            scanCell.setInformation(Messages.getString(
                "ScanAssign.scanStatus.specimen.missing", //$NON-NLS-1$
                missingSpecimen.getInventoryId()));
            scanCell.setTitle("?"); //$NON-NLS-1$
            // MISSING in {0}\: specimen {1} from visit {2} (patient {3})
            // missing
            appendNewLog(Messages.getString(
                "ScanAssign.activitylog.specimen.missing", position, //$NON-NLS-1$
                missingSpecimen.getInventoryId(), missingSpecimen
                    .getCollectionEvent().getVisitNumber(), missingSpecimen
                    .getCollectionEvent().getPatient().getPnumber()));
            movedAndMissingSpecimensFromPallet.put(rcp, true);
        } else {
            movedAndMissingSpecimensFromPallet.remove(rcp);
            scanCell.setStatus(CellStatus.EMPTY);
        }
    }

    /**
     * specimen not found in site (not yet linked ?)
     */
    private void updateCellAsNotLinked(String position, Cell scanCell) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.specimen.notlinked"));//$NON-NLS-1$
        appendNewLog(Messages.getString(
            "ScanAssign.activitylog.specimen.notlinked", position, //$NON-NLS-1$
            scanCell.getValue()));
    }

    /**
     * specimen found but another specimen already at this position
     */
    private void updateCellAsPositionAlreadyTaken(String position,
        Cell scanCell, SpecimenWrapper expectedSpecimen,
        SpecimenWrapper foundSpecimen) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.specimen.positionTakenError")); //$NON-NLS-1$
        scanCell.setTitle("!"); //$NON-NLS-1$
        appendNewLog(Messages.getString(
            "ScanAssign.activitylog.specimen.positionTaken", position, //$NON-NLS-1$
            expectedSpecimen.getInventoryId(), expectedSpecimen
                .getCollectionEvent().getPatient().getPnumber(),
            foundSpecimen.getInventoryId(), foundSpecimen.getCollectionEvent()
                .getPatient().getPnumber()));
    }

    /**
     * this cell has already a position. Check if it was on the pallet or not
     * 
     * @throws Exception
     */
    private void processCellWithPreviousPosition(Cell scanCell,
        String positionString, SpecimenWrapper foundSpecimen,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet)
        throws Exception {
        if (foundSpecimen.getParentContainer().equals(
            ((AssignProcessData) data).getPallet(appService))) {
            // same pallet
            RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
            if (!foundSpecimen.getPosition().equals(rcp)) {
                // moved inside the same pallet
                updateCellAsMoved(positionString, scanCell, foundSpecimen);
                RowColPos movedFromPosition = foundSpecimen.getPosition();
                Boolean posHasMissing = movedAndMissingSpecimensFromPallet
                    .get(movedFromPosition);
                if (Boolean.TRUE.equals(posHasMissing)) {
                    // missing position has already been processed: remove
                    // the MISSING flag
                    // missingSpecimen.setStatus(UICellStatus.EMPTY);
                    // missingSpecimen.setTitle("");
                    movedAndMissingSpecimensFromPallet
                        .remove(movedFromPosition);
                } else {
                    // missing position has not yet been processed
                    movedAndMissingSpecimensFromPallet.put(movedFromPosition,
                        true);
                }
            }
        } else {
            // old position was on another pallet
            updateCellAsMoved(positionString, scanCell, foundSpecimen);
        }
    }

    private void updateCellAsMoved(String position, Cell scanCell,
        SpecimenWrapper foundSpecimen) {
        String expectedPosition = foundSpecimen.getPositionString(true, false);
        if (expectedPosition == null) {
            expectedPosition = "none"; //$NON-NLS-1$
        }

        scanCell.setStatus(CellStatus.MOVED);
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.specimen.moved", expectedPosition)); //$NON-NLS-1$

        appendNewLog(Messages.getString(
            "ScanAssign.activitylog.specimen.moved", //$NON-NLS-1$
            position, scanCell.getValue(), expectedPosition));
    }

    private void updateCellAsInOtherSite(String position, Cell scanCell,
        SpecimenWrapper foundSpecimen) {
        String currentPosition = foundSpecimen.getPositionString(true, false);
        if (currentPosition == null) {
            currentPosition = "none"; //$NON-NLS-1$
        }
        String siteName = foundSpecimen.getCenterString();
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.specimen.otherSite", siteName)); //$NON-NLS-1$

        appendNewLog(Messages.getString(
            "ScanAssign.activitylog.specimen.otherSite", position, //$NON-NLS-1$
            scanCell.getValue(), siteName, currentPosition));
    }

    private void updateCellAsTypeError(String position, Cell scanCell,
        SpecimenWrapper foundSpecimen, ContainerTypeWrapper containerType) {
        String palletType = containerType.getName();
        String sampleType = foundSpecimen.getSpecimenType().getName();

        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell
            .setInformation(Messages
                .getString(
                    "ScanAssign.scanStatus.specimen.typeError", palletType, sampleType)); //$NON-NLS-1$
        appendNewLog(Messages.getString(
            "ScanAssign.activitylog.specimen.typeError", position, palletType, //$NON-NLS-1$
            sampleType));
    }

    private void updateCellAsDispatchedError(String positionString,
        Cell scanCell, SpecimenWrapper foundSpecimen) {
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.specimen.dispatchedError")); //$NON-NLS-1$
        appendNewLog(Messages.getString(
            "ScanAssign.activitylog.specimen.dispatchedError", positionString)); //$NON-NLS-1$

    }

}
