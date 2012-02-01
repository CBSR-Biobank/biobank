package edu.ualberta.med.biobank.treeview.patient;

import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventDeleteAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.forms.CollectionEventEntryForm;
import edu.ualberta.med.biobank.forms.CollectionEventViewForm;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CollectionEventAdapter extends AbstractNewAdapterBase {

    public SimpleCEventInfo ceventInfo;

    public CollectionEventAdapter(AbstractAdapterBase parent,
        SimpleCEventInfo ceventInfo) {
        super(parent, ceventInfo == null ? null : ceventInfo.cevent.getId(),
            null, null, false);
        this.ceventInfo = ceventInfo;
        setEditable(parent instanceof PatientAdapter || parent == null);
    }

    @Override
    protected String getLabelInternal() {
        Assert.isNotNull(ceventInfo, "collection event is null"); //$NON-NLS-1$
        return new StringBuilder("#") //$NON-NLS-1$ 
            .append(ceventInfo.cevent.getVisitNumber())
            .append(" - ")//$NON-NLS-1$
            .append(
                ceventInfo.minSourceSpecimenDate == null ? Messages.CollectionEventAdapter_nospecimens_label
                    : DateFormatter
                        .formatAsDateTime(ceventInfo.minSourceSpecimenDate))
            .append(" [").append(ceventInfo.sourceSpecimenCount) //$NON-NLS-1$ 
            .append("]").toString(); //$NON-NLS-1$ 
    }

    @Override
    public String getTooltipTextInternal() {
        String tabName = null;
        if (ceventInfo != null)
            if (ceventInfo.cevent.getId() == null) {
                tabName = Messages.CollectionEventEntryForm_title_new;
                // FIXME this should not be done in a getter!
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
            } else {
                tabName = NLS.bind(
                    Messages.CollectionEventEntryForm_title_edit,
                    ceventInfo.cevent.getVisitNumber());
            }
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
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
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

    public Patient getPatient() {
        if (ceventInfo != null && ceventInfo.cevent != null)
            return ceventInfo.cevent.getPatient();
        return null;
    }

    // FIXME?
    // public void setCollectionEventInfo(SimpleCEventInfo ceventInfo) {
    // this.ceventInfo = ceventInfo;
    // if (ceventInfo != null)
    // setId(ceventInfo.cevent.id);
    // }
    //
    // public void setCollectionEventId(Integer id) throws ApplicationException
    // {
    // // TODO Auto-generated method stub
    // // FIXME set id and set retrieve new CollectionEventInfo
    // // setCollectionEventInfo(SessionManager.getAppService().doAction(
    // // new GetCollectionEventInfoAction(id)));
    // }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        if (o instanceof CollectionEventAdapter) {
            CollectionEventAdapter ce2 = (CollectionEventAdapter) o;
            return ceventInfo.cevent.getVisitNumber()
                .compareTo(ce2.ceventInfo.cevent.getVisitNumber());
        }
        return 0;
    }

    @Override
    public void setValue(Object value) {
        if (value instanceof SimpleCEventInfo)
            ceventInfo = (SimpleCEventInfo) value;
    }

    @Override
    protected void runDelete() throws Exception {
        SessionManager.getAppService().doAction(
            new CollectionEventDeleteAction(getId()));
    }
}
