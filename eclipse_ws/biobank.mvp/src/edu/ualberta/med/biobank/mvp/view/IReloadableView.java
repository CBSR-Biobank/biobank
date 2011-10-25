package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface IReloadableView extends IView {
    HasClickHandlers getReload();
}
