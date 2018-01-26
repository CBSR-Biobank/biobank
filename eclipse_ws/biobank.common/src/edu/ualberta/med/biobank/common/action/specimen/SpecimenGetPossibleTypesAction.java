package edu.ualberta.med.biobank.common.action.specimen;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetPossibleTypesAction.SpecimenTypeData;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;

/**
 * Used to get the possible specimen types that an existing specimen can be changed to. If the
 * specimen is a child specimen, then the corresponding volumes for the specimen types are also
 * returned. The information comes from the study's source specimens and aliquoted specimens.
 *
 * @author Nelson Loyola
 *
 */
public class SpecimenGetPossibleTypesAction implements Action<SpecimenTypeData> {
    private static final long serialVersionUID = 1L;
    private final Integer specimenId;

    public SpecimenGetPossibleTypesAction(Specimen specimen) {
        this.specimenId = specimen.getId();
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(specimenId).isAllowed(context);
    }

    @Override
    public SpecimenTypeData run(ActionContext context) throws ActionException {
        Specimen specimen = context.load(Specimen.class, specimenId);
        Study study = specimen.getCollectionEvent().getPatient().getStudy();

        Set<SpecimenType> specimenTypes = new HashSet<SpecimenType>();
        Map<SpecimenType, BigDecimal> volumeMap = new HashMap<SpecimenType, BigDecimal>();

        if (specimen.getParentSpecimen() == null) {
            for (SourceSpecimen sourceSpecimen : study.getSourceSpecimens()) {
                specimenTypes.add(sourceSpecimen.getSpecimenType());
            }
            Set<SpecimenType> aliquotSpecimenTypes = new HashSet<SpecimenType>();
            for (AliquotedSpecimen aliquotedSpecimen : study.getAliquotedSpecimens()) {
                aliquotSpecimenTypes.add(aliquotedSpecimen.getSpecimenType());
            }
            specimenTypes.removeAll(aliquotSpecimenTypes);
        } else {
            for (AliquotedSpecimen aliquotedSpecimen : study.getAliquotedSpecimens()) {
                SpecimenType specimenType = aliquotedSpecimen.getSpecimenType();
                specimenTypes.add(specimenType);
                volumeMap.put(specimenType, aliquotedSpecimen.getVolume());
            }
        }

        if (specimen.getSpecimenPosition() != null) {
            Container container = specimen.getSpecimenPosition().getContainer();
            specimenTypes.retainAll(container.getContainerType().getSpecimenTypes());
        }

        return new SpecimenTypeData(specimenTypes, volumeMap);
    }

    /**
     * volumeMap is only filled in if the specimen is a child specimen
     *
     * @author Nelson Loyola
     *
     */
    public static class SpecimenTypeData implements ActionResult {
        private static final long serialVersionUID = 1L;

        private final Set<SpecimenType> specimenTypes;

        private final Map<SpecimenType, BigDecimal> volumeMap;

        public SpecimenTypeData(Set<SpecimenType> specimenTypes,
            Map<SpecimenType, BigDecimal> volumeMap) {
            this.specimenTypes = specimenTypes;
            this.volumeMap = volumeMap;
        }

        public Set<SpecimenType> getSpecimenTypes() {
            return specimenTypes;
        }

        public Map<SpecimenType, BigDecimal> getVolumeMap() {
            return volumeMap;
        }
    }
}
