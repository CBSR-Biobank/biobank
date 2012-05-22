package edu.ualberta.med.biobank.test.model.util;

import org.apache.commons.lang.StringUtils;
import org.hibernate.EntityMode;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;

public class HibernateHelper {
    public static Query getDehydratedPropertyQuery(Session session, Object o,
        String propertyName) {
        ClassMetadata meta = session.getSessionFactory()
            .getClassMetadata(o.getClass());
        AbstractEntityPersister persister = (AbstractEntityPersister) meta;
        String[] columnNames = persister.getPropertyColumnNames(propertyName);
        SQLQuery query = session.createSQLQuery(
            "SELECT " + StringUtils.join(columnNames, ", ") +
                " FROM " + persister.getTableName() +
                " WHERE " + persister.getIdentifierPropertyName() + " = ?");
        query.setParameter(0, persister.getIdentifier(o, EntityMode.POJO));
        return query;
    }
}
