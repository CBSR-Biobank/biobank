package edu.ualberta.med.biobank.widgets.grids.well;

import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.exception.AccessDeniedException;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfo;
import edu.ualberta.med.biobank.common.action.scanprocess.CellInfoStatus;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.SbsLabeling;
import edu.ualberta.med.scannerconfig.dmscanlib.DecodedWell;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenCell extends AbstractUIWell {
    private String information;

    private String title = StringUtil.EMPTY_STRING;

    private SpecimenWrapper sourceSpecimen;

    private SpecimenWrapper specimen;

    private SpecimenWrapper expectedSpecimen;

    private DecodedWell decodedWell;

    public SpecimenCell(Integer row, Integer col, DecodedWell scanCell) {
        super(row, col);
        this.decodedWell = scanCell;
    }

    public static Map<RowColPos, SpecimenCell> convertArray(Set<DecodedWell> decodedWells) {
        Map<RowColPos, SpecimenCell> palletScanned = new TreeMap<RowColPos, SpecimenCell>();
        for (DecodedWell decodedWell : decodedWells) {
            RowColPos pos = SbsLabeling.toRowCol(decodedWell.getLabel());
            palletScanned.put(pos, new SpecimenCell(pos.getRow(), pos.getCol(), decodedWell));
        }
        return palletScanned;
    }

    /**
     * usually displayed in the middle of the cell
     */
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Usually used for the tooltip of the cell
     * 
     * @return
     */
    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getTypeString() {
        if (specimen != null && specimen.getSpecimenType() != null) {
            SpecimenTypeWrapper type = specimen.getSpecimenType();
            if (type.getNameShort() != null) {
                return type.getNameShort();
            }
            return type.getName();
        }
        return StringUtil.EMPTY_STRING;
    }

    public SpecimenTypeWrapper getType() {
        if (specimen == null)
            return null;
        return specimen.getSpecimenType();
    }

    public void setSpecimenType(SpecimenTypeWrapper type) {
        if (specimen == null) {
            specimen = new SpecimenWrapper(SessionManager.getAppService());
        }
        specimen.setSpecimenType(type);
    }

    public void setSpecimen(SpecimenWrapper specimen) {
        this.specimen = specimen;
    }

    public SpecimenWrapper getSpecimen() {
        return specimen;
    }

    public String getValue() {
        if (decodedWell != null) {
            return decodedWell.getMessage();
        }
        return null;
    }

    public void setValue(String value) {
        decodedWell = new DecodedWell(decodedWell.getLabel(), value);
    }

    @SuppressWarnings("nls")
    public RowColPos getRowColPos() {
        Integer row = getRow();
        Integer col = getCol();
        if ((row == null) || (col == null)) {
            throw new RuntimeException("row or column is null");
        }
        return new RowColPos(row, col);
    }

    public static boolean hasValue(SpecimenCell cell) {
        return cell != null && cell.getValue() != null;
    }

    public void setExpectedSpecimen(SpecimenWrapper expectedSpecimen) {
        this.expectedSpecimen = expectedSpecimen;
    }

    public SpecimenWrapper getExpectedSpecimen() {
        return expectedSpecimen;
    }

    public void setSourceSpecimen(SpecimenWrapper sourceSpecimen) {
        this.sourceSpecimen = sourceSpecimen;
    }

    public SpecimenWrapper getSourceSpecimen() {
        return sourceSpecimen;
    }

    // FIXME: this should be changed so that only one call to the server is made
    @SuppressWarnings("nls")
    public void merge(WritableApplicationService appService, CellInfo cell) throws Exception {
        setStatus(cell.getStatus());
        if (cell.getInformation() != null) {
            setInformation(cell.getInformation().toString());
        } else {
            setInformation(StringUtil.EMPTY_STRING);
        }
        decodedWell = new DecodedWell(decodedWell.getLabel(), cell.getValue());
        setTitle(cell.getTitle().toString());
        SpecimenWrapper expectedSpecimen = null;
        if (cell.getExpectedSpecimenId() != null) {
            expectedSpecimen = new SpecimenWrapper(appService);
            expectedSpecimen.getWrappedObject().setId(cell.getExpectedSpecimenId());
            expectedSpecimen.reload();
        }
        setExpectedSpecimen(expectedSpecimen);
        SpecimenWrapper specimen = null;
        if (cell.getSpecimenId() != null) {
            specimen = new SpecimenWrapper(appService);

            try {
                specimen.setWrappedObject(SessionManager.getAppService().doAction(
                    new SpecimenGetInfoAction(cell.getSpecimenId())).getSpecimen());
            } catch (AccessDeniedException e) {
                throw new Exception(e.getLocalizedMessage() + " for specimen with Id "
                    + cell.getValue());
            }
        }
        setSpecimen(specimen);
    }

    public void setStatus(CellInfoStatus status) {
        if (status != null)
            setStatus(UICellStatus.valueOf(status.name()));
    }

    @SuppressWarnings("deprecation")
    public CellInfo transformIntoServerCell() {
        CellInfo serverCell = new CellInfo(getRow(), getCol(), getValue(),
            (getStatus() == null) ? null : CellInfoStatus.valueOf(getStatus().name()));
        serverCell.setExpectedSpecimenId((getExpectedSpecimen() == null)
            ? null : getExpectedSpecimen().getId());
        if (getStatus() != null) {
            serverCell.setStatus(CellInfoStatus.valueOf(getStatus().name()));
        }
        if (getInformation() != null) {
            serverCell.setInformation(LString.lit(getInformation().toString()));
        }
        serverCell.setSpecimenId(getSpecimen() == null ? null : getSpecimen().getId());
        serverCell.setTitle(getTitle());
        return serverCell;
    }

    public String getLabel() {
        return decodedWell.getLabel();
    }
}
