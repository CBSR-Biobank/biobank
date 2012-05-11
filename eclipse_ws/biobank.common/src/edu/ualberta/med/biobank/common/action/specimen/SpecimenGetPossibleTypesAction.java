package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.AbstractBiobankModel;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenGetPossibleTypesAction implements
    Action<ListResult<AbstractBiobankModel>> {

    private static final long serialVersionUID = 1L;
    private Integer id;

    @SuppressWarnings("nls")
    private static final String ALIQUOTED_TYPES =
        "select aspec from "
            + Specimen.class.getName()
            + " s "
            + "LEFT JOIN s.specimenPosition p "
            + "INNER JOIN s.collectionEvent.patient.study.aliquotedSpecimens aspec "
            + "INNER JOIN FETCH aspec.specimenType st "
            + "where s.id=? " // specimen id
            + "and (p is null or st.id in (select cst.id from p.container.containerType.specimenTypes cst))" // containermatch
            + "and st.id in (select pst.id from s.parentSpecimen.specimenType.childSpecimenTypes pst)"; // parenttypematch

    @SuppressWarnings("nls")
    private static final String SOURCE_TYPES =
        "select sspec from "
            + Specimen.class.getName()
            + " s "
            + "LEFT JOIN s.specimenPosition p "
            + "INNER JOIN s.collectionEvent.patient.study.sourceSpecimens sspec "
            + "INNER JOIN FETCH sspec.specimenType st "
            + "where s.id=? " // specimen id
            + "and (p is null or st.id in (select cst.id from p.container.containerType.specimenTypes cst))"; // containermatch

    public SpecimenGetPossibleTypesAction(Integer id) {
        this.id = id;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(id).isAllowed(context);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<AbstractBiobankModel> run(ActionContext context)
        throws ActionException {
        Specimen spec = context.load(Specimen.class, id);
        if (!spec.getChildSpecimens().isEmpty())
            return new ListResult<AbstractBiobankModel>(
                new ArrayList<AbstractBiobankModel>());
        Query q;
        if (spec.getParentSpecimen() == null)
            q = context.getSession().createQuery(SOURCE_TYPES);
        else
            q = context.getSession().createQuery(ALIQUOTED_TYPES);
        q.setParameter(0, id);
        return new ListResult<AbstractBiobankModel>(q.list());
    }
}
