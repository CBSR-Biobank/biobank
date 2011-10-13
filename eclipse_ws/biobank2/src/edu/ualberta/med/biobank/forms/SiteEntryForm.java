package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEditPresenter;

public class SiteEntryForm extends EditorPart {

    public static final String ID = "edu.ualberta.med.biobank.forms.SiteEntryForm"; //$NON-NLS-1$

    private SiteEditPresenter presenter;

    @Override
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        presenter = BiobankPlugin.getInjector().getInstance(
            SiteEditPresenter.class);
    }

    @Override
    public void createPartControl(Composite parent) {
        // presenter.getDisplay();
        // presenter.bind();
    }

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        return true;
    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub
    }
}
