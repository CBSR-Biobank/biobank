package edu.ualberta.med.biobank.widgets.grids.well;

public abstract class AbstractUIWell {

    private boolean selected = false;

    private UICellStatus status;

    private final Integer row;

    private final Integer col;

    public AbstractUIWell(Integer row, Integer col) {
        this.row = row;
        this.col = col;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public UICellStatus getStatus() {
        return status;
    }

    public void setStatus(UICellStatus status) {
        this.status = status;
    }

    public Integer getRow() {
        return row;
    }

    public Integer getCol() {
        return col;
    }

}
