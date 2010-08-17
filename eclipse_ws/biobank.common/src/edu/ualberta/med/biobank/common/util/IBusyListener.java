package edu.ualberta.med.biobank.common.util;

import java.util.EventListener;

public interface IBusyListener extends EventListener {

    public void showBusy();

    public void done();

}
