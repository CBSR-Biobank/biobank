package edu.ualberta.med.biobank.widgets.infotables;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.core.runtime.Assert;

/**
 * Classes derived from InfoTableWidget should derive their table sorter from
 * this class.
 */
public class BiobankTableSorter implements Comparator<Object>, Serializable {

    private static final long serialVersionUID = 1L;

    @Override
    public int compare(Object o1, Object o2) {
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }

        if (o1 instanceof String) {
            Assert.isTrue(o2 instanceof String);
            return ((String) o1).compareToIgnoreCase((String) o2);
        } else if (o1 instanceof Integer) {
            Assert.isTrue(o2 instanceof Integer);
            return ((Integer) o1).compareTo((Integer) o2);
        } else if (o1 instanceof Double) {
            Assert.isTrue(o2 instanceof Double);
            return ((Double) o1).compareTo((Double) o2);
        } else if (o1 instanceof Long) {
            Assert.isTrue(o2 instanceof Long);
            return ((Long) o1).compareTo((Long) o2);
        } else if (o1 instanceof Boolean) {
            Assert.isTrue(o2 instanceof Boolean);
            return ((Boolean) o1).compareTo((Boolean) o2);
        }
        Assert
            .isTrue(false, "invalid class for o1: " + o1.getClass().getName());
        return 0;
    }

}