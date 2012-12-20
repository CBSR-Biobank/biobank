package edu.ualberta.med.biobank.action.scanprocess;

import java.util.Locale;
import java.util.Map;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.action.specimen.SpecimenIsUsedInDispatchAction;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.ActivityStatus;
import edu.ualberta.med.biobank.model.type.ItemState;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class DispatchCreateProcessAction extends ServerProcessAction {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Tr SPECIMEN_MISSING =
        bundle.tr("Specimen {0} missing");
    @SuppressWarnings("nls")
    public static final LString SPECIMEN_NOT_FOUND =
        bundle.tr("Specimen does not exist.").format();
    @SuppressWarnings("nls")
    public static final LString NO_INFORMATION = bundle.tr("").format();
    @SuppressWarnings("nls")
    public static final Tr SPECIMEN_IN_NON_CLOSED_DISPATCH =
        bundle.tr("{0} is already in a not closed dispatch.");

    private final ShipmentProcessInfo data;

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
    @SuppressWarnings("nls")
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
            scanCell.setInformation(SPECIMEN_MISSING.format(
                expectedSpecimen.getInventoryId()));
            scanCell.setTitle("?");
        } else {
            Specimen foundSpecimen = searchSpecimen(session, value);
            if (foundSpecimen == null) {
                // not in database
                scanCell.setStatus(CellInfoStatus.ERROR);
                scanCell.setInformation(SPECIMEN_NOT_FOUND);
            } else {
                if (expectedSpecimen != null
                    && !foundSpecimen.equals(expectedSpecimen)) {
                    // Position taken
                    scanCell.setStatus(CellInfoStatus.ERROR);
                    scanCell.setInformation(bundle.tr(
                        "Specimen different from the one registered" +
                            " at this position").format());
                    scanCell.setTitle("!");
                } else {
                    scanCell.setSpecimenId(foundSpecimen.getId());
                    if (expectedSpecimen != null
                        || data.getPallet(session) == null) {
                        checkCanAddSpecimen(scanCell, foundSpecimen,
                            sender, checkAlreadyAdded);
                    } else {
                        // should not be there
                        scanCell.setStatus(CellInfoStatus.ERROR);
                        scanCell.setTitle(foundSpecimen
                            .getCollectionEvent().getPatient().getPnumber());
                        scanCell.setInformation(bundle.tr(
                            "This specimen should be on another pallet")
                            .format());
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
    @SuppressWarnings("nls")
    private void checkCanAddSpecimen(CellInfo cell, Specimen specimen,
        Center sender, boolean checkAlreadyAdded) {
        if (specimen.getId() == null) {
            cell.setStatus(CellInfoStatus.ERROR);
            cell.setInformation(NO_INFORMATION);
        } else if (specimen.getActivityStatus() != ActivityStatus.ACTIVE) {
            cell.setStatus(CellInfoStatus.ERROR);
            cell.setInformation(bundle.tr(
                "Activity status of {0} is not ''Active''. Check" +
                    " comments on this specimen for more information.")
                .format(specimen.getInventoryId()));
        } else if (!specimen.getCurrentCenter().equals(sender)) {
            cell.setStatus(CellInfoStatus.ERROR);
            cell.setInformation(bundle.tr(
                "Specimen {0} is currently assigned to center" +
                    " {1}. It should first be sent to center {2}.")
                .format(specimen.getInventoryId(), specimen.getCurrentCenter()
                    .getName(), sender.getName()));
        } else {
            Map<Integer, ItemState> currentSpecimenIds = data
                .getCurrentDispatchSpecimenIds();
            boolean alreadyInShipment = currentSpecimenIds != null
                && currentSpecimenIds.get(specimen.getId()) != null;
            if (checkAlreadyAdded && alreadyInShipment) {
                cell.setStatus(CellInfoStatus.ERROR);
                cell.setInformation(bundle.tr(
                    "{0} is already in this dispatch.")
                    .format(specimen.getInventoryId()));
            } else if (new SpecimenIsUsedInDispatchAction(specimen.getId())
                .run(actionContext).isTrue()) {
                cell.setStatus(CellInfoStatus.ERROR);
                cell.setInformation(SPECIMEN_IN_NON_CLOSED_DISPATCH.format(
                    specimen.getInventoryId()));
            } else {
                if (alreadyInShipment)
                    cell.setStatus(CellInfoStatus.IN_SHIPMENT_ADDED);
                else
                    cell.setStatus(CellInfoStatus.FILLED);
                cell.setTitle(specimen.getCollectionEvent()
                    .getPatient().getPnumber());
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
