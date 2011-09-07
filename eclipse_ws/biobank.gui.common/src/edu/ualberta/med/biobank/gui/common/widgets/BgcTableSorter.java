package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.jface.viewers.ViewerSorter;

public class BgcTableSorter extends ViewerSorter {

    protected static final int DESCENDING = 1;

    protected int propertyIndex = 0;

    protected int direction = DESCENDING;

    public void setColumn(int column) {
        if (column == this.propertyIndex) {
            // Same column as last sort; toggle the direction
            direction = DESCENDING - direction;
        } else {
            // New column; do an ascending sort
            this.propertyIndex = column;
            direction = DESCENDING;
        }
    }

}
