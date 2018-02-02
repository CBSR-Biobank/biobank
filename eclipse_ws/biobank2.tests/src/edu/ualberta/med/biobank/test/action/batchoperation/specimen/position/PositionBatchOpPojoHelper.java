package edu.ualberta.med.biobank.test.action.batchoperation.specimen.position;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpPojo;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.action.batchoperation.specimen.SpecimenBatchOpPojoHelper;

public class PositionBatchOpPojoHelper {

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpAction.class);

    static Set<PositionBatchOpPojo> createPojos(Study study, Set<Patient> patients) {
        if (study.getSourceSpecimens().size() == 0) {
            throw new IllegalStateException("study does not have any source specimens");
        }

        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException("study does not have any source specimens");
        }

        Set<PositionBatchOpPojo> pojos = new LinkedHashSet<PositionBatchOpPojo>();

        for (Patient patient : patients) {
            for (CollectionEvent ce : patient.getCollectionEvents()) {
                pojos.addAll(createPojosForSpecimens(ce.getAllSpecimens()));
            }
        }
        return pojos;
    }

    static Set<PositionBatchOpPojo> createPojosWithPositions(Study          study,
                                                             Set<Patient>   patients,
                                                             Set<Container> containers,
                                                             boolean        useProductBarcode) {

        Set<PositionBatchOpPojo> pojos = createPojos(study, patients);
        SpecimenBatchOpPojoHelper.assignPositionsToPojos(pojos, containers, useProductBarcode);
        return pojos;
    }

    static Set<PositionBatchOpPojo> createPositionPojosWithLabels(Study          study,
                                                                  Set<Patient>   patients,
                                                                  Set<Container> containers) {
        return createPojosWithPositions(study, patients, containers, false);

    }

    static Set<PositionBatchOpPojo> createPositionPojosWithBarcodes(Study          study,
                                                                   Set<Patient>   patients,
                                                                   Set<Container> containers) {
        return createPojosWithPositions(study, patients, containers, true);

    }

    private static Set<PositionBatchOpPojo> createPojosForSpecimens(Set<Specimen> specimens) {
        Set<PositionBatchOpPojo> pojos = new HashSet<PositionBatchOpPojo>(0);

        for (Specimen specimen : specimens) {
            PositionBatchOpPojo pojo = new PositionBatchOpPojo();
            pojo.setLineNumber(pojos.size());
            pojo.setInventoryId(specimen.getInventoryId());

            SpecimenPosition position = specimen.getSpecimenPosition();
            if (position != null) {
                pojo.setCurrentPalletLabel(position.getContainer().getLabel());
            }

            log.debug("createPojosForSpecimens: added pojo for specimen " + specimen.getInventoryId());
            pojos.add(pojo);
        }

        return pojos;
    }
}
