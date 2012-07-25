package edu.ualberta.med.biobank.model.id;

import java.io.Serializable;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.model.Revision;

/**
 * Intended only to work with the {@link Revision} ({@link @RevisionEntity}) to
 * generate its {@link Revision#getId()} ({@link @RevisionNumber}). After the id
 * (revision number) is set, the time is also recorded. Note that it would be
 * better to record the time from the database or somewhere else, but MySQL does
 * not have millisecond precision (without a custom UDF).
 * 
 * @author Jonathan Ferland
 */
public class RevisionNumberGenerator extends CustomTableGenerator {

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
            revision.setGeneratedAt(System.currentTimeMillis());
            return id;
        }

        throw new RuntimeException("This persistent identifier generator " +
            "is intended to only work with " + Revision.class.getName());
    }
}
