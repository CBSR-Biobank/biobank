package edu.ualberta.med.biobank.common.action.scanprocess;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.LString;

public class CellInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private final Integer row;

    private final Integer col;

    private final String value;

    private CellInfoStatus status;

    private LString information;

    private String title = StringUtil.EMPTY_STRING;

    private Integer expectedSpecimenId;

    private Integer specimenId;

    public CellInfo(int row, int col, String value, CellInfoStatus status) {
        this.row = row;
        this.col = col;
        this.value = value;
        this.status = status;
    }

    public CellInfoStatus getStatus() {
        return status;
    }

    public void setStatus(CellInfoStatus status) {
        this.status = status;
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
    public LString getInformation() {
        return information;
    }

    public void setInformation(LString information) {
        this.information = information;
    }

    public String getValue() {
        return value;
    }

    public static boolean hasValue(CellInfo cell) {
        return (cell != null) && (cell.getValue() != null);
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }

    public void setExpectedSpecimenId(Integer specId) {
        this.expectedSpecimenId = specId;
    }

    public Integer getExpectedSpecimenId() {
        return expectedSpecimenId;
    }

    public void setSpecimenId(Integer id) {
        this.specimenId = id;
    }

    public Integer getSpecimenId() {
        return specimenId;
    }

}
