package edu.ualberta.med.biobank.common.permission.specimen;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenReadPermission implements Permission {
    private static final long serialVersionUID = 1L;
    
    @SuppressWarnings("nls")
    private static final String HQL_QRY = 
        "FROM "+ Specimen.class.getName() + " spec " 
            + "INNER JOIN FETCH spec.currentCenter " 
            + "INNER JOIN FETCH spec.collectionEvent ce " 
            + "INNER JOIN FETCH ce.patient p " 
            + "INNER JOIN FETCH p.study " 
            + "WHERE spec.inventoryId=?";

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
            Query q = context.getSession().createQuery(HQL_QRY);
            q.setParameter(0, inventoryId);
            specimen = (Specimen) q.uniqueResult();
        }

        if (specimen == null) return true;
        
        Center center = specimen.getCurrentCenter();
        Study study = specimen.getCollectionEvent().getPatient().getStudy();

        return PermissionEnum.SPECIMEN_READ.isAllowed(context.getUser(),
            center, study);
    }
}
