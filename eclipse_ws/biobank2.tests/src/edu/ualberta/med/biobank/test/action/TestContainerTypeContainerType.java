package edu.ualberta.med.biobank.test.action;

import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.junit.Test;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

public class TestContainerTypeContainerType extends TestAction {
    @Test
    public void testCtCt1() {
        Transaction tx = session.beginTransaction();

        String methodNameR = getMethodNameR();

        Site site = new Site();

        site.setActivityStatus(ActivityStatus.ACTIVE);
        site.getAddress().setCity("something");
        site.setName(methodNameR);
        site.setNameShort(methodNameR);

        session.save(site);

        ContainerLabelingScheme sbsScheme = (ContainerLabelingScheme) session
            .createCriteria(ContainerLabelingScheme.class)
            .add(Restrictions.idEq(1))
            .uniqueResult();

        ContainerType ct1 = new ContainerType();
        ct1.setName(methodNameR + "_1");
        ct1.setNameShort(methodNameR + "_1");
        ct1.setActivityStatus(ActivityStatus.ACTIVE);
        ct1.setChildLabelingScheme(sbsScheme);
        ct1.setSite(site);
        ct1.setTopLevel(true);
        ct1.getCapacity().setRowCapacity(5);
        ct1.getCapacity().setColCapacity(5);

        ContainerType ct2 = new ContainerType();
        ct2.setName(methodNameR + "_2");
        ct2.setNameShort(methodNameR + "_2");
        ct2.setActivityStatus(ActivityStatus.ACTIVE);
        ct2.setChildLabelingScheme(sbsScheme);
        ct2.setSite(site);
        ct2.getCapacity().setRowCapacity(5);
        ct2.getCapacity().setColCapacity(5);

        ContainerType ct3 = new ContainerType();
        ct3.setName(methodNameR + "_3");
        ct3.setNameShort(methodNameR + "_3");
        ct3.setActivityStatus(ActivityStatus.ACTIVE);
        ct3.setChildLabelingScheme(sbsScheme);
        ct3.setSite(site);
        ct3.getCapacity().setRowCapacity(5);
        ct3.getCapacity().setColCapacity(5);

        session.save(ct2);
        session.flush();

        session.save(ct3);
        session.flush();

        ct1.getChildContainerTypes().add(ct2);
        ct1.getChildContainerTypes().add(ct3);

        session.save(ct1);
        session.flush();

        tx.commit();
        session.close();

        session = openSession();
        tx = session.beginTransaction();

        Container c1 = new Container();
        c1.setLabel(methodNameR + "_1");
        c1.setActivityStatus(ActivityStatus.ACTIVE);
        c1.setContainerType(ct1);
        c1.setSite(site);

        Container c2 = new Container();
        c2.setLabel(methodNameR + "_2");
        c2.setActivityStatus(ActivityStatus.ACTIVE);
        c2.setContainerType(ct2);
        c2.setSite(site);

        ContainerPosition cp = new ContainerPosition();
        cp.setParentContainer(c1);
        cp.setContainer(c2);
        cp.setRow(0);
        cp.setCol(0);

        session.save(c1);
        session.flush();

        session.save(c2);
        session.flush();

        session.save(cp);
        session.flush();

        tx.commit();
        tx = session.beginTransaction();

        ct1.getChildContainerTypes().remove(ct3);
        session.saveOrUpdate(ct1);
        session.flush();
        // hope the above causes some collection event?

        c2.setContainerType(ct1);
        session.saveOrUpdate(c2);
        session.flush();

        // ct1.getChildContainerTypes().clear();
        // session.saveOrUpdate(ct1);

        tx.commit();
    }
}
