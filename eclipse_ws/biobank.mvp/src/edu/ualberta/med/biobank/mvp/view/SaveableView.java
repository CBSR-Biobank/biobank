package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface SaveableView extends BaseView {
    // TODO: should probably use an interface that allows this "button" to be
    // enabled or disabled
    HasClickHandlers getSave();
}
