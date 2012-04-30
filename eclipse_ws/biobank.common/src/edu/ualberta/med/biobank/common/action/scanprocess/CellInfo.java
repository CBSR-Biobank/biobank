package edu.ualberta.med.biobank.common.action.scanprocess;

import java.io.Serializable;

public class CellInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer row;

    private Integer col;

    private String value;

    private CellInfoStatus status;

    private String information;

    private String title = ""; //$NON-NLS-1$

    private Integer expectedSpecimenId;

    private Integer specimenId;

    public CellInfo() {
    }

    public CellInfo(int row, int col, String value, CellInfoStatus status) {
        this();
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
    public String getInformation() {
        return information;
    }

    public void setInformation(String information) {
        this.information = information;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static boolean hasValue(CellInfo cell) {
        return cell != null && cell.getValue() != null;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getRow() {
        return row;
    }

    public void setCol(Integer col) {
        this.col = col;
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
