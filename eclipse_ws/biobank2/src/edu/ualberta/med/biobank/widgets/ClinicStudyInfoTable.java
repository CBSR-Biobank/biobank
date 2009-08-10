package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.ClinicStudyInfo;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ClinicStudyInfoTable extends BiobankCollectionTable {

    private static final String[] headings = new String[] { "Study",
        "No. Patients", "No. Patient Visits" };

    private static final int[] bounds = new int[] { 200, 130, 100, -1, -1, -1,
        -1 };

    private Clinic clinic;

    private List<BiobankCollectionModel> model;

    private WritableApplicationService appService;

    public ClinicStudyInfoTable(Composite parent,
        WritableApplicationService appService, Clinic clinic) {
        super(parent, SWT.NONE, headings, bounds, null);
        this.appService = appService;
        this.clinic = clinic;
        int size = clinic.getStudyCollection().size();

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
                    for (Study study : clinic.getStudyCollection()) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        item = model.get(count);
                        ClinicStudyInfo info = new ClinicStudyInfo();
                        item.o = info;
                        info.study = study;
                        info.studyShortName = study.getNameShort();

                        HQLCriteria c = new HQLCriteria(
                            "select count(patients)"
                                + " from edu.ualberta.med.biobank.model.Study as study"
                                + " inner join study.clinicCollection as clinics"
                                + " inner join study.patientCollection as patients"
                                + " where study.id=? and clinics.id=?");

                        c.setParameters(Arrays.asList(new Object[] {
                            study.getId(), clinic.getId() }));

                        List<Long> results = appService.query(c);
                        if (results.size() == 0)
                            info.patients = 0L;
                        else
                            info.patients = results.get(0);

                        c = new HQLCriteria(
                            "select count(patients)"
                                + " from edu.ualberta.med.biobank.model.Study as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visit"
                                + " inner join visit.clinic as clinic"
                                + " where study.id=? and clinic.id=?");

                        c.setParameters(Arrays.asList(new Object[] {
                            study.getId(), clinic.getId() }));

                        results = appService.query(c);
                        if (results.size() == 0)
                            info.patientVisits = 0L;
                        else
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
