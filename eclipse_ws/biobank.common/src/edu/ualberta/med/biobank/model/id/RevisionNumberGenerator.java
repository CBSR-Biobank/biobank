package edu.ualberta.med.biobank.model.id;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.model.Revision;

public class RevisionNumberGenerator
    extends SmartTableGenerator {

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Type type, Properties params, Dialect dialect)
        throws MappingException {

        // always go back to fetch an id from the database so that the revision
        // numbers are truly sequential
        params.put(TableGenerator.INITIAL_PARAM, 1);

        super.configure(type, params, dialect);
    }

    @SuppressWarnings("nls")
    @Override
    public Serializable generate(SessionImplementor session, Object object)
        throws HibernateException {
        if (object instanceof Revision) {
            Revision revision = (Revision) object;

            Serializable id = super.generate(session, object);

            long time = System.currentTimeMillis();
            revision.setIdGeneratedAt(time);

            return id;
        }

        throw new RuntimeException("This persistent identifier generator " +
            "is intended to only work with the class "
            + Revision.class.getName());
    }
}
