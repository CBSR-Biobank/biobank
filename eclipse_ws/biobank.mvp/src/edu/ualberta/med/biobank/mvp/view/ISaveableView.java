package edu.ualberta.med.biobank.mvp.view;

import com.pietschy.gwt.pectin.client.value.ValueTarget;

import edu.ualberta.med.biobank.mvp.user.ui.HasButton;

public interface ISaveableView extends IView {
    HasButton getSave();

    ValueTarget<Boolean> getDirty();
}
