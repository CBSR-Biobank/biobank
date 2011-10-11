package edu.ualberta.med.biobank.mvp.view;

import com.google.gwt.event.dom.client.HasClickHandlers;

public interface SaveableView extends View {
    HasClickHandlers getSave();
}
