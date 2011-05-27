package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;

public class SourceToAliquotedTypeInfoTree extends
    InfoTreeWidget<SourceSpecimenWrapper> {

    private static final int PAGE_SIZE_ROWS = 10;

    protected static class TreeRowData {
        SourceSpecimenWrapper studySourceVessel;
        AliquotedSpecimenWrapper studyAliquotedSpecimen;
        SpecimenTypeWrapper type;
        public String name;
        public Double volume;
        public Integer quantity;
        public String status;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { name,
                (volume != null) ? volume.toString() : "",
                (quantity != null) ? quantity.toString() : "", status }, "\t");
        }
    }

    private final static String[] HEADINGS = new String[] {
        Messages.getString("AliquotedSpecimen.field.type.label"),
        Messages.getString("AliquotedSpecimen.field.volume.label"),
        Messages.getString("AliquotedSpecimen.field.quantity.label"),
        Messages.getString("label.activity") };

    public SourceToAliquotedTypeInfoTree(Composite parent,
        List<SourceSpecimenWrapper> collection) {
        super(parent, collection, HEADINGS, PAGE_SIZE_ROWS);
    }

    @Override
    protected BiobankLabelProvider getLabelProvider() {
        return new BiobankLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TreeRowData info = (TreeRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return "";
                }
                switch (columnIndex) {
                case 0:
                    return info.name;
                case 1:
                    return (info.volume != null) ? info.volume.toString() : "";
                case 2:
                    return (info.quantity != null) ? info.quantity.toString()
                        : "";
                case 3:
                    return info.status;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TreeRowData getCollectionModelObject(Object item) throws Exception {
        TreeRowData info = new TreeRowData();
        if (item instanceof SourceSpecimenWrapper) {
            SourceSpecimenWrapper studySourceVessel = (SourceSpecimenWrapper) item;
            info.studySourceVessel = studySourceVessel;
            Assert.isNotNull(studySourceVessel.getSpecimenType(),
                "study specimen type is null");
            String needOriginalVolume = (studySourceVessel
                .getNeedOriginalVolume() != null) ? (studySourceVessel
                .getNeedOriginalVolume() ? "Yes" : "No") : "No";
            info.name = studySourceVessel.getSpecimenType().getName()
                + " (need volume=" + needOriginalVolume + ")";
        } else if (item instanceof AliquotedSpecimenWrapper) {
            AliquotedSpecimenWrapper aliquotedSpecimen = (AliquotedSpecimenWrapper) item;
            info.studyAliquotedSpecimen = aliquotedSpecimen;
            SpecimenTypeWrapper type = aliquotedSpecimen.getSpecimenType();
            Assert.isNotNull(type, "aliquotedSpecimen - sample type is null");
            info.name = type.getName();
            info.volume = aliquotedSpecimen.getVolume();
            info.quantity = aliquotedSpecimen.getQuantity();
            ActivityStatusWrapper status = aliquotedSpecimen
                .getActivityStatus();
            Assert
                .isNotNull(status, "sample storage - activity status is null");
            info.status = status.getName();
        }
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TreeRowData) o).toString();
    }

    @Override
    public Object getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TreeRowData row = (TreeRowData) item.o;
        Assert.isNotNull(row);
        if (row.studySourceVessel == null)
            return row.studyAliquotedSpecimen;
        return row.studySourceVessel;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    protected List<Node> getNodeChildren(Node node) throws Exception {
        if (node != null && node instanceof BiobankCollectionModel) {
            BiobankCollectionModel model = (BiobankCollectionModel) node;
            TreeRowData row = (TreeRowData) model.o;
            if (row != null)
                if (row.studySourceVessel != null)
                    return createNodes(node,
                        row.studySourceVessel
                            .getAliquotedSpecimenCollection(false));
        }
        return super.getNodeChildren(node);
    }
}
