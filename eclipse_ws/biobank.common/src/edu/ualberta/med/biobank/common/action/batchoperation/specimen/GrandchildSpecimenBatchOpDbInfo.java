package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import edu.ualberta.med.biobank.model.CollectionEvent;

/**
 * Store database entities for pojos used in specimen batch operations.
 *
 * @author Nelson Loyola
 */
public class GrandchildSpecimenBatchOpDbInfo
                                            extends
                                            CommonSpecimenPojoDbInfo<GrandchildSpecimenBatchOpInputPojo> {

    GrandchildSpecimenBatchOpDbInfo(GrandchildSpecimenBatchOpInputPojo pojo) {
        super(pojo);
    }

    @SuppressWarnings("nls")
    public CollectionEvent getCevent() {
        if (getParentSpecimen() == null) {
            throw new IllegalStateException("parentSpecimen is null");
        }
        return getParentSpecimen().getCollectionEvent();
    }

}
