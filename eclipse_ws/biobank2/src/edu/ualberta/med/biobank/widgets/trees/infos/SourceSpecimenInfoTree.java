package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.SourceSpecimenWrapper;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;

public class SourceSpecimenInfoTree extends
    InfoTreeWidget<SourceSpecimenWrapper> {

    private static final int PAGE_SIZE_ROWS = 5;

    protected static class TreeRowData {
        SourceSpecimenWrapper studySourceVessel;
        public String name;
        public String needOriginalVolume;

        @Override
        public String toString() {
            return StringUtils.join(new String[] {
                name,
                (needOriginalVolume != null) ? needOriginalVolume.toString()
                    : "" }, "\t");
        }
    }

    private final static String[] HEADINGS = new String[] {
        Messages.getString("SourceSpecimen.field.type.label"),
        Messages.getString("SourceSpecimen.field.originalVolume.label") };

    public SourceSpecimenInfoTree(Composite parent,
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
                    return info.needOriginalVolume;
                default:
                    return "";
                }
            }
        };
    }

    @Override
    public TreeRowData getCollectionModelObject(
        SourceSpecimenWrapper studySourceVessel) throws Exception {
        TreeRowData info = new TreeRowData();
        info.studySourceVessel = studySourceVessel;
        Assert.isNotNull(studySourceVessel.getSpecimenType(),
            "study specimen type is null");
        info.name = studySourceVessel.getSpecimenType().getName();
        info.needOriginalVolume = (studySourceVessel.getNeedOriginalVolume() != null) ? (studySourceVessel
            .getNeedOriginalVolume() ? "Yes" : "No") : "No";
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TreeRowData) o).toString();
    }

    @Override
    public SourceSpecimenWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        TreeRowData row = (TreeRowData) item.o;
        Assert.isNotNull(row);
        return row.studySourceVessel;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

}
