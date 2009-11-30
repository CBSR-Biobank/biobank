package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.StudyContactAndPatientInfo;

/**
 * Used to display clinic and contact information. Meant to be used by
 * StudyViewForm only.
 */
public class StudyContactInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final String[] HEADINGS = new String[] { "Clinic",
        "#Patients", "#Patient Visits", "Contact Name", "Title" };

    private static final int[] BOUNDS = new int[] { 100, 80, 100, 150, 150 };

    private StudyWrapper studyWrapper;

    public StudyContactInfoTable(Composite parent, StudyWrapper studyWrapper) {
        super(parent, null, HEADINGS, BOUNDS);
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
    public Object getCollectionModelObject(ContactWrapper contact)
        throws Exception {
        StudyContactAndPatientInfo info = new StudyContactAndPatientInfo();
        info.contact = contact;
        ClinicWrapper clinic = contact.getClinic();
        info.clinicName = clinic.getName();
        info.patients = studyWrapper.getPatientCountForClinic(clinic);
        info.patientVisits = studyWrapper.getPatientVisitCountForClinic(clinic);
        return info;
    }
}
