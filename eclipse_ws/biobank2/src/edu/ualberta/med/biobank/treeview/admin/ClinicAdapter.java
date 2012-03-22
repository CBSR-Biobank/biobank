package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.clinic.ClinicDeleteAction;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicDeletePermission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicReadPermission;
import edu.ualberta.med.biobank.common.permission.clinic.ClinicUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ClinicAdapter extends AdapterBase {

    public ClinicAdapter(AdapterBase parent, ClinicWrapper clinicWrapper) {
        super(parent, clinicWrapper);
    }

    @Override
    public void init() {
        try {
            Integer id = ((ClinicWrapper) getModelObject()).getId();
            this.isDeletable =
                SessionManager.getAppService().isAllowed(
                    new ClinicDeletePermission(id));
            this.isReadable =
                SessionManager.getAppService().isAllowed(
                    new ClinicReadPermission(id));
            this.isEditable =
                SessionManager.getAppService().isAllowed(
                    new ClinicUpdatePermission(id));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    @Override
    protected String getLabelInternal() {
        ClinicWrapper wrapper = (ClinicWrapper) getModelObject();
        Assert.isNotNull(wrapper, "client is null"); //$NON-NLS-1$
        return wrapper.getNameShort();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Messages.ClinicAdapter_clinic_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.ClinicAdapter_clinic_label);
        addViewMenu(menu, Messages.ClinicAdapter_clinic_label);
        addDeleteMenu(menu, Messages.ClinicAdapter_clinic_label);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.ClinicAdapter_delete_confirm_msg;
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
