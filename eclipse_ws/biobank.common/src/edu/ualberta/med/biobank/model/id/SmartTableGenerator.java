package edu.ualberta.med.biobank.model.id;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.SessionImplementor;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.Type;

/**
 * 
 * 
 * @author Jonathan Ferland
 */
@SuppressWarnings("nls")
public class SmartTableGenerator extends TableGenerator {
    private String targetTable;
    private String targetColumn;

    private boolean initialized = false;

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Type type, Properties params, Dialect dialect)
        throws MappingException {

        targetTable = params.getProperty("target_table");
        targetColumn = params.getProperty("target_column");

        // let the 'next_val' column mean the next free value, NOT the next
        // free value minus the increment
        params.put(TableGenerator.OPT_PARAM, OptimizerFactory.POOL);

        // use the 'target_table' property as the segment name
        params.put(TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, "true");

        super.configure(type, params, dialect);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized Serializable generate(final SessionImplementor session,
        Object obj) {
        if (!initialized) {
            initialized = true;
            initialize(session);
        }
        Serializable id = super.generate(session, obj);
        return id;
    }

    private void initialize(final SessionImplementor session) {
        try {
            Field field = TableGenerator.class.getDeclaredField("initialValue");
            field.setAccessible(true);
            int defaultValue = field.getInt(this);
            int initialValue = selectInitialValue(session, defaultValue);
            field.set(this, initialValue);
        } catch (Exception e) {
            throw new HibernateException(
                "Unable to initialize table generator",
                e);
        }
    }

    private int selectInitialValue(final SessionImplementor session,
        int defaultValue) throws SQLException {
        int initialValue = defaultValue;

        Connection conn = session.connection();
        PreparedStatement selectPS = conn.prepareStatement(
            "select max(" + targetColumn + ") from " + targetTable);

        try {
            ResultSet selectRS = selectPS.executeQuery();
            if (selectRS.next()) {
                int maxValue = selectRS.getInt(1);
                initialValue = maxValue + 1;
            }
            selectRS.close();
        } finally {
            selectPS.close();
        }

        return initialValue;
    }
}