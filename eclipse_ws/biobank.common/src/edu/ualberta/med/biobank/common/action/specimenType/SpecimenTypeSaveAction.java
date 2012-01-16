package edu.ualberta.med.biobank.common.action.specimenType;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.peer.SpecimenTypePeer;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.specimenType.SpecimenTypeCreatePermission;
import edu.ualberta.med.biobank.common.permission.specimenType.SpecimenTypeUpdatePermission;
import edu.ualberta.med.biobank.common.util.SetDifference;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.User;

public class SpecimenTypeSaveAction implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    public Integer specimenTypeId;
    public String name;
    public String nameShort;
    public Set<Integer> childSpecimenTypeIds;

    private SpecimenType specimenType;
    private ActionContext context;

    public SpecimenTypeSaveAction(String name, String nameShort) {
        this.name = name;
        this.nameShort = nameShort;
        this.childSpecimenTypeIds = new HashSet<Integer>();
    }

    public void setId(Integer specimenTypeId) {
        this.specimenTypeId = specimenTypeId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setChildSpecimenTypeIds(Set<Integer> childSpecimenTypeIds) {
        this.childSpecimenTypeIds = childSpecimenTypeIds;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        Permission permission;
        if (specimenTypeId == null)
            permission = new SpecimenTypeCreatePermission();
        else
            permission = new SpecimenTypeUpdatePermission(specimenTypeId);
        return permission.isAllowed(user, session);
    }

    @Override
    public IdResult run(User user, Session session) throws ActionException {
        context = new ActionContext(user, session);

        // check for duplicate name
        List<ValueProperty<SpecimenType>> uniqueValProps =
            new ArrayList<ValueProperty<SpecimenType>>();
        uniqueValProps.add(new ValueProperty<SpecimenType>(
            SpecimenTypePeer.NAME, name));
        new UniquePreCheck<SpecimenType>(SpecimenType.class, specimenTypeId,
            uniqueValProps).run(user,
            session);

        // check for duplicate name short
        uniqueValProps = new ArrayList<ValueProperty<SpecimenType>>();
        uniqueValProps.add(new ValueProperty<SpecimenType>(
            SpecimenTypePeer.NAME_SHORT, nameShort));
        new UniquePreCheck<SpecimenType>(SpecimenType.class, specimenTypeId,
            uniqueValProps).run(user,
            session);

        specimenType =
            context.get(SpecimenType.class, specimenTypeId, new SpecimenType());
        specimenType.setName(name);
        specimenType.setNameShort(nameShort);

        saveChildSpecimenTypes();

        session.saveOrUpdate(specimenType);
        session.flush();

        return new IdResult(specimenType.getId());
    }

    private void saveChildSpecimenTypes() {
        Map<Integer, SpecimenType> studies =
            context.load(SpecimenType.class, childSpecimenTypeIds);
        SetDifference<SpecimenType> sitesDiff =
            new SetDifference<SpecimenType>(
                specimenType.getChildSpecimenTypeCollection(), studies.values());
        specimenType.setChildSpecimenTypeCollection(sitesDiff.getNewSet());
        for (SpecimenType childType : sitesDiff.getRemoveSet()) {
            context.getSession().delete(childType);
        }

    }
}
