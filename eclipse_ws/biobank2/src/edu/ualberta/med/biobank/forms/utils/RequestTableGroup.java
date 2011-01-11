package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.RequestAliquotState;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestAliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPath;
import edu.ualberta.med.biobank.model.RequestAliquot;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.RequestAliquotAdapter;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RequestTableGroup implements Node {

    private Integer numAliquots = 0;
    private RequestAliquotState state;
    private List<Object> tops;
    private static final Pattern p = Pattern.compile("/");
    private Object parent = null;

    private RequestTableGroup(RequestAliquotState state, RequestWrapper request) {
        this.state = state;
        getAdapterTree(state.getId(), request);
    }

    @Override
    public String toString() {
        return state.getLabel();
    }

    public String getTitle() {
        return state.getLabel() + " (" + numAliquots + ")";
    }

    public static List<RequestTableGroup> getGroupsForShipment(
        RequestWrapper ship) {
        List<RequestTableGroup> groups = new ArrayList<RequestTableGroup>();
        groups.add(new RequestTableGroup(RequestAliquotState.PROCESSED_STATE,
            ship));
        groups.add(new RequestTableGroup(
            RequestAliquotState.NONPROCESSED_STATE, ship));
        groups.add(new RequestTableGroup(RequestAliquotState.UNAVAILABLE_STATE,
            ship));
        return groups;
    }

    public void getAdapterTree(Integer state, RequestWrapper request) {
        List<Object[]> results = new ArrayList<Object[]>();
        // test hql
        HQLCriteria query = new HQLCriteria(
            "select ra, cp.container, cp.path from "
                + RequestAliquot.class.getName()
                + " ra inner join fetch ra.aliquot inner join fetch ra.aliquot.sampleType, "
                + ContainerPath.class.getName()
                + " cp where ra.request ="
                + request.getId()
                + " and ra.aliquot.aliquotPosition.container=cp.container and ra.state=?",
            Arrays.asList(new Object[] { state }));
        try {
            results = SessionManager.getAppService().query(query);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        HashSet<Integer> containers = new HashSet<Integer>();
        HashMap<Integer, RequestContainerAdapter> adapters = new HashMap<Integer, RequestContainerAdapter>();
        List<Object> tops = new ArrayList<Object>();

        // get all the containers to display
        for (Object o : results) {
            String path = (String) ((Object[]) o)[2];
            RequestAliquot ra = (RequestAliquot) ((Object[]) o)[0];
            Container container = (Container) ((Object[]) o)[1];
            String[] cIds = p.split(path);
            int i = 0;
            for (; i < cIds.length; i++) {
                Integer id = Integer.parseInt(cIds[i]);
                containers.add(id);
                RequestContainerAdapter adapter = null;
                if (!adapters.containsKey(id)) {
                    // add adapter
                    ContainerWrapper cw = new ContainerWrapper(
                        SessionManager.getAppService(), container);
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
                new RequestAliquotAdapter(adapters.get(Integer
                    .parseInt(cIds[i - 1])), new RequestAliquotWrapper(
                    SessionManager.getAppService(), ra)));
            numAliquots++;
        }

        this.tops = tops;
    }

    @Override
    public List<Object> getChildren() {
        return tops;
    }

    @Override
    public Object getParent() {
        return parent;
    }
}
