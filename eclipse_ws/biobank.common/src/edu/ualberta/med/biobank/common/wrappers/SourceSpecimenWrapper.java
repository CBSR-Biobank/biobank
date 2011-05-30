package edu.ualberta.med.biobank.common.wrappers;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.wrappers.base.SourceSpecimenBaseWrapper;
import edu.ualberta.med.biobank.model.SourceSpecimen;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceSpecimenWrapper extends SourceSpecimenBaseWrapper {

    private Set<AliquotedSpecimenWrapper> deletedAliquotedSpecimens = new HashSet<AliquotedSpecimenWrapper>();

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

    /**
     * Removes the sample storage objects that are not contained in the
     * collection.
     */
    private void deleteAliquotedSpecimens() throws Exception {
        for (AliquotedSpecimenWrapper st : deletedAliquotedSpecimens) {
            if (!st.isNew()) {
                st.delete();
            }
        }
    }

    @Override
    protected void persistDependencies(SourceSpecimen origObject)
        throws Exception {
        deleteAliquotedSpecimens();
    }

    @Override
    public void addToAliquotedSpecimenCollection(
        List<AliquotedSpecimenWrapper> AliquotedSpecimenCollection) {
        super.addToAliquotedSpecimenCollection(AliquotedSpecimenCollection);

        // make sure previously deleted ones, that have been re-added, are
        // no longer deleted
        deletedAliquotedSpecimens.removeAll(AliquotedSpecimenCollection);
    }

    @Override
    public void removeFromAliquotedSpecimenCollection(
        List<AliquotedSpecimenWrapper> AliquotedSpecimensToRemove) {
        deletedAliquotedSpecimens.addAll(AliquotedSpecimensToRemove);
        super.removeFromAliquotedSpecimenCollection(AliquotedSpecimensToRemove);
    }

    @Override
    public void resetInternalFields() {
        deletedAliquotedSpecimens.clear();
    }

}
