package edu.ualberta.med.biobank.common.wrappers;


/**
 * Convenience class for defining cascade-type methods to be performed.
 * 
 * @author jferland
 * 
 * @param <E> wrapped object
 */
public class WrapperCascader<E> {
    private final ModelWrapper<E> wrapper;

    /**
     * @param wrapper the {@link ModelWrapper} to perform the cascade methods on
     */
    WrapperCascader(ModelWrapper<E> wrapper) {
        this.wrapper = wrapper;
    }

}
