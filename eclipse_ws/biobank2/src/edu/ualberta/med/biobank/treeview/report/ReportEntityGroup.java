package edu.ualberta.med.biobank.treeview.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ReportWrapper;
import edu.ualberta.med.biobank.model.Entity;
import edu.ualberta.med.biobank.model.Report;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

// TODO: delete AbstractReportsGroup
public class ReportEntityGroup extends AdapterBase {
    private final AbstractReportGroup parent;
    private final Entity entity;

    public ReportEntityGroup(AbstractReportGroup parent, int id, Entity entity) {
        super(parent, id, entity.getName(), true, true);

        this.parent = parent;
        this.entity = entity;
    }

    @Override
    public void openViewForm() {
        Assert.isTrue(false, "should not be called"); //$NON-NLS-1$
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @Override
    public String getTooltipText() {
        return null;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (parent.isModifiable()) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(NLS.bind(Messages.ReportEntityGroup_new_label, entity.getName()));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    openNewReport();
                }
            });
        }
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
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
    public List<AdapterBase> search(Object searchedObject) {
        return searchChildren(searchedObject);
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return getWrapperChildren() == null ? 0 : getWrapperChildren().size();
    }

    @Override
    protected AdapterBase createChildNode() {
        return new ReportAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof ReportWrapper);
        return new ReportAdapter(this, (ReportWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        Collection<ReportWrapper> reports = new ArrayList<ReportWrapper>();
        for (ReportWrapper report : parent.getReports()) {
            if (entity.getId().equals(report.getEntity().getId())) {
                reports.add(report);
            }
        }
        return reports;
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    private void openNewReport() {
        if (SessionManager.getInstance().isConnected()) {
            ReportWrapper report = new ReportWrapper(
                SessionManager.getAppService());

            Report rawReport = report.getWrappedObject();
            rawReport.setUserId(SessionManager.getUser().getId().intValue());
            rawReport.setEntity(entity);

            ReportAdapter reportAdapter = new ReportAdapter(this, report);
            reportAdapter.openEntryForm();
        }
    }
}
