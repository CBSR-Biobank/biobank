package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.researchGroup.RequestSubmitAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.test.action.IActionExecutor;
import edu.ualberta.med.biobank.test.Utils;

public class RequestHelper extends Helper {

    public static Integer createRequest(
        IActionExecutor actionExecutor, Integer rgId) throws Exception {

        String name = Utils.getRandomString(5);

        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(rgId);
        ResearchGroupReadInfo rg = actionExecutor.exec(reader);

        // create specs
        Integer p =
            PatientHelper.createPatient(actionExecutor, name + "_patient",
                rg.rg.getStudy().getId());
        Integer ceId =
            CollectionEventHelper.createCEventWithSourceSpecimens(actionExecutor,
                p, rgId);

        CollectionEventGetInfoAction ceReader =
            new CollectionEventGetInfoAction(ceId);
        CEventInfo ceInfo = actionExecutor.exec(ceReader);
        List<String> specs = new ArrayList<String>();
        for (SpecimenInfo specInfo : ceInfo.sourceSpecimenInfos)
            specs.add(specInfo.specimen.getInventoryId());

        // request specs
        RequestSubmitAction action =
            new RequestSubmitAction(rgId, specs);
        return actionExecutor.exec(action).getId();

    }
}
