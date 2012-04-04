package edu.ualberta.med.biobank.widgets.infotables;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class SpecimenTypeInfoTable extends InfoTableWidget<SpecimenTypeWrapper> {

    private static final int PAGE_SIZE_ROWS = 10;

    private static final String[] HEADINGS = new String[] {
        Messages.SpecimenTypeInfoTable_type_label,
        Messages.SpecimenTypeInfoTable_shortname_label };

    public SpecimenTypeInfoTable(Composite parent,
        List<SpecimenTypeWrapper> sampleTypeCollection) {
        super(parent, sampleTypeCollection, HEADINGS, PAGE_SIZE_ROWS,
            SpecimenTypeWrapper.class);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SpecimenTypeWrapper item =
                    (SpecimenTypeWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return "loading...";
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                case 1:
                    return item.getNameShort();
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        SpecimenTypeWrapper type = (SpecimenTypeWrapper) o;
        return type.getName() + "\t" + type.getNameShort(); //$NON-NLS-1$
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    public SpecimenTypeWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null) return null;
        return (SpecimenTypeWrapper) item.o;
    }

}
