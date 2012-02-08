package edu.ualberta.med.biobank.common.action.scanprocess;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenIsUsedInDispatchAction;
import edu.ualberta.med.biobank.common.util.ItemState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Specimen;

public class DispatchCreateProcessAction extends ServerProcessAction {
    private static final long serialVersionUID = 1L;

    private ShipmentProcessInfo data;

    public DispatchCreateProcessAction(ShipmentProcessInfo data,
        Integer currentWorkingCenterId,
        Map<RowColPos, CellInfo> cells,
        boolean isRescanMode, Locale locale) {
        super(currentWorkingCenterId, cells, isRescanMode, locale);
        this.data = data;
    }

    public DispatchCreateProcessAction(ShipmentProcessInfo data,
        Integer currentWorkingCenterId,
        CellInfo cell,
        Locale locale) {
        super(currentWorkingCenterId, cell, locale);
        this.data = data;
    }

    /**
     * Process of a map of cells
     */
    @Override
    protected ScanProcessResult getScanProcessResult(
        Map<RowColPos, CellInfo> cells, boolean isRescanMode)
        throws ActionException {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells, createProcess(cells));
        return res;
    }

    /**
     * Process of only one cell
     */
    @Override
    protected CellProcessResult getCellProcessResult(CellInfo cell)
        throws ActionException {
        CellProcessResult res = new CellProcessResult();
        ShipmentProcessInfo dispatchData = data;
        Center sender = null;
        if (dispatchData.getSenderId() != null) {
            sender =
                actionContext.load(Center.class, dispatchData.getSenderId());
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
    private CellInfoStatus createProcess(Map<RowColPos, CellInfo> cells) {
        CellInfoStatus currentScanState = CellInfoStatus.EMPTY;
        ShipmentProcessInfo dispatchData = data;
        Center sender = null;

        if (dispatchData.getSenderId() != null) {
            sender =
                (Center) session.load(Center.class, dispatchData.getSenderId());
        }
        if (dispatchData.getPallet(session) == null) {
            for (CellInfo cell : cells.values()) {
                processCellDipatchCreateStatus(cell, sender, false);
                currentScanState = currentScanState.mergeWith(cell.getStatus());
            }

        } else {
            for (int row = 0; row < dispatchData
                .getPalletRowCapacity(actionContext); row++) {
                for (int col = 0; col < dispatchData
                    .getPalletColCapacity(actionContext); col++) {
                    RowColPos rcp = new RowColPos(row, col);
                    CellInfo cell = cells.get(rcp);
                    Specimen expectedSpecimen = dispatchData
                        .getSpecimen(session, row, col);
                    if (expectedSpecimen != null) {
                        if (cell == null) {
                            cell = new CellInfo(row, col, null, null);
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
    private CellInfoStatus processCellDipatchCreateStatus(CellInfo scanCell,
        Center sender, boolean checkAlreadyAdded) {
        Specimen expectedSpecimen = null;
        if (scanCell.getExpectedSpecimenId() != null) {
            expectedSpecimen = actionContext.load(Specimen.class,
                scanCell.getExpectedSpecimenId());
        }
        String value = scanCell.getValue();
        if (value == null) { // no specimen scanned
            scanCell.setStatus(CellInfoStatus.MISSING);
            scanCell.setInformation(MessageFormat.format(Messages.getString(
                "ScanAssign.scanStatus.specimen.missing", locale), //$NON-NLS-1$
                expectedSpecimen.getInventoryId()));
            scanCell.setTitle("?"); //$NON-NLS-1$
        } else {
            Specimen foundSpecimen = searchSpecimen(session, value);
            if (foundSpecimen == null) {
                // not in database
                scanCell.setStatus(CellInfoStatus.ERROR);
                scanCell.setInformation(Messages.getString(
                    "DispatchProcess.scanStatus.specimen.notfound", locale)); //$NON-NLS-1$
            } else {
                if (expectedSpecimen != null
                    && !foundSpecimen.equals(expectedSpecimen)) {
                    // Position taken
                    scanCell.setStatus(CellInfoStatus.ERROR);
                    scanCell
                        .setInformation(Messages
                            .getString(
                                "ScanAssign.scanStatus.specimen.positionTakenError", locale)); //$NON-NLS-1$
                    scanCell.setTitle("!"); //$NON-NLS-1$
                } else {
                    scanCell.setSpecimenId(foundSpecimen.getId());
                    if (expectedSpecimen != null
                        || data.getPallet(session) == null) {
                        checkCanAddSpecimen(session, scanCell, foundSpecimen,
                            sender, checkAlreadyAdded);
                    } else {
                        // should not be there
                        scanCell.setStatus(CellInfoStatus.ERROR);
                        scanCell.setTitle(foundSpecimen.getCollectionEvent()
                            .getPatient().getPnumber());
                        scanCell
                            .setInformation(Messages
                                .getString(
                                    "DispatchProcess.create.specimen.anotherPallet", locale)); //$NON-NLS-1$
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
    private void checkCanAddSpecimen(Session session, CellInfo cell,
        Specimen specimen,
        Center sender, boolean checkAlreadyAdded) {
        if (specimen.getId() == null) {
            cell.setStatus(CellInfoStatus.ERROR);
            cell.setInformation(""); //$NON-NLS-1$
        } else if (specimen.getActivityStatus() != ActivityStatus.ACTIVE) {
            cell.setStatus(CellInfoStatus.ERROR);
            cell.setInformation(MessageFormat.format(Messages.getString(
                "DispatchProcess.create.specimen.status", locale), //$NON-NLS-1$
                specimen.getInventoryId()));
        } else if (!specimen.getCurrentCenter().equals(sender)) {
            cell.setStatus(CellInfoStatus.ERROR);
            cell.setInformation(MessageFormat.format(Messages.getString(
                "DispatchProcess.create.specimen.currentCenter", locale), //$NON-NLS-1$
                specimen.getInventoryId(), specimen.getCurrentCenter()
                    .getNameShort(), sender.getNameShort()));
        } else {
            Map<Integer, ItemState> currentSpecimenIds = data
                .getCurrentDispatchSpecimenIds();
            boolean alreadyInShipment = currentSpecimenIds != null
                && currentSpecimenIds.get(specimen.getId()) != null;
            if (checkAlreadyAdded && alreadyInShipment) {
                cell.setStatus(CellInfoStatus.ERROR);
                cell.setInformation(MessageFormat.format(Messages.getString(
                    "DispatchProcess.create.specimen.alreadyAdded", locale), //$NON-NLS-1$
                    specimen.getInventoryId()));
            } else if (new SpecimenIsUsedInDispatchAction(specimen.getId())
                .run(actionContext).isTrue()) {
                cell.setStatus(CellInfoStatus.ERROR);
                cell.setInformation(MessageFormat.format(
                    Messages
                        .getString(
                            "DispatchProcess.create.specimen.inNotClosedDispatch", locale), //$NON-NLS-1$
                    specimen.getInventoryId()));
            } else {
                if (alreadyInShipment)
                    cell.setStatus(CellInfoStatus.IN_SHIPMENT_ADDED);
                else
                    cell.setStatus(CellInfoStatus.FILLED);
                cell.setTitle(specimen.getCollectionEvent().getPatient()
                    .getPnumber());
                cell.setSpecimenId(specimen.getId());
            }
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // FIXME add dispatch create permission
        return true;
    }

}
