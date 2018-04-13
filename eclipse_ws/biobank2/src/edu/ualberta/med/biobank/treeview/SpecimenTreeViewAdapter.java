package edu.ualberta.med.biobank.treeview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetAliquotedSpecimenListInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenDeletePermission;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenUpdatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.views.CollectionView;
import gov.nih.nci.system.applicationservice.ApplicationException;
/**
 *
 * @author OHSDEV
 * The class build Specimen object for tree view
 *
 */

public class SpecimenTreeViewAdapter extends SpecimenAdapter {

    public SpecimenTreeViewAdapter(AbstractAdapterBase parent, SpecimenWrapper sample) {
        super(parent, sample);
    }

    private static final I18n i18n = I18nFactory
            .getI18n(CollectionView.class);


    @Override
    public void init() {
        Integer id = ((SpecimenWrapper) getModelObject()).getId();

        this.isDeletable = isAllowed(new SpecimenDeletePermission(id));
        this.isReadable = isAllowed(new SpecimenReadPermission(id));
        this.isEditable = isAllowed(new SpecimenUpdatePermission(id));
    }

    @Override
    protected SpecimenTreeViewAdapter createChildNode() {
        return new SpecimenTreeViewAdapter(this, null);
    }


    @Override
    protected SpecimenTreeViewAdapter createChildNode(Object child) {
    return new SpecimenTreeViewAdapter(this,(SpecimenWrapper)child);
    }


    @SuppressWarnings("nls")
    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
            throws Exception {
	    String inventoryId = ((SpecimenWrapper) getModelObject()).getInventoryId();
		SpecimenWrapper activeWrapper = null;
		try {
			activeWrapper = SpecimenWrapper.getSpecimen(SessionManager.getAppService(), inventoryId);
		} catch (BiobankCheckException e) {
			BgcPlugin.openAsyncError(
	                i18n.tr("SpecimenAdapter:Not found specimens with inventory id: "+ inventoryId ),
	                e.getLocalizedMessage());
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
	    return activeWrapper.getChildSpecimenCollection(true);
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {

	// in order to match return type need to create MAP structure providing key as speciemn id

	List<SpecimenInfo> infos = SessionManager.getAppService().doAction(
                new CollectionEventGetAliquotedSpecimenListInfoAction(((SpecimenWrapper) getModelObject()).getCollectionEvent().getId())).getList();

	 HashMap<Integer, SpecimenInfo> specimenInfos =new HashMap<Integer, SpecimenInfo>();

	 for (SpecimenInfo info : infos) {
		 specimenInfos.put(info.specimen.getId(), info);
         }

	return specimenInfos;

    }

    @Override
    public void addChild(AbstractAdapterBase child) {
	hasChildren = true;
        AbstractAdapterBase existingNode = contains(child);
        if (existingNode != null) {
            // don't add - assume our model is up to date
            return;
        }

        child.setParent(this);
        children.add(child);
        child.addListener(deltaListener);

        fireAdd(child);
    }


    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
	this.loadChildren(true);
	if (!this.hasChildren())
		return null;
	// parameters are not used since search is internally

	List<AbstractAdapterBase> specimenAdapters = this.getChildren();

        for (AbstractAdapterBase adapter: specimenAdapters ){
		adapter.search(searchedClass, adapter.getId());
        }
        return specimenAdapters;
    }

}
