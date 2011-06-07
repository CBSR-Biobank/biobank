package edu.ualberta.med.biobank.treeview.processing;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.forms.ProcessingEventEntryForm;
import edu.ualberta.med.biobank.forms.ProcessingEventViewForm;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;

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
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
    }

    @Override
    protected String getLabelInternal() {
        ProcessingEventWrapper pevent = getWrapper();
        Assert.isNotNull(pevent, "processing event is null");
        String worksheet = pevent.getWorksheet();
        String name = pevent.getFormattedCreatedAt()
            + (worksheet == null ? "" : " - #" + pevent.getWorksheet());

        long count = -1;
        try {
            count = pevent.getSpecimenCount(true);
        } catch (Exception e) {
            logger.error("Problem counting specimens", e);
        }
        return name + " [" + count + "]";
    }

    @Override
    public String getTooltipText() {
        if (getWrapper() == null)
            return Messages.getString("ProvessingEventAdapter.tooltiptext");
        return Messages.getString(
            "ProvessingEventAdapter.tooltiptext.withdate", getWrapper()
                .getFormattedCreatedAt());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Processing Event");
        addViewMenu(menu, "Processing Event");
        addDeleteMenu(menu, "Processing Event");
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.getString("ProcessingEventAdapter.deleteMsg");
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
    public String getViewFormId() {
        return ProcessingEventViewForm.ID;
    }

    @Override
    public String getEntryFormId() {
        return ProcessingEventEntryForm.ID;
    }

}
