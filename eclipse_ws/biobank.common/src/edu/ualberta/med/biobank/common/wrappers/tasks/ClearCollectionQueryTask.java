package edu.ualberta.med.biobank.common.wrappers.tasks;

import java.util.Collection;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.NoActionWrapperAction;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;

/**
 * Clears the given {@link Collection} (literally calls {@code clear()}) after
 * this task is executed. Useful for clearing an internal list in a
 * {@link ModelWrapper} that is used for tracking other affected
 * {@link ModelWrapper}-s. Note that the {@link Collection} reference is never
 * exposed.
 * 
 * @author jferland
 * 
 */
public class ClearCollectionQueryTask implements QueryTask {
    private final Collection<?> collectionToClear;
    private final NoActionWrapperAction<?> noActionAction;

    public <T, U extends ModelWrapper<T>> ClearCollectionQueryTask(U wrapper,
        Collection<?> collectionToClear) {
        this.collectionToClear = collectionToClear;
        this.noActionAction = new NoActionWrapperAction<T>(wrapper);
    }

    @Override
    public SDKQuery getSDKQuery() {
        return noActionAction;
    }

    @Override
    public void afterExecute(SDKQueryResult result) {
        collectionToClear.clear();
    }
}
