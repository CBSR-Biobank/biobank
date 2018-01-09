package edu.ualberta.med.biobank.tools.testconfig;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.tools.GenericAppArgs;

/**
 * Augments the configuration created by {@link TestConfig} by adding:
 * - a granchchild specimen type,
 * - an aliquot specimen type to the studies
 * - child specimens to each patient's collection event
 *
 * @author Nelson Loyola
 *
 */
public class GrandchildSpecimenTypeTestConfig extends TestConfig {

    private static final Logger log = LoggerFactory.getLogger(GrandchildSpecimenTypeTestConfig.class);

    private static final String ALQ_GRANDCHILD_SPC_TYPE_NAME = "DNA";

    private final SpecimenType grandchildSpecimenType;

    public GrandchildSpecimenTypeTestConfig(GenericAppArgs appArgs) throws Exception {
        super(appArgs);

        session.beginTransaction();
        grandchildSpecimenType = createSpecimenTypes();
        addToStudies();
        addAliquots();
        session.getTransaction().commit();
    }

    private SpecimenType createSpecimenTypes() {
        SpecimenType aliquotSpecimenType = getSpecimenType(ALQ_SPC_TYPE_NAME);

        SpecimenType grandchild = new SpecimenType();
        grandchild.setName(ALQ_GRANDCHILD_SPC_TYPE_NAME);
        grandchild.setNameShort(ALQ_GRANDCHILD_SPC_TYPE_NAME);
        session.save(grandchild);

        grandchild.getParentSpecimenTypes().add(aliquotSpecimenType);
        aliquotSpecimenType.getChildSpecimenTypes().add(grandchild);
        session.update(grandchild);
        session.update(aliquotSpecimenType);

        return grandchild;
    }

    private void addToStudies() {
        for (Study study : studies) {
            for (AliquotedSpecimen aliquotedSpecimen : study.getAliquotedSpecimens()) {
                if (!aliquotedSpecimen.getSpecimenType().getName().equals(ALQ_SPC_TYPE_NAME)) continue;
                addGrandchildSpecimen(study);
            }
        }

    }

    private void addGrandchildSpecimen(Study study) {
        log.info("adding grandchild to study "  + study.getNameShort());

        AliquotedSpecimen grandchild = new AliquotedSpecimen();
        grandchild.setStudy(study);
        grandchild.setVolume(new BigDecimal("1.00"));
        grandchild.setQuantity(1);
        grandchild.setSpecimenType(grandchildSpecimenType);
        study.getAliquotedSpecimens().add(grandchild);
        session.save(grandchild);
    }

    @SuppressWarnings("unchecked")
    private void addAliquots() {
        List<Patient> patients = session.createCriteria(Patient.class).list();

        Site site = (Site) session.createCriteria(Site.class)
            .add(Restrictions.eq("name", "Site1")).uniqueResult();

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
