package edu.ualberta.med.biobank.common.wrappers.tasks;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.QueryTask;
import edu.ualberta.med.biobank.common.wrappers.actions.NullAction;
import gov.nih.nci.system.query.SDKQuery;

public abstract class NullActionWrapperQueryTask<T, U extends ModelWrapper<T>>
    implements QueryTask {
    private final U wrapper;
    private final NullAction<T> nullAction;

    public NullActionWrapperQueryTask(U wrapper) {
        this.wrapper = wrapper;
        this.nullAction = new NullAction<T>(wrapper);
    }

    @Override
    public SDKQuery getSDKQuery() {
        return nullAction;
    }

    protected U getWrapper() {
        return wrapper;
    }
}
