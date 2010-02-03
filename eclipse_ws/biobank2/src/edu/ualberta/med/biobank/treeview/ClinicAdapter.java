package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.ClinicEntryForm;
import edu.ualberta.med.biobank.forms.ClinicViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;

public class ClinicAdapter extends AdapterBase {

    public ClinicAdapter(AdapterBase parent, ClinicWrapper clinicWrapper) {
        super(parent, clinicWrapper);
    }

    public ClinicAdapter(AdapterBase parent, ClinicWrapper clinicWrapper,
        boolean enableActions) {
        super(parent, clinicWrapper, enableActions, false);
    }

    public ClinicWrapper getWrapper() {
        return (ClinicWrapper) modelObject;
    }

    @Override
    protected String getNameInternal() {
        ClinicWrapper wrapper = getWrapper();
        Assert.isNotNull(wrapper.getWrappedObject(), "client is null");
        return wrapper.getName();
    }

    @Override
    public String getTitle() {
        return getTitle("Patient");
    }

    @Override
    public void executeDoubleClick() {
        openForm(new FormInput(this), ClinicViewForm.ID);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Clinic", ClinicEntryForm.ID);
        addViewMenu(menu, "Clinic", ClinicViewForm.ID);
        addDeleteMenu(menu, "Clinic",
            "Are you sure you want to delete this clinic?");
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return null;
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

}
