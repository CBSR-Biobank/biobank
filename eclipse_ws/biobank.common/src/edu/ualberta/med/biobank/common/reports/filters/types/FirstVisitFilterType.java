package edu.ualberta.med.biobank.common.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Subqueries;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.server.reports.ReportRunner;

public class FirstVisitFilterType implements FilterType {
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        FilterTypeUtil.checkValues(values, 0, 0);

        // Expects no FilterOperator and an aliasedProperty of "dateProcessed"

        // There is a bug that prevents joins on DetachedCriteria, so a patch
        // may have to be applied, see
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-952

        // Convert the aliasedProperty back into a property, get the parent, and
        // walk to the patient id
        String property = ReportRunner.getProperty(aliasedProperty);
        String parentProperty = ReportRunner.getParentProperty(property);
        String patientId = ReportRunner.getProperty(parentProperty,
            "shipmentPatient", "patient", "id");

        ReportRunner.createAssociations(criteria, patientId);
        String aliasedPatientId = ReportRunner.getAliasedProperty(patientId);

        DetachedCriteria minDateProcessed = DetachedCriteria
            .forClass(ProcessingEvent.class, "pv")
            .createAlias("pv.shipmentPatient", "sp")
            .createAlias("sp.patient", "p")
            .add(Property.forName("p.id").eqProperty(aliasedPatientId));
        minDateProcessed.setProjection(Property.forName("pv.dateProcessed")
            .min());

        criteria.add(Subqueries.propertyEq(aliasedProperty, minDateProcessed));
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList();
    }
}