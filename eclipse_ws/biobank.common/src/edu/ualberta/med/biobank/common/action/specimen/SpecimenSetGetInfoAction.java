package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenStudyCenterReadPermission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenSetGetInfoAction implements Action<ListResult<SpecimenBriefInfo>> {
    private static final long serialVersionUID = 1L;

    private final Integer centerId;

    private final Integer studyId;

    private final Set<String> inventoryIds;

    @SuppressWarnings("nls")
    public SpecimenSetGetInfoAction(Center center, Study study, Set<String> inventoryIds) {
        this.centerId = center.getId();
        this.studyId = study.getId();
        if (inventoryIds == null) {
            throw new IllegalArgumentException("inventory ids is null");
        }
        this.inventoryIds = Collections.unmodifiableSet(inventoryIds);
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenStudyCenterReadPermission(centerId, studyId).isAllowed(context);
    }

    @Override
    public ListResult<SpecimenBriefInfo> run(ActionContext context) throws ActionException {
        List<SpecimenBriefInfo> result = new ArrayList<SpecimenBriefInfo>(inventoryIds.size());
        for (String inventoryId : inventoryIds) {
            SpecimenBriefInfo info = SpecimenActionHelper.getSpecimenBriefInfo(
                context, null, inventoryId);
            if (info != null) {
                result.add(info);
            }
        }
        return new ListResult<SpecimenBriefInfo>(result);
    }

}
