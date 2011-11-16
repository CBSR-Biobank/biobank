package edu.ualberta.med.biobank.mvp.model;

import com.pietschy.gwt.pectin.client.bean.HasDirtyModel;
import com.pietschy.gwt.pectin.client.binding.Disposable;

public interface ITracker extends HasDirtyModel, Disposable {
    void checkpoint();

    void revert();
}
