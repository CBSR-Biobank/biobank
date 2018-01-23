package edu.ualberta.med.biobank.test.action.batchoperation.specimenPosition;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimenPosition.PositionBatchOpPojo;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class PositionBatchOpPojoHelper {

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpAction.class);

    static List<PositionBatchOpPojo> createPositionPojos(Study          study,
                                                        Set<Patient>   patients,
                                                        Set<Container> containers,
                                                        boolean        useProductBarcode) {
        if (study.getSourceSpecimens().size() == 0) {
            throw new IllegalStateException("study does not have any source specimens");
        }

        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException("study does not have any source specimens");
        }

        List<PositionBatchOpPojo> pojos = new ArrayList<PositionBatchOpPojo>();

        for (Patient patient : patients) {
            for (CollectionEvent ce : patient.getCollectionEvents()) {
                pojos.addAll(createPojosForSpecimens(ce.getAllSpecimens()));
            }
        }

        fillPojoContainerInfo(pojos, containers, useProductBarcode);
        return pojos;
    }

    static List<PositionBatchOpPojo> createPositionPojosWithLabels(Study          study,
                                                                  Set<Patient>   patients,
                                                                  Set<Container> containers) {
        return createPositionPojos(study, patients, containers, false);

    }

    static List<PositionBatchOpPojo> createPositionPojosWithBarcodes(Study          study,
                                                                   Set<Patient>   patients,
                                                                   Set<Container> containers) {
        return createPositionPojos(study, patients, containers, true);

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

    private static void fillPojoContainerInfo(List<PositionBatchOpPojo> pojos,
                                              Set<Container>            containers,
                                              boolean                   useProductBarcode) {

        // fill as many containers as space will allow
        List<PositionBatchOpPojo> pojosToAdd = new ArrayList<PositionBatchOpPojo>(pojos);
        Iterator<PositionBatchOpPojo> iterator = pojosToAdd.iterator();

        for (Container container : containers) {
            ContainerType ctype = container.getContainerType();

            int maxRows = container.getContainerType().getCapacity().getRowCapacity();
            int maxCols = container.getContainerType().getCapacity().getColCapacity();

            for (int r = 0; r < maxRows; ++r) {
                for (int c = 0; c < maxCols; ++c) {
                    if (pojosToAdd.isEmpty()) break;

                    PositionBatchOpPojo pojo = iterator.next();
                    iterator.remove();
                    RowColPos pos = new RowColPos(r, c);
                    pojo.setPalletPosition(ctype.getPositionString(pos));

                    if (useProductBarcode) {
                        pojo.setPalletProductBarcode(container.getProductBarcode());
                    } else {
                        pojo.setPalletLabel(container.getLabel());
                        pojo.setRootContainerType(ctype.getNameShort());
                    }
                }
            }
        }
    }
}
