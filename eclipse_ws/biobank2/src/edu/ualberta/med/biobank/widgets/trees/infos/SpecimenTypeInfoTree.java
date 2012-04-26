package edu.ualberta.med.biobank.widgets.trees.infos;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.gui.common.widgets.BgcLabelProvider;
import edu.ualberta.med.biobank.model.HasName;
import edu.ualberta.med.biobank.model.HasNameShort;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;

public class SpecimenTypeInfoTree extends InfoTreeWidget<SpecimenTypeWrapper> {
    private static final I18n i18n = I18nFactory
        .getI18n(SpecimenTypeInfoTree.class);

    private static final String[] HEADINGS = new String[] {
        HasName.PropertyName.NAME.toString(),
        HasNameShort.PropertyName.NAME_SHORT.toString() };

    protected List<SpecimenTypeWrapper> needReload =
        new ArrayList<SpecimenTypeWrapper>();

    public SpecimenTypeInfoTree(Composite parent,
        List<SpecimenTypeWrapper> specimenCollection) {
        super(parent, specimenCollection, HEADINGS, 20);
    }

    @Override
    protected BgcLabelProvider getLabelProvider() {
        return new BgcLabelProvider() {
            @SuppressWarnings("nls")
            @Override
            public String getColumnText(Object element, int columnIndex) {
                SpecimenTypeWrapper item = null;
                if (element instanceof SpecimenTypeWrapper)
                    item = (SpecimenTypeWrapper) element;
                else
                    item =
                        (SpecimenTypeWrapper) ((BiobankCollectionModel) element).o;
                if (item == null) {
                    if (columnIndex == 0) {
                        return i18n.tr("loading...");
                    }
                    return StringUtil.EMPTY_STRING;
                }
                switch (columnIndex) {
                case 0:
                    return item.getName();
                case 1:
                    return item.getNameShort();
                default:
                    return null;
                }
            }
        };
    }

    @Override
    protected String getCollectionModelObjectToString(Object o) {
        if (o == null)
            return null;
        return ((SpecimenTypeWrapper) o).toString();
    }

    @Override
    public SpecimenTypeWrapper getSelection() {
        BiobankCollectionModel item = getSelectionInternal();
        if (item == null)
            return null;
        SpecimenTypeWrapper source = (SpecimenTypeWrapper) item.o;
        Assert.isNotNull(source);
        return source;
    }

    @Override
    protected BiobankTableSorter getComparator() {
        return null;
    }

    @Override
    protected List<Node> getNodeChildren(Node node) throws Exception {
        if (node != null && node instanceof BiobankCollectionModel) {
            BiobankCollectionModel model = (BiobankCollectionModel) node;
            Object obj = model.o;
            if (obj != null) {
                SpecimenTypeWrapper spc = (SpecimenTypeWrapper) obj;
                if (needReload.contains(spc)) {
                    spc.reload();
                    needReload.remove(spc);
                }
                return createNodes(node,
                    spc.getChildSpecimenTypeCollection(true));
            }
        }
        return super.getNodeChildren(node);
    }

}