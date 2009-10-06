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
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ClinicStudyInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicStudyInfoTable extends InfoTableWidget<StudyWrapper> {

    private static final String[] HEADINGS = new String[] { "Study",
        "No. Patients", "No. Patient Visits" };

    private static final int[] BOUNDS = new int[] { 200, 130, 100, -1, -1, -1,
        -1 };

    private ClinicWrapper clinicWrapper;

    private WritableApplicationService appService;

    public ClinicStudyInfoTable(Composite parent,
        WritableApplicationService appService, ClinicWrapper clinicWrapper)
        throws Exception {
        super(parent, null, HEADINGS, BOUNDS);
        this.appService = appService;
        this.clinicWrapper = clinicWrapper;
        Collection<StudyWrapper> collection = clinicWrapper
            .getStudyCollection();
        for (int i = 0, n = collection.size(); i < n; ++i) {
            model.add(new BiobankCollectionModel());
        }
        getTableViewer().refresh();
        setCollection(collection);
    }

    @Override
    public void setCollection(final Collection<StudyWrapper> collection) {
        Assert.isNotNull(collection, "collection is null");
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

                    for (StudyWrapper study : collection) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        final BiobankCollectionModel item = model.get(count);
                        ClinicStudyInfo info = new ClinicStudyInfo();
                        item.o = info;
                        info.studyWrapper = study;
                        info.studyShortName = study.getNameShort();

                        HQLCriteria c = new HQLCriteria(
                            "select distinct patients"
                                + " from "
                                + Study.class.getName()
                                + " as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visits"
                                + " inner join visits.clinic as clinic"
                                + " where study.id=? and clinic.id=?", Arrays
                                .asList(new Object[] { study.getId(),
                                    clinicWrapper.getId() }));

                        List<Patient> result1 = appService.query(c);
                        info.patients = result1.size();

                        c = new HQLCriteria(
                            "select count(patients)"
                                + " from "
                                + Study.class.getName()
                                + " as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visit"
                                + " inner join visit.clinic as clinic"
                                + " where study.id=? and clinic.id=?", Arrays
                                .asList(new Object[] { study.getId(),
                                    clinicWrapper.getId() }));

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
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
