package edu.ualberta.med.biobank.rcp;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;

import edu.ualberta.med.biobank.forms.ScanLinkEntryForm;

public class BiobankPartListener implements IPartListener {

    @Override
    public void partActivated(IWorkbenchPart part) {

    }

    @Override
    public void partBroughtToTop(IWorkbenchPart part) {

    }

    @Override
    public void partClosed(IWorkbenchPart part) {
        if (part instanceof ScanLinkEntryForm) {
            ((ScanLinkEntryForm) part).onClose();
        }
    }

    @Override
    public void partDeactivated(IWorkbenchPart part) {

    }

    @Override
    public void partOpened(IWorkbenchPart part) {

    }

}
