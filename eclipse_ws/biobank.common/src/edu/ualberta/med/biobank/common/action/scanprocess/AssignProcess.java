package edu.ualberta.med.biobank.common.action.scanprocess;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.scanprocess.data.AssignProcessData;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenIsUsedInDispatchAction;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenAssignPermission;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class AssignProcess extends ServerProcess {

    private static final long serialVersionUID = 1L;
    private AssignProcessData data;

    public AssignProcess(AssignProcessData data,
        Integer currentWorkingCenterId,
        Map<RowColPos, Cell> cells,
        boolean isRescanMode, Locale locale) {
        super(currentWorkingCenterId, cells, isRescanMode, locale);
        this.data = data;
    }

    public AssignProcess(AssignProcessData data,
        Integer currentWorkingCenterId,
        Cell cell,
        Locale locale) {
        super(currentWorkingCenterId, cell, locale);
        this.data = data;
    }

    @Override
    protected ScanProcessResult getScanProcessResult(Session session,
        Map<RowColPos, Cell> cells, boolean isRescanMode)
        throws ActionException {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells,
            internalProcessScanResult(session, cells, isRescanMode));
        return res;
    }

    protected CellStatus internalProcessScanResult(Session session,
        Map<RowColPos, Cell> cells,
        boolean rescanMode) throws ActionException {
        AssignProcessData assignData = data;
        CellStatus currentScanState = CellStatus.EMPTY;
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet = new HashMap<RowColPos, Boolean>();
        for (int row = 0; row < assignData.getPalletRowCapacity(session); row++) {
            for (int col = 0; col < assignData.getPalletColCapacity(session); col++) {
                RowColPos rcp = new RowColPos(row, col);
                Cell cell = cells.get(rcp);
                if (!rescanMode || cell == null || cell.getStatus() == null
                    || cell.getStatus() == CellStatus.EMPTY
                    || cell.getStatus() == CellStatus.ERROR
                    || cell.getStatus() == CellStatus.MISSING) {
                    Specimen expectedSpecimen = assignData
                        .getExpectedSpecimen(session, row, col);
                    if (expectedSpecimen != null) {
                        if (cell == null) {
                            cell = new Cell(rcp.getRow(), rcp.getCol(), null,
                                null);
                            cells.put(rcp, cell);
                        }
                        cell.setExpectedSpecimenId(expectedSpecimen.getId());
                    }
                    if (cell != null) {
                        internalProcessCellAssignStatus(session, cell,
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
    protected CellProcessResult getCellProcessResult(Session session, Cell cell)
        throws ActionException {
        CellProcessResult res = new CellProcessResult();
        internalProcessCellAssignStatus(session, cell, null);
        res.setResult(cell);
        return res;
    }

    /**
     * set the status of the cell
     */
    protected CellStatus internalProcessCellAssignStatus(Session session,
        Cell scanCell,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet)
        throws ActionException {
        Specimen expectedSpecimen = null;
        if (scanCell.getExpectedSpecimenId() != null) {
            expectedSpecimen = ActionUtil.sessionGet(session, Specimen.class,
                scanCell.getExpectedSpecimenId());
        }
        String value = scanCell.getValue();
        String positionString = data
            .getPalletLabel(session)
            + ContainerLabelingSchemeWrapper.rowColToSbs(new RowColPos(scanCell
                .getRow(), scanCell.getCol()));
        if (value == null) { // no specimen scanned
            updateCellAsMissing(positionString, scanCell, expectedSpecimen,
                movedAndMissingSpecimensFromPallet);
        } else {
            Specimen foundSpecimen = searchSpecimen(session, value);
            if (foundSpecimen == null) {
                updateCellAsNotFound(positionString, scanCell);
            } else if (!foundSpecimen.getCurrentCenter().getId()
                .equals(currentWorkingCenterId)) {
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
                    ContainerType cType = data
                        .getContainerType(session);
                    if (cType.getSpecimenTypeCollection().contains(
                        foundSpecimen.getSpecimenType())) {
                        if (foundSpecimen.getSpecimenPosition() != null
                            && foundSpecimen.getSpecimenPosition()
                                .getContainer() != null) { // moved ?
                            processCellWithPreviousPosition(session, scanCell,
                                positionString, foundSpecimen,
                                movedAndMissingSpecimensFromPallet);
                        } else { // new in pallet
                            if (new SpecimenIsUsedInDispatchAction(
                                foundSpecimen.getId()).run(null, session)) {
                                updateCellAsDispatchedError(
                                    positionString,
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
        Specimen missingSpecimen,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet) {
        RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
        Boolean posHasMovedSpecimen = movedAndMissingSpecimensFromPallet
            .get(rcp);
        if (!Boolean.TRUE.equals(posHasMovedSpecimen)) {
            scanCell.setStatus(CellStatus.MISSING);
            scanCell.setInformation(MessageFormat.format(Messages.getString(
                "ScanAssign.scanStatus.specimen.missing", locale), //$NON-NLS-1$
                missingSpecimen.getInventoryId()));
            scanCell.setTitle("?"); //$NON-NLS-1$
            // MISSING in {0}\: specimen {1} from visit {2} (patient {3})
            // missing
            appendNewLog(MessageFormat.format(Messages.getString(
                "ScanAssign.activitylog.specimen.missing", locale), //$NON-NLS-1$
                position, missingSpecimen.getInventoryId(), missingSpecimen
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
    private void updateCellAsNotFound(String position, Cell scanCell) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.specimen.notfound", locale));//$NON-NLS-1$
        appendNewLog(MessageFormat.format(Messages.getString(
            "ScanAssign.activitylog.specimen.notfound", locale), //$NON-NLS-1$
            position, scanCell.getValue()));
    }

    /**
     * specimen found but another specimen already at this position
     */
    private void updateCellAsPositionAlreadyTaken(String position,
        Cell scanCell, Specimen expectedSpecimen,
        Specimen foundSpecimen) {
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.specimen.positionTakenError", locale)); //$NON-NLS-1$
        scanCell.setTitle("!"); //$NON-NLS-1$
        appendNewLog(MessageFormat.format(Messages.getString(
            "ScanAssign.activitylog.specimen.positionTaken", locale), //$NON-NLS-1$
            position, expectedSpecimen.getInventoryId(), expectedSpecimen
                .getCollectionEvent().getPatient().getPnumber(), foundSpecimen
                .getInventoryId(), foundSpecimen.getCollectionEvent()
                .getPatient().getPnumber()));
    }

    /**
     * this cell has already a position. Check if it was on the pallet or not
     * 
     * @throws Exception
     */
    private void processCellWithPreviousPosition(Session session,
        Cell scanCell,
        String positionString, Specimen foundSpecimen,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet) {
        if (foundSpecimen.getSpecimenPosition() != null && foundSpecimen
            .getSpecimenPosition().getContainer().equals(
                data.getPallet(session))) {
            // same pallet
            RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
            RowColPos foundSpecPosition = new RowColPos(foundSpecimen
                .getSpecimenPosition().getRow(),
                foundSpecimen.getSpecimenPosition().getCol());
            if (!foundSpecPosition.equals(rcp)) {
                // moved inside the same pallet
                updateCellAsMoved(positionString, scanCell,
                    foundSpecimen);
                RowColPos movedFromPosition = foundSpecPosition;
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
        Specimen foundSpecimen) {
        String expectedPosition = SpecimenActionHelper.getPositionString(
            foundSpecimen, true, false);
        if (expectedPosition == null) {
            expectedPosition = "none"; //$NON-NLS-1$
        }

        scanCell.setStatus(CellStatus.MOVED);
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(MessageFormat.format(
            Messages.getString("ScanAssign.scanStatus.specimen.moved", locale), //$NON-NLS-1$
            expectedPosition));

        appendNewLog(MessageFormat
            .format(Messages.getString(
                "ScanAssign.activitylog.specimen.moved", locale), //$NON-NLS-1$
                position, scanCell.getValue(), expectedPosition));
    }

    private void updateCellAsInOtherSite(String position, Cell scanCell,
        Specimen foundSpecimen) {
        String currentPosition = SpecimenActionHelper.getPositionString(
            foundSpecimen, true, false);
        if (currentPosition == null) {
            currentPosition = "none"; //$NON-NLS-1$
        }
        String siteName = foundSpecimen.getCurrentCenter().getNameShort();
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(MessageFormat.format(Messages.getString(
            "ScanAssign.scanStatus.specimen.otherSite", locale), //$NON-NLS-1$
            siteName));

        appendNewLog(MessageFormat.format(Messages.getString(
            "ScanAssign.activitylog.specimen.otherSite", locale), //$NON-NLS-1$
            position, scanCell.getValue(), siteName, currentPosition));
    }

    private void updateCellAsTypeError(String position, Cell scanCell,
        Specimen foundSpecimen, ContainerType containerType) {
        String palletType = containerType.getName();
        String sampleType = foundSpecimen.getSpecimenType().getName();

        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(MessageFormat.format(Messages.getString(
            "ScanAssign.scanStatus.specimen.typeError", locale), //$NON-NLS-1$
            palletType, sampleType));
        appendNewLog(MessageFormat.format(Messages.getString(
            "ScanAssign.activitylog.specimen.typeError", locale), //$NON-NLS-1$
            position, palletType, sampleType));
    }

    private void updateCellAsDispatchedError(String positionString,
        Cell scanCell, Specimen foundSpecimen) {
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellStatus.ERROR);
        scanCell.setInformation(Messages.getString(
            "ScanAssign.scanStatus.specimen.dispatchedError", locale)); //$NON-NLS-1$
        appendNewLog(MessageFormat.format(Messages.getString(
            "ScanAssign.activitylog.specimen.dispatchedError", locale), //$NON-NLS-1$
            positionString));

    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new SpecimenAssignPermission(currentWorkingCenterId).isAllowed(
            user, session);
    }

}
