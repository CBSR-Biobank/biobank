package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class PatientTodayNode extends AbstractTodayNode {

    public PatientTodayNode(AdapterBase parent, int id) {
        super(parent, id);
        setName("From today's shipments");
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof SiteWrapper);
        return new SiteAdapter(this, (SiteWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new SiteAdapter(this, null);
    }

}
