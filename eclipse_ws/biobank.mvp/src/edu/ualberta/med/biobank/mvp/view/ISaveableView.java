package edu.ualberta.med.biobank.mvp.view;

import com.pietschy.gwt.pectin.client.value.ValueTarget;

import edu.ualberta.med.biobank.mvp.user.ui.IButton;

public interface ISaveableView extends IView {
    IButton getSave();

    ValueTarget<Boolean> getDirty();
}
