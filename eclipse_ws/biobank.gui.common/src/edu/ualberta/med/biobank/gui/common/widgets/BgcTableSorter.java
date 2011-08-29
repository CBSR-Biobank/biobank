package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.jface.viewers.ViewerSorter;

public class BgcTableSorter extends ViewerSorter {
    protected int propertyIndex = 0;

    private int direction = 1;

    public void setColumn(int column) {
        if (column == this.propertyIndex) {
            // Same column as last sort; toggle the direction
            direction = 1 - direction;
        } else {
            // New column; do an ascending sort
            this.propertyIndex = column;
            direction = 1;
        }
    }

}
