package edu.ualberta.med.biobank.common.action.reports;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.EntityGetAction.EntityData;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.EntityColumn;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.EntityProperty;
import edu.ualberta.med.biobank.model.Report;

public class EntityGetAction implements Action<EntityData> {
    private static final long serialVersionUID = 1L;

    private final Integer entityId;

    @SuppressWarnings("nls")
    public EntityGetAction(Entity entity) {
        if (entity == null) {
            throw new IllegalArgumentException("entity is null");
        }
        this.entityId = entity.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ReportsPermission().isAllowed(context);
    }

    @Override
    public EntityData run(ActionContext context) throws ActionException {
        Entity entity = context.load(Entity.class, entityId);

        for (Report report : entity.getReports()) {
            report.getIsPublic();
        }

        List<EntityColumn> columns = new ArrayList<EntityColumn>();
        List<EntityFilter> filters = new ArrayList<EntityFilter>();

        for (EntityProperty entityProperty : entity.getEntityProperties()) {
            for (EntityColumn entityColumn : entityProperty.getEntityColumns()) {
                entityColumn.getName();
            }
            columns.addAll(entityProperty.getEntityColumns());

            for (EntityFilter entityFilter : entityProperty.getEntityFilters()) {
                entityFilter.getName();
            }
            filters.addAll(entityProperty.getEntityFilters());
        }

        return new EntityData(entity, columns, filters);
    }

    public static class EntityData implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final Entity entity;
        private final List<EntityColumn> columns;
        private final List<EntityFilter> filters;

        public EntityData(Entity entity,
            List<EntityColumn> columns,
            List<EntityFilter> filters) {
            this.entity = entity;
            this.columns = columns;
            this.filters = filters;
        }

        public Entity getEntity() {
            return entity;
        }

        public List<EntityColumn> getColumns() {
            return columns;
        }

        public List<EntityFilter> getFilters() {
            return filters;
        }
    }
}
