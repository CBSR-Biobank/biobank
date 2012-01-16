package edu.ualberta.med.biobank.common.action.center;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.check.UniquePreCheck;
import edu.ualberta.med.biobank.common.action.check.ValueProperty;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.NullPropertyException;
import edu.ualberta.med.biobank.common.peer.CenterPeer;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.User;

public abstract class CenterSaveAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    protected Integer centerId = null;

    // Specific properties force the programmer only to modify the intended
    // data. A little faster. But disregards version checks. Version checks
    // might apply, but they might not, up to the individual action (e.g.
    // "incrementCountAction" shouldn't care).

    private String name;
    private String nameShort;
    private Address address;
    private Integer aStatusId;

    public void setId(Integer id) {
        this.centerId = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setActivityStatusId(Integer activityStatusId) {
        this.aStatusId = activityStatusId;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    protected IdResult run(User user, Session session,
        ActionContext context, Center center) throws ActionException {
        if (name == null) {
            throw new NullPropertyException(Center.class, CenterPeer.NAME);
        }
        if (nameShort == null) {
            throw new NullPropertyException(Center.class, CenterPeer.NAME_SHORT);
        }
        if (address == null) {
            throw new NullPropertyException(Center.class, "address");
        }
        if (aStatusId == null) {
            throw new NullPropertyException(Center.class,
                CenterPeer.ACTIVITY_STATUS);
        }

        // check for duplicate name
        List<ValueProperty<Center>> uniqueValProps =
            new ArrayList<ValueProperty<Center>>();
        uniqueValProps.add(new ValueProperty<Center>(CenterPeer.NAME, name));
        new UniquePreCheck<Center>(Center.class, centerId, uniqueValProps).run(
            user, session);

        // check for duplicate name short
        uniqueValProps = new ArrayList<ValueProperty<Center>>();
        uniqueValProps.add(new ValueProperty<Center>(CenterPeer.NAME_SHORT,
            nameShort));
        new UniquePreCheck<Center>(Center.class, centerId, uniqueValProps).run(
            user, session);

        // TODO: check permission? (can edit site?)

        // TODO: error checks
        // TODO: version check?

        // TODO: LocalizedMessage in Exception?

        center.setName(name);
        center.setNameShort(nameShort);

        ActivityStatus aStatus = context.load(ActivityStatus.class, aStatusId);
        center.setActivityStatus(aStatus);

        // TODO: remember to check the address
        center.setAddress(address);

        session.saveOrUpdate(center);
        session.flush();

        // TODO: SHOULD NOT require a flush so that we can get the inserted id
        // if this was an insert, try using a callback that sets the response
        // value instead?

        return new IdResult(center.getId());
    }
}
