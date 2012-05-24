package edu.ualberta.med.biobank.views;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISourceProviderListener;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.reports.ReportsPermission;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.LoginPermissionSessionState;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.report.AbstractReportGroup;
import edu.ualberta.med.biobank.treeview.report.PrivateReportsGroup;
import edu.ualberta.med.biobank.treeview.report.SharedReportsGroup;

public class AdvancedReportsView extends AbstractAdministrationView {
    public static final String ID =
        "edu.ualberta.med.biobank.views.AdvancedReportsView"; //$NON-NLS-1$

    private static AdvancedReportsView currentView;

    private Boolean allowed = false;

    public AdvancedReportsView() {
        currentView = this;
        SessionManager.addView(this);
        try {
            if (SessionManager.getInstance().isConnected()) allowed =
                SessionManager.getAppService().isAllowed(
                    new ReportsPermission());
        } catch (Exception e) {
            BgcPlugin.openAccessDeniedErrorMessage(e);
        }
        BgcPlugin.getLoginStateSourceProvider()
            .addSourceProviderListener(new ISourceProviderListener() {

                @SuppressWarnings("rawtypes")
                @Override
                public void sourceChanged(int sourcePriority,
                    Map sourceValuesByName) {
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    try {
                        if (sourceName
                            .equals(LoginPermissionSessionState.LOGIN_STATE_SOURCE_NAME)) {
                            if (sourceValue.equals(false)) {
                                allowed = false;
                            } else {
                                allowed =
                                    SessionManager.getAppService().isAllowed(
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
        createNodes();

        for (AbstractAdapterBase adapter : rootNode.getChildren()) {
            adapter.rebuild();
        }

        super.reload();
    }

    public static AdvancedReportsView getCurrent() {
        return currentView;
    }

    private void createNodes() {
        if (allowed) {
            AbstractReportGroup adapter = new PrivateReportsGroup(
                (RootNode) rootNode, 0);
            adapter.setParent(rootNode);
            adapter.setModifiable(true);
            rootNode.addChild(adapter);

            adapter = new SharedReportsGroup((RootNode) rootNode, 1);
            adapter.setParent(rootNode);
            rootNode.addChild(adapter);
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
