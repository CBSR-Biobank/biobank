package edu.ualberta.med.biobank.reports.filters;

/**
 * These <code>enum</code>-s should only be @Depricate-d, NEVER DELETED. They
 * correspond to database entries and if are altered may result in catastrophe.
 * 
 * @author jferland
 * 
 */
public enum FilterOperator {
    // These enum-s should only be @Deprecated, NEVER DELETED. They
    // correspond to database entries and if are altered may result in
    // catastrophe (at least if the id value is altered).

    // Shared operators
    BETWEEN(1, false, true), NOT_BETWEEN(2, false, true), IS_IN(3), IS_NOT_IN(4), BETWEEN_ANY(
        5), NOT_BETWEEN_ANY(6), IS_NOT_SET(7, false, false), EQUALS(8, false,
        true), DOES_NOT_EQUAL(9, false, true), IS_SET(10, false, false),

    // String operators
    MATCHES(101, false, true), DOES_NOT_MATCH(102, false, true), MATCHES_ALL(
        103), MATCHES_ANY(104), DOES_NOT_MATCH_ANY(105),

    // Number operators
    LESS_THAN(201, false, true), LESS_THAN_OR_EQUAL_TO(202, false, true), GREATER_THAN(
        203, false, true), GREATER_THAN_OR_EQUAL_TO(204, false, true),

    // Date operators
    ON_OR_BEFORE(301, false, true), ON_OR_AFTER(302, false, true),

    THIS_DAY(321, false, false), THIS_WEEK(322, false, false), THIS_MONTH(323,
        false, false), THIS_YEAR(324, false, false),

    SAME_DAY_AS_ANY(341), SAME_WEEK_AS_ANY(342), SAME_MONTH_AS_ANY(343), SAME_YEAR_AS_ANY(
        344),

    // Boolean operators
    YES(401, false, false), NO(402, false, false),

    // Custom filter operators
    IS_NOT_IN_A_CONTAINER(1001, false, false);

    private final int id;
    private final boolean isSetOperator;
    private final boolean isValueRequired;

    private FilterOperator(int id, boolean isSetOperator,
        boolean isValueRequired) {
        this.id = id;
        this.isSetOperator = isSetOperator;
        this.isValueRequired = isValueRequired;
    }

    private FilterOperator(int id) {
        this(id, true, true);
    }

    public int getId() {
        return id;
    }

    public boolean isValueRequired() {
        return isValueRequired;
    }

    public boolean isSetOperator() {
        return isSetOperator;
    }

    public String getDisplayString() {
        return this.name().replace('_', ' ').toLowerCase();
    }

    public static FilterOperator getFilterOperator(int id) {
        // TODO: make a map once and use it instead
        for (FilterOperator op : FilterOperator.values()) {
            if (op.getId() == id) {
                return op;
            }
        }
        return null;
    }
}