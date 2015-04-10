package edu.ualberta.med.biobank.treeview.processing;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SpecimenLinkAndAssignAdapter extends AdapterBase {
    private static final I18n i18n = I18nFactory.getI18n(SpecimenLinkAndAssignAdapter.class);

    public SpecimenLinkAndAssignAdapter(
        AdapterBase parent, int id, String name, boolean hasChildren) {
        super(parent, id, name, hasChildren);
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public String getTooltipTextInternal() {
        return i18n.tr("Specimen Link");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {

    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
