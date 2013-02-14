package edu.ualberta.med.biobank.common.action.specimenType;

import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeReadPermission;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenTypesGetForContainerTypesAction implements Action<ListResult<SpecimenType>> {
    private static final long serialVersionUID = 1L;

    private final Integer siteId;
    private final Set<Capacity> capacities;

    public SpecimenTypesGetForContainerTypesAction(Site site, Set<Capacity> capacities) {
        this.siteId = site.getId();
        this.capacities = capacities;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Site site = context.load(Site.class, siteId);
        return new ContainerTypeReadPermission(site).isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public ListResult<SpecimenType> run(ActionContext context) throws ActionException {
        if (capacities.isEmpty()) {
            throw new ActionException("capacities is empty");
        }

        Criteria criteria = context.getSession().createCriteria(SpecimenType.class, "stype")
            .createAlias("stype.containerTypes", "ctypes")
            .add(Restrictions.eq("ctypes.site.id", siteId));

        Disjunction disjunction = Restrictions.disjunction();
        for (Capacity capacity : capacities) {
            Criterion cap = Restrictions.and(
                Restrictions.eq("ctypes.capacity.rowCapacity", capacity.getRowCapacity()),
                Restrictions.eq("ctypes.capacity.colCapacity", capacity.getColCapacity()));
            disjunction.add(cap);
        }
        criteria.add(disjunction);

        @SuppressWarnings("unchecked")
        List<SpecimenType> specimenTypes = criteria.list();

        return new ListResult<SpecimenType>(specimenTypes);
    }
}
