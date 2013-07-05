package edu.ualberta.med.biobank.common.action.specimen;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.InventoryIdUtil;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class SpecimenMicroplateConsistentAction implements Action<BooleanResult> {

    public static class SpecimenMicroplateInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public String inventoryId;
        public Integer containerId; // container where this specimen will be stored
        public RowColPos position; // position on container where this specimen will be stored
    }

    @SuppressWarnings("nls")
    // find specimens of a microplate, which are not at a given center
    private static final String SPECIMENS_CENTER_HQL =
        "SELECT s.inventoryId"
            + " FROM " + Specimen.class.getName() + " s"
            + " WHERE s.currentCenter.id != ?"
            + " AND s.inventoryId LIKE ?";

    @SuppressWarnings("nls")
    // find specimens of a microplate, which are not in a given container
    private static final String SPECIMENS_CONTAINER_HQL =
        "SELECT s.inventoryId"
            + " FROM " + Specimen.class.getName() + " s"
            + " LEFT JOIN s.specimenPosition sp"
            + " LEFT JOIN sp.container c"
            + " WHERE (c.id != ? OR c.id IS NULL)"
            + " AND s.inventoryId LIKE ?";

    @SuppressWarnings("nls")
    private static final String SPECIMENS_CONTAINER_NULL_HQL =
        // find specimens of a microplate, which are in any container
        "SELECT s.inventoryId"
            + " FROM " + Specimen.class.getName() + " s"
            + " WHERE s.specimenPosition.id IS NOT NULL"
            + " AND s.inventoryId LIKE ?";

    private static final long serialVersionUID = 1L;
    private final Integer centerId; // center where new or all specimens in list will be located
    private final Boolean justNew; // centerId is just for new specimens in list, as opposed to all
                                   // specimens in list
    private final List<SpecimenMicroplateInfo> specimenMicroplateInfos; // (potentially microplate)
                                                                        // specimens to be created,
                                                                        // changed, moved or stored

    public SpecimenMicroplateConsistentAction(Integer centerId, Boolean justNew,
        List<SpecimenMicroplateInfo> specimenMicroplateInfos) {
        this.centerId = centerId;
        this.justNew = justNew;
        this.specimenMicroplateInfos = specimenMicroplateInfos;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return true;
    }

    @Override
    public BooleanResult run(ActionContext context) throws ActionException {
        List<String> inventoryIds = new ArrayList<String>(0);
        Set<String> microplateIds = new HashSet<String>(0);
        // get distinct microplates from list of specimens
        for (SpecimenMicroplateInfo smInfo : specimenMicroplateInfos) {
            inventoryIds.add(smInfo.inventoryId);
            if (InventoryIdUtil.isFormatMicroplatePosition(smInfo.inventoryId)) {
                microplateIds.add(InventoryIdUtil.microplatePart(smInfo.inventoryId));
            }
        }

        // perform series of tests, only if at least one specimen is microplate specimen
        if (microplateIds.size() > 0) {
            if (centerId == null) {
                throw new ActionException("Center for microplate specimens is null"); //$NON-NLS-1$
            }

            String microplateId = null; // if just 1 microplate
            Integer containerId = null;
            Container container = null;
            Set<Integer> containerIds = new HashSet<Integer>(0);
            // get distinct containers from list of specimens
            for (SpecimenMicroplateInfo smInfo : specimenMicroplateInfos) {
                containerIds.add(smInfo.containerId);
            }
            if (containerIds.size() == 1 && containerIds.iterator().next() == null) {
                // containers for all specimens are null
            }
            else if (containerIds.size() > 1) {
                throw new ActionException("Containers for microplate specimens are not all the same"); //$NON-NLS-1$
            }
            else if (containerIds.size() > 0) {
                containerId = containerIds.iterator().next();
                container = context.load(Container.class, containerId);
                if (!container.getSite().getId().equals(centerId)) {
                    throw new ActionException("Center for microplate specimens is different from container site"); //$NON-NLS-1$
                }
                if (!container.getContainerType().getIsMicroplate()) {
                    throw new ActionException("Container is not a microplate"); //$NON-NLS-1$
                }
                if (microplateIds.size() > 1) {
                    throw new ActionException("Specimen microplates are not all the same"); //$NON-NLS-1$
                }

                microplateId = microplateIds.iterator().next();
                String productBarcode = container.getProductBarcode();
                if (productBarcode == null || !productBarcode.equals(microplateId)) {
                    throw new ActionException("Container is different from specimen microplate " //$NON-NLS-1$
                        + microplateId);
                }

                for (SpecimenMicroplateInfo smInfo : specimenMicroplateInfos) {
                    ContainerType type = container.getContainerType();
                    String positionString = ContainerLabelingScheme.getPositionString(
                        smInfo.position, type.getChildLabelingScheme().getId(), type.getCapacity().getRowCapacity(),
                        type.getCapacity().getColCapacity(), type.getLabelingLayout());
                    if (!InventoryIdUtil.positionPart(smInfo.inventoryId).equals(positionString)) {
                        throw new ActionException("Specimen position for " //$NON-NLS-1$
                            + smInfo.inventoryId + " is different from container position"); //$NON-NLS-1$
                    }
                }
            }

            for (String mpId : microplateIds) {
                Query query = context.getSession().createQuery(SPECIMENS_CENTER_HQL);
                query.setParameter(0, centerId);
                query.setParameter(1, InventoryIdUtil.patternFromMicroplateId(mpId));
                if (justNew) {
                    // new specimens in list will be at centerId, but others for same microplate are
                    // elsewhere
                    if (query.list().size() > 0) {
                        throw new ActionException("New specimens for microplate " //$NON-NLS-1$
                            + mpId + " have location consistency problem"); //$NON-NLS-1$
                    }
                }
                else {
                    // all specimens in list will be at centerId, but others for same microplate are
                    // elsewhere
                    if (!inventoryIds.containsAll(query.list())) {
                        throw new ActionException("Specimens for microplate " //$NON-NLS-1$
                            + mpId + " have location consistency problem"); //$NON-NLS-1$
                    }
                }
            }

            if (containerId == null) {
                for (String mpId : microplateIds) {
                    Query query = context.getSession().createQuery(SPECIMENS_CONTAINER_NULL_HQL);
                    query.setParameter(0, InventoryIdUtil.patternFromMicroplateId(mpId));
                    // all specimens in list will not be in any container, but other(s) for same
                    // microplate are in some container(s)
                    if (!inventoryIds.containsAll(query.list())) {
                        throw new ActionException("Specimens for microplate " //$NON-NLS-1$
                            + mpId + " have storage consistency problem"); //$NON-NLS-1$
                    }
                }
            }

            if (containerId != null) {
                Query query = context.getSession().createQuery(SPECIMENS_CONTAINER_HQL);
                query.setParameter(0, containerId);
                query.setParameter(1, InventoryIdUtil.patternFromMicroplateId(microplateId));
                // all specimens in list will be in containerId, but others for same microplate are
                // stored elsewhere or not stored in any container
                if (!inventoryIds.containsAll(query.list())) {
                    throw new ActionException("Specimens for microplate " //$NON-NLS-1$
                        + microplateId + " have container consistency problem"); //$NON-NLS-1$
                }
            }
        }
        return new BooleanResult(true);
    }
}
