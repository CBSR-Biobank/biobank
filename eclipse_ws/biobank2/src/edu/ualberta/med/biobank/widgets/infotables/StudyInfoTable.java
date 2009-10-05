package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SiteStudyInfo;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyInfoTable extends InfoTableWidget<StudyWrapper> {

    private static final String[] HEADINGS = new String[] { "Name",
        "Short Name", "Activity Staus", "Patients", "Patient Visists" };

    private static final int[] BOUNDS = new int[] { 200, 130, 130, 130, 130,
        -1, -1 };

    public StudyInfoTable(Composite parent, Collection<StudyWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

    @Override
    public void setCollection(final Collection<StudyWrapper> collection) {
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

                    for (StudyWrapper studyWrapper : collection) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        final BiobankCollectionModel item = model.get(count);
                        SiteStudyInfo info = new SiteStudyInfo();
                        item.o = info;
                        info.studyWrapper = studyWrapper;

                        HQLCriteria c = new HQLCriteria(
                            "select count(visits)"
                                + " from "
                                + Study.class.getName()
                                + " as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visits"
                                + " where study=? ", Arrays
                                .asList(new Object[] { studyWrapper
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
                    SessionManager.getLogger().error(
                        "Error while retrieving the clinic", e);
                }
            }
        };
        t.start();
    }

}
