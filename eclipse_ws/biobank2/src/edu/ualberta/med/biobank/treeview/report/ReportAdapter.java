package edu.ualberta.med.biobank.treeview.report;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.forms.ReportEntryForm;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.AdvancedReportsView;

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
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Copy");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                copyReport();
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Delete");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                boolean delete = BiobankGuiCommonPlugin.openConfirm("Delete Report",
                    "Are you sure you want to delete the report named '"
                        + ((ReportWrapper) modelObject).getName()
                        + "'? This action cannot be undone.");
                if (delete) {
                    try {
                        modelObject.delete();
                        parent.removeChild(ReportAdapter.this);
                        AdvancedReportsView.getCurrent().reload();
                    } catch (Exception e) {
                    }
                }
            }
        });
    }

    private void copyReport() {
        if (SessionManager.getInstance().isConnected()) {
            ReportWrapper report = new ReportWrapper(
                (ReportWrapper) modelObject);
            report.setName(report.getName() + " Copy");

            int userId = SessionManager.getUser().getId().intValue();
            report.setUserId(userId);

            ReportAdapter reportAdapter = new ReportAdapter(this.parent, report);
            reportAdapter.openEntryForm();
        }
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
