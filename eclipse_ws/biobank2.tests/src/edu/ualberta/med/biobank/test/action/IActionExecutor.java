package edu.ualberta.med.biobank.test.action;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.User;

public interface IActionExecutor {

    public void setUser(User user);

    public User getUser();

    public Session getSession();

    public <T extends ActionResult> T exec(Action<T> action)
        throws ActionException;

}
