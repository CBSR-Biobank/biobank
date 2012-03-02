package edu.ualberta.med.biobank.test.action;

import junit.framework.Assert;

import org.hibernate.Transaction;
import org.junit.Test;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.BbGroup;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class TestMembership extends TestAction {
    @Test
    public void testUniqueAllStudiesAllCentersMembership() {
        Transaction tx = session.beginTransaction();

        String name = getMethodNameR();

        Site site = new Site();

        ActivityStatus active = ActivityStatus.ACTIVE;

        site.setActivityStatus(active);
        site.getAddress().setCity("something");
        site.setName(name);
        site.setNameShort(name);

        Study study = new Study();
        study.setName(name);
        study.setNameShort(name);
        study.setActivityStatus(active);

        session.save(study);
        session.save(site);

        Membership membership1 = new Membership();
        Membership membership2 = new Membership();
        membership2.setCenter(site);
        membership2.setStudy(study);

        BbGroup group = new BbGroup();
        group.setName(name);
        group.setDescription(name);

        group.getMembershipCollection().add(membership1);
        group.getMembershipCollection().add(membership2);

        membership1.setPrincipal(group);
        membership2.setPrincipal(group);

        session.save(group);
        session.save(membership1);
        session.save(membership2);

        session.flush();

        tx.commit();

        tx = session.beginTransaction();

        Membership membership3 = new Membership();
        membership3.setPrincipal(group);
        group.getMembershipCollection().add(membership3);

        session.save(membership3);

        try {
            tx.commit();
            Assert
                .fail("Should not be able to save two memberships with no center or study");
        } catch (Exception e) {
            tx.rollback();
        }
    }
}
