package edu.ualberta.med.biobank.common.wrappers.tasks;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public interface RebindableWrapperQueryTask extends QueryTask {
    /**
     * @return the {@link ModelWrapper} that should be rebound.
     */
    public ModelWrapper<?> getWrapperToRebind();
}
