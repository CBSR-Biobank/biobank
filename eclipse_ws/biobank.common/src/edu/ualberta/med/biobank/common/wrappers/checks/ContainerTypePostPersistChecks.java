package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.peer.ContainerPositionPeer;
import edu.ualberta.med.biobank.common.peer.ContainerTypePeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenPositionPeer;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.util.HibernateUtil;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadModelAction;
import edu.ualberta.med.biobank.common.wrappers.util.ProxyUtil;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class ContainerTypePostPersistChecks extends LoadModelAction<ContainerType> {
    private static final long serialVersionUID = 1L;

    private static final String CANNOT_REMOVE_CHILD_CONTAINER_TYPE_MSG = Messages.getString("ContainerTypePostPersistChecks.cannot.remove.child.container.type.msg"); //$NON-NLS-1$
    private static final String CANNOT_REMOVE_SPECIMEN_TYPE_MSG = Messages.getString("ContainerTypePostPersistChecks.cannot.remove.specimen.type.msg"); //$NON-NLS-1$
    private static final String INSUFFICIENT_LABELING_SCHEME_MSG = Messages.getString("ContainerTypePostPersistChecks.insufficient.labeling.scheme.msg"); //$NON-NLS-1$

    // @formatter:off
    private static final String COUNT_COUNTAINERS_WITH_CONTAINER_TYPE =
        "SELECT COUNT(cp)" + //$NON-NLS-1$
        " FROM " + ContainerPosition.class.getName() + " AS cp" + //$NON-NLS-1$ //$NON-NLS-2$
        " INNER JOIN cp." + ContainerPositionPeer.PARENT_CONTAINER.getName() + " AS cparent" + //$NON-NLS-1$ //$NON-NLS-2$
        " WHERE cparent." + ContainerPeer.CONTAINER_TYPE.to(ContainerTypePeer.ID).getName() + " = ?" + //$NON-NLS-1$ //$NON-NLS-2$
        "   AND cp." + ContainerPositionPeer.CONTAINER.to(ContainerPeer.CONTAINER_TYPE.to(ContainerTypePeer.ID)).getName() + //$NON-NLS-1$
        "       IN ({0})"; //$NON-NLS-1$
    private static final String COUNT_SPECIMENS_WITH_CONTAINER_TYPE =
        "SELECT COUNT(ap) " + //$NON-NLS-1$
        " FROM " + SpecimenPosition.class.getName() + " AS ap" + //$NON-NLS-1$ //$NON-NLS-2$
        " INNER JOIN ap." + SpecimenPositionPeer.CONTAINER.getName() + " AS aparent" + //$NON-NLS-1$ //$NON-NLS-2$
        " WHERE aparent." + ContainerPeer.CONTAINER_TYPE.to(ContainerTypePeer.ID).getName() + " = ?" + //$NON-NLS-1$ //$NON-NLS-2$
        "   AND ap." + SpecimenPositionPeer.SPECIMEN.to(SpecimenPeer.SPECIMEN_TYPE.to(SpecimenTypePeer.ID)).getName() + //$NON-NLS-1$
        "     IN ({0})"; //$NON-NLS-1$
    // @formatter:on

    private final Collection<ContainerType> removedChildContainerTypes;
    private final Collection<SpecimenType> removedSpecimenTypes;

    public ContainerTypePostPersistChecks(ModelWrapper<ContainerType> wrapper) {
        super(wrapper);

        removedChildContainerTypes = getRemovedChildContainerTypes(wrapper);
        removedSpecimenTypes = getRemovedSpecimenTypes(wrapper);
    }

    @Override
    public void doLoadModelAction(Session session, ContainerType freshObject)
        throws BiobankSessionException {
        checkLabelingScheme(freshObject);
        checkRemovedChildContainerTypes(session);
        checkRemovedSpecimenTypes(session);
    }

    private Collection<SpecimenType> getRemovedSpecimenTypes(
        ModelWrapper<ContainerType> wrapper) {

        Collection<ModelWrapper<SpecimenType>> removedSpecimenTypes = wrapper
            .getElementTracker().getRemovedElements(
                ContainerTypePeer.SPECIMEN_TYPE_COLLECTION);

        Collection<SpecimenType> unwrapped = new ArrayList<SpecimenType>();
        for (ModelWrapper<SpecimenType> specimenType : removedSpecimenTypes) {
            SpecimenType raw = specimenType.getWrappedObject();
            SpecimenType unproxied = ProxyUtil.convertProxyToObject(raw);

            unwrapped.add(unproxied);
        }

        return unwrapped;
    }

    private Collection<ContainerType> getRemovedChildContainerTypes(
        ModelWrapper<ContainerType> wrapper) {

        Collection<ModelWrapper<ContainerType>> removedContainerTypes = wrapper
            .getElementTracker().getRemovedElements(
                ContainerTypePeer.CHILD_CONTAINER_TYPE_COLLECTION);

        Collection<ContainerType> unwrapped = new ArrayList<ContainerType>();
        for (ModelWrapper<ContainerType> containerType : removedContainerTypes) {
            ContainerType raw = containerType.getWrappedObject();
            ContainerType unproxied = ProxyUtil.convertProxyToObject(raw);

            unwrapped.add(unproxied);
        }

        return unwrapped;
    }

    private void checkLabelingScheme(ContainerType type)
        throws BiobankSessionException {
        ContainerLabelingScheme scheme = type.getChildLabelingScheme();
        Capacity capacity = type.getCapacity();

        boolean canLabel = ContainerLabelingSchemeWrapper.canLabel(scheme,
            capacity);

        if (!canLabel) {
            String msg = MessageFormat.format(INSUFFICIENT_LABELING_SCHEME_MSG,
                capacity.getRowCapacity(), capacity.getColCapacity());

            throw new BiobankSessionException(msg);
        }
    }

    private void checkRemovedChildContainerTypes(Session session)
        throws BiobankSessionException {
        if (removedChildContainerTypes.isEmpty())
            return;

        Collection<Integer> ids = new ArrayList<Integer>();
        for (ContainerType type : removedChildContainerTypes) {
            Integer id = type.getId();
            if (id != null) {
                ids.add(id);
            }
        }

        String idsList = StringUtil.join(ids, ", "); //$NON-NLS-1$
        String hql = MessageFormat.format(
            COUNT_COUNTAINERS_WITH_CONTAINER_TYPE, idsList);

        Query query = session.createQuery(hql);
        query.setParameter(0, getModel().getId());

        Long count = HibernateUtil.getCountFromQuery(query);

        if (count != 0) {
            throw new BiobankSessionException(
                CANNOT_REMOVE_CHILD_CONTAINER_TYPE_MSG);
        }
    }

    private void checkRemovedSpecimenTypes(Session session)
        throws BiobankSessionException {
        if (removedSpecimenTypes.isEmpty())
            return;

        Collection<Integer> ids = new ArrayList<Integer>();
        for (SpecimenType type : removedSpecimenTypes) {
            Integer id = type.getId();
            if (id != null) {
                ids.add(id);
            }
        }

        String idsList = StringUtil.join(ids, ", "); //$NON-NLS-1$
        String hql = MessageFormat.format(COUNT_SPECIMENS_WITH_CONTAINER_TYPE,
            idsList);

        Query query = session.createQuery(hql);
        query.setParameter(0, getModel().getId());

        Long count = HibernateUtil.getCountFromQuery(query);

        if (count != 0) {
            throw new BiobankSessionException(CANNOT_REMOVE_SPECIMEN_TYPE_MSG);
        }
    }
}
