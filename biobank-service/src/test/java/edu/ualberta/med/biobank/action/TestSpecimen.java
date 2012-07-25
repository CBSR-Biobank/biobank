package edu.ualberta.med.biobank.action;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.action.helper.SiteHelper.Provisioning;

public class TestSpecimen extends ActionTest {

    private String name;

    private Provisioning provisioning;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
        provisioning = new Provisioning(getExecutor(), name);
    }

    @Test
    public void saveNew() throws Exception {

    }

    @Test
    public void checkGetAction() throws Exception {
        final Integer typeId = getSpecimenTypes().get(0).getId();
        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(5,
                typeId, getExecutor().getUserId(), provisioning.siteId);

        // Save a new cevent
        final Integer ceventId = exec(
            new CollectionEventSaveAction(null, provisioning.patientIds
                .get(0),
                getR().nextInt(20) + 1, ActivityStatus.ACTIVE, null,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()),
                null))
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
}
