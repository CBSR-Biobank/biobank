package edu.ualberta.med.biobank.treeview.report;

import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.springframework.remoting.RemoteConnectFailureException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.reports.EntitiesGetAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public abstract class AbstractReportGroup extends AdapterBase {
    private static final I18n i18n = I18nFactory.getI18n(ReportAdapter.class);

    private static BgcLogger logger = BgcLogger.getLogger(ReportAdapter.class.getName());

    @SuppressWarnings("nls")
    private static final String INIT_FAILED_TITLE = i18n.tr("Initialization failed");

    private Collection<Entity> entities;
    private boolean isModifiable;

    public AbstractReportGroup(AdapterBase parent, int id, String name) {
        super(parent, id, name, true);

        int i = getStartNodeId();
        for (Entity entity : getEntities()) {
            ReportEntityGroup group = new ReportEntityGroup(this, i++, entity);
            group.setParent(this);
            addChild(group);
        }
    }

    protected abstract int getStartNodeId();

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

    protected abstract Collection<ReportWrapper> getReports() throws Exception;

    @SuppressWarnings("nls")
    protected Collection<Entity> getEntities() {
        if (entities == null) {
            try {
                entities = SessionManager.getAppService().doAction(
                    new EntitiesGetAction()).getList();
            } catch (final RemoteConnectFailureException exp) {
                BgcPlugin.openRemoteConnectErrorMessage(exp);
            } catch (ActionException e) {
                BgcPlugin.openAsyncError(INIT_FAILED_TITLE, e);
            } catch (Exception e) {
                BgcPlugin.openAsyncError(INIT_FAILED_TITLE, e);
                logger.error("AbstractReportGroup.getEntities Error", e);
            }
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
    public int compareTo(AbstractAdapterBase o) {
        return 0;
    }
}
