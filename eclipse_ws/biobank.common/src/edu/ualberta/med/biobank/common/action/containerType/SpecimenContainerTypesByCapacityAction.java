package edu.ualberta.med.biobank.common.action.containerType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;

/**
 * Returns a list of container types, that hold specimens, matching the dimensions requested.
 * 
 * @author loyola
 * 
 */
public class SpecimenContainerTypesByCapacityAction implements Action<ListResult<ContainerType>> {
    private static final long serialVersionUID = 1L;

    private final Integer siteId;

    private final Set<Capacity> requestedCapacities;

    public SpecimenContainerTypesByCapacityAction(Site site, Set<Capacity> capacities) {
        if (site == null) {
            throw new IllegalArgumentException();
        }
        if (capacities == null) {
            throw new IllegalArgumentException();
        }
        this.siteId = site.getId();
        this.requestedCapacities = capacities;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @SuppressWarnings({ "nls", "unchecked" })
    @Override
    public ListResult<ContainerType> run(ActionContext context) throws ActionException {
        List<ContainerType> ctypes = context.getSession()
            .createCriteria(ContainerType.class, "ctype")
            .createAlias("ctype.site", "site")
            .add(Restrictions.eq("site.id", siteId))
            .add(Restrictions.isNotEmpty("ctype.specimenTypes"))
            .add(Restrictions.isEmpty("ctype.childContainerTypes"))
            .list();

        List<ContainerType> result = new ArrayList<ContainerType>();
        for (ContainerType ctype : ctypes) {
            Capacity cap = ctype.getCapacity();
            if (requestedCapacities.contains(cap)) {
                result.add(ctype);
            }
        }
        return new ListResult<ContainerType>(result);
    }
}
