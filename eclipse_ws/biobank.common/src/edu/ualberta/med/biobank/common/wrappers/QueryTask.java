package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

/**
 * Supplies an {@code SDKQuery} and can perform an action (via a hook) if and
 * when an {@code SDKQueryResult} is returned from the server.
 * 
 * @author jferland
 * 
 */
public interface QueryTask {
    /**
     * 
     * @return a (non-null) {@code SDKQuery} to execute.
     */
    public SDKQuery getSDKQuery();

    /**
     * Hook called after the {@code SDKQuery} is executed and an
     * {@code SDKQueryResult} is returned from the server.
     * 
     * @param result from the server.
     */
    public void afterExecute(SDKQueryResult result);
}
