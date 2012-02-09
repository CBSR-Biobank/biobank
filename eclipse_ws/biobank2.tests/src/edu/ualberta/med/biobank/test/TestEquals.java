package edu.ualberta.med.biobank.test;

import org.hibernate.Transaction;
import org.junit.Test;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.test.action.TestAction;

public class TestEquals extends TestAction {
    @Test
    public void testEquals() {
        Transaction tx = session.beginTransaction();

        String name = getMethodNameR();

        Site site1 = new Site();
        site1.setName(name);
        site1.setNameShort(name);
        site1.getAddress().setCity(name);

        name = getMethodNameR();

        Site site2 = new Site();
        site2.setName(name);
        site2.setNameShort(name);
        site2.getAddress().setCity(name);

        // name = getMethodNameR();
        //
        // Clinic clinic1 = new Clinic();
        // clinic.setName(name);

        session.save(site1);
        session.save(site2);
        session.flush();

        tx.commit();
        session.close();

        session = SESSION_PROVIDER.openSession();
        tx = session.beginTransaction();

        Site site1loaded = (Site) session.load(Site.class, site1.getId());
        Site site2loaded = (Site) session.load(Site.class, site2.getId());

        Assert.isTrue(site1.equals(site1));
        Assert.isTrue(site1.equals(site1loaded));
        Assert.isTrue(site1loaded.equals(site1));
        Assert.isTrue(site1loaded.equals(site1loaded));

        Assert.isTrue(site2.equals(site2));
        Assert.isTrue(site2.equals(site2loaded));
        Assert.isTrue(site2loaded.equals(site2));
        Assert.isTrue(site2loaded.equals(site2loaded));

        Assert.isTrue(!site1.equals(site2));
        Assert.isTrue(!site1.equals(site2loaded));
        Assert.isTrue(!site1loaded.equals(site2));
        Assert.isTrue(!site1loaded.equals(site2loaded));

        tx.commit();
    }
}
