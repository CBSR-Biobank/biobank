package edu.ualberta.med.biobank.common.action.Specimen;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.permission.patient.OriginInfoReadPermission;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;

public class ShipmentGetSpecimenInfosAction implements
    Action<ArrayList<SpecimenInfo>> {

    public static final String SPECIMEN_INFO_HQL = " ";

    private static final long serialVersionUID = 1L;
    private Integer oiId;

    public ShipmentGetSpecimenInfosAction(Integer oiId) {
        this.oiId = oiId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        return new OriginInfoReadPermission(oiId).isAllowed(user, session);
    }

    @Override
    public ArrayList<SpecimenInfo> run(User user, Session session)
        throws ActionException {
        ArrayList<SpecimenInfo> specInfos = new ArrayList<SpecimenInfo>();

        Query query = session.createQuery(SPECIMEN_INFO_HQL);
        query.setParameter(0, oiId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        for (Object[] row : rows) {
            SpecimenInfo specInfo = new SpecimenInfo();
            specInfo.specimen = (Specimen) row[0];
            specInfo.parentLabel = (String) row[1];
            specInfo.positionString = (String) row[2];
            specInfo.topContainerTypeNameShort = (String) row[3];
            specInfos.add(specInfo);
        }
        return specInfos;
    }
}
