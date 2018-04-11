package edu.ualberta.med.biobank.common.action.info;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.ResearchGroup;

/**
 *
 * Data object that holds the research group information as
 * well as the list of studies that the research group can
 * be associated with when the Research Group is being read.
 *
 * This object is created by the ResearchGroupGetInfoAction class.
 *
 * Code Changes -
 * 		1> Enhance class for accepting a list of studies
 * 		2> Set the class variables as private
 * 		3> Add setter/getter methods for variables
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupReadInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    private ResearchGroup researchGroup;
    private List<StudyCountInfo> studies = new ArrayList<StudyCountInfo>();

    public ResearchGroup getResearchGroup() {
		return researchGroup;
	}

	public void setResearchGroup(ResearchGroup researchGroup) {
		this.researchGroup = researchGroup;
	}

	public List<StudyCountInfo> getStudies() {
		return studies;
	}

	public void setStudies(List<StudyCountInfo> studies) {
		this.studies = studies;
	}
}