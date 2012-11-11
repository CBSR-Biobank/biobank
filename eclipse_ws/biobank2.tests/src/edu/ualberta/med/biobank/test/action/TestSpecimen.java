package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.search.SpecimenByInventorySearchAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestSpecimen extends TestAction {

    private Provisioning provisioning;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        session.beginTransaction();
        provisioning = new Provisioning(session, factory);
        session.getTransaction().commit();
    }

    @Test
    public void saveNew() throws Exception {

    }

    @Test
    public void checkGetAction() throws Exception {
        final Integer typeId = getSpecimenTypes().get(0).getId();
        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(5,
                typeId, getExecutor().getUserId());

        // Save a new cevent
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, provisioning.patientIds.get(0),
                getR().nextInt(20) + 1, ActivityStatus.ACTIVE, null,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()), null,
            provisioning.getClinic()))
            .getId();

        CEventInfo ceventInfo =
            exec(new CollectionEventGetInfoAction(ceventId));

        SpecimenInfo specimenInfo = ceventInfo.sourceSpecimenInfos.get(0);

        SpecimenBriefInfo specimenBriefInfo =
            exec(new SpecimenGetInfoAction(specimenInfo.specimen
                .getId()));

        Assert.assertEquals(specimenInfo.specimen,
            specimenBriefInfo.getSpecimen());
    }

    // search for an invalid specimenId
    @Test
    public void searchByInventoryIdActionBadId() {
        String badInventoryId = new UUID(128, 256).toString();
        
        final List<Integer> actionResult = exec(
            new SpecimenByInventorySearchAction(badInventoryId, provisioning.siteId)).getList();
        Assert.assertEquals(0, actionResult.size());
    }

    @Test
    public void searchByInventoryIdAction() {
        session.beginTransaction();
        factory.createClinic();
        Site site = factory.createSite();
        factory.createStudy();
        Specimen spc = factory.createParentSpecimen();
        session.getTransaction().commit();
        
        final List<Integer> actionResult = exec(new SpecimenByInventorySearchAction(
            spc.getInventoryId(), site.getId())).getList();
        Assert.assertEquals(1, actionResult.size());
        Assert.assertEquals(spc.getId(), actionResult.get(0));
    }
}
