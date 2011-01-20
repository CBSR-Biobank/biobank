package edu.ualberta.med.biobank.widgets.report;

import edu.ualberta.med.biobank.model.EntityColumn;

public class ColumnChangeEvent {
    private final EntityColumn entityColumn;

    public ColumnChangeEvent(EntityColumn entityColumn) {
        this.entityColumn = entityColumn;
    }

    public EntityColumn getEntityColumn() {
        return entityColumn;
    }
}
