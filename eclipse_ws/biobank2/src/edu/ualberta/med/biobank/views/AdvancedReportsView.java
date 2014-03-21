package edu.ualberta.med.biobank.views;

import java.util.List;
import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISourceProviderListener;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportsGetAction;
import edu.ualberta.med.biobank.common.action.reports.AdvancedReportsGetAction.AdvancedReportsData;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.LoginPermissionSessionState;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.report.AbstractReportGroup;
import edu.ualberta.med.biobank.treeview.report.PrivateReportsGroup;
import edu.ualberta.med.biobank.treeview.report.SharedReportsGroup;

public class AdvancedReportsView extends AbstractAdministrationView {
    @SuppressWarnings("nls")
    public static final String ID = "edu.ualberta.med.biobank.views.AdvancedReportsView";

    private static final I18n i18n = I18nFactory.getI18n(AdvancedReportsView.class);

    private static BgcLogger logger = BgcLogger.getLogger(AdvancedReportsView.class.getName());

    @SuppressWarnings("nls")
    private static final String LOAD_FAILED_TITLE = i18n.tr("Load Failed");

    @SuppressWarnings("nls")
    private static final String INIT_FAILED_TITLE = i18n.tr("Exception in initialization");

    private static AdvancedReportsView currentView;

    private Boolean allowed = false;

    public AdvancedReportsView() {
        currentView = this;
        SessionManager.addView(this);
        try {
            if (SessionManager.getInstance().isConnected()) {
                allowed = SessionManager.getAppService().isAllowed(
                    new ReportsPermission());
            }
        } catch (Exception e) {
            BgcPlugin.openAccessDeniedErrorMessage(e);
        }
        BgcPlugin.getLoginStateSourceProvider().addSourceProviderListener(
            new ISourceProviderListener() {

                @SuppressWarnings("rawtypes")
                @Override
                public void sourceChanged(int sourcePriority, Map sourceValuesByName) {
                }

                @Override
                public void sourceChanged(int sourcePriority, String sourceName, Object sourceValue) {
                    try {
                        if (sourceName.equals(LoginPermissionSessionState.LOGIN_STATE_SOURCE_NAME)) {
                            if (sourceValue.equals(false)) {
                                allowed = false;
                            } else {
                                allowed = SessionManager.getAppService().isAllowed(
                                    new ReportsPermission());
                                reload();
                            }
                        }
                    } catch (Exception e) {
                        BgcPlugin.openAccessDeniedErrorMessage(e);
                    }
                }
            });
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);
        createNodes();
    }

    @Override
    public void reload() {
        rootNode.removeAll();
        if (allowed) {
            createNodes();

            for (AbstractAdapterBase adapter : rootNode.getChildren()) {
                adapter.rebuild();
            }

            super.reload();
        }
    }

    public static AdvancedReportsView getCurrent() {
        return currentView;
    }

    @SuppressWarnings("nls")
    private void createNodes() {
        if (!allowed) {
            throw new IllegalStateException("user is not allowed to view reports");
        }

        try {
            User user = SessionManager.getUser().getWrappedObject();
            AdvancedReportsData advancedReportsData = SessionManager.getAppService()
                .doAction(new AdvancedReportsGetAction(user));

            List<ReportWrapper> userReports = ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(),
                advancedReportsData.userReports,
                ReportWrapper.class);

            List<ReportWrapper> sharedReports = ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(),
                advancedReportsData.sharedReports,
                ReportWrapper.class);

            RootNode reportsRootNode = (RootNode) rootNode;

            AbstractReportGroup adapter =
                new PrivateReportsGroup(reportsRootNode, 0, userReports);
            adapter.setParent(rootNode);
            adapter.setModifiable(true);
            rootNode.addChild(adapter);

            adapter = new SharedReportsGroup(reportsRootNode, 1, sharedReports);
            adapter.setParent(rootNode);
            rootNode.addChild(adapter);
        } catch (final RemoteConnectFailureException exp) {
            BgcPlugin.openRemoteConnectErrorMessage(exp);
        } catch (ActionException e) {
            BgcPlugin.openAsyncError(LOAD_FAILED_TITLE, e);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(INIT_FAILED_TITLE, e);
            logger.error("AdvancedReportsView.createNodes Error", e);
        }
    }

    @Override
    protected void internalSearch() {
        //
    }

    @Override
    protected String getTreeTextToolTip() {
        return null;
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected void createRootNode() {
        createOldRootNode();
    }

}
