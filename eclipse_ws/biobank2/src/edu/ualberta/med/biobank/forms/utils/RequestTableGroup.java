package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import edu.ualberta.med.biobank.treeview.admin.RequestContainerAdapter;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class RequestTableGroup extends TableGroup<RequestWrapper> {

    public RequestTableGroup(RequestSpecimenState ds, RequestWrapper dispatch) {
        super(ds, dispatch);
    }

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
    public void createAdapterTree(Integer state, RequestWrapper request) {
        List<Object[]> results = new ArrayList<Object[]>();
        // test hql
        HQLCriteria query = new HQLCriteria(
            "select ra, cp.container, c.path from "
                + Request.class.getName()
                + " ra inner join fetch ra.specimen inner join fetch ra.specimen.specimenType, "
                + Container.class.getName()
                + " c where ra.request ="
                + request.getId()
                + " and ra.specimen.specimenPosition.container=c and ra.state=?",
            Arrays.asList(new Object[] { state }));
        try {
            results = SessionManager.getAppService().query(query);
        } catch (Exception e) {
            BiobankGuiCommonPlugin.openAsyncError("Error",
                "Unable to retrieve data from server");
        }

        HashSet<Integer> containers = new HashSet<Integer>();
        HashMap<Integer, RequestContainerAdapter> adapters = new HashMap<Integer, RequestContainerAdapter>();
        List<Object> tops = new ArrayList<Object>();

        // get all the containers to display
        for (Object o : results) {
            String path = (String) ((Object[]) o)[2];
            RequestSpecimen ra = (RequestSpecimen) ((Object[]) o)[0];
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
                new TreeItemAdapter(
                    adapters.get(Integer.parseInt(cIds[i - 1])),
                    new RequestSpecimenWrapper(SessionManager.getAppService(),
                        ra)));
            numSpecimens++;
        }

        // this.tops = tops;
    }
}
