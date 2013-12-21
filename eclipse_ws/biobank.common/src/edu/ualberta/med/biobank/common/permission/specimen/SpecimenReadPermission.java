package edu.ualberta.med.biobank.common.permission.specimen;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private final Integer specimenId;
    private final String inventoryId;

    public SpecimenReadPermission(Integer specimenId) {
        this.specimenId = specimenId;
        this.inventoryId = null;
    }

    public SpecimenReadPermission(String inventoryId) {
        this.inventoryId = inventoryId;
        this.specimenId = null;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Specimen specimen = null;
        if (specimenId != null) {
            specimen = context.get(Specimen.class, specimenId);
        } else {
            @SuppressWarnings("nls")
            Criteria criteria = context.getSession()
                .createCriteria(Specimen.class)
                .add(Restrictions.eq("inventoryId", inventoryId));
            specimen = (Specimen) criteria.uniqueResult();
        }

        if (specimen == null) return true;

        Center center = specimen.getCurrentCenter();
        Study study = specimen.getCollectionEvent().getPatient().getStudy();

        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser(), center, study);
    }
}
