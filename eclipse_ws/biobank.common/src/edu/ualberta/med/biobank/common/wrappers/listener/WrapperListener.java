package edu.ualberta.med.biobank.common.wrappers.listener;

import java.util.EventListener;

public interface WrapperListener extends EventListener {

    public void updated(WrapperEvent event);

    public void inserted(WrapperEvent event);

    public void deleted(WrapperEvent event);

}
