package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.IActionExecutor;

public class RequestHelper extends Helper {

    public static Integer createRequest(Session session,
        IActionExecutor actionExecutor, ResearchGroup researchGroup) throws Exception {

        session.beginTransaction();
        String name = Utils.getRandomString(5);

        ResearchGroupGetInfoAction reader =
            new ResearchGroupGetInfoAction(researchGroup.getId());
        ResearchGroupReadInfo rg = actionExecutor.exec(reader);

        // create specs
        Integer p = PatientHelper.createPatient(actionExecutor, name + "_patient", rg.getResearchGroup().getStudies().iterator().next().getId());
        Integer ceId = CollectionEventHelper.createCEventWithSourceSpecimens(
                actionExecutor, p, researchGroup);

        CollectionEventGetInfoAction ceReader =
            new CollectionEventGetInfoAction(ceId);
        CEventInfo ceInfo = actionExecutor.exec(ceReader);
        List<String> specs = new ArrayList<String>();
        for (SpecimenInfo specInfo : ceInfo.sourceSpecimenInfos)
            specs.add(specInfo.specimen.getInventoryId());

        // request specs
        Request request = new Request();
        request.setResearchGroup(researchGroup);
        request.setCreatedAt(new Date());
        request.setAddress(researchGroup.getAddress());

        session.saveOrUpdate(request);

        for (String id : specs) {
            if (id == null || id.equals(""))
                throw new Exception(
                    "Blank specimen id, please check your your file for correct input.");

            Query q = session.createQuery("from "
                + Specimen.class.getName() + " where inventoryId=?");
            q.setParameter(0, id);

            Specimen spec = (Specimen) q.list().get(0);
            if (spec == null)
                continue;
            RequestSpecimen r =
                new RequestSpecimen();
            r.setRequest(request);
            r.setState(RequestSpecimenState.AVAILABLE_STATE);
            r.setSpecimen(spec);
            session.saveOrUpdate(r);
        }
        session.getTransaction().commit();
        session.flush();
        return request.getId();

    }
}
