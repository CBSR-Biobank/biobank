package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.SourceSpecimenBaseWrapper;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceSpecimenWrapper extends SourceSpecimenBaseWrapper {

    public SourceSpecimenWrapper(WritableApplicationService appService,
        SourceSpecimen wrappedObject) {
        super(appService, wrappedObject);
    }

    public SourceSpecimenWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<SourceSpecimen> o) {
        if (o instanceof SourceSpecimenWrapper) {
            return getSpecimenType().compareTo(
                ((SourceSpecimenWrapper) o).getSpecimenType());
        }
        return 0;
    }

    public List<AliquotedSpecimenWrapper> getPossibleDerivedTypes() {
        List<SpecimenTypeWrapper> typeChildren = getSpecimenType()
            .getChildSpecimenTypeCollection(false);
        List<AliquotedSpecimenWrapper> possibleDerived = new ArrayList<AliquotedSpecimenWrapper>();
        for (AliquotedSpecimenWrapper asw : getStudy()
            .getAliquotedSpecimenCollection(false)) {
            if (typeChildren.contains(asw.getSpecimenType())) {
                possibleDerived.add(asw);
            }
        }
        return possibleDerived;
    }

}
