package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicDeletePermission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicReadPermission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ClinicAdapter extends AdapterBase {
    private static final I18n i18n = I18nFactory.getI18n(ClinicAdapter.class);

    public ClinicAdapter(AdapterBase parent, ClinicWrapper clinicWrapper) {
        super(parent, clinicWrapper);
    }

    @Override
    public void init() {
        Integer id = ((ClinicWrapper) getModelObject()).getId();
        this.isDeletable = isAllowed(new ClinicDeletePermission(id));
        this.isReadable = isAllowed(new ClinicReadPermission(id));
        this.isEditable = isAllowed(new ClinicUpdatePermission(id));
    }

    @SuppressWarnings("nls")
    @Override
    protected String getLabelInternal() {
        ClinicWrapper wrapper = (ClinicWrapper) getModelObject();
        Assert.isNotNull(wrapper, "client is null");
        return wrapper.getNameShort();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Clinic.NAME.singular().toString());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Clinic.NAME.singular().toString());
        addViewMenu(menu, Clinic.NAME.singular().toString());
        addDeleteMenu(menu, Clinic.NAME.singular().toString());
    }

    @SuppressWarnings("nls")
    @Override
    protected String getConfirmDeleteMessage() {
        // dialog message.
        return i18n.tr("Are you sure you want to delete this clinic?");
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
    public String getEntryFormId() {
        return ClinicEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return ClinicViewForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ClinicAdapter)
            return internalCompareTo(o);
        return 0;
    }

    @Override
    protected void runDelete() throws Exception {
        SessionManager.getAppService()
            .doAction(
                new ClinicDeleteAction((Clinic) getModelObject()
                    .getWrappedObject()));
        SessionManager.updateAllSimilarNodes(getParent(), true);
    }
}
