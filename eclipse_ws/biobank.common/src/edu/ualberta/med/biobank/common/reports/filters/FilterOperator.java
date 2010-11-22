package edu.ualberta.med.biobank.common.reports.filters;

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
    BETWEEN(1),
    NOT_BETWEEN(2),
    IN(3),
    NOT_IN(4),

    // String operators
    MATCHES(101),
    DOES_NOT_MATCH(102),
    MATCHES_ALL(103),

    // Number operators
    LESS_THAN(201),
    LESS_THAN_OR_EQUAL_TO(202),
    GREATER_THAN(203),
    GREATER_THAN_OR_EQUAL_TO(204),

    // Date operators
    ON_OR_BEFORE(301),
    ON_OR_AFTER(302),

    THIS_DAY(321),
    THIS_WEEK(322),
    THIS_MONTH(323),
    THIS_YEAR(324),

    SAME_DAY_AS(341),
    SAME_WEEK_AS(342),
    SAME_MONTH_AS(343),
    SAME_YEAR_AS(344),

    // Custom filter operators
    IS_NOT_IN_A_CONTAINER(401);

    private final int id;
    private final boolean isSetOperator;
    private final boolean isRange = false; // TODO: support range, which means 2
                                           // inputs are required
    private final boolean isValueRequired = true; // TODO: value may not be
                                                  // required, eg
                                                  // "today, this week, etc."

    private FilterOperator(int id, boolean isSetOperator) {
        this.id = id;
        this.isSetOperator = isSetOperator;
    }

    private FilterOperator(int id) {
        this(id, false);
    }

    public int getId() {
        return id;
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