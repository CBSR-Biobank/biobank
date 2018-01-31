package edu.ualberta.med.biobank.common.action.batchoperation.specimen.position;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpGetAction;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.SpecimenPositionBatchOpGetResult.SpecimenInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;

public class SpecimenPositionBatchOpGetAction implements Action<SpecimenPositionBatchOpGetResult> {
    private static final long serialVersionUID = 1L;

    private final Integer id;

    public SpecimenPositionBatchOpGetAction(Integer batchOperationId) {
        this.id = batchOperationId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public SpecimenPositionBatchOpGetResult run(ActionContext context) throws ActionException {
        Session session = context.getSession();
        BatchOperation batch = context.load(BatchOperation.class, id);
        List<Specimen> specimens = SpecimenBatchOpGetAction.getSpecimens(id, session);
        List<SpecimenInfo> specimenData = new ArrayList<SpecimenInfo>(0);

        for (Specimen specimen : specimens) {
            SpecimenInfo info = new SpecimenInfo();
            info.specimen = specimen;

            specimenData.add(info);
            SpecimenPosition specimenPosition = specimen.getSpecimenPosition();
            StringBuffer fullPosition = new StringBuffer();
            fullPosition.append(specimenPosition.getPositionString());

            Container container = specimenPosition.getContainer();
            while (container != null) {
                if (container.getParentContainer() != null) {
                    fullPosition.insert(0, container.getPositionString());
                } else {
                    fullPosition.insert(0, container.getLabel());
                }
                container = container.getParentContainer();
            }

            container = specimenPosition.getContainer().getTopContainer();
            fullPosition.append(" (")
                .append(container.getContainerType().getNameShort())
                .append(")");
            info.fullPositionString = fullPosition.toString();
        }

        SpecimenPositionBatchOpGetResult result =
            new SpecimenPositionBatchOpGetResult(batch,
                                                 BatchOpActionUtil.getFileMetaData(session, id),
                                                 specimenData);

        return result;
    }

}
