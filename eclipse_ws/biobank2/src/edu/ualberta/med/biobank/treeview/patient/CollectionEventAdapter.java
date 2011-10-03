package edu.ualberta.med.biobank.treeview.patient;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.CollectionEventEntryForm;
import edu.ualberta.med.biobank.forms.CollectionEventViewForm;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CollectionEventAdapter extends AbstractNewAdapterBase {

    public CollectionEventAdapter(AbstractAdapterBase parent,
        CollectionEvent collectionEvent) {
        super(parent, collectionEvent);
        setEditable(parent instanceof PatientAdapter || parent == null);
    }

    @Override
    protected String getLabelInternal() {
        CollectionEvent cevent = getModelObject();
        Assert.isNotNull(cevent, "collection event is null"); //$NON-NLS-1$
        long count = -1;
        // FIXME action ?
        // try {
        // count = cevent.getSourceSpecimensCount(false);
        // } catch (Exception e) {
        //            logger.error("Problem counting specimens", e); //$NON-NLS-1$
        // }
        return new StringBuilder("#") //$NON-NLS-1$ 
            .append(cevent.getVisitNumber()).append(" - ")//$NON-NLS-1$
            // FIXME
            // .append(
            // DateFormatter.formatAsDateTime(cevent
            // .getMinSourceSpecimenDate()))
            .append(" [").append(count) //$NON-NLS-1$ 
            .append("]").toString(); //$NON-NLS-1$ 
    }

    @Override
    public CollectionEvent getModelObject() {
        return (CollectionEvent) super.getModelObject();
    }

    @Override
    public String getTooltipText() {
        String tabName = null;
        CollectionEvent cEvent = getModelObject();
        if (cEvent != null)
            // FIXME
            // if (cEvent.isNew()) {
            // tabName = Messages.CollectionEventEntryForm_title_new;
            // try {
            // cEvent
            // .setActivityStatus(ActivityStatusWrapper
            // .getActiveActivityStatus(SessionManager
            // .getAppService()));
            // } catch (Exception e) {
            // BgcPlugin.openAsyncError(
            // Messages.CollectionEventAdapter_error_title,
            // Messages.CollectionEventAdapter_create_error_msg);
            // }
            // } else {
            tabName = NLS.bind(Messages.CollectionEventEntryForm_title_edit,
                cEvent.getVisitNumber());
        // }
        return tabName;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, Messages.CollectionEventAdapter_cevent_label);
        addViewMenu(menu, Messages.CollectionEventAdapter_cevent_label);
        addDeleteMenu(menu, Messages.CollectionEventAdapter_cevent_label);
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return Messages.CollectionEventAdapter_delete_confirm_msg;
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
    protected Collection<?> getChildrenObjects() throws Exception {
        return null;
    }

    @Override
    protected int getChildrenCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return CollectionEventEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return CollectionEventViewForm.ID;
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

}
