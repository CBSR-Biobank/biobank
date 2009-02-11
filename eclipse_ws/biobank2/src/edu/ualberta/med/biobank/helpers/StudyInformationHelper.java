package edu.ualberta.med.biobank.helpers;

import java.util.List;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.SdataType;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Returns the objects required by the form.
 *
 */
public class StudyInformationHelper implements Runnable {
	
	private WritableApplicationService appService;
	
	private Study study;
	
	private List<SdataType> sdataTypes = null;		
	
	private List<Clinic> allClinics = null;
	
	public StudyInformationHelper(WritableApplicationService appService, Study study) {
		this.appService = appService;
		this.study = study;
	}

	@Override
	public void run() {
		SdataType sdataType = new SdataType();
		Clinic clinic = new Clinic();
		try {
			sdataTypes = appService.search(SdataType.class, sdataType);
			allClinics = appService.search(Clinic.class, clinic);
		} catch (ApplicationException e) {
			e.printStackTrace();
		}
		
		if (study.getId() != null) {
			study.getClinicCollection();
		}
	}
	
	public List<SdataType> getSdataTypes() {
		return sdataTypes;
	}
	
	public List<Clinic> getAllClinics() {
		return allClinics;
	}
}
