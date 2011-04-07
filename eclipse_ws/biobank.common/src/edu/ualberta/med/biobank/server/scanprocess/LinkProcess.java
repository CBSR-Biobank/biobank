package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.Cell;
import edu.ualberta.med.biobank.common.scanprocess.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.CellStatus;
import edu.ualberta.med.biobank.common.scanprocess.ProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class LinkProcess {

    private WritableApplicationService appService;
    private User user;

    public LinkProcess(WritableApplicationService appService, User user) {
        this.appService = appService;
        this.user = user;
    }

    public ScanProcessResult processScanLinkResult(Map<RowColPos, Cell> cells,
        boolean rescanMode) throws Exception {
        ScanProcessResult res = new ScanProcessResult();
        if (cells != null) {
            Map<String, Cell> values = new HashMap<String, Cell>();
            for (Entry<RowColPos, Cell> entry : cells.entrySet()) {
                Cell cell = entry.getValue();
                if (!rescanMode
                    || (cell != null && cell.getStatus() != CellStatus.TYPE && cell
                        .getStatus() != CellStatus.NO_TYPE)) {
                    processCellLinkStatus(appService, cell, res, user, values);
                }
            }
        }
        res.setResult(cells, null);
        return res;
    }

    public CellProcessResult processCellLinkStatus(Cell cell) throws Exception {
        CellProcessResult res = new CellProcessResult();
        res.setResult(cell,
            processCellLinkStatus(appService, cell, res, user, null));
        return res;
    }

    /**
     * Process the cell: apply a status and set correct information
     * 
     * @throws Exception
     */
    private CellStatus processCellLinkStatus(
        WritableApplicationService appService, Cell cell, ProcessResult res,
        User user, Map<String, Cell> values) throws Exception {
        if (cell == null) {
            return CellStatus.EMPTY;
        } else {
            String value = cell.getValue();
            if (value != null) {
                Cell otherValue = values.get(value);
                if (otherValue != null) {
                    String msg = "existe ailleurs en " + otherValue.getRow()
                        + ":" + otherValue.getCol();
                    cell.setInformation(msg);
                    res.appendNewLog(msg);
                    cell.setStatus(CellStatus.ERROR);
                } else {
                    values.put(value, cell);
                    SpecimenWrapper foundAliquot = SpecimenWrapper.getSpecimen(
                        appService, value, user);
                    if (foundAliquot != null) {
                        cell.setStatus(CellStatus.ERROR);
                        cell.setInformation(Messages
                            .getString("ScanLink.scanStatus.aliquot.alreadyExists")); //$NON-NLS-1$
                        String palletPosition = ContainerLabelingSchemeWrapper
                            .rowColToSbs(new RowColPos(cell.getRow(), cell
                                .getCol()));
                        if (foundAliquot.getParentSpecimen() == null)
                            res.appendNewLog(Messages
                                .getString(
                                    "ScanLink.activitylog.aliquot.existsError.noParent",
                                    palletPosition, value, foundAliquot
                                        .getCollectionEvent().getVisitNumber(),
                                    foundAliquot.getCollectionEvent()
                                        .getPatient().getPnumber(),
                                    foundAliquot.getCurrentCenter()
                                        .getNameShort()));
                        else
                            res.appendNewLog(Messages
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
                }
            } else {
                cell.setStatus(CellStatus.EMPTY);
            }
            return cell.getStatus();
        }
    }
}
