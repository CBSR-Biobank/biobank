package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.ResearchGroupPeer;
import edu.ualberta.med.biobank.common.wrappers.base.ResearchGroupBaseWrapper;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;


/**
 *
 * Code Changes -
 * 		1> Remove query for loading the available Studies not used in other research groups
 * 		   as all Studies will be available to all Research Groups to be associated
 *
 * @author OHSDEV
 *
 */
@SuppressWarnings("unused")
public class ResearchGroupWrapper extends ResearchGroupBaseWrapper {

    public ResearchGroupWrapper(WritableApplicationService appService,
        ResearchGroup rg) {
        super(appService, rg);
    }

    public ResearchGroupWrapper(WritableApplicationService appService) {
        super(appService);
        // TODO Auto-generated constructor stub
    }

    private static final String ALL_RG_QRY = "from " //$NON-NLS-1$
        + ResearchGroup.class.getName();

    public static HashMap<Integer, ResearchGroup> getAllResearchGroups(
        WritableApplicationService appService) throws ApplicationException {
        HashMap<Integer, ResearchGroup> rgs =
            new HashMap<Integer, ResearchGroup>();
        HQLCriteria c = new HQLCriteria(ALL_RG_QRY);
        List<ResearchGroup> ResearchGroups = appService.query(c);
        for (ResearchGroup researchGroup : ResearchGroups)
            rgs.put(researchGroup.getId(), researchGroup);
        return rgs;
    }

    private static final String RG_COUNT_QRY = "select count (*) from " //$NON-NLS-1$
        + ResearchGroup.class.getName();

    public static long getCount(WritableApplicationService appService)
        throws BiobankException, ApplicationException {
        return getCountResult(appService, new HQLCriteria(RG_COUNT_QRY));
    }

    //OHSDEV
    @Override
    public List<StudyWrapper> getStudyCollection() {
        return getStudyCollection(true);
    }

    public long getCollectionEventCountForStudy(StudyWrapper study) {
        // TODO Auto-generated method stub
        return 0;
    }

    public long getPatientCountForStudy(StudyWrapper study) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Long getPatientCount() {
        return (long) 0;
    }

    @Override
    public int compareTo(ModelWrapper<ResearchGroup> wrapper) {
        if (wrapper instanceof ResearchGroupWrapper) {
            String myName = wrappedObject.getName();
            String wrapperName = wrapper.wrappedObject.getName();
            return myName.compareTo(wrapperName);
        }
        return 0;
    }

    @SuppressWarnings("nls")
    private static final String RG_GET = "from "
        + ResearchGroup.class.getName() + " rg where rg.id=?";

    public static ResearchGroupWrapper getResearchGroupById(
        WritableApplicationService appService, Integer id)
        throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(RG_GET);
        criteria.setParameters(Arrays.asList(id));
        List<ResearchGroup> rgs = appService.query(criteria);
        return new ResearchGroupWrapper(appService, rgs.get(0));
    }
}