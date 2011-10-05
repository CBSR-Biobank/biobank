package edu.ualberta.med.biobank.treeview.report;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.EntityWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractReportGroup extends AdapterBase {
    private Collection<Entity> entities;
    private boolean isModifiable;

    public AbstractReportGroup(AdapterBase parent, int id, String name) {
        super(parent, id, name, true, false);

        int i = 0;
        for (Entity entity : getEntities()) {
            ReportEntityGroup group = new ReportEntityGroup(this, i++, entity);
            group.setParent(this);
            addChild(group);
        }
    }

    public void setModifiable(boolean isModifiable) {
        this.isModifiable = isModifiable;
    }

    public boolean isModifiable() {
        return isModifiable;
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        //
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @Override
    public void rebuild() {
        for (AbstractAdapterBase adaper : getChildren()) {
            adaper.rebuild();
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return searchChildren(searchedClass, objectId);
    }

    protected abstract Collection<ReportWrapper> getReports();

    protected Collection<Entity> getEntities() {
        if (entities == null) {
            WritableApplicationService appService = SessionManager
                .getAppService();
            entities = EntityWrapper.getEntities(appService,
                EntityWrapper.ORDER_BY_NAME);
        }

        return entities;
    }

    @Override
    protected String getLabelInternal() {
        return null;
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
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }
}
