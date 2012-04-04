package edu.ualberta.med.biobank.common.action.scanprocess;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.scanprocess.data.AssignProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenIsUsedInDispatchAction;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenAssignPermission;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenAssignProcessAction extends ServerProcessAction {

    private static final long serialVersionUID = 1L;
    private AssignProcessInfo data;

    // multiple cells assign process
    public SpecimenAssignProcessAction(AssignProcessInfo data,
        Integer currentWorkingCenterId,
        Map<RowColPos, CellInfo> cells,
        boolean isRescanMode, Locale locale) {
        super(currentWorkingCenterId, cells, isRescanMode, locale);
        this.data = data;
    }

    // single cell assign process
    public SpecimenAssignProcessAction(AssignProcessInfo data,
        Integer currentWorkingCenterId,
        CellInfo cell,
        Locale locale) {
        super(currentWorkingCenterId, cell, locale);
        this.data = data;
    }

    @Override
    protected ScanProcessResult getScanProcessResult(
        Map<RowColPos, CellInfo> cells, boolean isRescanMode)
        throws ActionException {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells,
            internalProcessScanResult(session, cells, isRescanMode));
        return res;
    }

    protected CellInfoStatus internalProcessScanResult(Session session,
        Map<RowColPos, CellInfo> cells,
        boolean rescanMode) throws ActionException {
        AssignProcessInfo assignData = data;
        CellInfoStatus currentScanState = CellInfoStatus.EMPTY;
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet =
            new HashMap<RowColPos, Boolean>();
        for (int row = 0; row < assignData.getPalletRowCapacity(actionContext); row++) {
            for (int col = 0; col < assignData
                .getPalletColCapacity(actionContext); col++) {
                RowColPos rcp = new RowColPos(row, col);
                CellInfo cell = cells.get(rcp);
                if (!rescanMode || cell == null || cell.getStatus() == null
                    || cell.getStatus() == CellInfoStatus.EMPTY
                    || cell.getStatus() == CellInfoStatus.ERROR
                    || cell.getStatus() == CellInfoStatus.MISSING) {
                    Specimen expectedSpecimen = assignData
                        .getExpectedSpecimen(session, row, col);
                    if (expectedSpecimen != null) {
                        if (cell == null) {
                            cell =
                                new CellInfo(rcp.getRow(), rcp.getCol(), null,
                                    null);
                            cells.put(rcp, cell);
                        }
                        cell.setExpectedSpecimenId(expectedSpecimen.getId());
                    }
                    if (cell != null) {
                        internalProcessCellAssignStatus(cell,
                            movedAndMissingSpecimensFromPallet);
                    }
                }
                CellInfoStatus newStatus = CellInfoStatus.EMPTY;
                if (cell != null) {
                    newStatus = cell.getStatus();
                }
                currentScanState = currentScanState.mergeWith(newStatus);
            }
        }
        return currentScanState;
    }

    @Override
    protected CellProcessResult getCellProcessResult(CellInfo cell)
        throws ActionException {
        CellProcessResult res = new CellProcessResult();
        internalProcessCellAssignStatus(cell, null);
        res.setResult(cell);
        return res;
    }

    /**
     * set the status of the cell
     */
    protected CellInfoStatus internalProcessCellAssignStatus(CellInfo scanCell,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet)
        throws ActionException {
        Specimen expectedSpecimen = null;
        if (scanCell.getExpectedSpecimenId() != null) {
            expectedSpecimen =
                actionContext.load(Specimen.class,
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
                    scanCell.setStatus(CellInfoStatus.FILLED);
                    scanCell.setTitle(foundSpecimen.getCollectionEvent()
                        .getPatient().getPnumber());
                    scanCell.setSpecimenId(expectedSpecimen.getId());
                } else {
                    ContainerType cType =
                        data.getContainerType(session, actionContext);
                    if (cType.getSpecimenTypes().contains(
                        foundSpecimen.getSpecimenType())) {
                        if (foundSpecimen.getSpecimenPosition() != null
                            && foundSpecimen.getSpecimenPosition()
                                .getContainer() != null) { // moved ?
                            processCellWithPreviousPosition(session, scanCell,
                                positionString, foundSpecimen,
                                movedAndMissingSpecimensFromPallet);
                        } else { // new in pallet
                            if (new SpecimenIsUsedInDispatchAction(
                                foundSpecimen.getId()).run(actionContext)
                                .isTrue()) {
                                updateCellAsDispatchedError(
                                    positionString,
                                    scanCell, foundSpecimen);
                            } else {
                                scanCell.setStatus(CellInfoStatus.NEW);
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
    private void updateCellAsMissing(String position, CellInfo scanCell,
        Specimen missingSpecimen,
        Map<RowColPos, Boolean> movedAndMissingSpecimensFromPallet) {
        RowColPos rcp = new RowColPos(scanCell.getRow(), scanCell.getCol());
        Boolean posHasMovedSpecimen = movedAndMissingSpecimensFromPallet
            .get(rcp);
        if (!Boolean.TRUE.equals(posHasMovedSpecimen)) {
            scanCell.setStatus(CellInfoStatus.MISSING);
            scanCell.setInformation(MessageFormat.format("", 
                missingSpecimen.getInventoryId()));
            scanCell.setTitle("?"); 
            // MISSING in {0}\: specimen {1} from visit {2} (patient {3})
            // missing
            appendNewLog(MessageFormat.format("MISSING in {0}: specimen ''{1}'' from visit {2} (patient {3}) missing", 
                position, missingSpecimen.getInventoryId(), missingSpecimen
                    .getCollectionEvent().getVisitNumber(), missingSpecimen
                    .getCollectionEvent().getPatient().getPnumber()));
            movedAndMissingSpecimensFromPallet.put(rcp, true);
        } else {
            movedAndMissingSpecimensFromPallet.remove(rcp);
            scanCell.setStatus(CellInfoStatus.EMPTY);
        }
    }

    /**
     * specimen not found in site (not yet linked ?)
     */
    private void updateCellAsNotFound(String position, CellInfo scanCell) {
        scanCell.setStatus(CellInfoStatus.ERROR);
        scanCell.setInformation("Specimen not found");
        appendNewLog(MessageFormat.format("ERROR in {0}: specimen ''{1}'' not found in the database", 
            position, scanCell.getValue()));
    }

    /**
     * specimen found but another specimen already at this position
     */
    private void updateCellAsPositionAlreadyTaken(String position,
        CellInfo scanCell, Specimen expectedSpecimen,
        Specimen foundSpecimen) {
        scanCell.setStatus(CellInfoStatus.ERROR);
        scanCell.setInformation(""); 
        scanCell.setTitle("!"); 
        appendNewLog(MessageFormat.format("ERROR in {0}: Expected inventoryId {1} from patient {2} -- Found inventoryId {3} from patient {4}", 
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
        CellInfo scanCell,
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

    private void updateCellAsMoved(String position, CellInfo scanCell,
        Specimen foundSpecimen) {
        String expectedPosition = SpecimenActionHelper.getPositionString(
            foundSpecimen, true, false);
        if (expectedPosition == null) {
            expectedPosition = "none"; 
        }

        scanCell.setStatus(CellInfoStatus.MOVED);
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(MessageFormat.format(
            "Specimen previously registered on another position: {0}", 
            expectedPosition));

        appendNewLog(MessageFormat
            .format("MOVED in {0}: specimen ''{1}'' previously registered on another position: {2}", 
                position, scanCell.getValue(), expectedPosition));
    }

    private void updateCellAsInOtherSite(String position, CellInfo scanCell,
        Specimen foundSpecimen) {
        String currentPosition = SpecimenActionHelper.getPositionString(
            foundSpecimen, true, false);
        if (currentPosition == null) {
            currentPosition = "none"; 
        }
        String siteName = foundSpecimen.getCurrentCenter().getNameShort();
        scanCell.setStatus(CellInfoStatus.ERROR);
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setInformation(MessageFormat.format("Specimen has a position in another site (site {0})", 
            siteName));

        appendNewLog(MessageFormat.format("ERROR in {0}: specimen ''{1}'' registered in another site ({2}) in position: {3}", 
            position, scanCell.getValue(), siteName, currentPosition));
    }

    private void updateCellAsTypeError(String position, CellInfo scanCell,
        Specimen foundSpecimen, ContainerType containerType) {
        String palletType = containerType.getName();
        String sampleType = foundSpecimen.getSpecimenType().getName();

        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellInfoStatus.ERROR);
        scanCell.setInformation(MessageFormat.format("This pallet type {0} can''t hold a specimen of type {1}", 
            palletType, sampleType));
        appendNewLog(MessageFormat.format("ERROR in {0}: this pallet type {1} can''t hold a specimen of type {2}", 
            position, palletType, sampleType));
    }

    private void updateCellAsDispatchedError(String positionString,
        CellInfo scanCell, Specimen foundSpecimen) {
        scanCell.setTitle(foundSpecimen.getCollectionEvent().getPatient()
            .getPnumber());
        scanCell.setStatus(CellInfoStatus.ERROR);
        scanCell.setInformation("Cannot assign position to a specimen that in dispatch transit"); 
        appendNewLog(MessageFormat.format("ERROR in {0}: Cannot assign position to a specimen that still is in dispatch transit", 
            positionString));

    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenAssignPermission(currentWorkingCenterId).isAllowed(
            context);
    }

}
