package edu.ualberta.med.biobank.common.action.site;

import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.User;

public class UpdateSiteAction implements Action<Integer> {
    private static final long serialVersionUID = 1L;

    private final Integer siteId;

    private String name;
    private String nameShort;
    private String comment;
    private Address address;
    private ActivityStatus activityStatus;
    private Collection<Integer> studyIds;

    public interface HasValidator<T> {
    }

    public interface Validator<T> {

    }

    public UpdateSiteAction(Integer siteId) {
        this.siteId = siteId;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean isAllowed(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public Integer run(User user, Session session) throws ActionException {
        // TODO Auto-generated method stub
        return null;
    }

}
