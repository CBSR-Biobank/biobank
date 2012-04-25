package edu.ualberta.med.biobank.treeview.report;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportDeleteAction;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.forms.ReportEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.AdvancedReportsView;

public class ReportAdapter extends AdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(ReportAdapter.class);

    public ReportAdapter(AdapterBase parent, ReportWrapper report) {
        super(parent, report);
    }

    @Override
    protected String getLabelInternal() {
        String label = StringUtil.EMPTY_STRING;

        ReportWrapper report = (ReportWrapper) getModelObject();
        if (report != null && report.getName() != null) {
            label = report.getName();
        }

        return label;
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Report.NAME.singular().toString());
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(i18n.tr("Copy"));
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                copyReport();
            }
        });

        mi = new MenuItem(menu, SWT.PUSH);
        mi.setText(i18n.tr("Delete"));
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                boolean delete =
                    BgcPlugin.openConfirm(
                        i18n.tr("Delete Report"),
                        i18n.tr(
                            "Are you sure you want to delete the report named ''{0}''? This action cannot be undone.",
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

            @SuppressWarnings("nls")
            String reportCopyName =
                i18n.tr("{0} Report Copy", report.getName());
            report.setName(reportCopyName);

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
