package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.forms.ProcessingEventEntryForm;
import edu.ualberta.med.biobank.forms.ProcessingEventViewForm;
import edu.ualberta.med.biobank.logs.BiobankLogger;

public class ProcessingEventAdapter extends AdapterBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ProcessingEventAdapter.class.getName());

    public ProcessingEventAdapter(AdapterBase parent,
        ProcessingEventWrapper pEvent) {
        super(parent, pEvent);
    }

    public ProcessingEventWrapper getWrapper() {
        return (ProcessingEventWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        ProcessingEventWrapper pevent = getWrapper();
        Assert.isNotNull(pevent, "processing event is null");
        String worksheet = pevent.getWorksheet();
        String name = pevent.getFormattedCreatedAt() + worksheet == null ? ""
            : " - #" + pevent.getWorksheet();

        long count = -1;
        try {
            count = pevent.getChildSpecimenCount(true);
        } catch (Exception e) {
            logger.error("Problem counting specimens", e);
        }
        return name + " [" + count + "]";
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        // TODO Auto-generated method stub

    }

    @Override
    protected AdapterBase createChildNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getViewFormId() {
        return ProcessingEventViewForm.ID;
    }

    @Override
    public String getEntryFormId() {
        return ProcessingEventEntryForm.ID;
    }

}
