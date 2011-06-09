package edu.ualberta.med.biobank.forms.utils;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.DispatchSpecimenState;
import edu.ualberta.med.biobank.common.util.ItemState;
import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.TreeItemAdapter;

public class DispatchTableGroup extends TableGroup<DispatchWrapper> {

    public DispatchTableGroup(DispatchSpecimenState ds, String alternateLabel,
        DispatchWrapper dispatch) {
        super(ds, alternateLabel, dispatch);
    }

    public DispatchTableGroup(DispatchSpecimenState ds, DispatchWrapper dispatch) {
        super(ds, dispatch);
    }

    public static List<DispatchTableGroup> getGroupsForShipment(
        DispatchWrapper ship) {
        List<DispatchTableGroup> groups = new ArrayList<DispatchTableGroup>();
        if (ship.isInCreationState()) {
            groups.add(new DispatchTableGroup(DispatchSpecimenState.NONE,
                "Added", ship));
        } else {
            groups.add(new DispatchTableGroup(DispatchSpecimenState.NONE,
                "Non processed", ship));
        }
        if (ship.hasBeenReceived()) {
            groups.add(new DispatchTableGroup(DispatchSpecimenState.RECEIVED,
                ship));
            groups
                .add(new DispatchTableGroup(DispatchSpecimenState.EXTRA, ship));
        }
        if (ship.hasBeenReceived() || ship.isInTransitState()) {
            groups.add(new DispatchTableGroup(DispatchSpecimenState.MISSING,
                ship));
        }
        return groups;
    }

    @Override
    public void createAdapterTree(ItemState state, DispatchWrapper request) {
        List<DispatchSpecimenWrapper> cache = request.getMap().get(state);
        List<Node> adapters = new ArrayList<Node>();

        if (cache == null) {
            switch (DispatchSpecimenState.getState(state.getId())) {
            case NONE:
                cache = request.getNonProcessedDispatchSpecimenCollection();
                break;
            case MISSING:
                cache = request.getMissingDispatchSpecimens();
                break;
            case EXTRA:
                cache = request.getExtraDispatchSpecimens();
                break;
            case RECEIVED:
                cache = request.getReceivedDispatchSpecimens();
                break;
            }
        }

        for (DispatchSpecimenWrapper wrapper : cache) {
            adapters.add(new TreeItemAdapter(null, wrapper));
            numSpecimens++;
        }

        this.tops = adapters;
    }
}
