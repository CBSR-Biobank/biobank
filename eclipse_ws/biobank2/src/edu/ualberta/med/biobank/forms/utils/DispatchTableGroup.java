package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class DispatchTableGroup extends TableGroup<DispatchWrapper> {

    public DispatchTableGroup(DispatchSpecimenState ds, DispatchWrapper dispatch) {
        super(ds, dispatch);
    }

    public static List<DispatchTableGroup> getGroupsForShipment(
        DispatchWrapper ship) {
        List<DispatchTableGroup> groups = new ArrayList<DispatchTableGroup>();
        groups
            .add(new DispatchTableGroup(DispatchSpecimenState.RECEIVED, ship));
        groups.add(new DispatchTableGroup(DispatchSpecimenState.MISSING, ship));
        groups.add(new DispatchTableGroup(DispatchSpecimenState.NONE, ship));
        groups.add(new DispatchTableGroup(DispatchSpecimenState.EXTRA, ship));
        return groups;
    }

    @Override
    public void createAdapterTree(Integer state, DispatchWrapper request) {
        List<Object[]> results = new ArrayList<Object[]>();
        // test hql
        HQLCriteria query = new HQLCriteria(
            "select ra from "
                + DispatchSpecimen.class.getName()
                + " ra inner join fetch ra.specimen inner join fetch ra.specimen.specimenType inner join fetch ra.specimen.collectionEvent inner join fetch ra.specimen.collectionEvent.patient "
                + " where ra.dispatch =" + request.getId() + " and ra.state=?",
            Arrays.asList(new Object[] { state }));
        try {
            results = SessionManager.getAppService().query(query);
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Error",
                "Unable to retrieve data from server");
        }

        List<Node> adapters = new ArrayList<Node>();

        // get all the containers to display
        for (Object o : results) {
            DispatchSpecimen ra = (DispatchSpecimen) o;
            adapters.add(new TreeItemAdapter(null, new DispatchSpecimenWrapper(
                SessionManager.getAppService(), ra)));
            numAliquots++;
        }

        this.tops = adapters;
    }
}
