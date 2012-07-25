package edu.ualberta.med.biobank.reports.filters.types;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.reports.ReportsUtil;
import edu.ualberta.med.biobank.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.reports.filters.FilterType;
import edu.ualberta.med.biobank.reports.filters.SelectableFilterType;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class TopContainerFilterType implements FilterType, SelectableFilterType {
    // TODO: this class should be replaced with a generic IdFilterType. The
    // getOptions() method should be replaced with an EntityFilter or
    // EntityProperty property that can specify a data source (hql query?)
    // Probably should be a property on EntityFilter that is an optional HQL
    // string to select valid options. Do this after merged in with others since
    // it will require additional model changes?
    private static final String OPTIONS_HQL = "select" //$NON-NLS-1$
        + " id, label || ' (' || containerType.nameShort || ') ' || site.nameShort" //$NON-NLS-1$
        + " from " + Container.class.getName() //$NON-NLS-1$
        + " where containerType.topLevel <> 0"; //$NON-NLS-1$

    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {
        if (values != null && values.size() > 0) {
            Collection<Integer> intValues = new ArrayList<Integer>();
            for (ReportFilterValue value : values) {
                intValues.add(Integer.parseInt(value.getValue()));
            }

            switch (op) {
            case IS_IN:
                criteria.add(Restrictions.in(aliasedProperty,
                    intValues.toArray()));
                break;
            case IS_NOT_IN:
                // TODO: use left join because not in includes Specimen-s
                // without any container.
                Disjunction or = ReportsUtil.idIsNullOr(aliasedProperty);
                or.add(Restrictions.not(Restrictions.in(aliasedProperty,
                    intValues.toArray())));
                criteria.add(or);
                break;
            }
        }
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList(FilterOperator.IS_IN, FilterOperator.IS_NOT_IN);
    }

    @Override
    public Map<String, String> getOptions(WritableApplicationService appService) {
        Map<String, String> options = new HashMap<String, String>();

        // TODO: should this be re-written in a security (read-access) conscious
        // manner?
        HQLCriteria criteria = new HQLCriteria(OPTIONS_HQL, Arrays.asList());
        try {
            List<Object> results = appService.query(criteria);
            for (Object o : results) {
                if (o instanceof Object[]) {
                    Object[] row = (Object[]) o;
                    options.put(row[0].toString(), row[1].toString());
                }
            }
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }

        return options;
    }
}