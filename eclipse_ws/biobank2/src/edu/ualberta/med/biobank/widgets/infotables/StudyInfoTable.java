package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.SiteStudyInfo;

public class StudyInfoTable extends InfoTableWidget<StudyWrapper> {

    private static final String[] HEADINGS = new String[] { "Name",
        "Short Name", "Status", "Patients", "Patient Visits" };

    private static final int[] BOUNDS = new int[] { 160, 130, 130, 130, 130 };

    public StudyInfoTable(Composite parent, Collection<StudyWrapper> collection) {
        super(parent, collection, HEADINGS, BOUNDS);
    }

    @Override
    public Object getCollectionModelObject(StudyWrapper study) throws Exception {
        SiteStudyInfo info = new SiteStudyInfo();
        info.studyWrapper = study;
        info.patientVisits = study.getPatientVisitCount();
        return info;
    }

}
