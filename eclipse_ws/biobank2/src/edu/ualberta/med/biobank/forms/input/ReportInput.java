package edu.ualberta.med.biobank.forms.input;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.common.reports.QueryObject;

public class ReportInput implements IEditorInput {

    public Class<? extends QueryObject> query;

    public ReportInput(Class<? extends QueryObject> q) {
        this.query = q;
    }

    @Override
    public boolean exists() {
        return true;
    }

    @Override
    public ImageDescriptor getImageDescriptor() {
        return null;
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public IPersistableElement getPersistable() {
        return null;
    }

    @Override
    public String getToolTipText() {
        return "";
    }

    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

}
