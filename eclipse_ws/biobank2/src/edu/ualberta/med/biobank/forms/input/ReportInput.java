package edu.ualberta.med.biobank.forms.input;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IPersistableElement;

import edu.ualberta.med.biobank.common.reports.ReportTreeNode;

public class ReportInput implements IEditorInput {

    public ReportTreeNode node;

    public ReportInput(ReportTreeNode node) {
        this.node = node;
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

    @SuppressWarnings("unchecked")
    @Override
    public Object getAdapter(Class adapter) {
        return null;
    }

}
