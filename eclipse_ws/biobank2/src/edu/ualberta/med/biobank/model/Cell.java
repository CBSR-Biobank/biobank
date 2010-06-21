package edu.ualberta.med.biobank.model;

public abstract class Cell {

    private boolean selected = false;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public abstract Integer getRow();

    public abstract Integer getCol();

}
