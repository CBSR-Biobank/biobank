package edu.ualberta.med.biobank.server.reports;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;

import edu.ualberta.med.biobank.common.reports.filters.FilterOperator;
import edu.ualberta.med.biobank.common.reports.filters.FilterType;
import edu.ualberta.med.biobank.common.reports.filters.FilterTypes;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.EntityFilter;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.model.ReportColumn;
import edu.ualberta.med.biobank.model.ReportFilter;

public class ReportRunner {
    private static final String PROPERTY_DELIMITER = ".";
    private static final String ALIAS_DELIMITER = "-";

    // TODO: CLEAN UP THIS WHOLE CLASS!!! was a lame copy-paste from a
    // prototype.
    @SuppressWarnings("rawtypes")
    public static List runReport(Session session, Report report) {
        Criteria criteria = session.createCriteria(report.getEntity()
            .getClassName());

        criteria.setMaxResults(100);

        createAssociations(report, criteria);

        ProjectionList pList = Projections.projectionList();

        List<ReportColumn> orderedCols = new ArrayList<ReportColumn>(
            report.getReportColumnCollection());

        Collections.sort(orderedCols, new Comparator<ReportColumn>() {
            @Override
            public int compare(ReportColumn lhs, ReportColumn rhs) {
                return lhs.getPosition() - rhs.getPosition();
            }
        });

        int colNum = 1;
        for (ReportColumn col : orderedCols) {
            String path = col.getEntityColumn().getEntityProperty()
                .getProperty();
            String aliasedProperty = getAliasedProperty(path);

            Projection projection = null;
            if (col.getPropertyModifier() != null) {
                // TODO: resupport property modifiers.
                // String modifiedProperty = col.getPropertyModifier()
                // .modifyPath(getSqlColumn(criteria, aliasedProperty));
                //
                // // TODO: give a better alias than "_aliasx", which is may not
                // // be unique.
                // String sqlAlias = "_alias" + colNum;
                //
                // if (report.isCount) {
                // projection = Projections.sqlGroupProjection(
                // modifiedProperty + " as " + sqlAlias, sqlAlias,
                // new String[] { sqlAlias },
                // new Type[] { Hibernate.STRING });
                // } else {
                // projection = Projections.sqlProjection(modifiedProperty
                // + " as " + sqlAlias, new String[] { sqlAlias },
                // new Type[] { Hibernate.STRING });
                // }
            } else {
                if (report.isCount) {
                    projection = Projections.groupProperty(aliasedProperty);
                } else {
                    projection = Projections.property(aliasedProperty);
                }
            }

            pList.add(projection);
            colNum++;
        }

        if (report.isCount) {
            pList.add(Projections.countDistinct("id"));
        }

        criteria.setProjection(pList);

        Collection<ReportFilter> rfCollection = report
            .getReportFilterCollection();
        if (rfCollection != null) {
            for (ReportFilter reportFilter : rfCollection) {
                EntityFilter filter = reportFilter.getEntityFilter();
                FilterType filterType = FilterTypes.getFilterType(filter
                    .getFilterType());
                String propertyPath = filter.getEntityProperty().getProperty();
                String aliasedProperty = getAliasedProperty(propertyPath);

                Collection<String> rfvCollection = ReportWrapper
                    .getFilterValueStrings(reportFilter);

                filterType
                    .addCriteria(criteria, aliasedProperty, FilterOperator
                        .getFilterOperator(reportFilter.getOperator()),
                        new ArrayList<String>(rfvCollection));
            }
        }

        return criteria.list();
    }

    public static String getSqlColumn(Criteria criteria, String aliasedProperty) {
        CriteriaQueryTranslator translator = getCriteriaQueryTranslator(criteria);

        String alias = null;
        String propertyName = aliasedProperty;

        int lastDelimiter = aliasedProperty.lastIndexOf(PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            alias = aliasedProperty.substring(0, lastDelimiter);
            propertyName = aliasedProperty.substring(lastDelimiter + 1);
        }

        Criteria aliasCriteria = criteria;
        if (alias != null) {
            aliasCriteria = getCriteriaByAlias(criteria, alias);
        }

        return translator.getColumn(aliasCriteria, propertyName);
    }

    private static CriteriaQueryTranslator getCriteriaQueryTranslator(
        Criteria criteria) {
        CriteriaImpl criteriaImpl = (CriteriaImpl) criteria;
        SessionImplementor session = ((CriteriaImpl) criteria).getSession();
        SessionFactoryImplementor factory = session.getFactory();
        String[] implementors = factory.getImplementors(criteriaImpl
            .getEntityOrClassName());

        return new CriteriaQueryTranslator(factory, (CriteriaImpl) criteria,
            implementors[0], CriteriaQueryTranslator.ROOT_SQL_ALIAS);
    }

    private static Criteria getCriteriaByAlias(Criteria criteria, String alias) {
        @SuppressWarnings("rawtypes")
        Iterator subcriterias = ((CriteriaImpl) criteria).iterateSubcriteria();
        while (subcriterias.hasNext()) {
            Criteria subcriteria = (Criteria) subcriterias.next();
            if (subcriteria.getAlias().equals(alias)) {
                return subcriteria;
            }
        }
        return null;
    }

    private static void createAssociations(Report report, Criteria criteria) {
        Set<String> createdPaths = new HashSet<String>();

        Collection<ReportColumn> cols = report.getReportColumnCollection();
        if (cols != null) {
            for (ReportColumn col : cols) {
                String property = col.getEntityColumn().getEntityProperty()
                    .getProperty();
                createAssociations(criteria, property, createdPaths);
            }
        }

        Collection<ReportFilter> filters = report.getReportFilterCollection();
        if (filters != null) {
            for (ReportFilter filter : filters) {
                String property = filter.getEntityFilter().getEntityProperty()
                    .getProperty();
                createAssociations(criteria, property, createdPaths);
            }
        }
    }

    private static void createAssociations(Criteria criteria, String path,
        Set<String> createdPaths) {
        String parentPath = getParentPath(path);
        while (parentPath != null) {
            if (!createdPaths.contains(parentPath)) {
                // TODO: how do we determine if we should use a join or a left
                // join? CriteriaSpecification.LEFT_JOIN - NOPE, null is
                // confusing to the user.
                criteria.createCriteria(parentPath, getPathAlias(parentPath));
                createdPaths.add(parentPath);
            }
            parentPath = getParentPath(parentPath);
        }
    }

    private static String getPathAlias(String path) {
        return path == null ? null : path.replace(PROPERTY_DELIMITER,
            ALIAS_DELIMITER);
    }

    private static String getParentPath(String path) {
        String parentPath = null;
        int lastDelimiter = path.lastIndexOf(PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            parentPath = path.substring(0, lastDelimiter);
        }
        return parentPath;
    }

    private static String getAliasedProperty(String path) {
        int lastDelimiter = path.lastIndexOf(PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            String parentProperty = path.substring(0, lastDelimiter);
            String lastProperty = path.substring(lastDelimiter + 1);
            return getPathAlias(parentProperty) + PROPERTY_DELIMITER
                + lastProperty;
        }
        return path;
    }
}