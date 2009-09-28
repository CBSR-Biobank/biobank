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
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyContactAndPatientInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * Used to display clinic and contact information. Meant to be used by
 * StudyViewForm only.
 */
public class StudyContactInfoTable extends InfoTableWidget<Contact> {

    private static final String[] HEADINGS = new String[] { "Clinic",
        "#Patients", "#Patient Visits", "Contact Name", "Title" };

    private static final int[] BOUNDS = new int[] { 100, 80, 100, 150, 150 };

    private Study study;

    private WritableApplicationService appService;

    public StudyContactInfoTable(Composite parent,
        WritableApplicationService appService, Study study) {
        super(parent, null, HEADINGS, BOUNDS);
        this.appService = appService;
        this.study = study;
        Collection<Contact> collection = study.getContactCollection();
        for (int i = 0, n = collection.size(); i < n; ++i) {
            model.add(new BiobankCollectionModel());
        }
        getTableViewer().refresh();
        setCollection(collection);
    }

    @Override
    public void setCollection(final Collection<Contact> collection) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    final TableViewer viewer = getTableViewer();
                    Display display = viewer.getTable().getDisplay();
                    int count = 0;

                    for (Contact contact : collection) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        final BiobankCollectionModel item = model.get(count);
                        StudyContactAndPatientInfo info = new StudyContactAndPatientInfo();
                        item.o = info;

                        info.contact = contact;
                        info.clinicName = contact.getClinic().getName();

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
                                .asList(new Object[] { study,
                                    contact.getClinic() }));

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
                                .asList(new Object[] { study,
                                    contact.getClinic() }));

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
