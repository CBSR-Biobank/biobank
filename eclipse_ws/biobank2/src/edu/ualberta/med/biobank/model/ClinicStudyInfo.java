package edu.ualberta.med.biobank.model;

import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class ClinicStudyInfo {

    public Study study;

    public String studyShortName;

    public Long patients;

    public Long patientVisits;

    public void performDoubleClick() {
        try {
            StudyAdapter adapter = new StudyAdapter(null, study);
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                .getActivePage().openEditor(new FormInput(adapter),
                    StudyViewForm.ID, true);
        } catch (PartInitException e) {
            e.printStackTrace();
        }
    }

}
