package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;

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
        List<DispatchSpecimenWrapper> cache = request.getMap().get(
            DispatchSpecimenState.getState(state));
        List<Node> adapters = new ArrayList<Node>();

        if (cache == null) {
            switch (DispatchSpecimenState.getState(state)) {
            case NONE:
                cache = request.getNonProcessedDispatchSpecimenCollection();
            case MISSING:
                cache = request.getMissingDispatchSpecimens();
            case EXTRA:
                cache = request.getExtraDispatchSpecimens();
            case RECEIVED:
                cache = request.getReceivedDispatchSpecimens();
            }
        }

        for (DispatchSpecimenWrapper wrapper : cache) {
            adapters.add(new TreeItemAdapter(null, wrapper));
            numAliquots++;
        }

        this.tops = adapters;
    }
}
