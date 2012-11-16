package edu.ualberta.med.biobank.widgets.grids.well;

public abstract class AbstractUIWell {

    private boolean selected = false;

    private UICellStatus status;

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

    public abstract Integer getRow();

    public abstract Integer getCol();

}
