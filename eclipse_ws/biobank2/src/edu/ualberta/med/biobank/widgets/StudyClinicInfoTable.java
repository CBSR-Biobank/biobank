package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyClinicInfo;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyClinicInfoTable extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Clinic",
        "No. Patients", "No. Patient Visits" };

    private static final int[] bounds = new int[] { 200, 130, 100, -1, -1, -1,
        -1 };

    private Study study;

    private List<BiobankCollectionModel> model;

    private WritableApplicationService appService;

    public StudyClinicInfoTable(Composite parent,
        WritableApplicationService appService, Study study) {
        super(parent, SWT.NONE, headings, bounds, null);
        this.appService = appService;
        this.study = study;
        int size = study.getClinicCollection().size();

        model = new ArrayList<BiobankCollectionModel>();
        for (int i = 0; i < size; ++i) {
            model.add(new BiobankCollectionModel());
        }

        getTableViewer().setInput(model);
        getTableViewer().addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(DoubleClickEvent event) {
            }
        });
        setStudyInfo();
    }

    public void setStudyInfo() {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    BiobankCollectionModel item;
                    int count = 0;
                    for (Clinic clinic : study.getClinicCollection()) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        item = model.get(count);
                        StudyClinicInfo info = new StudyClinicInfo();
                        item.o = info;
                        info.clinic = clinic;
                        info.clinicName = clinic.getName();

                        HQLCriteria c = new HQLCriteria(
                            "select distinct count(patients)"
                                + " from edu.ualberta.med.biobank.model.Study as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visits"
                                + " inner join visits.clinic as clinic"
                                + " where study.id=? and clinic.id=?"
                                + " group by patients");

                        c.setParameters(Arrays.asList(new Object[] {
                            study.getId(), clinic.getId() }));

                        List<Long> results = appService.query(c);
                        Assert.isTrue(results.size() == 1,
                            "Invalid size for HQL query");
                        info.patients = results.get(0);

                        c = new HQLCriteria(
                            "select distinct count(visits)"
                                + " from edu.ualberta.med.biobank.model.Study as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visits"
                                + " inner join visits.clinic as clinic"
                                + " where study.id=? and clinic.id=?");

                        c.setParameters(Arrays.asList(new Object[] {
                            study.getId(), clinic.getId() }));

                        results = appService.query(c);
                        Assert.isTrue(results.size() == 1,
                            "Invalid size for HQL query");
                        info.patientVisits = results.get(0);

                        getTableViewer().getTable().getDisplay().asyncExec(
                            new Runnable() {

                                public void run() {
                                    getTableViewer().refresh();
                                }

                            });
                        ++count;
                    }
                } catch (ApplicationException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }
}
