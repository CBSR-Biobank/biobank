package edu.ualberta.med.biobank.mvp.presenter;

import com.pietschy.gwt.pectin.client.bean.HasDirtyModel;

public interface IDirtiablePresenter extends HasDirtyModel {
    /**
     * Takes a snapshot of the current data, which should clear the dirty state.
     */
    void checkpoint();

    /**
     * Reverts the data to the last checkpoint.
     */
    void revert();
}
