package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

/**
 * A bare-bones implementation of {@code QueryTask} that does nothing but hold
 * an {@code SDKQuery}.
 * 
 * @author jferland
 * 
 */
public class InactiveQueryTask implements QueryTask {
    private final SDKQuery sdkQuery;

    public InactiveQueryTask(SDKQuery sdkQuery) {
        this.sdkQuery = sdkQuery;
    }

    @Override
    public SDKQuery getSDKQuery() {
        return sdkQuery;
    }

    @Override
    public void afterExecute(SDKQueryResult result) {
    }
}
