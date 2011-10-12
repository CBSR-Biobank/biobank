package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface ReloadableView extends BaseView {
    HasClickHandlers getReload();
}
