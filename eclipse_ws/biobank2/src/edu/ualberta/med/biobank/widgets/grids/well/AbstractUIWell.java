package edu.ualberta.med.biobank.widgets.grids.well;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractUIWell {

    private static Logger log = LoggerFactory.getLogger(AbstractUIWell.class.getName());

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

    @SuppressWarnings("nls")
    public void setSelected(boolean selected) {
        log.trace("cell: row: " + row + ", col: " + col + ", selected: " + selected);
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

    @SuppressWarnings("nls")
    public String getPositionStr() {
        StringBuffer sb = new StringBuffer();
        sb.append(row).append(", ").append(col);
        return sb.toString();
    }

}
