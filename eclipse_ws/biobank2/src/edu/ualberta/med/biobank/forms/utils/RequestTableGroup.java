package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ContainerPathPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.RequestSpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RequestTableGroup extends TableGroup<RequestWrapper> {

    public RequestTableGroup(RequestSpecimenState ds, RequestWrapper dispatch) {
        super(ds, dispatch);
    }

    public static final String TREE_QUERY2 = "select ra, cp."
        + ContainerPathPeer.PATH.getName() + " from "
        + RequestSpecimen.class.getName() + " ra inner join fetch ra."
        + RequestSpecimenPeer.SPECIMEN.getName() + " a inner join fetch a."
        + SpecimenPeer.SPECIMEN_TYPE.getName() + " st inner join fetch a."
        + SpecimenPeer.SPECIMEN_POSITION.getName() + " sp inner join fetch sp."
        + SpecimenPositionPeer.CONTAINER.getName() + " c inner join fetch c."
        + ContainerPeer.CONTAINER_PATH.getName() + " cp where ra."
        + RequestSpecimenPeer.REQUEST.getName() + "=? and ra."
        + RequestSpecimenPeer.STATE.getName() + "=?";

    public static final String TREE_QUERY = "select count(*) from specimen where id > ? and ? is not null";

    public static List<RequestTableGroup> getGroupsForShipment(
        RequestWrapper ship) {
        List<RequestTableGroup> groups = new ArrayList<RequestTableGroup>();
        groups.add(new RequestTableGroup(
            RequestSpecimenState.NONPROCESSED_STATE, ship));
        groups.add(new RequestTableGroup(RequestSpecimenState.PROCESSED_STATE,
            ship));
        groups.add(new RequestTableGroup(
            RequestSpecimenState.UNAVAILABLE_STATE, ship));
        return groups;
    }

    @Override
    public void createAdapterTree(Integer state, RequestWrapper request)
        throws Exception {
        List<Object[]> results = new ArrayList<Object[]>();
        // test hql
        HQLCriteria query = new HQLCriteria(TREE_QUERY,
            Arrays.asList(new Object[] { request, state }));
        try {
            results = SessionManager.getAppService().query(query);
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Error",
                "Unable to retrieve data from server");
        }

        HashSet<Integer> containers = new HashSet<Integer>();
        HashMap<Integer, RequestContainerAdapter> adapters = new HashMap<Integer, RequestContainerAdapter>();
        this.tops = new ArrayList<Node>();

        // get all the containers to display
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
                    adapter = new RequestContainerAdapter(null, cw);
                    if (i == 0)
                        tops.add(adapter);
                    else
                        adapters.get(Integer.parseInt(cIds[i - 1])).addChild(
                            adapter);
                    adapters.put(id, adapter);
                }
            }
            adapters.get(Integer.parseInt(cIds[i - 1])).addChild(
                new TreeItemAdapter(
                    adapters.get(Integer.parseInt(cIds[i - 1])),
                    new RequestSpecimenWrapper(SessionManager.getAppService(),
                        ra)));
            numSpecimens++;
        }

    }
}
