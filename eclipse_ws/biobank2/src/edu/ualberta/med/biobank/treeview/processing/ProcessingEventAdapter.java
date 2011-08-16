package edu.ualberta.med.biobank.treeview.processing;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.forms.ProcessingEventEntryForm;
import edu.ualberta.med.biobank.forms.ProcessingEventViewForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ProcessingEventAdapter extends AdapterBase {

    private static BgcLogger logger = BgcLogger
        .getLogger(ProcessingEventAdapter.class.getName());

    public ProcessingEventAdapter(AdapterBase parent,
        ProcessingEventWrapper pEvent) {
        super(parent, pEvent);
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
    }

    @Override
    protected String getLabelInternal() {
        ProcessingEventWrapper pevent = (ProcessingEventWrapper) getModelObject();
        Assert.isNotNull(pevent, "processing event is null"); //$NON-NLS-1$
        String worksheet = pevent.getWorksheet();
        String name = pevent.getFormattedCreatedAt()
            + (worksheet == null ? "" : " - #" + pevent.getWorksheet()); //$NON-NLS-1$ //$NON-NLS-2$

        long count = -1;
        try {
            count = pevent.getSpecimenCount(true);
        } catch (Exception e) {
            logger.error("Problem counting specimens", e); //$NON-NLS-1$
        }
        return name + " [" + NumberFormatter.format(count) + "]"; //$NON-NLS-1$ //$NON-NLS-2$
    }

    @Override
    public String getTooltipText() {
        ProcessingEventWrapper pevent = (ProcessingEventWrapper) getModelObject();
        if (pevent == null)
            return Messages.ProvessingEventAdapter_tooltiptext;
        return NLS.bind(Messages.ProvessingEventAdapter_tooltiptext_withdate,
            pevent.getFormattedCreatedAt());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.ProcessingEventAdapter_pevent_label);
        addViewMenu(menu, Messages.ProcessingEventAdapter_pevent_label);
        addDeleteMenu(menu, Messages.ProcessingEventAdapter_pevent_label);
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.ProcessingEventAdapter_deleteMsg;
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
