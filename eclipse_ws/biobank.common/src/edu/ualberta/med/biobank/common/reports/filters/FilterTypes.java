package edu.ualberta.med.biobank.common.reports.filters;

import edu.ualberta.med.biobank.common.reports.filters.types.BooleanFilterType;
import edu.ualberta.med.biobank.common.reports.filters.types.DateFilterType;
import edu.ualberta.med.biobank.common.reports.filters.types.NumberFilterType;
import edu.ualberta.med.biobank.common.reports.filters.types.StringFilterType;
import edu.ualberta.med.biobank.common.reports.filters.types.TopContainerFilterType;

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
    NUMBER(2, new NumberFilterType()),
    DATE(3, new DateFilterType()),
    TOP_CONTAINER(4, new TopContainerFilterType()),
    BOOLEAN(5, new BooleanFilterType());

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