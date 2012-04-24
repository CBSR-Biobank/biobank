package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.MapResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupAdapterInfo;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupReadPermission;
import edu.ualberta.med.biobank.model.ResearchGroup;

public class ResearchGroupGetAllAction implements
    Action<MapResult<Integer, ResearchGroupAdapterInfo>> {

    public static final String ALL_RG = "from "
        + ResearchGroup.class.getName();

    private static final long serialVersionUID = 1L;

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // anyone can call this... but only users with permissions will get
        // results
        return true;
    }

    @Override
    public MapResult<Integer, ResearchGroupAdapterInfo> run(
        ActionContext context)
        throws ActionException {
        Query q = context.getSession().createQuery(ALL_RG);
        @SuppressWarnings("unchecked")
        List<ResearchGroup> rgs = q.list();
        Map<Integer, ResearchGroupAdapterInfo> adapterInfo =
            new HashMap<Integer, ResearchGroupAdapterInfo>();
        for (ResearchGroup rg : rgs)
            if (new ResearchGroupReadPermission(rg.getId()).isAllowed(context))
                adapterInfo
                    .put(rg.getId(), new ResearchGroupAdapterInfo(rg.getId(),
                        rg.getNameShort()));
        return new MapResult<Integer, ResearchGroupAdapterInfo>(adapterInfo);
    }
}
