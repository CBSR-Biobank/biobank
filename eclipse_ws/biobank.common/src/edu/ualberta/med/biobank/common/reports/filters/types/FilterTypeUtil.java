package edu.ualberta.med.biobank.common.reports.filters.types;

import java.util.Collection;

import edu.ualberta.med.biobank.model.ReportFilterValue;

public class FilterTypeUtil {
    public static int NOT_BOUND = -1;

    public static void checkValues(Collection<ReportFilterValue> values,
        int min, int max) {
        int size = values.size();
        if (min != NOT_BOUND && size < min) {
            throw new IllegalArgumentException("expecting at least " + min
                + " argument(s), but received " + size);
        } else if (max != NOT_BOUND && size > max) {
            throw new IllegalArgumentException("expecting at most " + max
                + " argument(s), but received " + size);
        }
    }
}
