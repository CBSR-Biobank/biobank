package edu.ualberta.med.biobank.widgets.report;

import edu.ualberta.med.biobank.model.EntityFilter;

public class FilterChangeEvent {
    private final EntityFilter entityFilter;
    private final boolean isSelected;

    public FilterChangeEvent(EntityFilter entityFilter) {
        this(entityFilter, true);
    }

    public FilterChangeEvent(EntityFilter entityFilter, boolean isSelected) {
        this.entityFilter = entityFilter;
        this.isSelected = isSelected;
    }

    public EntityFilter getEntityFilter() {
        return entityFilter;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
