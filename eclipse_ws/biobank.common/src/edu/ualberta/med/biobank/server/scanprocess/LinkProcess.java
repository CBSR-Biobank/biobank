package edu.ualberta.med.biobank.server.scanprocess;

import edu.ualberta.med.biobank.common.Messages;
import edu.ualberta.med.biobank.common.scanprocess.CellProcessResult;
import edu.ualberta.med.biobank.common.scanprocess.ScanProcessResult;
import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.util.linking.Cell;
import edu.ualberta.med.biobank.common.util.linking.CellStatus;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Map;
import java.util.Map.Entry;

public class LinkProcess implements ScanProcess {
    public static ScanProcessResult processScanLinkResult(
        WritableApplicationService appService, Map<RowColPos, Cell> cells,
        boolean rescanMode, User user) throws Exception {
        StringBuffer consoleLog = new StringBuffer();
        if (cells != null) {
            for (Entry<RowColPos, Cell> entry : cells.entrySet()) {
                Cell cell = entry.getValue();
                if (!rescanMode
                    || (cell != null && cell.getStatus() != CellStatus.TYPE && cell
                        .getStatus() != CellStatus.NO_TYPE)) {
                    processCellLinkStatus(appService, cell, consoleLog, user);
                }
            }
        }
        return new ScanProcessResult(cells, consoleLog.toString(), null);
    }

    public static CellProcessResult processCellLinkStatus(
        WritableApplicationService appService, Cell cell, User user)
        throws Exception {
        StringBuffer consoleLog = new StringBuffer();
        CellStatus status = processCellLinkStatus(appService, cell, consoleLog,
            user);
        return new CellProcessResult(status, consoleLog.toString());
    }

    /**
     * Process the cell: apply a status and set correct information
     * 
     * @throws Exception
     */
    private static CellStatus processCellLinkStatus(
        WritableApplicationService appService, Cell cell,
        StringBuffer consoleLog, User user) throws Exception {
        if (cell == null) {
            return CellStatus.EMPTY;
        } else {
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
                    consoleLog.append(Messages.getString(
                        "ScanLink.activitylog.aliquot.existsError",
                        palletPosition, value, foundAliquot
                            .getCollectionEvent().getVisitNumber(),
                        foundAliquot.getCollectionEvent().getPatient()
                            .getPnumber(), foundAliquot.getCurrentCenter()
                            .getNameShort()));
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
