package edu.ualberta.med.biobank.widgets.infotables;

import org.eclipse.jface.viewers.ViewerSorter;

/**
 * Classes derived from InfoTableWidget should derive their table sorter from
 * this class.
 */
public class BiobankTableSorter extends ViewerSorter {
    protected int propertyIndex = 0;

    protected int direction = 0;

    public void setColumn(int colId) {
        if (propertyIndex == colId) {
            direction = 1 - direction;
        } else {
            propertyIndex = colId;
            direction = 1 - direction;
        }
    }
}