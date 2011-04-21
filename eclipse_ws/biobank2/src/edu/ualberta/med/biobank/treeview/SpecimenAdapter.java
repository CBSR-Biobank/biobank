package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.SpecimenEntryForm;
import edu.ualberta.med.biobank.forms.SpecimenViewForm;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SpecimenAdapter extends AdapterBase {

    public SpecimenAdapter(AdapterBase parent, SpecimenWrapper sample) {
        super(parent, sample);
    }

    public SpecimenWrapper getSpecimen() {
        return (SpecimenWrapper) modelObject;
    }

    @Override
    public void addChild(AdapterBase child) {
        Assert.isTrue(false, "Cannot add children to this adapter");
    }

    @Override
    protected String getLabelInternal() {
        SpecimenWrapper specimen = getSpecimen();
        Assert.isNotNull(specimen, "specimen is null");
        return specimen.getInventoryId();
    }

    @Override
    public String getTooltipText() {
        return getTooltipText("Specimen");
    }

    @Override
    public WritableApplicationService getAppService() {
        return modelObject.getAppService();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addViewMenu(menu, "Specimen");
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

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return SpecimenEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return SpecimenViewForm.ID;
    }

}
