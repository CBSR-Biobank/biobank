package edu.ualberta.med.biobank.permission.specimen;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

public class SpecimenReadPermission implements Permission {
    private static final long serialVersionUID = 1L;

    private Integer specimenId = null;
    private String inventoryId = null;

    public SpecimenReadPermission(Integer specimenId) {
        this.specimenId = specimenId;
    }

    public SpecimenReadPermission(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Specimen specimen = null;
        if (specimenId != null)
            specimen = context.get(Specimen.class, specimenId);
        else {
            Query q =
                context
                    .getSession()
                    .createQuery(
                        "from " //$NON-NLS-1$
                            + Specimen.class.getName()
                            + " spec inner join fetch spec.currentCenter inner join fetch " //$NON-NLS-1$
                            + "spec.collectionEvent ce inner join fetch" //$NON-NLS-1$
                            + " ce.patient p inner join fetch p.study where spec.inventoryId=?"); //$NON-NLS-1$
            q.setParameter(0, inventoryId);
            if (q.list().size() > 0)
                specimen = (Specimen) q.list().get(0);
        }

        if (specimen == null) return true;
        Center center = specimen.getCurrentCenter();
        Study study = specimen.getCollectionEvent().getPatient().getStudy();

        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser(),
            center, study);
    }
}
