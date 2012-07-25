package edu.ualberta.med.biobank.reports.filters;

import edu.ualberta.med.biobank.reports.filters.types.ActivityStatusFilterType;
import edu.ualberta.med.biobank.reports.filters.types.BooleanFilterType;
import edu.ualberta.med.biobank.reports.filters.types.DateFilterType;
import edu.ualberta.med.biobank.reports.filters.types.DoubleFilterType;
import edu.ualberta.med.biobank.reports.filters.types.FirstTimeProcessedFilterType;
import edu.ualberta.med.biobank.reports.filters.types.IntegerFilterType;
import edu.ualberta.med.biobank.reports.filters.types.StringFilterType;
import edu.ualberta.med.biobank.reports.filters.types.TopContainerFilterType;

/**
 * These <code>enum</code>-s should only be @Depricate-d, NEVER DELETED. They
 * correspond to database entries and if are altered may result in catastrophe.
 * 
 * @author jferland
 * 
 */
public enum FilterTypes {
    // These enum-s should only be @Deprecated, NEVER DELETED. They
    // correspond to database entries and if are altered may result in
    // catastrophe (at least if the id value is altered).
    STRING(1, new StringFilterType()),
    DOUBLE(2, new DoubleFilterType()),
    DATE(3, new DateFilterType()),
    TOP_CONTAINER(4, new TopContainerFilterType()),
    BOOLEAN(5, new BooleanFilterType()),
    FIRST_TIME_PROCESSED(6, new FirstTimeProcessedFilterType()),
    INTEGER(7, new IntegerFilterType()),
    ACTIVITY_STATUS(8, new ActivityStatusFilterType());

    private final int id;
    private final FilterType filterType;

    private FilterTypes(int id, FilterType filterType) {
        this.id = id;
        this.filterType = filterType;
    }

    public int getId() {
        return id;
    }

    public FilterType getFilterType() {
        return filterType;
    }

    public static FilterType getFilterType(int id) {
        // TODO: make a map once and use it instead
        for (FilterTypes filterTypes : FilterTypes.values()) {
            if (filterTypes.getId() == id) {
                return filterTypes.getFilterType();
            }
        }
        return null;
    }
}