package edu.ualberta.med.biobank.common.action.specimen;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenGetPossibleTypesAction implements
    Action<ListResult<AliquotedSpecimen>> {

    private static final long serialVersionUID = 1L;
    private Integer id;

    @SuppressWarnings("nls")
    private static final String FILTERED_TYPES =
        "select as from "
            + Specimen.class.getName()
            + " s "
            + "LEFT JOIN s.specimenPosition p "
            + "INNER JOIN s.collectionEvent.patient.study.aliquotedSpecimens as "
            + "INNER JOIN FETCH as.specimenType st "
            + "where s.id=? " // specimen id
            + "and (p is null or st.id in (select cst.id from p.container.containerType.specimenTypes cst))" // containermatch
            + "and st.id in (select pst.id from s.parentSpecimen.specimenType.childSpecimenTypes pst)"; // parenttypematch

    public SpecimenGetPossibleTypesAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(id).isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<AliquotedSpecimen> run(ActionContext context)
        throws ActionException {
        Query q = context.getSession().createQuery(FILTERED_TYPES);
        q.setParameter(0, id);
        return new ListResult<AliquotedSpecimen>(q.list());
    }
}
