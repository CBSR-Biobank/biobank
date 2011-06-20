package edu.ualberta.med.biobank.common.reports;

import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;

public class ReportsUtil {
    // TODO: share this? get from elsewhere?
    private static final String PROPERTY_DELIMITER = "."; //$NON-NLS-1$

    public static String getId(String aliasedPropertyString) {
        AliasedProperty aliasedProperty = getAliasedProperty(aliasedPropertyString);
        return aliasedProperty.alias + PROPERTY_DELIMITER + "id"; //$NON-NLS-1$
    }

    public static Disjunction idIsNullOr(String aliasedProperty) {
        Disjunction or = Restrictions.disjunction();
        or.add(Restrictions.isNull(getId(aliasedProperty)));
        return or;
    }

    public static Criterion isNotSet(String aliasedProperty) {
        Disjunction or = Restrictions.disjunction();
        or.add(Restrictions.isNull(getId(aliasedProperty)));
        or.add(Restrictions.isNull(aliasedProperty));
        return or;
    }

    private static AliasedProperty getAliasedProperty(String aliasedProperty) {
        int lastDelimiter = aliasedProperty.lastIndexOf(PROPERTY_DELIMITER);
        if (lastDelimiter != -1) {
            String alias = aliasedProperty.substring(0, lastDelimiter);
            String propertyName = aliasedProperty.substring(lastDelimiter + 1);
            return new AliasedProperty(alias, propertyName);
        }
        return null;
    }

    public static String getSqlColumn(Criteria criteria,
        String aliasedPropertyString) {
        CriteriaQueryTranslator translator = getCriteriaQueryTranslator(criteria);

        String alias = null;
        String propertyName = aliasedPropertyString;

        AliasedProperty aliasedProperty = getAliasedProperty(aliasedPropertyString);
        if (aliasedProperty != null) {
            alias = aliasedProperty.alias;
            propertyName = aliasedProperty.property;
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

    private static class AliasedProperty {
        public final String alias;
        public final String property;

        public AliasedProperty(String alias, String property) {
            this.alias = alias;
            this.property = property;
        }
    }
}
