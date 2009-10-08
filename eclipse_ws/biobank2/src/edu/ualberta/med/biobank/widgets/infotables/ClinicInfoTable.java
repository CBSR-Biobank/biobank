package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.SiteClinicInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicInfoTable extends InfoTableWidget<ClinicWrapper> {

    private static Logger LOGGER = Logger.getLogger(ClinicInfoTable.class
        .getName());

    private static final String[] HEADINGS = new String[] { "Name",
        "Num Studies" };

    private static final int[] BOUNDS = new int[] { 200, 130, -1, -1, -1, -1,
        -1 };

    public ClinicInfoTable(Composite parent,
        Collection<ClinicWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

    @Override
    public void setCollection(final Collection<ClinicWrapper> collection) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final TableViewer viewer = getTableViewer();
                    Display display = viewer.getTable().getDisplay();
                    int count = 0;

                    if (model.size() != collection.size()) {
                        model.clear();
                        for (int i = 0, n = collection.size(); i < n; ++i) {
                            model.add(new BiobankCollectionModel());
                        }
                        display.asyncExec(new Runnable() {
                            public void run() {
                                if (!viewer.getTable().isDisposed())
                                    getTableViewer().refresh();
                            }
                        });
                    }

                    WritableApplicationService appService = SessionManager
                        .getAppService();

                    for (ClinicWrapper clinicWrapper : collection) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        final BiobankCollectionModel item = model.get(count);
                        SiteClinicInfo info = new SiteClinicInfo();
                        item.o = info;
                        info.clinicWrapper = clinicWrapper;

                        HQLCriteria c = new HQLCriteria(
                            "select count(visits) from "
                                + Clinic.class.getName()
                                + " as clinic"
                                + " inner join clinic.patientVisitCollection as visits"
                                + " where clinic = ? ", Arrays
                                .asList(new Object[] { clinicWrapper
                                    .getWrappedObject() }));

                        List<Long> results = appService.query(c);
                        Assert.isTrue(results.size() == 1,
                            "Invalid size for HQL query");
                        info.patientVisits = results.get(0);

                        display.asyncExec(new Runnable() {
                            public void run() {
                                if (!viewer.getTable().isDisposed())
                                    viewer.refresh(item, false);
                            }
                        });
                        ++count;
                    }
                } catch (final RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    LOGGER.error("Error while retrieving the clinic", e);
                }
            }
        };
        t.start();
    }

}
