package edu.ualberta.med.biobank.test.action.batchoperation.specimen;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GrandchildSpecimenBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpAction;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.test.NameGenerator;
import edu.ualberta.med.biobank.test.Utils;

/**
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
class GrandchildSpecimenBatchOpPojoHelper {

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpAction.class);

    private final NameGenerator nameGenerator;

    GrandchildSpecimenBatchOpPojoHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    /**
     * Creates grandchild specimen BatchOp pojos.
     *
     * @param study the study the the patients belong to. Note that the study must have valid source
     *        specimens and aliquoted specimens defined.
     * @param patients the patients that these specimens will belong to.
     */
    Set<GrandchildSpecimenBatchOpInputPojo> createSpecimens(Study study, Set<Patient> patients) {
        if (study.getSourceSpecimens().size() == 0) {
            throw new IllegalStateException("study does not have any source specimens");
        }

        if (study.getAliquotedSpecimens().size() == 0) {
            throw new IllegalStateException("study does not have any source specimens");
        }

        Set<String> aliquotSpecimenTypesWithChildren = new HashSet<String>();
        for (AliquotedSpecimen as : study.getAliquotedSpecimens()) {
            SpecimenType specimenType = as.getSpecimenType();
            if (specimenType.getChildSpecimenTypes().size() > 0) {
                log.trace("aliquotSpecimenTypesWithChildren: adding " + specimenType.getName());
                aliquotSpecimenTypesWithChildren.add(specimenType.getName());
            }
        }

        if (aliquotSpecimenTypesWithChildren.isEmpty()) {
            throw new IllegalStateException("study does not have grandchild aliquot types");
        }

        Set<GrandchildSpecimenBatchOpInputPojo> specimenInfos =
            new HashSet<GrandchildSpecimenBatchOpInputPojo>();

        for (Patient patient : patients) {
            for (CollectionEvent ce : patient.getCollectionEvents()) {
                for (Specimen specimen : ce.getAllSpecimens()) {
                    SpecimenType specimenType = specimen.getSpecimenType();
                    if (aliquotSpecimenTypesWithChildren.contains(specimenType.getName())) {
                        specimenInfos.addAll(specimensCreate(patient, specimen));
                    }
                }
            }
        }

        return specimenInfos;
    }

    /**
     * Creates child specimens for the given parent specimen.
     *
     * @param patient The patient the parent specimen belongs to.
     *
     * @param parentSpecimen the parent specimen for the specimens to be created.
     *
     * @return the child specimens in a Set.
     */
    public Set<GrandchildSpecimenBatchOpInputPojo> specimensCreate(Patient patient,
                                                                   Specimen parentSpecimen) {
        Set<GrandchildSpecimenBatchOpInputPojo> pojos = new HashSet<GrandchildSpecimenBatchOpInputPojo>();

        for (SpecimenType grandchildSpecimenType : parentSpecimen.getSpecimenType().getChildSpecimenTypes()) {
            GrandchildSpecimenBatchOpInputPojo specimenInfo = new GrandchildSpecimenBatchOpInputPojo();
            specimenInfo.setInventoryId(nameGenerator.next(Specimen.class));
            specimenInfo.setPatientNumber(patient.getPnumber());
            specimenInfo.setParentInventoryId(parentSpecimen.getInventoryId());
            specimenInfo.setSpecimenType(grandchildSpecimenType.getName());
            specimenInfo.setCreatedAt(Utils.getRandomDate());
            specimenInfo.setLineNumber(-1);

            log.trace("specimenCreate: adding specimen of type " + grandchildSpecimenType.getName());
            pojos.add(specimenInfo);
        }

        return pojos;
    }

    public void
    fillContainersWithSpecimenBatchOpPojos(List<GrandchildSpecimenBatchOpInputPojo> specimenCsvInfos,
                                           Set<Container>                           containers,
                                           boolean                                  useProductBarcode) {

        // fill as many containers as space will allow
        int count = 0;
        for (Container container : containers) {
            ContainerType ctype = container.getContainerType();

            int maxRows =
                container.getContainerType().getCapacity().getRowCapacity();
            int maxCols =
                container.getContainerType().getCapacity().getColCapacity();

            for (int r = 0; r < maxRows; ++r) {
                for (int c = 0; c < maxCols; ++c) {
                    if (count >= specimenCsvInfos.size()) break;

                    GrandchildSpecimenBatchOpInputPojo csvInfo = specimenCsvInfos.get(count);
                    RowColPos pos = new RowColPos(r, c);
                    csvInfo.setPalletPosition(ctype.getPositionString(pos));

                    if (useProductBarcode) {
                        csvInfo.setPalletProductBarcode(container.getProductBarcode());
                    } else {
                        csvInfo.setPalletLabel(container.getLabel());
                        csvInfo.setRootContainerType(ctype.getNameShort());
                    }

                    count++;
                }
            }
        }
    }

    public void addComments(Set<GrandchildSpecimenBatchOpInputPojo> specimenCsvInfos) {
        for (GrandchildSpecimenBatchOpInputPojo specimenCsvInfo : specimenCsvInfos) {
            specimenCsvInfo.setComment(nameGenerator.next(String.class));
        }
    }
}
