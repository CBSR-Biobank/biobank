package edu.ualberta.med.biobank.tools.testconfig;


import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;


/**
 * Augments the configuration created by {@link TestConfig} by adding child (aliquot) specimens to
 * each patient's collection events.
 *
 * @author Nelson Loyola
 *
 */
public class AliquotsTestConfig extends TestConfig {

    private static final Logger log = LoggerFactory.getLogger(AliquotsTestConfig.class);

    public AliquotsTestConfig(AppArgs appArgs) throws Exception {
        super(appArgs);

        session.beginTransaction();
        addAliquots();
        session.getTransaction().commit();
    }

    @SuppressWarnings("unchecked")
    protected void addAliquots() {
        List<Patient> patients = session.createCriteria(Patient.class).list();

        Site site = (Site) session.createCriteria(Site.class)
            .add(Restrictions.eq("name", "Site1"))
            .uniqueResult();

        List<OriginInfo> originInfos = session.createCriteria(OriginInfo.class)
            .add(Restrictions.eq("center.id", site.getId())).list();
        OriginInfo originInfo = originInfos.get(0);

        SpecimenType specimenType = getSpecimenType(ALQ_SPC_TYPE_NAME);

        for (Patient patient : patients) {
            log.info("adding aliquots to collection event for patient "  + patient.getPnumber());

            int i = 1;
            for (CollectionEvent collectionEvent : patient.getCollectionEvents()) {
                for (Specimen source : collectionEvent.getOriginalSpecimens()) {
                    Specimen specimen = new Specimen();
                    specimen.setInventoryId(source.getInventoryId() + "-" + i);
                    ++i;
                    specimen.setSpecimenType(specimenType);
                    specimen.setParentSpecimen(source);
                    specimen.setCurrentCenter(site);
                    specimen.setCollectionEvent(collectionEvent);
                    specimen.setOriginInfo(originInfo);
                    specimen.setCreatedAt(new Date());

                    collectionEvent.getAllSpecimens().add(specimen);
                    session.update(collectionEvent);
                    session.save(specimen);
                }
            }
        }
    }

}
