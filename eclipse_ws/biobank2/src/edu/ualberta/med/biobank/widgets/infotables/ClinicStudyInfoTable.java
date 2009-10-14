package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ClinicStudyInfo;

public class ClinicStudyInfoTable extends InfoTableWidget<StudyWrapper> {

    private static final String[] HEADINGS = new String[] { "Study",
        "No. Patients", "No. Patient Visits" };

    private static final int[] BOUNDS = new int[] { 200, 130, 100, -1, -1, -1,
        -1 };

    private ClinicWrapper clinicWrapper;

    public ClinicStudyInfoTable(Composite parent, ClinicWrapper clinicWrapper)
        throws Exception {
        super(parent, null, HEADINGS, BOUNDS);
        this.clinicWrapper = clinicWrapper;
        Collection<StudyWrapper> collection = clinicWrapper
            .getStudyCollection(true);
        for (int i = 0, n = collection.size(); i < n; ++i) {
            model.add(new BiobankCollectionModel());
        }
        getTableViewer().refresh();
        setCollection(collection);
    }

    @Override
    public Object getCollectionModelObject(StudyWrapper study) throws Exception {
        ClinicStudyInfo info = new ClinicStudyInfo();
        info.studyWrapper = study;
        info.studyShortName = study.getNameShort();
        info.patients = study.getPatientCountForClinic(clinicWrapper);
        info.patientVisits = study.getPatientVisitCountForClinic(clinicWrapper);
        return info;
    }
}
