package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.AssignProcessData;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.ProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
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
    public CellProcessResult processCellStatus(Cell cell) throws Exception {
        CellProcessResult res = new CellProcessResult();
        res.setResult(cell, processCellAssignStatus(cell, res, null));
        return res;
    }

    @Override
    protected CellStatus internalProcessScanResult(ScanProcessResult res,
        Map<RowColPos, Cell> cells, boolean rescanMode) throws Exception {
        AssignProcessData assignData = (AssignProcessData) data;
        CellStatus currentScanState = CellStatus.EMPTY;
        // FIXME need to remember movedAndMissingAliquotsFromPallet when rescan
        // ?
        Map<RowColPos, Boolean> movedAndMissingAliquotsFromPallet = new HashMap<RowColPos, Boolean>();
        for (int row = 0; row < assignData.getPalletRowCapacity(); row++) {
            for (int col = 0; col < assignData.getPalletColCapacity(); col++) {
                RowColPos rcp = new RowColPos(row, col);
                Cell cell = cells.get(rcp);
                if (!rescanMode || cell == null || cell.getStatus() == null
                    || cell.getStatus() == CellStatus.EMPTY
                    || cell.getStatus() == CellStatus.ERROR
                    || cell.getStatus() == CellStatus.MISSING) {
                    if (assignData.getExpectedSpecimens() != null) {
                        Integer specId = assignData.getExpectedSpecimens().get(
                            rcp);
                        if (specId != null) {
                            if (cell == null) {
                                cell = new Cell(rcp.row, rcp.col, null, null);
                                cells.put(rcp, cell);
                            }
                            cell.setExpectedSpecimenId(specId);
                        }
                    }
                    if (cell != null) {
                        processCellAssignStatus(cell, res,
                            movedAndMissingAliquotsFromPallet);
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

    /**
     * set the status of the cell
     */
    private CellStatus processCellAssignStatus(Cell scanCell,
        ProcessResult res,
        Map<RowColPos, Boolean> movedAndMissingAliquotsFromPallet)
        throws Exception {
        SpecimenWrapper expectedSpecimen = null;
        if (scanCell.getExpectedSpecimenId() != null) {
            expectedSpecimen = new SpecimenWrapper(appService);
            expectedSpecimen.getWrappedObject().setId(
                scanCell.getExpectedSpecimenId());
            expectedSpecimen.reload();
        }

        String value = scanCell.getValue();
        String positionString = ((AssignProcessData) data).getPalletLabel()
            + ContainerLabelingSchemeWrapper.rowColToSbs(new RowColPos(scanCell
                .getRow(), scanCell.getCol()));
        if (value == null) { // no specimen scanned
            updateCellAsMissing(positionString, res, scanCell,
                expectedSpecimen, movedAndMissingAliquotsFromPallet);
        } else {
            // FIXME test what happen if don't have read rights on the site
            SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(
                appService, value, user);
            if (foundSpecimen == null) {
                updateCellAsNotLinked(positionString, scanCell, res);
            } else if (expectedSpecimen != null
                && !foundSpecimen.equals(expectedSpecimen)) {
                updateCellAsPositionAlreadyTaken(positionString, scanCell,
                    expectedSpecimen, foundSpecimen, res);
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
                    ContainerTypeWrapper cType = new ContainerTypeWrapper(
                        appService);
                    cType.getWrappedObject().setId(
                        ((AssignProcessData) data).getContainerTypeId());
                    cType.reload();
                    if (cType.getSpecimenTypeCollection().contains(
                        foundSpecimen.getSpecimenType())) {
                        if (foundSpecimen.hasParent()) { // moved
                            processCellWithPreviousPosition(scanCell,
                                positionString, foundSpecimen, res,
                                movedAndMissingAliquotsFromPallet);
                        } else { // new in pallet
                            if (foundSpecimen.isUsedInDispatch()) {
                                updateCellAsDispatchedError(positionString,
                                    scanCell, foundSpecimen, res);
                            } else {
                                scanCell.setStatus(CellStatus.NEW);
                                scanCell.setTitle(foundSpecimen
                                    .getCollectionEvent().getPatient()
                                    .getPnumber());
                            }
                        }
                    } else {
                        // pallet can't hold this aliquot type
                        updateCellAsTypeError(positionString, scanCell,
                            foundSpecimen, res, cType);
                    }
                }
            }
        }
        return scanCell.getStatus();
    }

    /**
     * specimen missing
     */
    private void updateCellAsMissing(String position, ProcessResult res,
        Cell scanCell, SpecimenWrapper missingAliquot,
        Map<RowColPos, Boolean> movedAndMissingAliquotsFromPallet) {
        RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
        Boolean posHasMovedSpecimen = movedAndMissingAliquotsFromPallet
            .get(rcp);
        if (!Boolean.TRUE.equals(posHasMovedSpecimen)) {
            scanCell.setStatus(CellStatus.MISSING);
            scanCell
                .setInformation(Messages
                    .getString(
                        "ScanAssign.scanStatus.aliquot.missing", missingAliquot.getInventoryId())); //$NON-NLS-1$
            scanCell.setTitle("?"); //$NON-NLS-1$
            // MISSING in {0}\: specimen {1} from visit {2} (patient {3})
            // missing
            res.appendNewLog(Messages
                .getString(
                    "ScanAssign.activitylog.aliquot.missing", position, missingAliquot //$NON-NLS-1$
                        .getInventoryId(), missingAliquot.getCollectionEvent()
                        .getVisitNumber(), missingAliquot.getCollectionEvent()
                        .getPatient().getPnumber()));
            movedAndMissingAliquotsFromPallet.put(rcp, true);
        } else {
            movedAndMissingAliquotsFromPallet.remove(rcp);
            scanCell.setStatus(CellStatus.EMPTY);
        }
    }

    /**
     * specimen not found in site (not yet linked ?)
     */
    // FIXME not linked or not added into any cevent
    private void updateCellAsNotLinked(String position, Cell scanCell,
        ProcessResult res) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.notlinked")); //$NON-NLS-1$
        res.appendNewLog(Messages
            .getString(
                "ScanAssign.activitylog.aliquot.notlinked", position, scanCell.getValue())); //$NON-NLS-1$
    }

    /**
     * specimen found but another specimen already at this position
     */
    private void updateCellAsPositionAlreadyTaken(String position,
        Cell scanCell, SpecimenWrapper expectedAliquot,
        SpecimenWrapper foundAliquot, ProcessResult res) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.positionTakenError")); //$NON-NLS-1$
        scanCell.setTitle("!"); //$NON-NLS-1$
        res.appendNewLog(Messages
            .getString(
                "ScanAssign.activitylog.aliquot.positionTaken", position, expectedAliquot //$NON-NLS-1$
                    .getInventoryId(), expectedAliquot.getCollectionEvent()
                    .getPatient().getPnumber(), foundAliquot.getInventoryId(),
                foundAliquot.getCollectionEvent().getPatient().getPnumber()));
    }

    /**
     * this cell has already a position. Check if it was on the pallet or not
     */
    private void processCellWithPreviousPosition(Cell scanCell,
        String positionString, SpecimenWrapper foundSpecimen,
        ProcessResult res,
        Map<RowColPos, Boolean> movedAndMissingAliquotsFromPallet) {
        if (foundSpecimen.getParentContainer().getSite()
            .equals(user.getCurrentWorkingSite())) {
            if (foundSpecimen.getParentContainer().getId()
                .equals(((AssignProcessData) data).getPalletId())) {
                // same pallet
                RowColPos rcp = new RowColPos(scanCell.getRow(),
                    scanCell.getCol());
                if (!foundSpecimen.getPosition().equals(rcp)) {
                    // moved inside the same pallet
                    updateCellAsMoved(positionString, scanCell, foundSpecimen,
                        res);
                    RowColPos movedFromPosition = foundSpecimen.getPosition();
                    Boolean posHasMissing = movedAndMissingAliquotsFromPallet
                        .get(movedFromPosition);
                    if (Boolean.TRUE.equals(posHasMissing)) {
                        // FIXME
                        // missing position has already been processed: remove
                        // the MISSING flag
                        // missingAliquot.setStatus(UICellStatus.EMPTY);
                        // missingAliquot.setTitle("");
                        movedAndMissingAliquotsFromPallet
                            .remove(movedFromPosition);
                    } else {
                        // missing position has not yet been processed
                        movedAndMissingAliquotsFromPallet.put(
                            movedFromPosition, true);
                    }
                }
            } else {
                // old position was on another pallet
                updateCellAsMoved(positionString, scanCell, foundSpecimen, res);
            }
        } else {
            updateCellAsInOtherSite(positionString, scanCell, foundSpecimen,
                res);
        }
    }

    private void updateCellAsMoved(String position, Cell scanCell,
        SpecimenWrapper foundAliquot, ProcessResult res) {
        String expectedPosition = foundAliquot.getPositionString(true, false);
        if (expectedPosition == null) {
            expectedPosition = "none"; //$NON-NLS-1$
        }

        scanCell.setStatus(CellStatus.MOVED);
        scanCell.setTitle(foundAliquot.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.aliquot.moved", expectedPosition)); //$NON-NLS-1$

        res.appendNewLog(Messages
            .getString(
                "ScanAssign.activitylog.aliquot.moved", position, scanCell.getValue(), //$NON-NLS-1$
                expectedPosition));
    }

    private void updateCellAsInOtherSite(String position, Cell scanCell,
        SpecimenWrapper foundAliquot, ProcessResult res) {
        String currentPosition = foundAliquot.getPositionString(true, false);
        if (currentPosition == null) {
            currentPosition = "none"; //$NON-NLS-1$
        }
        String siteName = foundAliquot.getParentContainer().getSite()
            .getNameShort();
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setTitle(foundAliquot.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.aliquot.otherSite", siteName)); //$NON-NLS-1$

        res.appendNewLog(Messages
            .getString(
                "ScanAssign.activitylog.aliquot.otherSite", position, scanCell.getValue(), //$NON-NLS-1$
                siteName, currentPosition));
    }

    private void updateCellAsTypeError(String position, Cell scanCell,
        SpecimenWrapper foundAliquot, ProcessResult res,
        ContainerTypeWrapper containerType) {
        String palletType = containerType.getName();
        String sampleType = foundAliquot.getSpecimenType().getName();

        scanCell.setTitle(foundAliquot.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.aliquot.typeError", palletType, sampleType)); //$NON-NLS-1$
        res.appendNewLog(Messages.getString(
            "ScanAssign.activitylog.aliquot.typeError", position, palletType, //$NON-NLS-1$
            sampleType));
    }

    private void updateCellAsDispatchedError(String positionString,
        Cell scanCell, SpecimenWrapper foundAliquot, ProcessResult res) {
        scanCell.setTitle(foundAliquot.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.dispatchedError")); //$NON-NLS-1$
        res.appendNewLog(Messages.getString(
            "ScanAssign.activitylog.aliquot.dispatchedError", positionString));

    }

}
