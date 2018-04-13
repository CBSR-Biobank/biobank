package edu.ualberta.med.biobank.treeview.patient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventDeleteAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetSourceSpecimenListInfoAction;
import edu.ualberta.med.biobank.common.action.patient.PatientGetSimpleCollectionEventInfosAction.SimpleCEventInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventDeletePermission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventReadPermission;
import edu.ualberta.med.biobank.common.permission.collectionEvent.CollectionEventUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.CollectionEventEntryForm;
import edu.ualberta.med.biobank.forms.CollectionEventViewForm;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenTreeViewAdapter;

public class CollectionEventAdapter extends AbstractNewAdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(CollectionEventAdapter.class);

    public SimpleCEventInfo ceventInfo;

    public CollectionEventAdapter(AbstractAdapterBase parent,
        SimpleCEventInfo ceventInfo) {
        super(parent, ceventInfo == null ? null : ceventInfo.cevent.getId(),
            null, null, false);
        this.ceventInfo = ceventInfo;

        if (ceventInfo.cevent.getId() != null) init();
    }

    @Override
    public void init() {
        this.isDeletable = isAllowed(
            new CollectionEventDeletePermission(ceventInfo.cevent.getId()));
        this.isReadable = isAllowed(
            new CollectionEventReadPermission(ceventInfo.cevent.getId()));
        this.isEditable = isAllowed(
            new CollectionEventUpdatePermission(ceventInfo.cevent.getId()));
    }

    @SuppressWarnings("nls")
    @Override
    protected String getLabelInternal() {
        Assert.isNotNull(ceventInfo, "collection event is null");
        return new StringBuilder("#")
            .append(ceventInfo.cevent.getVisitNumber())
            .append(" - ")
            .append(
                ceventInfo.minSourceSpecimenDate == null ? i18n
                    .tr("No Specimens")
                    : DateFormatter
                        .formatAsDateTime(ceventInfo.minSourceSpecimenDate))
            .append(" [").append(ceventInfo.sourceSpecimenCount)
            .append("]").toString();
    }

    @SuppressWarnings("nls")
    @Override
    public String getTooltipTextInternal() {
        String tabName = null;
        if (ceventInfo != null)
            if (ceventInfo.cevent.getId() == null) {
                tabName = i18n.tr("New collection event");
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
            tabName = i18n.tr("Collection Event - #{0}",
                ceventInfo.cevent.getVisitNumber());
        }
        return tabName;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, CollectionEvent.NAME.singular().toString());
        addViewMenu(menu, CollectionEvent.NAME.singular().toString());
        addDeleteMenu(menu, CollectionEvent.NAME.singular().toString());
    }

    @SuppressWarnings("nls")
    @Override
    protected String getConfirmDeleteMessage() {
        return i18n
            .tr("Are you sure you want to delete this collection event?");
    }

    //OHSDEV
    // Specimen tree view implementation
    @Override
    protected SpecimenTreeViewAdapter createChildNode() {
        return new SpecimenTreeViewAdapter(this, null);
    }

    @Override
    protected SpecimenTreeViewAdapter createChildNode(Object child) {
        Specimen spec = ((SpecimenInfo)child).specimen;
        return new SpecimenTreeViewAdapter(this,new SpecimenWrapper(SessionManager.getAppService(),spec));
	// return null;
    }
    //OHSDEV
    // Specimen tree view implementation

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {

	// in order to match return type need to create MAP structure providing key as speciemn id

		List<SpecimenInfo> infos = SessionManager.getAppService().doAction(
                new CollectionEventGetSourceSpecimenListInfoAction(ceventInfo.cevent.getId())).getList();

	 HashMap<Integer, SpecimenInfo> specimenInfos =new HashMap<Integer, SpecimenInfo>();

	 for (SpecimenInfo info : infos) {
		 specimenInfos.put(info.specimen.getId(), info);
         }

	return specimenInfos;


	// Initially was  - return null;
    }

    @Override
    public String getEntryFormId() {
        return CollectionEventEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return CollectionEventViewForm.ID;
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
    protected void runDelete() throws Exception {
        SessionManager.getAppService().doAction(
            new CollectionEventDeleteAction(ceventInfo.cevent));
    }

    @Override
    public void setValue(Object val) {
        this.ceventInfo = (SimpleCEventInfo) val;
        setId(ceventInfo.cevent.getId());
        if (ceventInfo.cevent.getId() != null) init();
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId,
			SourceSpecimen.class);
    }
}
