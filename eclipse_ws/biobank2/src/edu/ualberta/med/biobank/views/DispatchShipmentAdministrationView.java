package edu.ualberta.med.biobank.views;

import java.util.Map;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.DispatchShipmentWrapper;
import edu.ualberta.med.biobank.rcp.DispatchShipmentAdministrationPerspective;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.treeview.DispatchNode;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.widgets.AdapterTreeWidget;

public class DispatchShipmentAdministrationView extends
    AbstractViewWithAdapterTree {

    public static final String ID = "edu.ualberta.med.biobank.views.DispatchShipmentAdmininistrationView";

    public DispatchShipmentAdministrationView() {
        SessionManager.addView(DispatchShipmentAdministrationPerspective.ID,
            this);
        setSiteManagement();
    }

    private void setSiteManagement() {
        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);

        ISourceProviderListener siteStateListener = new ISourceProviderListener() {
            @Override
            public void sourceChanged(int sourcePriority, String sourceName,
                Object sourceValue) {
                if (sourceName.equals(SiteSelectionState.SITE_SELECTION_ID)) {
                    rootNode.removeAll();
                    if (sourceValue != null
                        && !SessionManager.getInstance().isAllSitesSelected()) {
                        reload();
                    }
                }
            }

            @Override
            public void sourceChanged(int sourcePriority,
                @SuppressWarnings("rawtypes") Map sourceValuesByName) {
            }
        };

        siteSelectionStateSourceProvider
            .addSourceProviderListener(siteStateListener);
    }

    @Override
    public void createPartControl(Composite parent) {
        adaptersTree = new AdapterTreeWidget(parent, false);
        rootNode = new RootNode();
        getTreeViewer().setInput(rootNode);
    }

    @Override
    public void reload() {
        rootNode.removeAll();
        DispatchNode<DispatchShipmentWrapper> sent = new DispatchNode<DispatchShipmentWrapper>(
            rootNode, "Sent");
        sent.addChildren(SessionManager.getInstance().getCurrentSite()
            .getSentDispatchShipmentCollection());
        DispatchNode<DispatchShipmentWrapper> received = new DispatchNode<DispatchShipmentWrapper>(
            rootNode, "Received");
        received.addChildren(SessionManager.getInstance().getCurrentSite()
            .getReceivedDispatchShipmentCollection());
        rootNode.addChild(received);
        rootNode.addChild(sent);
    }

}
