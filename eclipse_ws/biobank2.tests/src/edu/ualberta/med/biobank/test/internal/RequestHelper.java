package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;

public class RequestHelper extends DbHelper {

    public static List<RequestWrapper> createdRequests = new ArrayList<RequestWrapper>();

    public static RequestWrapper newRequest(StudyWrapper study,
        SpecimenWrapper... specimens) throws Exception {
        RequestWrapper request = new RequestWrapper(appService);
        request.setStudy(study);

        List<RequestSpecimenWrapper> specs = new ArrayList<RequestSpecimenWrapper>();
        if (specimens != null) {
            for (SpecimenWrapper spec : specimens) {
                RequestSpecimenWrapper rs = new RequestSpecimenWrapper(
                    appService);
                rs.setSpecimen(spec);
                rs.setState(RequestSpecimenState.AVAILABLE_STATE);
                rs.setRequest(request);
                specs.add(rs);
            }
        }
        request.addToRequestSpecimenCollection(specs);
        AddressWrapper address = new AddressWrapper(appService);
        address.setCity("derp");
        address.persist();
        request.setAddress(address);

        return request;
    }

    public static RequestWrapper addRequest(StudyWrapper study,
        boolean addToCreatedList, SpecimenWrapper... containers)
        throws Exception {
        RequestWrapper request = newRequest(study, containers);
        request.persist();
        request.reload();
        if (addToCreatedList) {
            createdRequests.add(request);
        }
        return request;

    }

    public static void deleteCreatedRequests() throws Exception {
        DbHelper.deleteRequests(createdRequests);
        createdRequests.clear();
    }
}
