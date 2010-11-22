package edu.ualberta.med.biobank.common.reports;

import java.util.Iterator;

import org.hibernate.Criteria;
import org.hibernate.engine.SessionFactoryImplementor;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.loader.criteria.CriteriaQueryTranslator;

public class ReportsUtil {
    // TODO: share this? get from elsewhere?
    private static final String PROPERTY_DELIMITER = ".";

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
}
