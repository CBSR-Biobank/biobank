package edu.ualberta.med.biobank.helpers;

import java.util.List;

import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.SdataType;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Returns the objects required by the form.
 *
 */
public class StudyEntryHelper implements Runnable {
	
	StudyEntryForm form;
	
	public StudyEntryHelper(StudyEntryForm form) {
		this.form = form;
	}

	@Override
	public void run() {		
		List<SdataType> sdataTypes = null;		
		List<Clinic> allClinics = null;
		
		WritableApplicationService appService = form.getSessionAdapter().getAppService();						
		SdataType sdataType = new SdataType();
		Clinic clinic = new Clinic();
		try {
			sdataTypes = appService.search(SdataType.class, sdataType);
			allClinics = appService.search(Clinic.class, clinic);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		
		Study study = form.getStudy();
		
		if (study.getId() != null) {
			study.getClinicCollection();
		}
		
		form.helperResult(sdataTypes, allClinics);
	}
}
