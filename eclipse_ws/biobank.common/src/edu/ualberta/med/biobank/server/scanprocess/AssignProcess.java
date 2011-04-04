package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.util.linking.Cell;
import edu.ualberta.med.biobank.common.util.linking.CellStatus;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.HashMap;
import java.util.Map;

public class AssignProcess implements ScanProcess {
    public static ScanProcessResult processScanAssignResult(
        WritableApplicationService appService, Map<RowColPos, Cell> cells,
        StringBuffer consoleLog, Map<RowColPos, Integer> expectedSpecimens,
        String palletLabel, Integer palletId, Integer containerTypeId,
        int rowCapacity, int colCapacity, boolean rescanMode, User user)
        throws Exception {
        CellStatus currentScanState = CellStatus.EMPTY;
        // FIXME need to remember when rescan ?
        Map<RowColPos, Boolean> movedAndMissingAliquotsFromPallet = new HashMap<RowColPos, Boolean>();
        for (int row = 0; row < rowCapacity; row++) {
            for (int col = 0; col < colCapacity; col++) {
                RowColPos rcp = new RowColPos(row, col);
                Cell cell = cells.get(rcp);
                if (!rescanMode || cell == null || cell.getStatus() == null
                    || cell.getStatus() == CellStatus.EMPTY
                    || cell.getStatus() == CellStatus.ERROR
                    || cell.getStatus() == CellStatus.MISSING) {
                    if (expectedSpecimens != null) {
                        Integer specId = expectedSpecimens.get(rcp);
                        if (specId != null) {
                            if (cell == null) {
                                cell = new Cell(rcp.row, rcp.col, null, null);
                                cells.put(rcp, cell);
                            }
                            cell.setExpectedSpecimenId(specId);
                        }
                    }
                    if (cell != null) {
                        processCellAssignStatus(appService, cell, palletLabel,
                            palletId, consoleLog,
                            movedAndMissingAliquotsFromPallet, containerTypeId,
                            user);
                    }
                }
                CellStatus newStatus = CellStatus.EMPTY;
                if (cell != null) {
                    newStatus = cell.getStatus();
                }
                currentScanState = currentScanState.mergeWith(newStatus);
            }
        }
        return new ScanProcessResult(cells, consoleLog.toString(),
            currentScanState);
    }

    /**
     * set the status of the cell
     */
    private static void processCellAssignStatus(
        WritableApplicationService appService, Cell scanCell,
        String palletLabel, Integer palletId, StringBuffer consoleLog,
        Map<RowColPos, Boolean> movedAndMissingAliquotsFromPallet,
        Integer containerTypeId, User user) throws Exception {
        SpecimenWrapper expectedSpecimen = null;
        if (scanCell.getExpectedSpecimenId() != null) {
            expectedSpecimen = new SpecimenWrapper(appService);
            expectedSpecimen.getWrappedObject().setId(
                scanCell.getExpectedSpecimenId());
            expectedSpecimen.reload();
        }

        String value = scanCell.getValue();
        String positionString = palletLabel
            + ContainerLabelingSchemeWrapper.rowColToSbs(new RowColPos(scanCell
                .getRow(), scanCell.getCol()));
        if (value == null) { // no specimen scanned
            updateCellAsMissing(positionString, consoleLog, scanCell,
                expectedSpecimen, movedAndMissingAliquotsFromPallet);
        } else {
            // FIXME test what happen if don't have read rights on the site
            SpecimenWrapper foundSpecimen = SpecimenWrapper.getSpecimen(
                appService, value, user);
            if (foundSpecimen == null) {
                updateCellAsNotLinked(positionString, scanCell, consoleLog);
            } else if (expectedSpecimen != null
                && !foundSpecimen.equals(expectedSpecimen)) {
                updateCellAsPositionAlreadyTaken(positionString, scanCell,
                    expectedSpecimen, foundSpecimen, consoleLog);
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
                    cType.getWrappedObject().setId(containerTypeId);
                    cType.reload();
                    if (cType.getSpecimenTypeCollection().contains(
                        foundSpecimen.getSpecimenType())) {
                        if (foundSpecimen.hasParent()) { // moved
                            processCellWithPreviousPosition(scanCell,
                                positionString, foundSpecimen, consoleLog,
                                palletId, user,
                                movedAndMissingAliquotsFromPallet);
                        } else { // new in pallet
                            if (foundSpecimen.isUsedInDispatch()) {
                                updateCellAsDispatchedError(positionString,
                                    scanCell, foundSpecimen, consoleLog);
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
                            foundSpecimen, consoleLog);
                    }
                }
            }
        }
    }

    /**
     * aliquot missing
     */
    private static void updateCellAsMissing(String position,
        StringBuffer consoleLog, Cell scanCell, SpecimenWrapper missingAliquot,
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
            consoleLog
                .append(Messages
                    .getString(
                        "ScanAssign.activitylog.aliquot.missing", position, missingAliquot //$NON-NLS-1$
                            .getInventoryId(), missingAliquot
                            .getCollectionEvent().getVisitNumber(),
                        missingAliquot.getCollectionEvent().getPatient()
                            .getPnumber()));
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
    private static void updateCellAsNotLinked(String position, Cell scanCell,
        StringBuffer consoleLog) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.notlinked")); //$NON-NLS-1$
        consoleLog
            .append(Messages
                .getString(
                    "ScanAssign.activitylog.aliquot.notlinked", position, scanCell.getValue())); //$NON-NLS-1$
    }

    /**
     * specimen found but another specimen already at this position
     */
    private static void updateCellAsPositionAlreadyTaken(String position,
        Cell scanCell, SpecimenWrapper expectedAliquot,
        SpecimenWrapper foundAliquot, StringBuffer consoleLog) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.positionTakenError")); //$NON-NLS-1$
        scanCell.setTitle("!"); //$NON-NLS-1$
        consoleLog
            .append(Messages
                .getString(
                    "ScanAssign.activitylog.aliquot.positionTaken", position, expectedAliquot //$NON-NLS-1$
                        .getInventoryId(), expectedAliquot.getCollectionEvent()
                        .getPatient().getPnumber(),
                    foundAliquot.getInventoryId(), foundAliquot
                        .getCollectionEvent().getPatient().getPnumber()));
    }

    /**
     * this cell has already a position. Check if it was on the pallet or not
     */
    private static void processCellWithPreviousPosition(Cell scanCell,
        String positionString, SpecimenWrapper foundSpecimen,
        StringBuffer consoleLog, Integer palletId, User user,
        Map<RowColPos, Boolean> movedAndMissingAliquotsFromPallet) {
        if (foundSpecimen.getParentContainer().getSite()
            .equals(user.getCurrentWorkingSite())) {
            if (foundSpecimen.getParentContainer().getId().equals(palletId)) {
                // same pallet
                RowColPos rcp = new RowColPos(scanCell.getRow(),
                    scanCell.getCol());
                if (!foundSpecimen.getPosition().equals(rcp)) {
                    // moved inside the same pallet
                    updateCellAsMoved(positionString, scanCell, foundSpecimen,
                        consoleLog);
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
                updateCellAsMoved(positionString, scanCell, foundSpecimen,
                    consoleLog);
            }
        } else {
            updateCellAsInOtherSite(positionString, scanCell, foundSpecimen,
                consoleLog);
        }
    }

    private static void updateCellAsMoved(String position, Cell scanCell,
        SpecimenWrapper foundAliquot, StringBuffer consoleLog) {
        String expectedPosition = foundAliquot.getPositionString(true, false);
        if (expectedPosition == null) {
            expectedPosition = "none"; //$NON-NLS-1$
        }

        scanCell.setStatus(CellStatus.MOVED);
        scanCell.setTitle(foundAliquot.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.aliquot.moved", expectedPosition)); //$NON-NLS-1$

        consoleLog
            .append(Messages
                .getString(
                    "ScanAssign.activitylog.aliquot.moved", position, scanCell.getValue(), //$NON-NLS-1$
                    expectedPosition));
    }

    private static void updateCellAsInOtherSite(String position, Cell scanCell,
        SpecimenWrapper foundAliquot, StringBuffer consoleLog) {
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

        consoleLog
            .append(Messages
                .getString(
                    "ScanAssign.activitylog.aliquot.otherSite", position, scanCell.getValue(), //$NON-NLS-1$
                    siteName, currentPosition));
    }

    private static void updateCellAsTypeError(String position, Cell scanCell,
        SpecimenWrapper foundAliquot, StringBuffer consoleLog) {
        String palletType = "TO BE REPLACED"; // FIXME
                                              // currentPalletWrapper.getContainerType().getName();
        String sampleType = foundAliquot.getSpecimenType().getName();

        scanCell.setTitle(foundAliquot.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.aliquot.typeError", palletType, sampleType)); //$NON-NLS-1$
        consoleLog.append(Messages.getString(
            "ScanAssign.activitylog.aliquot.typeError", position, palletType, //$NON-NLS-1$
            sampleType));
    }

    private static void updateCellAsDispatchedError(String positionString,
        Cell scanCell, SpecimenWrapper foundAliquot, StringBuffer consoleLog) {
        scanCell.setTitle(foundAliquot.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages
            .getString("ScanAssign.scanStatus.aliquot.dispatchedError")); //$NON-NLS-1$
        consoleLog.append(Messages.getString(
            "ScanAssign.activitylog.aliquot.dispatchedError", positionString));

    }
}
