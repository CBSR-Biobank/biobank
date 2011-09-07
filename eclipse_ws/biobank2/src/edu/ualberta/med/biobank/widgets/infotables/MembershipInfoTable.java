package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.MembershipWrapper;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;

public class MembershipInfoTable extends InfoTableWidget<MembershipWrapper> {
    public static final int ROWS_PER_PAGE = 7;
    private static final String[] HEADINGS = new String[] {
        Messages.MembershipInfoTable_center_label,
        Messages.MembershipInfoTable_study_label,
        Messages.MembershipInfoTable_role_right_privilege_label };

    protected static class TableRowData {
        MembershipWrapper ms;
        String center;
        String study;
        String roleOrRP;

        @Override
        public String toString() {
            return StringUtils.join(new String[] { center, study, roleOrRP },
                "\t"); //$NON-NLS-1$
        }
    }

    public MembershipInfoTable(Composite parent,
        final PrincipalWrapper<?> principal) {
        super(parent, principal.getMembershipCollection(true), HEADINGS,
            ROWS_PER_PAGE, MembershipWrapper.class);

        addDeleteItemListener(new IInfoTableDeleteItemListener() {
            @Override
            public void deleteItem(InfoTableEvent event) {
                MembershipWrapper ms = ((TableRowData) getSelection()).ms;
                principal.removeFromMembershipCollection(Arrays.asList(ms));
                getCollection().remove(ms);
                reloadCollection(principal.getMembershipCollection(true));
            }
        });
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object o1, Object o2) {
                if (o1 instanceof MembershipWrapper
                    && o2 instanceof MembershipWrapper) {
                    MembershipWrapper rp1 = (MembershipWrapper) o1;
                    MembershipWrapper rp2 = (MembershipWrapper) o2;
                    return rp1.compareTo(rp2);
                }
                return 0;
            }
        };
    }

    @Override
    public Object getCollectionModelObject(MembershipWrapper ms)
        throws Exception {
        TableRowData info = new TableRowData();
        info.center = ms.getCenter() == null ? Messages.MembershipInfoTable_all_label : ms.getCenter()
            .getNameShort();
        info.study = ms.getStudy() == null ? Messages.MembershipInfoTable_all_label : ms.getStudy()
            .getNameShort();
        info.roleOrRP = ms.getMembershipObjectsListString();
        info.ms = ms;
        return info;
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((TableRowData) o).toString();
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @Override
            public String getColumnText(Object element, int columnIndex) {
                TableRowData info = (TableRowData) ((BiobankCollectionModel) element).o;
                if (info == null) {
                    if (columnIndex == 0) {
                        return Messages.MembershipInfoTable_loading;
                    }
                    return ""; //$NON-NLS-1$
                }
                switch (columnIndex) {
                case 0:
                    return info.center;
                case 1:
                    return info.study;
                case 2:
                    return info.roleOrRP;
                default:
                    return ""; //$NON-NLS-1$
                }
            }
        };
    }
}