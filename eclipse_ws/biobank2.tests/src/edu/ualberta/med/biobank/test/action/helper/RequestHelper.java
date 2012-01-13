package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.SubmitRequestAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.test.action.IActionExecutor;
import edu.ualberta.med.biobank.test.Utils;

public class RequestHelper extends Helper {

    public static Integer createRequest(
        IActionExecutor appService, Integer rgId) throws Exception {

        String name = Utils.getRandomString(5);

        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = appService.exec(reader);

        // create specs
        Integer p =
            PatientHelper.createPatient(appService, name + "_patient",
                rg.rg.getStudy().getId());
        Integer ceId =
            CollectionEventHelper.createCEventWithSourceSpecimens(appService,
                p, rgId);

        CollectionEventGetInfoAction ceReader =
            new CollectionEventGetInfoAction(ceId);
        CEventInfo ceInfo = appService.exec(ceReader);
        List<String> specs = new ArrayList<String>();
        for (SpecimenInfo specInfo : ceInfo.sourceSpecimenInfos)
            specs.add(specInfo.specimen.getInventoryId());

        // request specs
        SubmitRequestAction action =
            new SubmitRequestAction(rgId, specs);
        return appService.exec(action).getId();

    }
}
