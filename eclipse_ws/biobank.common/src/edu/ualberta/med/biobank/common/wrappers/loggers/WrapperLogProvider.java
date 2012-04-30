package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.Log;

/**
 * Implementing classes will be serialised and sent to the server to explain how
 * to log the appropriate information for the given model object. Specifically,
 * the center, patientNumber, inventoryId, and locationLabel properties should
 * be set. Everything else will be set some other way.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public interface WrapperLogProvider<E> extends Serializable {
    public Log getLog(E model);

    // FIXME should not need this but the method calling this can't cast...
    public Log getObjectLog(Object model);
}
