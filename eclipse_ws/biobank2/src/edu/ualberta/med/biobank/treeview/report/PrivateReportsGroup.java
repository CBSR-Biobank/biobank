package edu.ualberta.med.biobank.treeview.report;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
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
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PrivateReportsGroup extends AbstractReportsGroup {
    private static final String NODE_NAME = "My Reports";
    private static final String HQL_REPORT_OF_USER = "from "
        + Report.class.getName() + " where userId = ?";
    private static final Comparator<ReportWrapper> ORDER_REPORT_WRAPPERS_BY_NAME = new Comparator<ReportWrapper>() {
        @Override
        public int compare(ReportWrapper lhs, ReportWrapper rhs) {
            if (lhs.getName() == null) {
                if (rhs.getName() == null) {
                    return 0;
                }
                return -rhs.getName().compareToIgnoreCase(lhs.getName());
            }
            return lhs.getName().compareToIgnoreCase(rhs.getName());
        }
    };

    public PrivateReportsGroup(AdapterBase parent, int id) {
        super(parent, id, NODE_NAME);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        List<ReportWrapper> reports = new ArrayList<ReportWrapper>();

        if (SessionManager.getInstance().isConnected()) {
            Integer userId = SessionManager.getUser().getId().intValue();
            HQLCriteria criteria = new HQLCriteria(HQL_REPORT_OF_USER,
                Arrays.asList(new Object[] { userId }));
            try {
                List<Report> rawReports = SessionManager.getAppService().query(
                    criteria);
                for (Report rawReport : rawReports) {
                    reports.add(new ReportWrapper(SessionManager
                        .getAppService(), rawReport));
                }
                Collections.sort(reports, ORDER_REPORT_WRAPPERS_BY_NAME);
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }

        return reports;
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(ReportWrapper.class, null)) {

            Menu subMenu = new Menu(menu);

            MenuItem mi = new MenuItem(menu, SWT.CASCADE);
            mi.setMenu(subMenu);
            mi.setText("New Report");

            MenuItem subItem = new MenuItem(subMenu, SWT.PUSH);
            subItem.setText("Aliquot Report");
            subItem.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    newReport();
                }
            });
        }
    }

    protected void newReport() {

        // TODO: be smarter! read once?
        // TODO: eager load
        HQLCriteria criteria = new HQLCriteria("from " + Entity.class.getName());
        Entity entity = null;
        try {
            List<Entity> entities = SessionManager.getAppService().query(
                criteria);
            entity = entities.get(0);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }

        ReportWrapper report = new ReportWrapper(SessionManager.getAppService());
        Report rawReport = report.getWrappedObject();
        rawReport.setUserId(SessionManager.getUser().getId().intValue());
        rawReport.setEntity(entity);
        ReportAdapter reportAdapter = new ReportAdapter(this, report);
        reportAdapter.openEntryForm();
    }
}
