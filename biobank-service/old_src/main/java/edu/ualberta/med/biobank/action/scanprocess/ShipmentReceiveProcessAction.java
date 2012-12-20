package edu.ualberta.med.biobank.action.scanprocess;

import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.action.scanprocess.data.ShipmentProcessInfo;
import edu.ualberta.med.biobank.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.ShipmentItemState;
import edu.ualberta.med.biobank.model.type.ItemState;
import edu.ualberta.med.biobank.model.util.RowColPos;

/**
 * Used by dispatch and request
 */
public class ShipmentReceiveProcessAction extends ServerProcessAction {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    private final ShipmentProcessInfo data;

    public ShipmentReceiveProcessAction(ShipmentProcessInfo data,
        Integer currentWorkingCenterId,
        Map<RowColPos, CellInfo> cells,
        boolean isRescanMode, Locale locale) {
        super(currentWorkingCenterId, cells, isRescanMode, locale);
        this.data = data;
    }

    public ShipmentReceiveProcessAction(ShipmentProcessInfo data,
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
        res.setResult(cells, receiveProcess(session, cells));
        return res;
    }

    /**
     * Process of only one cell
     */
    @Override
    protected CellProcessResult getCellProcessResult(CellInfo cell)
        throws ActionException {
        CellProcessResult res = new CellProcessResult();
        processCellDipatchReceiveStatus(session, cell);
        res.setResult(cell);
        return res;
    }

    /**
     * Process cells in receive mode
     * 
     * @param cells
     * @return
     * @throws Exception
     */
    private CellInfoStatus receiveProcess(Session session,
        Map<RowColPos, CellInfo> cells) {
        CellInfoStatus currentScanState = CellInfoStatus.EMPTY;
        if (cells != null) {
            for (CellInfo cell : cells.values()) {
                processCellDipatchReceiveStatus(session, cell);
                currentScanState = currentScanState.mergeWith(cell.getStatus());
            }
        }
        return currentScanState;
    }

    /**
     * Update cell data with this specimen
     * 
     * @param cell
     * @param specimen
     */
    private void updateCellWithSpecimen(CellInfo cell, Specimen specimen) {
        cell.setSpecimenId(specimen.getId());
        cell.setTitle(specimen.getCollectionEvent()
            .getPatient().getPnumber());
    }

    /**
     * Processing a cell in receive mode
     * 
     * @param cell
     * @throws Exception
     */
    // TODO: the server local may be different than the client, baking strings
    // here is a bad idea.
    @SuppressWarnings("nls")
    private void processCellDipatchReceiveStatus(Session session, CellInfo cell) {
        Specimen foundSpecimen = searchSpecimen(session, cell.getValue());
        if (foundSpecimen == null) {
            // not in db
            cell.setStatus(CellInfoStatus.ERROR);
            cell.setInformation(bundle.tr(
                "Specimen {0} not found in database")
                .format(cell.getValue()));
            cell.setTitle("!");
        } else {
            ItemState state = data
                .getCurrentDispatchSpecimenIds().get(foundSpecimen.getId());
            if (state == null) {
                // not in the shipment
                updateCellWithSpecimen(cell, foundSpecimen);
                cell.setStatus(CellInfoStatus.EXTRA);
                cell.setInformation(bundle.tr(
                    "Specimen should not be in shipment").format());
            } else {
                if (ShipmentItemState.RECEIVED == state) {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellInfoStatus.IN_SHIPMENT_RECEIVED);
                } else if (ShipmentItemState.EXTRA == state) {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellInfoStatus.EXTRA);
                } else {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellInfoStatus.IN_SHIPMENT_EXPECTED);
                }
            }
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // FIXME create Permission for shipment receive
        return true;
    }

}
