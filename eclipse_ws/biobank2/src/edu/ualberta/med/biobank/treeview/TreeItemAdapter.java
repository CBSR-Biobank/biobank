package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.DispatchSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.ItemWrapper;

public class TreeItemAdapter implements Node {

    public Node parent;
    public ItemWrapper raw;

    public TreeItemAdapter(Node parent, ItemWrapper raw) {
        this.parent = parent;
        this.raw = raw;
    }

    public ItemWrapper getSpecimen() {
        return raw;
    }

    @Override
    public Object getParent() {
        return parent;
    }

    @Override
    public List<Node> getChildren() {
        return new ArrayList<Node>();
    }

    public String getColumnText(int columnIndex) {
        switch (columnIndex) {
        case 0:
            return raw.getSpecimen().getInventoryId();
        case 1:
            return raw.getSpecimen().getSpecimenType().getNameShort();
        case 2:
            return raw.getSpecimen().getCollectionEvent().getPatient()
                .getPnumber();
        case 3:
            return raw.getSpecimen().getActivityStatus().getName();
        case 4:
            return ((DispatchSpecimenWrapper) raw).getComment();
        }
        return ""; //$NON-NLS-1$
    }
}
