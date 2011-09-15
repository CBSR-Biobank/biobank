package edu.ualberta.med.biobank.test.wrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.AddressWrapper;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.test.TestDatabase;
import edu.ualberta.med.biobank.test.internal.CollectionEventHelper;
import edu.ualberta.med.biobank.test.internal.PatientHelper;
import edu.ualberta.med.biobank.test.internal.RequestHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestRequest extends TestDatabase {

    @Test
    public void testGettersAndSetters() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testRequestSite");
        StudyWrapper study = StudyHelper.addStudy("testRequestStudy");
        PatientWrapper patient = PatientHelper.addPatient("testPatientNumber",
            study);
        CollectionEventWrapper eventWrapper = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, SpecimenHelper
                .newSpecimen(SpecimenTypeHelper.addSpecimenType("stTypeName")));
        RequestWrapper req = RequestHelper.addRequest(study, true, eventWrapper
            .getOriginalSpecimenCollection(false).get(0));
        testGettersAndSetters(req);

    }

    @Test
    public void testGetConstructorsAndRetrieval() throws Exception {
        RequestWrapper request = new RequestWrapper(appService);
        RequestWrapper request2 = new RequestWrapper(appService, new Request());

        SiteWrapper site = SiteHelper.addSite("testRequestSite");
        StudyWrapper study = StudyHelper.addStudy("testRequestStudy");
        PatientWrapper patient = PatientHelper.addPatient("testPatientNumber",
            study);
        CollectionEventWrapper eventWrapper = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, SpecimenHelper
                .newSpecimen(SpecimenTypeHelper.addSpecimenType("stTypeName")));
        AddressWrapper address = new AddressWrapper(appService);
        address.setCity("testCity");
        address.persist();

        request.setStudy(study);
        request.setSubmitted(new Date());
        request.setAddress(address);

        request2.setStudy(study);
        request2.setSubmitted(new Date());
        request2.setAddress(address);

        request.persist();
        request2.persist();

        try {
            Assert.assertEquals(request,
                RequestWrapper.getRequestByNumber(appService, request.getId())
                    .get(0));
            Assert.assertEquals(request2,
                RequestWrapper.getRequestByNumber(appService, request2.getId())
                    .get(0));
        } finally {
            request.delete();
            request2.delete();
        }
    }

    @Test
    public void testGetRequestSpecimens() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testRequestSite");
        StudyWrapper study = StudyHelper.addStudy("testRequestStudy");
        PatientWrapper patient = PatientHelper.addPatient("testPatientNumber",
            study);
        CollectionEventWrapper eventWrapper = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, SpecimenHelper
                .newSpecimen(SpecimenTypeHelper.addSpecimenType("stTypeName")));
        RequestWrapper req = RequestHelper.addRequest(study, true, eventWrapper
            .getOriginalSpecimenCollection(false).get(0));

        List<RequestSpecimenWrapper> specs = req
            .getNonProcessedRequestSpecimenCollection();

        Assert.assertEquals(1, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getUnavailableRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getProcessedRequestSpecimenCollection()
            .size());

        specs.get(0).setState(RequestSpecimenState.PULLED_STATE);
        specs.get(0).persist();
        req.reload();

        Assert.assertEquals(0, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getUnavailableRequestSpecimenCollection()
            .size());
        Assert.assertEquals(1, req.getProcessedRequestSpecimenCollection()
            .size());

        specs.get(0).setState(RequestSpecimenState.UNAVAILABLE_STATE);
        specs.get(0).persist();
        req.reload();

        Assert.assertEquals(0, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(1, req.getUnavailableRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getProcessedRequestSpecimenCollection()
            .size());
    }

    @Test
    public void testRequestReceiving() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testRequestSite");
        StudyWrapper study = StudyHelper.addStudy("testRequestStudy");
        PatientWrapper patient = PatientHelper.addPatient("testPatientNumber",
            study);
        CollectionEventWrapper eventWrapper = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, SpecimenHelper
                .newSpecimen(SpecimenTypeHelper.addSpecimenType("stTypeName")));
        RequestWrapper req = RequestHelper.addRequest(study, true, eventWrapper
            .getOriginalSpecimenCollection(false).get(0));

        List<RequestSpecimenWrapper> rspecs = req
            .getNonProcessedRequestSpecimenCollection();
        List<SpecimenWrapper> specs = new ArrayList<SpecimenWrapper>();
        specs.add(rspecs.get(0).getSpecimen());

        Assert.assertEquals(1, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getUnavailableRequestSpecimenCollection()
            .size());

        req.receiveSpecimens(specs);

        Assert.assertEquals(0, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(1, req.getProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getUnavailableRequestSpecimenCollection()
            .size());

    }

    @Test
    public void testResetStateList() throws Exception {
        SiteWrapper site = SiteHelper.addSite("testRequestSite");
        StudyWrapper study = StudyHelper.addStudy("testRequestStudy");
        PatientWrapper patient = PatientHelper.addPatient("testPatientNumber",
            study);
        CollectionEventWrapper eventWrapper = CollectionEventHelper
            .addCollectionEvent(site, patient, 1, SpecimenHelper
                .newSpecimen(SpecimenTypeHelper.addSpecimenType("stTypeName")));
        RequestWrapper req = RequestHelper.addRequest(study, true, eventWrapper
            .getOriginalSpecimenCollection(false).get(0));

        List<RequestSpecimenWrapper> rspecs = req
            .getNonProcessedRequestSpecimenCollection();
        List<SpecimenWrapper> specs = new ArrayList<SpecimenWrapper>();
        specs.add(rspecs.get(0).getSpecimen());

        Assert.assertEquals(1, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getUnavailableRequestSpecimenCollection()
            .size());

        req.resetStateLists();

        Assert.assertEquals(1, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getUnavailableRequestSpecimenCollection()
            .size());

        req.getRequestSpecimen(
            req.getNonProcessedRequestSpecimenCollection().get(0).getSpecimen()
                .getInventoryId()).setState(
            RequestSpecimenState.DISPATCHED_STATE);
        req.resetStateLists();

        Assert.assertEquals(0, req.getNonProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getProcessedRequestSpecimenCollection()
            .size());
        Assert.assertEquals(0, req.getUnavailableRequestSpecimenCollection()
            .size());
    }

}
