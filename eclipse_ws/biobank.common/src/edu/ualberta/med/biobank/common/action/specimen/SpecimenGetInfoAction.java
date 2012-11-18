package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Stack;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.permission.specimen.SpecimenReadPermission;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;

public class SpecimenGetInfoAction implements Action<SpecimenBriefInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String SPECIMEN_INFO_HQL =
        "SELECT distinct spc FROM " + Specimen.class.getName() + " spc"
            + " INNER JOIN FETCH spc.topSpecimen"
            + " INNER JOIN FETCH spc.specimenType"
            + " INNER JOIN FETCH spc.currentCenter"
            + " INNER JOIN FETCH spc.originInfo originInfo"
            + " INNER JOIN FETCH originInfo.center"
            + " INNER JOIN FETCH spc.collectionEvent cevent"
            + " INNER JOIN FETCH cevent.patient patient"
            + " INNER JOIN FETCH patient.study"
            + " LEFT JOIN FETCH spc.specimenPosition pos"
            + " LEFT JOIN FETCH originInfo.shipmentInfo"
            + " LEFT JOIN FETCH pos.container"
            + " LEFT JOIN FETCH spc.dispatchSpecimens ds"
            + " LEFT JOIN FETCH ds.dispatch"
            + " LEFT JOIN FETCH spc.childSpecimens"
            + " LEFT JOIN FETCH spc.comments comment"
            + " LEFT JOIN FETCH comment.user"
            + " WHERE spc.id=?";

    public static class SpecimenBriefInfo implements ActionResult {
        private static final long serialVersionUID = 1L;

        private Specimen specimen;
        private Stack<Container> parents = new Stack<Container>();
        private BatchOperation batch;

        public SpecimenBriefInfo() {
        }

        public SpecimenBriefInfo(Specimen specimen, Stack<Container> parents,
            BatchOperation batch) {
            this.specimen = specimen;
            this.parents = parents;
            this.batch = batch;
        }

        public Specimen getSpecimen() {
            return specimen;
        }

        public Stack<Container> getParents() {
            return parents;
        }

        public BatchOperation getBatch() {
            return batch;
        }
    }

    private final Integer specimenId;

    public SpecimenGetInfoAction(Integer specimenId) {
        this.specimenId = specimenId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SpecimenReadPermission(specimenId).isAllowed(context);
    }

    @Override
    public SpecimenBriefInfo run(ActionContext context) throws ActionException {
        Query query = context.getSession().createQuery(SPECIMEN_INFO_HQL);
        query.setParameter(0, specimenId);

        Specimen specimen = (Specimen) query.uniqueResult();

        // lazy load some associations
        ProcessingEvent pevent = specimen.getProcessingEvent();
        if (pevent != null) {
            specimen.getProcessingEvent().getCenter().getName();
        }
        Specimen topSpecimen = specimen.getTopSpecimen();
        if (topSpecimen != null) {
            ProcessingEvent topPevent = topSpecimen.getProcessingEvent();
            if (topPevent != null) {
                topPevent.getCenter();
            }
            topSpecimen.getOriginInfo().getCenter().getName();
        }

        // get all parent containers - can be used for visualisation
        Stack<Container> parents = new Stack<Container>();
        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos != null) {
            Container container = pos.getContainer();
            while (container != null) {
                container.getContainerType().getChildLabelingScheme()
                    .getName();
                container.getContainerType().getCapacity().getRowCapacity();
                parents.push(container);
                container = container.getParentContainer();
            }
        }

        @SuppressWarnings("nls")
        BatchOperation batch = (BatchOperation) context.getSession()
            .createQuery("select bos.batch " +
                " from " + BatchOperationSpecimen.class.getName() + " bos" +
                " where bos.specimen.id = ?")
            .setParameter(0, specimenId)
            .uniqueResult();

        return new SpecimenBriefInfo(specimen, parents, batch);
    }

}
