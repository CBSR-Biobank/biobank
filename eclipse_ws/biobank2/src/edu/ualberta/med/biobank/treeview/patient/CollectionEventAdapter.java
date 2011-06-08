package edu.ualberta.med.biobank.treeview.patient;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.Messages;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.forms.CollectionEventEntryForm;
import edu.ualberta.med.biobank.forms.CollectionEventViewForm;
import edu.ualberta.med.biobank.gui.common.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CollectionEventAdapter extends AdapterBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CollectionEventAdapter.class.getName());

    public CollectionEventAdapter(AdapterBase parent,
        CollectionEventWrapper collectionEventWrapper) {
        super(parent, collectionEventWrapper);
        setEditable(parent instanceof PatientAdapter || parent == null);
    }

    public CollectionEventWrapper getWrapper() {
        return (CollectionEventWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        CollectionEventWrapper cevent = getWrapper();
        Assert.isNotNull(cevent, "collection event is null");
        StringBuilder name = new StringBuilder(cevent.getPatient().getPnumber())
            .append(" - #").append(cevent.getVisitNumber());

        long count = -1;
        try {
            count = cevent.getSourceSpecimensCount(false);
        } catch (Exception e) {
            logger.error("Problem counting specimens", e);
        }
        return name.append(" [").append(count).append("]").toString();
    }

    @Override
    public String getTooltipText() {
        String tabName = null;
        if (modelObject != null)
            if (modelObject.isNew()) {
                tabName = Messages
                    .getString("CollectionEventEntryForm.title.new");
                try {
                    ((CollectionEventWrapper) modelObject)
                        .setActivityStatus(ActivityStatusWrapper
                            .getActiveActivityStatus(SessionManager
                                .getAppService()));
                } catch (Exception e) {
                    BiobankGuiCommonPlugin.openAsyncError("Error",
                        "Unable to create collection event.");
                }
            } else
                tabName = Messages.getString(
                    "CollectionEventEntryForm.title.edit",
                    ((CollectionEventWrapper) modelObject).getVisitNumber());
        return tabName;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Collection Event");
        addViewMenu(menu, "Collection Event");
        addDeleteMenu(menu, "Collection Event");
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this collection event?";
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
