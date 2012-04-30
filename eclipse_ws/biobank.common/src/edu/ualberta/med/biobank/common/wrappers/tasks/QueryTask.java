package edu.ualberta.med.biobank.common.wrappers.tasks;

import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

/**
 * Supplies an {@link SDKQuery} and can perform an action (via a hook) if and
 * when an {@link SDKQueryResult} is returned from the server.
 * 
 * @author jferland
 * 
 */
public interface QueryTask {
    /**
     * 
     * @return a (non-null) {@link SDKQuery} to execute.
     */
    public SDKQuery getSDKQuery();

    /**
     * Hook called after the {@link SDKQuery} is executed and an
     * {@link SDKQueryResult} is returned from the server.
     * 
     * @param result from the server.
     */
    public void afterExecute(SDKQueryResult result);
}
