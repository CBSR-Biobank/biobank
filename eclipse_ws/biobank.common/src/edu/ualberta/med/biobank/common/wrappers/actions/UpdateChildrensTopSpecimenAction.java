package edu.ualberta.med.biobank.common.wrappers.actions;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.peer.SpecimenPeer;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.util.ProxyUtil;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class UpdateChildrensTopSpecimenAction extends WrapperAction<Specimen> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String UPDATE_CHILDREN_HQL =
        "UPDATE "+Specimen.class.getName()+" s " +
        "SET s."+SpecimenPeer.TOP_SPECIMEN.getName()+" = ? " +
        "WHERE s."+SpecimenPeer.PARENT_SPECIMEN.to(SpecimenPeer.ID).getName()+" IN ({0})";
    @SuppressWarnings("nls")
    private static final String SELECT_CHILDREN_HQL = 
        "SELECT s."+Property.concatNames(SpecimenPeer.CHILD_SPECIMENS, SpecimenPeer.ID)+" " +
        "FROM "+Specimen.class.getName()+" s " +
        "WHERE s."+SpecimenPeer.ID.getName()+" IN ({0})";
    // @formatter:on

    private final Specimen topSpecimen;

    public UpdateChildrensTopSpecimenAction(SpecimenWrapper wrapper) {
        super(wrapper);
        this.topSpecimen = ProxyUtil.convertProxyToObject(wrapper
            .getTopSpecimen().getWrappedObject());
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        List<Integer> ids = Arrays.asList(getModel().getId());

        updateChildren(session, ids);

        return null;
    }

    private void updateChildren(Session session, List<Integer> ids)
        throws BiobankSessionException {
        if (ids.isEmpty()) {
            return;
        }

        String paramString = getParamString(ids.size());
        String hql = MessageFormat.format(UPDATE_CHILDREN_HQL, paramString);
        Query query = session.createQuery(hql);
        query.setParameter(0, topSpecimen);

        int position = 1;
        for (Integer id : ids) {
            query.setParameter(position++, id);
        }

        query.executeUpdate();

        List<Integer> childIds = selectChildren(session, ids);

        updateChildren(session, childIds);
    }

    private List<Integer> selectChildren(Session session, List<Integer> ids) {
        String paramString = getParamString(ids.size());
        String hql = MessageFormat.format(SELECT_CHILDREN_HQL, paramString);
        Query query = session.createQuery(hql);

        int position = 0;
        for (Integer id : ids) {
            query.setParameter(position++, id);
        }

        @SuppressWarnings("unchecked")
        List<Integer> childIds = query.list();

        return childIds;
    }

    private static String getParamString(int numParams) {
        StringBuilder buffer = new StringBuilder(numParams * 2);

        for (int i = 0; i < numParams; i++) {
            buffer.append("?"); //$NON-NLS-1$
            if (i < numParams - 1) {
                buffer.append(", "); //$NON-NLS-1$
            }
        }

        return buffer.toString();
    }
}