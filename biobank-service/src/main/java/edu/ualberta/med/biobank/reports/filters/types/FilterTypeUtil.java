package edu.ualberta.med.biobank.reports.filters.types;

import java.util.Collection;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public class FilterTypeUtil {
    public static int NOT_BOUND = -1;

    public static void checkValues(Collection<ReportFilterValue> values,
        int min, int max) {
        int size = values.size();
        if (min != NOT_BOUND && size < min) {
            throw new IllegalArgumentException("expecting at least " + min //$NON-NLS-1$
                + " argument(s), but received " + size); //$NON-NLS-1$
        } else if (max != NOT_BOUND && size > max) {
            throw new IllegalArgumentException("expecting at most " + max //$NON-NLS-1$
                + " argument(s), but received " + size); //$NON-NLS-1$
        }
    }
}
