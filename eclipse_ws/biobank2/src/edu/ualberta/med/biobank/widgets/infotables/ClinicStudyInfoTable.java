package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicStudyInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicStudyInfoTable extends InfoTableWidget<Study> {

    private static final String[] headings = new String[] { "Study",
        "No. Patients", "No. Patient Visits" };

    private static final int[] bounds = new int[] { 200, 130, 100, -1, -1, -1,
        -1 };

    private Clinic clinic;

    private WritableApplicationService appService;

    public ClinicStudyInfoTable(Composite parent,
        WritableApplicationService appService, Clinic clinic) {
        super(parent, null, headings, bounds);
        this.appService = appService;
        this.clinic = clinic;
        setCollection(clinic.getStudyCollection());
    }

    @Override
    public void setCollection(final Collection<Study> collection) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    BiobankCollectionModel item;
                    model.clear();
                    for (Study study : clinic.getStudyCollection()) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        item = new BiobankCollectionModel();
                        ClinicStudyInfo info = new ClinicStudyInfo();
                        item.o = info;
                        model.add(item);
                        info.study = study;
                        info.studyShortName = study.getNameShort();

                        HQLCriteria c = new HQLCriteria(
                            "select distinct patients"
                                + " from edu.ualberta.med.biobank.model.Study as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visits"
                                + " inner join visits.clinic as clinic"
                                + " where study.id=? and clinic.id=?");

                        c.setParameters(Arrays.asList(new Object[] {
                            study.getId(), clinic.getId() }));

                        List<Patient> result1 = appService.query(c);
                        info.patients = result1.size();

                        c = new HQLCriteria(
                            "select count(patients)"
                                + " from edu.ualberta.med.biobank.model.Study as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visit"
                                + " inner join visit.clinic as clinic"
                                + " where study.id=? and clinic.id=?");

                        c.setParameters(Arrays.asList(new Object[] {
                            study.getId(), clinic.getId() }));

                        List<Long> results = appService.query(c);
                        Assert.isTrue(results.size() == 1,
                            "Invalid size for HQL query");
                        info.patientVisits = results.get(0);
                    }

                    getTableViewer().getTable().getDisplay().asyncExec(
                        new Runnable() {

                            public void run() {
                                getTableViewer().refresh();
                            }

                        });
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
