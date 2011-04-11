package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.LinkProcessData;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkProcess extends ServerProcess {

    public LinkProcess(WritableApplicationService appService,
        LinkProcessData data, User user) {
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
        boolean isRescanMode) throws Exception {
        CellStatus currentScanState = CellStatus.EMPTY;
        if (cells != null) {
            Map<String, Cell> allValues = new HashMap<String, Cell>();
            for (Entry<RowColPos, Cell> entry : cells.entrySet()) {
                Cell cell = entry.getValue();
                if (cell != null) {
                    Cell otherValue = allValues.get(cell.getValue());
                    if (otherValue != null) {
                        String msg = "existe ailleurs en "
                            + otherValue.getRow() + ":" + otherValue.getCol();
                        cell.setInformation(msg);
                        appendNewLog(msg);
                        cell.setStatus(CellStatus.ERROR);
                    } else {
                        allValues.put(cell.getValue(), cell);
                    }
                }
                if (!isRescanMode
                    || (cell != null && cell.getStatus() != CellStatus.TYPE && cell
                        .getStatus() != CellStatus.NO_TYPE)) {
                    processCellLinkStatus(cell);
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
        processCellLinkStatus(cell);
        res.setResult(cell);
        return res;
    }

    /**
     * Process the cell: apply a status and set correct information
     * 
     * @throws Exception
     */
    private CellStatus processCellLinkStatus(Cell cell) throws Exception {
        if (cell == null)
            return CellStatus.EMPTY;
        if (cell.getStatus() == CellStatus.ERROR)
            return CellStatus.ERROR;
        else {
            String value = cell.getValue();
            if (value != null) {
                SpecimenWrapper foundAliquot = SpecimenWrapper.getSpecimen(
                    appService, value, user);
                if (foundAliquot != null) {
                    cell.setStatus(CellStatus.ERROR);
                    cell.setInformation(Messages
                        .getString("ScanLink.scanStatus.aliquot.alreadyExists")); //$NON-NLS-1$
                    String palletPosition = ContainerLabelingSchemeWrapper
                        .rowColToSbs(new RowColPos(cell.getRow(), cell.getCol()));
                    if (foundAliquot.getParentSpecimen() == null)
                        appendNewLog(Messages
                            .getString(
                                "ScanLink.activitylog.aliquot.existsError.noParent",
                                palletPosition, value, foundAliquot
                                    .getCollectionEvent().getVisitNumber(),
                                foundAliquot.getCollectionEvent().getPatient()
                                    .getPnumber(), foundAliquot
                                    .getCurrentCenter().getNameShort()));
                    else
                        appendNewLog(Messages
                            .getString(
                                "ScanLink.activitylog.aliquot.existsError.withParent",
                                palletPosition, value, foundAliquot
                                    .getParentSpecimen().getInventoryId(),
                                foundAliquot.getParentSpecimen()
                                    .getSpecimenType().getNameShort(),
                                foundAliquot.getCollectionEvent()
                                    .getVisitNumber(), foundAliquot
                                    .getCollectionEvent().getPatient()
                                    .getPnumber(), foundAliquot
                                    .getCurrentCenter().getNameShort()));
                } else {
                    cell.setStatus(CellStatus.NO_TYPE);
                }
            } else {
                cell.setStatus(CellStatus.EMPTY);
            }
            return cell.getStatus();
        }
    }

}
