package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Composite;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyClinicInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyClinicInfoTable extends InfoTableWidget<Clinic> {

    private static final String[] headings = new String[] { "Clinic",
        "#Patients", "#Patient Visits", "Contact Name", "Title", "Email",
        "Phone #", "Fax #" };

    private static final int[] bounds = new int[] { 100, 80, 100, 150, 150,
        100, 100, 100 };

    private Study study;

    private WritableApplicationService appService;

    public StudyClinicInfoTable(Composite parent,
        WritableApplicationService appService, Study study) throws Exception {
        super(parent, null, headings, bounds);
        this.appService = appService;
        this.study = study;
        setCollection(ModelUtils.getStudyClinicCollection(appService, study));
    }

    @Override
    public void setCollection(final Collection<Clinic> collection) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    model.clear();
                    BiobankCollectionModel item;
                    for (Clinic clinic : ModelUtils.getStudyClinicCollection(
                        appService, study)) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        item = new BiobankCollectionModel();
                        StudyClinicInfo info = new StudyClinicInfo();
                        item.o = info;
                        model.add(item);

                        info.clinic = clinic;
                        info.clinicName = clinic.getName();

                        HQLCriteria c = new HQLCriteria(
                            "select distinct patients"
                                + " from "
                                + Study.class.getName()
                                + " as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visits"
                                + " inner join visits.clinic as clinic"
                                + " where study=? and clinic=?"
                                + " group by patients", Arrays
                                .asList(new Object[] { study, clinic }));

                        List<Patient> result1 = appService.query(c);
                        info.patients = result1.size();

                        c = new HQLCriteria(
                            "select count(visits)"
                                + " from "
                                + Study.class.getName()
                                + " as study"
                                + " inner join study.patientCollection as patients"
                                + " inner join patients.patientVisitCollection as visits"
                                + " inner join visits.clinic as clinic"
                                + " where study=? and clinic=?", Arrays
                                .asList(new Object[] { study, clinic }));

                        List<Long> results = appService.query(c);
                        Assert.isTrue(results.size() == 1,
                            "Invalid size for HQL query");
                        info.patientVisits = results.get(0);

                        c = new HQLCriteria(
                            "select clinic from "
                                + Contact.class.getName()
                                + " as contacts"
                                + " inner join contacts.clinic as clinic"
                                + " where clinic = ? and contacts.studyCollection.id = ?",
                            Arrays
                                .asList(new Object[] { clinic, study.getId() }));

                        List<Contact> cresults = appService.query(c);
                        Assert.isTrue(cresults.size() == 1,
                            "Invalid size for HQL query");
                        info.contact = cresults.get(0);
                    }

                    getTableViewer().getTable().getDisplay().asyncExec(
                        new Runnable() {

                            public void run() {
                                getTableViewer().refresh();
                            }

                        });
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
