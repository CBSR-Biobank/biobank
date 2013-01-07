package edu.ualberta.med.biobank.common.action.scanprocess;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.hibernate.Session;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.scanprocess.result.CellProcessResult;
import edu.ualberta.med.biobank.common.action.scanprocess.result.ScanProcessResult;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenLinkPermission;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class SpecimenLinkProcessAction extends ServerProcessAction {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    private final Integer studyId;

    // multiple cells link process
    public SpecimenLinkProcessAction(Integer currentWorkingCenterId,
        Integer studyId,
        Map<RowColPos, CellInfo> cells, boolean isRescanMode, Locale locale) {
        super(currentWorkingCenterId, cells, isRescanMode, locale);
        this.studyId = studyId;
    }

    // single cell link process
    public SpecimenLinkProcessAction(Integer currentWorkingCenterId,
        Integer studyId,
        CellInfo cell, Locale locale) {
        super(currentWorkingCenterId, cell, locale);
        this.studyId = studyId;
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

    // TODO: the server local may be different than the client, baking strings
    // here is a bad idea.
    @SuppressWarnings("nls")
    protected CellInfoStatus internalProcessScanResult(Session session,
        Map<RowColPos, CellInfo> cells, boolean isRescanMode)
        throws ActionException {
        CellInfoStatus currentScanState = CellInfoStatus.EMPTY;
        if (cells != null) {
            Map<String, CellInfo> allValues = new HashMap<String, CellInfo>();
            for (Entry<RowColPos, CellInfo> entry : cells.entrySet()) {
                CellInfo cell = entry.getValue();
                if (cell != null) {
                    CellInfo otherValue = allValues.get(cell.getValue());
                    if (otherValue != null) {
                        String thisPosition = ContainerLabelingSchemeWrapper
                            .rowColToSbs(new RowColPos(cell.getRow(), cell
                                .getCol()));
                        String otherPosition = ContainerLabelingSchemeWrapper
                            .rowColToSbs(new RowColPos(otherValue.getRow(),
                                otherValue.getCol()));
                        cell.setInformation(bundle
                            .tr(
                                "Value ''{0}'' has already been scanned in position {1}")
                            .format(cell.getValue(), otherPosition));
                        appendNewLog(MessageFormat
                            .format(
                                "ERROR in {0}: Value ''{1}'' has already been scanned in position {2}",
                                thisPosition, cell.getValue(),
                                otherPosition));
                        cell.setStatus(CellInfoStatus.ERROR);
                    } else {
                        allValues.put(cell.getValue(), cell);
                    }
                }
                if (!isRescanMode
                    || (cell != null && cell.getStatus() != CellInfoStatus.TYPE && cell
                        .getStatus() != CellInfoStatus.NO_TYPE)) {
                    processCellLinkStatus(session, cell);
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
        processCellLinkStatus(session, cell);
        res.setResult(cell);
        return res;
    }

    /**
     * Process the cell: apply a status and set correct information
     * 
     * @throws Exception
     */
    // TODO: the server local may be different than the client, baking strings
    // here is a bad idea.
    @SuppressWarnings("nls")
    private CellInfoStatus processCellLinkStatus(Session session, CellInfo cell)
        throws ActionException {
        if (cell == null)
            return CellInfoStatus.EMPTY;
        if (cell.getStatus() == CellInfoStatus.ERROR)
            return CellInfoStatus.ERROR;
        String value = cell.getValue();
        if (value != null) {
            Specimen foundSpecimen = searchSpecimen(session, value);
            if (foundSpecimen != null) {
                cell.setStatus(CellInfoStatus.ERROR);
                cell.setInformation(bundle
                    .tr("Specimen already in database").format());
                String palletPosition = ContainerLabelingSchemeWrapper
                    .rowColToSbs(new RowColPos(cell.getRow(), cell.getCol()));
                if (foundSpecimen.getParentSpecimen() == null)
                    appendNewLog(MessageFormat
                        .format(
                            "ERROR in {0}: Specimen ''{1}'' already in database linked to visit {2} from patient {3} (currently in center {4})",
                            palletPosition, value,
                            foundSpecimen.getCollectionEvent()
                                .getVisitNumber(), foundSpecimen
                                .getCollectionEvent().getPatient()
                                .getPnumber(), foundSpecimen
                                .getCurrentCenter().getNameShort()));
                else
                    appendNewLog(MessageFormat
                        .format(
                            "ERROR in {0}: Specimen ''{1}'' already in database linked to source specimen ''{2}'' ({3}) of visit {4} from patient {5} (currently in center {6})",
                            palletPosition, value,
                            foundSpecimen.getParentSpecimen()
                                .getInventoryId(), foundSpecimen
                                .getParentSpecimen().getSpecimenType()
                                .getNameShort(), foundSpecimen
                                .getCollectionEvent().getVisitNumber(),
                            foundSpecimen.getCollectionEvent().getPatient()
                                .getPnumber(), foundSpecimen
                                .getCurrentCenter().getNameShort()));
            } else {
                cell.setStatus(CellInfoStatus.NO_TYPE);
            }
        } else {
            cell.setStatus(CellInfoStatus.EMPTY);
        }
        return cell.getStatus();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenLinkPermission(currentWorkingCenterId, studyId)
            .isAllowed(context);
    }

}
