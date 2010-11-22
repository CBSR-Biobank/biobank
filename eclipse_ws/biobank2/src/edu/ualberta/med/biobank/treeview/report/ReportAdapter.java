package edu.ualberta.med.biobank.treeview.report;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.forms.ReportEntryForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReportAdapter extends AdapterBase {
    public ReportAdapter(AdapterBase parent, ReportWrapper report) {
        super(parent, report);
    }

    public ReportWrapper getWrapper() {
        return (ReportWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        String label = "";

        ReportWrapper report = getWrapper();
        if (report != null && report.getName() != null) {
            label = report.getName();
        }

        return label;
    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Report");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        // TODO Auto-generated method stub
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getViewFormId() {
        return ReportEntryForm.ID;
    }

    @Override
    public String getEntryFormId() {
        return ReportEntryForm.ID;
    }

}
