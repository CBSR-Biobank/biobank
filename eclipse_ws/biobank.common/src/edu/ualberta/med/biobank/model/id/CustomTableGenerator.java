package edu.ualberta.med.biobank.model.id;

import java.util.Properties;

import org.hibernate.MappingException;
import org.hibernate.dialect.Dialect;
import org.hibernate.id.enhanced.OptimizerFactory;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.type.Type;

/**
 * Inherits from {@link TableGenerator} with the following changes:
 * <p>
 * <ul>
 * <li>Force the use of the {@link OptimizerFactory.POOL} strategy, where the
 * value shown in the database is the next legal value to use. That is, the
 * value is the low end of the increment size, as opposed to the high end.</li>
 * <li>Force the segment value default to be the entity's table name (note that
 * this can still be overridden).</li>
 * <li>If there is no existing next value in the database for the given segment,
 * use one more than the current maximum id. If the table is empty, then use the
 * configured initial value.</li>
 * </ul>
 * 
 * @author Jonathan Ferland
 */
@SuppressWarnings("nls")
public class CustomTableGenerator extends TableGenerator {
    private String insertQuery;

    /**
     * {@inheritDoc}
     */
    @Override
    public void configure(Type type, Properties params, Dialect dialect)
        throws MappingException {

        insertQuery = buildInsertQuery(params, dialect);

        // make sure the 'next_val' column means the next free value, NOT the
        // next free value minus the increment
        params.put(TableGenerator.OPT_PARAM, OptimizerFactory.POOL);

        // make sure to use the 'target_table' property as the segment name
        params.put(TableGenerator.CONFIG_PREFER_SEGMENT_PER_ENTITY, "true");

        super.configure(type, params, dialect);
    }

    private String buildInsertQuery(Properties params, Dialect dialect) {
        String tableName = determineGeneratorTableName(params, dialect);
        String segmentColumnName = determineSegmentColumnName(params, dialect);
        String valueColumnName = determineValueColumnName(params, dialect);

        String targetTable = params.getProperty("target_table");
        String targetColumn = params.getProperty("target_column");

        return "insert into " + tableName + " (" + segmentColumnName + ", "
            + valueColumnName + ") " + " values (?, (select coalesce(max("
            + targetColumn + ") + 1, ?) from " + targetTable + "))";
    }

    @Override
    protected String buildInsertQuery() {
        return insertQuery;
    }
}