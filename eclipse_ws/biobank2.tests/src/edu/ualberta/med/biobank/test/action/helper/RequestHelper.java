package edu.ualberta.med.biobank.test.action.helper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.test.Factory;

public class RequestHelper extends Helper {

    /*
     * Creates a request with specimens, and the specimens are stored in a container.
     */
    public static Request createRequest(Session session,
                                        Factory factory,
                                        ResearchGroup researchGroup) throws Exception {

        List<String> specimenIds = new ArrayList<>();

        session.beginTransaction();
        Request request = factory.createRequest();
        request.setResearchGroup(researchGroup);

        Set<RequestSpecimen> requestSpecimens = new HashSet<>(0);

        Container container = factory.createContainer();
        factory.createCollectionEvent();
        for (int i = 0; i < 5; i++) {
            Specimen specimen = factory.createParentSpecimen();
            specimenIds.add(specimen.getInventoryId());
            ContainerHelper.placeSpecimenInContainer(session, specimen, container);
            session.update(specimen);

            RequestSpecimen rs = new RequestSpecimen();
            rs.setRequest(request);
            rs.setState(RequestSpecimenState.AVAILABLE_STATE);
            rs.setSpecimen(specimen);
            session.saveOrUpdate(rs);

            requestSpecimens.add(rs);
        }

        request.getRequestSpecimens().addAll(requestSpecimens);
        session.saveOrUpdate(request);

        session.getTransaction().commit();
        session.flush();
        return request;
    }

    public static Dispatch requestAddDispatch(Session session,
                                              Factory factory,
                                              Request request) {
        session.beginTransaction();
        Site sendingSite = factory.getDefaultSite();
        Site receivingSite = factory.createSite();
        Dispatch dispatch = factory.createDispatch(sendingSite, receivingSite);

        Set<DispatchSpecimen> dispatchSpecimens = new HashSet<>(0);
        for (RequestSpecimen rs : request.getRequestSpecimens()) {
            DispatchSpecimen dispatchSpecimen = new DispatchSpecimen();
            dispatchSpecimen.setDispatch(dispatch);
            dispatchSpecimen.setSpecimen(rs.getSpecimen());
            session.save(dispatchSpecimen);
            session.flush();

            dispatchSpecimens.add(dispatchSpecimen);
        }

        dispatch.getDispatchSpecimens().addAll(dispatchSpecimens);
        session.saveOrUpdate(dispatch);

        request.setDispatches(new HashSet<>(Arrays.asList(dispatch)));
        session.saveOrUpdate(request);

        session.getTransaction().commit();
        session.flush();

        return dispatch;
    }
}
