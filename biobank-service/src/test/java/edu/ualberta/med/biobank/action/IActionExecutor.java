package edu.ualberta.med.biobank.action;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.exception.ActionException;

public interface IActionExecutor {
    public void setUserId(Integer userId);

    public Integer getUserId();

    public <T extends ActionResult> T exec(Action<T> action)
        throws ActionException;
}
