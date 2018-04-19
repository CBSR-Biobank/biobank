package edu.ualberta.med.biobank.tools.testconfig;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

/**
 * Augments the configuration created by {@link AliquotsTestConfig} by adding:
 * - a granchchild specimen type,
 * - an aliquot specimen type to the studies
 *
 * @author Nelson Loyola
 *
 */
public class GrandchildSpecimenTypeTestConfig extends AliquotsTestConfig {

    private static final Logger log = LoggerFactory.getLogger(GrandchildSpecimenTypeTestConfig.class);

    private static final String ALQ_GRANDCHILD_SPC_TYPE_NAME = "DNA";

    private final SpecimenType grandchildSpecimenType;

    public GrandchildSpecimenTypeTestConfig(AppArgs appArgs) throws Exception {
        super(appArgs);

        session.beginTransaction();
        grandchildSpecimenType = createSpecimenTypes();
        addToStudies();
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
}
