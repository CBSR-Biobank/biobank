package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.StudyContactAndPatientInfo;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

/**
 * Used to display clinic and contact information. Meant to be used by
 * StudyViewForm only.
 */
public class StudyContactInfoTable extends InfoTableWidget<ContactWrapper> {

    private static final String[] HEADINGS = new String[] { "Clinic",
        "#Patients", "#Patient Visits", "Contact Name", "Title" };

    private static final int[] BOUNDS = new int[] { 100, 80, 100, 150, 150 };

    private StudyWrapper study;

    public StudyContactInfoTable(Composite parent, StudyWrapper studyWrapper) {
        super(parent, null, HEADINGS, BOUNDS);
        this.study = studyWrapper;
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
        info.patients = study.getPatientCountForClinic(clinic);
        info.patientVisits = study.getPatientVisitCountForClinic(clinic);
        return info;
    }

    @Override
    public List<ContactWrapper> getCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BiobankLabelProvider getLabelProvider() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ContactWrapper getSelection() {
        // TODO Auto-generated method stub
        return null;
    }
}
