package edu.ualberta.med.biobank.test;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.Transaction;
import org.junit.Test;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
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

    @Test
    public void testFetching() {
        Transaction tx = session.beginTransaction();

        String name = getMethodNameR();

        Site site = new Site();
        site.setName(name);
        site.setNameShort(name);
        site.getAddress().setCity(name);

        session.save(site);

        Study study = new Study();
        study.setName(name);
        study.setNameShort(name);

        session.save(study);

        Patient patient = new Patient();
        patient.setCreatedAt(new Date());
        patient.setPnumber(name);
        patient.setStudy(study);

        session.save(patient);

        CollectionEvent collectionEvent = new CollectionEvent();
        collectionEvent.setPatient(patient);
        collectionEvent.setVisitNumber(1);

        session.save(collectionEvent);

        OriginInfo originInfo = new OriginInfo();
        originInfo.setCenter(site);

        session.save(originInfo);

        SpecimenType specimenType = new SpecimenType();
        specimenType.setName(name);
        specimenType.setNameShort(name);

        session.save(specimenType);

        Specimen specimen = new Specimen();
        specimen.setInventoryId(name);
        specimen.setCollectionEvent(collectionEvent);
        specimen.setCurrentCenter(site);
        specimen.setCreatedAt(new Date());
        specimen.setOriginInfo(originInfo);
        specimen.setSpecimenType(specimenType);

        Serializable specimenId = session.save(specimen);

        tx.commit();
        session.close();

        session = SESSION_PROVIDER.openSession();
        tx = session.beginTransaction();

        // System.out.println("start1");
        Specimen l1 = (Specimen) session.load(Specimen.class, specimenId);
        // System.out.println("middle1");
        l1.getCurrentCenter().getName();
        // System.out.println("end1");

        tx.commit();
        session.close();

        session = SESSION_PROVIDER.openSession();
        tx = session.beginTransaction();

        // PROBREM:
        // "SELECT spec"
        // + " FROM edu.ualberta.med.biobank.model.Specimen spec"
        // + " INNER JOIN FETCH spec.specimenType"
        // + " INNER JOIN FETCH spec.currentCenter"
        // + " LEFT JOIN spec.specimenPosition pos"
        // + " LEFT JOIN pos.container parent"
        // + " LEFT JOIN parent.topContainer topparent"
        // + " LEFT JOIN topparent.containerType toptype"
        // + " INNER JOIN FETCH spec.collectionEvent cevent"
        // + " INNER JOIN FETCH spec.originInfo originInfo"
        // + " INNER JOIN FETCH originInfo.center"
        // + " LEFT JOIN FETCH spec.commentCollection"
        // + " INNER JOIN FETCH cevent.patient patient"
        // + " INNER JOIN FETCH patient.study study"

        // System.out.println("start2");
        session
            .createQuery(
                "select specimen from edu.ualberta.med.biobank.model.Specimen specimen"
                    +
                    " inner join fetch specimen.currentCenter" +
                    " inner join fetch specimen.originInfo originInfo" +
                    " inner join fetch originInfo.center " +
                    " where specimen.id = ?").setParameter(0, specimenId)
            .list();
        // System.out.println("end2");

        tx.commit();
    }
}
