package edu.ualberta.med.biobank.treeview.report;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportDeleteAction;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportGetAction;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportGetAction.ReportData;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.common.wrappers.UserWrapper;
import edu.ualberta.med.biobank.forms.ReportEntryForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.AdvancedReportsView;

public class ReportAdapter extends AdapterBase {
    private static final I18n i18n = I18nFactory.getI18n(ReportAdapter.class);

    private static BgcLogger logger = BgcLogger.getLogger(ReportAdapter.class.getName());

    @SuppressWarnings("nls")
    private static final String COPY_FAILED_TITLE = i18n.tr("Copy Failed");

    public ReportAdapter(AdapterBase parent, ReportWrapper report) {
        super(parent, report);
        this.isReadable = true;
        this.isEditable = true;
        this.isDeletable = true;
    }

    @Override
    protected String getLabelInternal() {
        String label = StringUtil.EMPTY_STRING;

        ReportWrapper report = (ReportWrapper) getModelObject();
        if ((report != null) && (report.getName() != null)) {
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
                ReportWrapper report = (ReportWrapper) getModelObject();

                UserWrapper user = SessionManager.getUser();

                boolean canDelete = (user.equals(report.getUser()))
                    || SessionManager.getUser().isSuperAdmin();

                if (canDelete) {
                    boolean delete = BgcPlugin.openConfirm(
                        i18n.tr("Delete Report"),
                        i18n.tr(
                            "Are you sure you want to delete the report named ''{0}''? This action cannot be undone.",
                            report.getName()));
                    if (delete) {
                        try {
                            AdvancedReportDeleteAction action =
                                new AdvancedReportDeleteAction(report.getId());
                            SessionManager.getAppService().doAction(action);
                            parent.removeChild(ReportAdapter.this);
                            AdvancedReportsView.getCurrent().reload();
                        } catch (Exception e) {
                            BgcPlugin.openAsyncError(
                                // dialog title
                                i18n.tr("Error"),
                                e.getLocalizedMessage());
                        }
                    }
                } else {
                    BgcPlugin.openAsyncError(
                        // dialog title
                        i18n.tr("Error"),
                        i18n.tr("Only the owner of the shared report or a super administrator can "
                            + "delete a shared report. The owner is: {0}",
                            report.getUser().getFullName()));

                }
            }
        });
    }

    @SuppressWarnings("nls")
    private void copyReport() {
        if (!SessionManager.getInstance().isConnected()) {
            throw new IllegalStateException("user is not logged in");
        }

        try {
            ReportWrapper reportToCopy = (ReportWrapper) getModelObject();

            ReportData reportData = SessionManager.getAppService().doAction(
                new AdvancedReportGetAction(reportToCopy.getWrappedObject()));

            ReportWrapper originalReport = new ReportWrapper(
                SessionManager.getAppService(), reportData.report);

            ReportWrapper report = new ReportWrapper(originalReport);

            String reportCopyName = i18n.tr("{0} Report Copy", report.getName());

            report.setName(reportCopyName);
            report.setUser(SessionManager.getUser());
            // OHSDEV
            ReportAdapter reportAdapter = new ReportAdapter((AdapterBase)getParent(), report);
            reportAdapter.openEntryForm();
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
        } catch (ActionException e) {
            BgcPlugin.openAsyncError(COPY_FAILED_TITLE, e);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(COPY_FAILED_TITLE, e);
            logger.error("ReportAdapter.copyReport Error", e);
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
    protected List<? extends ModelWrapper<?>> getWrapperChildren() throws Exception {
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
        if (o instanceof ReportAdapter) {
            return internalCompareTo(o);
        }
        return 0;
    }

}
