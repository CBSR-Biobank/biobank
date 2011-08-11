package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.RequestSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.util.ItemState;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.request.RequestContainerAdapter;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RequestTableGroup extends TableGroup<RequestWrapper> {

    public RequestTableGroup(RequestSpecimenState ds, String alternateLabel,
        RequestWrapper request) {
        super(ds, alternateLabel, request);
    }

    public RequestTableGroup(RequestSpecimenState ds, RequestWrapper request) {
        super(ds, request);
    }

    public static final String TREE_QUERY = "select ra, concat(c.path, concat('/', c.id)), c.id, a.id, st.id, sp.id from " //$NON-NLS-1$
        + RequestSpecimen.class.getName() + " ra inner join fetch ra." //$NON-NLS-1$
        + RequestSpecimenPeer.SPECIMEN.getName() + " a inner join fetch a." //$NON-NLS-1$
        + SpecimenPeer.SPECIMEN_TYPE.getName() + " st inner join fetch a." //$NON-NLS-1$
        + SpecimenPeer.SPECIMEN_POSITION.getName() + " sp inner join fetch sp." //$NON-NLS-1$
        + SpecimenPositionPeer.CONTAINER.getName() + " c where ra." //$NON-NLS-1$
        + RequestSpecimenPeer.REQUEST.getName() + ".id=? order by ra." //$NON-NLS-1$
        + RequestSpecimenPeer.STATE.getName();

    public static List<RequestTableGroup> getGroupsForRequest(
        RequestWrapper ship) {
        ArrayList<RequestTableGroup> groups = new ArrayList<RequestTableGroup>();
        groups.add(new RequestTableGroup(null,
            Messages.RequestTableGroup_all_node_label, ship));
        groups.add(new RequestTableGroup(RequestSpecimenState.PULLED_STATE,
            ship));
        return groups;
    }

    @Override
    public void createAdapterTree(ItemState state, RequestWrapper request)
        throws Exception {
        List<Object[]> results = new ArrayList<Object[]>();
        // test hql
        HQLCriteria query = new HQLCriteria(TREE_QUERY,
            Arrays.asList(new Object[] { request.getId() }));
        try {
            results = SessionManager.getAppService().query(query);
        } catch (Exception e) {
            BgcPlugin.openAsyncError(Messages.RequestTableGroup_error_title,
                Messages.RequestTableGroup_data_error_msg, e);
        }

        HashSet<Integer> containers = new HashSet<Integer>();
        HashMap<Integer, RequestContainerAdapter> adapters = new HashMap<Integer, RequestContainerAdapter>();
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
                        Container c = new Container();
                        c.setId(id);
                        ContainerWrapper cw = new ContainerWrapper(
                            SessionManager.getAppService(),
                            (Container) SessionManager.getAppService()
                                .search(Container.class, c).get(0));
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
