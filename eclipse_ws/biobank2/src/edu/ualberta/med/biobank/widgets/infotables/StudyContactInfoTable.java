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
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyContactAndPatientInfo;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

/**
 * Used to display clinic and contact information. Meant to be used by
 * StudyViewForm only.
 */
public class StudyContactInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final String[] HEADINGS = new String[] { "Clinic",
        "#Patients", "#Patient Visits", "Contact Name", "Title" };

    private static final int[] BOUNDS = new int[] { 100, 80, 100, 150, 150 };

    private StudyWrapper studyWrapper;

    private WritableApplicationService appService;

    public StudyContactInfoTable(Composite parent,
        WritableApplicationService appService, StudyWrapper studyWrapper) {
        super(parent, null, HEADINGS, BOUNDS);
        this.appService = appService;
        this.studyWrapper = studyWrapper;
        Collection<ContactWrapper> collection = studyWrapper
            .getContactCollection();
        if (collection == null)
            return;

        for (int i = 0, n = collection.size(); i < n; ++i) {
            model.add(new BiobankCollectionModel());
        }
        getTableViewer().refresh();
        setCollection(collection);
    }

    @Override
    public void setCollection(final Collection<ContactWrapper> collection) {
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

                    for (ContactWrapper contact : collection) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        final BiobankCollectionModel item = model.get(count);
                        StudyContactAndPatientInfo info = new StudyContactAndPatientInfo();
                        item.o = info;

                        info.contact = contact;
                        info.clinicName = contact.getClinicWrapper().getName();

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
                                .asList(new Object[] {
                                    studyWrapper.getWrappedObject(),
                                    contact.getClinicWrapper()
                                        .getWrappedObject() }));

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
                                .asList(new Object[] {
                                    studyWrapper.getWrappedObject(),
                                    contact.getClinicWrapper()
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
