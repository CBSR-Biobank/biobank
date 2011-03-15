package edu.ualberta.med.biobank.common.reports.filters.types;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Subqueries;
import org.hibernate.impl.CriteriaImpl;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ReportFilterValue;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.reports.ReportRunner;

public class FirstTimeProcessedFilterType implements FilterType {
    /**
     * Expects no <code>FilterOperator</code> and the
     * <code>aliasedProperty</code> should be for the "createdAt" property of a
     * <code>ProcessingEvent</code> object.
     * 
     * @param criteria
     * @param aliasedProperty
     * @param op
     * @param values
     */
    @Override
    public void addCriteria(Criteria criteria, String aliasedProperty,
        FilterOperator op, List<ReportFilterValue> values) {

        FilterTypeUtil.checkValues(values, 0, 0);

        String patientIdAlias = null;
        if (criteria instanceof CriteriaImpl) {
            CriteriaImpl cimpl = (CriteriaImpl) criteria;
            String entityOrClassName = cimpl.getEntityOrClassName();

            if (Patient.class.getName().equals(entityOrClassName)) {
                patientIdAlias = "id";
            } else if (CollectionEvent.class.getName()
                .equals(entityOrClassName)) {
                String patientIdProperty = "patient.id";
                ReportRunner.createAssociations(criteria, "patient");
                patientIdAlias = ReportRunner
                    .getAliasedProperty(patientIdProperty);
            }
        }

        if (patientIdAlias == null) {
            throw new IllegalArgumentException(
                "Cannot determine path to patient id for the given Criteria");
        }

        // There is a bug that prevents joins on DetachedCriteria, so a patch
        // may have to be applied, see
        // http://opensource.atlassian.com/projects/hibernate/browse/HHH-952
        DetachedCriteria firstTimeProcessed = DetachedCriteria
            .forClass(Specimen.class, "s")
            .createAlias("s.collectionEvent", "ce")
            .createAlias("ce.patient", "p")
            .createAlias("s.parentSpecimen", "ps")
            .createAlias("ps.processingEvent", "pe")
            .add(Property.forName("p.id").eqProperty(patientIdAlias));
        firstTimeProcessed
            .setProjection(Property.forName("pe.createdAt").min());

        criteria
            .add(Subqueries.propertyEq(aliasedProperty, firstTimeProcessed));
    }

    @Override
    public Collection<FilterOperator> getOperators() {
        return Arrays.asList();
    }
}