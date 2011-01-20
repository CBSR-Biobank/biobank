package edu.ualberta.med.biobank.widgets.report;

import edu.ualberta.med.biobank.model.EntityFilter;

public class FilterChangeEvent extends ChangeEvent {
    private final EntityFilter entityFilter;
    private final boolean isSelected;

    public FilterChangeEvent(EntityFilter entityFilter) {
        this(entityFilter, true, true);
    }

    public FilterChangeEvent(EntityFilter entityFilter, boolean isSelected,
        boolean isDataChange) {
        super(isDataChange);
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
