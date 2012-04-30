package edu.ualberta.med.biobank.treeview.report;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportDeleteAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.forms.ReportEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.AdvancedReportsView;

public class ReportAdapter extends AdapterBase {
    public ReportAdapter(AdapterBase parent, ReportWrapper report) {
        super(parent, report);
        this.isReadable = true;
        this.isEditable = true;
        this.isDeletable = true;
    }

    @Override
    protected String getLabelInternal() {
        String label = ""; //$NON-NLS-1$

        ReportWrapper report = (ReportWrapper) getModelObject();
        if (report != null && report.getName() != null) {
            label = report.getName();
        }

        return label;
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Messages.ReportAdapter_report_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(Messages.ReportAdapter_copy_label);
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                copyReport();
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(Messages.ReportAdapter_delete_label);
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                boolean delete = BgcPlugin.openConfirm(
                    Messages.ReportAdapter_delete_confirm_title, NLS.bind(
                        Messages.ReportAdapter_delete_confirm_msg,
                        ((ReportWrapper) getModelObject()).getName()));
                if (delete) {
                    try {
                        AdvancedReportDeleteAction action =
                            new AdvancedReportDeleteAction(getModelObject()
                                .getId());
                        SessionManager.getAppService().doAction(action);
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
                (ReportWrapper) getModelObject());
            report.setName(report.getName()
                + " " + Messages.ReportAdapter_copy_naming); //$NON-NLS-1$

            int userId = SessionManager.getUser().getId().intValue();
            report.setUserId(userId);

            ReportAdapter reportAdapter =
                new ReportAdapter(getParent(), report);
            reportAdapter.openEntryForm();
        }
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getViewFormId() {
        return ReportEntryForm.ID;
    }

    @Override
    public String getEntryFormId() {
        return ReportEntryForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ReportAdapter)
            return internalCompareTo(o);
        return 0;
    }

}
