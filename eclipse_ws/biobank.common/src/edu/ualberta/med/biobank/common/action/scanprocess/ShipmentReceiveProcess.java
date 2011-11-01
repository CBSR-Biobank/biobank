package edu.ualberta.med.biobank.common.action.scanprocess;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.Map;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.scanprocess.data.ShipmentProcessData;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.ItemState;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

/**
 * Used by dispatch and request
 */
public class ShipmentReceiveProcess extends ServerProcess {

    private static final long serialVersionUID = 1L;

    private ShipmentProcessData data;

    public ShipmentReceiveProcess(ShipmentProcessData data,
        Integer currentWorkingCenterId,
        Map<RowColPos, Cell> cells,
        boolean isRescanMode, Locale locale) {
        super(currentWorkingCenterId, cells, isRescanMode, locale);
        this.data = data;
    }

    public ShipmentReceiveProcess(ShipmentProcessData data,
        Integer currentWorkingCenterId,
        Cell cell,
        Locale locale) {
        super(currentWorkingCenterId, cell, locale);
        this.data = data;
    }

    /**
     * Process of a map of cells
     */
    @Override
    protected ScanProcessResult getScanProcessResult(Session session,
        Map<RowColPos, Cell> cells, boolean isRescanMode)
        throws ActionException {
        ScanProcessResult res = new ScanProcessResult();
        res.setResult(cells, receiveProcess(session, cells));
        return res;
    }

    /**
     * Process of only one cell
     */
    @Override
    protected CellProcessResult getCellProcessResult(Session session, Cell cell)
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
    private CellStatus receiveProcess(Session session,
        Map<RowColPos, Cell> cells) {
        CellStatus currentScanState = CellStatus.EMPTY;
        if (cells != null) {
            for (Cell cell : cells.values()) {
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
    private void updateCellWithSpecimen(Cell cell, Specimen specimen) {
        cell.setSpecimenId(specimen.getId());
        cell.setTitle(specimen.getCollectionEvent().getPatient().getPnumber());
    }

    /**
     * Processing a cell in receive mode
     * 
     * @param cell
     * @throws Exception
     */
    private void processCellDipatchReceiveStatus(Session session, Cell cell) {
        Specimen foundSpecimen = searchSpecimen(session, cell.getValue());
        if (foundSpecimen == null) {
            // not in db
            cell.setStatus(CellStatus.ERROR);
            cell.setInformation(MessageFormat.format(Messages.getString(
                "DispatchReceiveScanDialog.cell.notInDb.msg", locale), cell //$NON-NLS-1$
                .getValue()));
            cell.setTitle("!"); //$NON-NLS-1$
        } else {
            ItemState state = data
                .getCurrentDispatchSpecimenIds().get(foundSpecimen.getId());
            if (state == null) {
                // not in the shipment
                updateCellWithSpecimen(cell, foundSpecimen);
                cell.setStatus(CellStatus.EXTRA);
                cell.setInformation(Messages.getString(
                    "DispatchReceiveScanDialog.cell.notInShipment.msg", locale)); //$NON-NLS-1$
            } else {
                if (DispatchSpecimenState.RECEIVED == state) {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellStatus.IN_SHIPMENT_RECEIVED);
                } else if (DispatchSpecimenState.EXTRA == state) {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellStatus.EXTRA);
                } else {
                    updateCellWithSpecimen(cell, foundSpecimen);
                    cell.setStatus(CellStatus.IN_SHIPMENT_EXPECTED);
                }
            }
        }
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // FIXME create Permission for shipment receive
        return true;
    }

}
