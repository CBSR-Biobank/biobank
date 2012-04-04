package edu.ualberta.med.biobank.treeview.processing;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventDeleteAction;
import edu.ualberta.med.biobank.common.formatters.NumberFormatter;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventDeletePermission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventReadPermission;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.forms.ProcessingEventEntryForm;
import edu.ualberta.med.biobank.forms.ProcessingEventViewForm;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingEventAdapter extends AdapterBase {

    private static BgcLogger logger = BgcLogger
        .getLogger(ProcessingEventAdapter.class.getName());

    public ProcessingEventAdapter(AdapterBase parent,
        ProcessingEventWrapper pEvent) {
        super(parent, pEvent);
    }

    @Override
    public void init() {
        try {
            this.isDeletable =
                SessionManager.getAppService().isAllowed(
                    new ProcessingEventDeletePermission());
            this.isReadable =
                SessionManager.getAppService()
                    .isAllowed(
                        new ProcessingEventReadPermission(null));
            this.isEditable =
                SessionManager.getAppService().isAllowed(
                    new ProcessingEventUpdatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
        openViewForm();
    }

    @Override
    protected String getLabelInternal() {
        ProcessingEventWrapper pevent =
            (ProcessingEventWrapper) getModelObject();
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
        return name + " [" + NumberFormatter.format(count) + "]";  
    }

    @Override
    public String getTooltipTextInternal() {
        ProcessingEventWrapper pevent =
            (ProcessingEventWrapper) getModelObject();
        if (pevent == null)
            return "Processing event";
        return NLS.bind("Processing event on date {0}",
            pevent.getFormattedCreatedAt());
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Processing Event");
        addViewMenu(menu, "Processing Event");
        addDeleteMenu(menu, "Processing Event");
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this processing event?";
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
        return ProcessingEventViewForm.ID;
    }

    @Override
    public String getEntryFormId() {
        return ProcessingEventEntryForm.ID;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof ProcessingEventAdapter)
            return internalCompareTo(o);
        return 0;
    }

    @Override
    public void runDelete() throws Exception {
        ProcessingEventDeleteAction action =
            new ProcessingEventDeleteAction((ProcessingEvent) getModelObject()
                .getWrappedObject());
        SessionManager.getAppService().doAction(action);
    }
}
