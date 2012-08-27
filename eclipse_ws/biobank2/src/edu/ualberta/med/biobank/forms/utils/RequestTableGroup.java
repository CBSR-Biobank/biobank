package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.request.RequestGetSpecimenInfosAction;
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
    private static final I18n i18n = I18nFactory
        .getI18n(RequestTableGroup.class);

    public RequestTableGroup(RequestSpecimenState ds, String alternateLabel,
        RequestWrapper request) {
        super(ds, alternateLabel, request);
    }

    public RequestTableGroup(RequestSpecimenState ds, RequestWrapper request) {
        super(ds, request);
    }

    @SuppressWarnings("nls")
    public static List<RequestTableGroup> getGroupsForRequest(
        RequestWrapper ship) {
        ArrayList<RequestTableGroup> groups =
            new ArrayList<RequestTableGroup>();
        groups.add(new RequestTableGroup(null,
            // tree node label
            i18n.tr("All"), ship));
        groups.add(new RequestTableGroup(RequestSpecimenState.PULLED_STATE,
            ship));
        return groups;
    }

    @SuppressWarnings("nls")
    @Override
    public void createAdapterTree(ItemState state, RequestWrapper request)
        throws Exception {
        List<Object[]> results = new ArrayList<Object[]>();

        RequestGetSpecimenInfosAction specAction =
            new RequestGetSpecimenInfosAction(request.getId());
        try {
            results =
                SessionManager.getAppService().doAction(specAction).getList();
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Error"),
                // dialog message
                i18n.tr("Unable to retrieve data from server"), e);
        }

        HashSet<Integer> containers = new HashSet<Integer>();
        HashMap<Integer, RequestContainerAdapter> adapters =
            new HashMap<Integer, RequestContainerAdapter>();
        this.tops = new ArrayList<Node>();

        if (state == null) {
            // construct tree
            for (Object o : results) {
                String path = (String) ((Object[]) o)[1];
                RequestSpecimen ra = (RequestSpecimen) ((Object[]) o)[0];
                String[] cIds = p.split(path);
                int i = 0;
                for (; i < cIds.length; i++) {
                    Integer id = Integer.parseInt(cIds[i]);
                    containers.add(id);
                    RequestContainerAdapter adapter = null;
                    if (!adapters.containsKey(id)) {
                        // add adapter
                        DetachedCriteria c = DetachedCriteria
                            .forClass(Container.class)
                            .add(Restrictions.idEq(id));
                        ContainerWrapper cw = new ContainerWrapper(
                            SessionManager.getAppService(),
                            (Container) SessionManager.getAppService()
                                .query(c).get(0));
                        adapter = new RequestContainerAdapter(this, cw);
                        if (i == 0)
                            tops.add(adapter);
                        else {
                            RequestContainerAdapter parent = adapters
                                .get(Integer.parseInt(cIds[i - 1]));
                            parent.addChild(adapter);
                            adapter.setParent(parent);
                        }
                        adapters.put(id, adapter);
                    }
                }
                adapters.get(Integer.parseInt(cIds[i - 1])).addChild(
                    new TreeItemAdapter(adapters.get(Integer
                        .parseInt(cIds[i - 1])), new RequestSpecimenWrapper(
                        SessionManager.getAppService(), ra)));
                numSpecimens++;
            }
        } else {
            for (Object o : results) {
                RequestSpecimen ra = (RequestSpecimen) ((Object[]) o)[0];
                if (RequestSpecimenState.getState(ra.getState()).equals(state)) {
                    tops.add(new TreeItemAdapter(null,
                        new RequestSpecimenWrapper(SessionManager
                            .getAppService(), ra)));
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
