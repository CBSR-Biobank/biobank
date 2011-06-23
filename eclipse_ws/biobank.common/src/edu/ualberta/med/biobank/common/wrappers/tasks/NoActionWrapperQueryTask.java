package edu.ualberta.med.biobank.common.wrappers.tasks;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.NoActionWrapperAction;
import gov.nih.nci.system.query.SDKQuery;

public abstract class NoActionWrapperQueryTask<E extends ModelWrapper<?>>
    implements QueryTask {
    private final E wrapper;
    private final NoActionWrapperAction<?> noActionAction;

    public NoActionWrapperQueryTask(E wrapper) {
        this.wrapper = wrapper;

        // TODO: get around generics in a better way?
        @SuppressWarnings({ "rawtypes", "unchecked" })
        NoActionWrapperAction tmp = new NoActionWrapperAction(wrapper);
        this.noActionAction = tmp;
    }

    @Override
    public SDKQuery getSDKQuery() {
        return noActionAction;
    }

    protected E getWrapper() {
        return wrapper;
    }
}
