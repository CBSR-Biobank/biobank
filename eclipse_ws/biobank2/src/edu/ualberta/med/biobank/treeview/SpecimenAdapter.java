package edu.ualberta.med.biobank.treeview;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.permission.specimen.SpecimenDeletePermission;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.SpecimenEntryForm;
import edu.ualberta.med.biobank.forms.SpecimenViewForm;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenAdapter extends AdapterBase {
	 //OHSDEV
    // Specimen tree view implementation
    // In order to attach SpecimenTreeViewAdapter to CollectionEventAddapter those objects
    // must be brought to the same parent class.
    public SpecimenAdapter(AbstractAdapterBase parent, SpecimenWrapper sample) {
        super(parent, sample);
    }

    public SpecimenAdapter(AdapterBase parent, SpecimenWrapper sample) {
        super(parent, sample);
    }

    @Override
    public void init() {
        Integer id = ((SpecimenWrapper) getModelObject()).getId();

        this.isDeletable = isAllowed(new SpecimenDeletePermission(id));
        this.isReadable = isAllowed(new SpecimenReadPermission(id));
        this.isEditable = isAllowed(new SpecimenUpdatePermission(id));
    }


    @SuppressWarnings("nls")
    @Override

    public void addChild(AbstractAdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter");
    }

    @SuppressWarnings("nls")
    @Override
    protected String getLabelInternal() {
        Assert.isNotNull(getModelObject(), "specimen is null");
        return ((SpecimenWrapper) getModelObject()).getInventoryId();
    }

    @Override
    public String getTooltipTextInternal() {
        return getTooltipText(Specimen.NAME.singular().toString());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, Specimen.NAME.singular().toString());
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
        return SpecimenEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return SpecimenViewForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof SpecimenAdapter)
            return internalCompareTo(o);
        return 0;
    }
    //OHSDEV
    // Specimen tree view implementation
    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
	return null;
    }

  }
