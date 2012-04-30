package edu.ualberta.med.biobank.mvp.view;

import com.pietschy.gwt.pectin.client.value.ValueTarget;

public interface IFormView extends ICloseableView, IReloadableView {
    /**
     * Holds identifier Object whose job is to determine whether this view is
     * .equal() to another view.
     * 
     * @param identifier
     */
    ValueTarget<Object> getIdentifier();
}
