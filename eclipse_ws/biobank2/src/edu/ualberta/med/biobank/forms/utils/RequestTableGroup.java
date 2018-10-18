package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.RequestReadInfo;
import edu.ualberta.med.biobank.common.action.request.RequestGetInfoAction;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.type.ItemState;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.request.RequestContainerAdapter;

public class RequestTableGroup extends TableGroup<RequestWrapper> {

    private static final I18n i18n = I18nFactory.getI18n(RequestTableGroup.class);

    @SuppressWarnings("unused")
    private static final Logger log = LoggerFactory.getLogger(RequestTableGroup.class);

    public RequestTableGroup(RequestSpecimenState ds,
                             String alternateLabel,
                             RequestWrapper request) {
        super(ds, alternateLabel, request);
    }

    public RequestTableGroup(RequestSpecimenState ds, RequestWrapper request) {
        super(ds, request);
    }

    @SuppressWarnings("nls")
    public static List<RequestTableGroup> getGroupsForRequest(RequestWrapper ship) {
        ArrayList<RequestTableGroup> groups =
            new ArrayList<RequestTableGroup>();
        groups.add(new RequestTableGroup(null,
                                         // tree node label
                                         i18n.tr("All"),
                                         ship));
        groups.add(new RequestTableGroup(RequestSpecimenState.PULLED_STATE, ship));
        return groups;
    }

    @SuppressWarnings("nls")
    @Override
    public void createAdapterTree(ItemState state, RequestWrapper request) throws Exception {
        RequestReadInfo requestReadInfo = null;

        try {
            requestReadInfo = SessionManager.getAppService()
                .doAction(new RequestGetInfoAction(request.getId()));
        } catch (Exception e) {
            BgcPlugin.openAsyncError(// dialog title
                                     i18n.tr("Error"),
                                     // dialog message
                                     i18n.tr("Unable to retrieve data from server"),
                                     e);
        }

        if (requestReadInfo == null) return;

        Map<Integer, RequestContainerAdapter> adapters = new HashMap<>();
        this.tops = new ArrayList<>();

        if (state == null) {
            // construct tree
            for (RequestSpecimen ra : requestReadInfo.request.getRequestSpecimens()) {

                List<Container> containerTree = new ArrayList<>();
                Container container = ra.getSpecimen().getSpecimenPosition().getContainer();
                while (container != null) {
                    containerTree.add(container);
                    container = container.getParentContainer();
                }
                Collections.reverse(containerTree);

                for (Container c: containerTree) {
                    Integer containerId = c.getId();
                    Integer topContainerId = c.getTopContainer().getId();
                    ContainerWrapper cw = new ContainerWrapper(SessionManager.getAppService(), c);
                    RequestContainerAdapter adapter = new RequestContainerAdapter(this, cw);

                    if (containerId == topContainerId) {
                        tops.add(adapter);
                    } else {
                        Integer parentContainerId = c.getParentContainer().getId();
                        RequestContainerAdapter parent = adapters.get(parentContainerId);
                        parent.addChild(adapter);
                        adapter.setParent(parent);

                    }
                    adapters.put(containerId, adapter);

                    RequestSpecimenWrapper wrapper =
                        new RequestSpecimenWrapper(SessionManager.getAppService(), ra);
                    adapter.addChild(new TreeItemAdapter(adapter, wrapper));
                }
                numSpecimens++;
            }
        } else {
            for (RequestSpecimen ra : requestReadInfo.request.getRequestSpecimens()) {
                if (ra.getState() == state) {
                    RequestSpecimenWrapper wrapper =
                        new RequestSpecimenWrapper(SessionManager.getAppService(), ra);
                    tops.add(new TreeItemAdapter(null, wrapper));
                    numSpecimens++;
                }
            }
        }
    }

    public void addChild(Node c) {
        tops.add(c);
        numSpecimens++;
    }

    @Override
    public void removeChild(Node o) {
        tops.remove(o);
        numSpecimens--;
    }

}
