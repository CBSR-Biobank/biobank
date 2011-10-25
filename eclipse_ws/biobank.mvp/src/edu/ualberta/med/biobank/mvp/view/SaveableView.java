package edu.ualberta.med.biobank.mvp.view;

import edu.ualberta.med.biobank.mvp.user.ui.HasButton;

public interface SaveableView extends BaseView {
    // TODO: should probably use an interface that allows this "button" to be
    // enabled or disabled
    HasButton getSave();
}
