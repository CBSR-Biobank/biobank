package edu.ualberta.med.biobank.test.action;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.processingEvent.ProcessingEventSaveAction;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.DuplicatePropertySetException;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.ContactHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;

public class TestProcessingEvent extends TestAction {

    private SiteWrapper site;
    private StudyWrapper study;
    private ClinicWrapper clinic;
    private Integer patientId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        String name = "Processing Event Test" + Utils.getRandomString(10);
        site = SiteHelper.addSite(name + "site");
        study = StudyHelper.addStudy(name);
        clinic = ClinicHelper.addClinic(name + "clinic");
        ContactWrapper contact = ContactHelper.addContact(clinic, name);
        study.addToContactCollection(Arrays.asList(contact));
        study.persist();
    }

    @Test
    public void testSaveWithoutSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(50);
        List<Comment> comments = Utils.getRandomComments();
        Date date = Utils.getRandomDate();
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, site.getId(), date, worksheet, 1, comments, null));

        openHibernateSession();
        // Check ProcessingEvent is in database with correct values
        ProcessingEvent pevent = (ProcessingEvent) session.get(
            ProcessingEvent.class, pEventId);
        Assert.assertEquals(worksheet, pevent.getWorksheet());
        Assert.assertEquals(comments.size(), pevent.getCommentCollection()
            .size());
        Assert.assertTrue(compareDateInHibernate(date, pevent.getCreatedAt()));
        Assert.assertEquals(0, pevent.getSpecimenCollection().size());
        closeHibernateSession();
    }

    @Test
    public void testSaveWithSpecimens() throws Exception {
        String worksheet = Utils.getRandomString(50);
        List<Comment> comments = Utils.getRandomComments();
        Date date = Utils.getRandomDate();

        // FIXME create a list of specimens to add.
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, site.getId(), date, worksheet, 1, comments, null));

        // FIXME should test to add specimens that can't add ???

        openHibernateSession();
        // Check ProcessingEvent is in database with correct values
        ProcessingEvent pevent = (ProcessingEvent) session.get(
            ProcessingEvent.class, pEventId);
        Assert.assertEquals(worksheet, pevent.getWorksheet());
        Assert.assertEquals(comments.size(), pevent.getCommentCollection()
            .size());
        Assert.assertTrue(compareDateInHibernate(date, pevent.getCreatedAt()));
        Assert.assertEquals(0, pevent.getSpecimenCollection().size());
        closeHibernateSession();
    }

    @Test
    public void testSaveSameWorksheet() throws Exception {
        String worksheet = Utils.getRandomString(50);
        Date date = Utils.getRandomDate();
        Integer pEventId = appService.doAction(new ProcessingEventSaveAction(
            null, site.getId(), date, worksheet, 1, null, null));

        // try to save another pevent with the same worksheet
        try {
            appService.doAction(new ProcessingEventSaveAction(null, site
                .getId(), new Date(), worksheet, 1, null, null));
            Assert
                .fail("should not be able to use the same worksheet to 2 different pevents");
        } catch (DuplicatePropertySetException e) {
            Assert.assertTrue(true);
        }
    }
}
