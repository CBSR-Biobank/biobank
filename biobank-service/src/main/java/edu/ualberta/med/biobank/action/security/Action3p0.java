package edu.ualberta.med.biobank.action.security;

import java.io.Serializable;

import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.security.Action3p0.Result;
import edu.ualberta.med.biobank.common.util.NotAProxy;

public interface Action3p0<R extends Result> {
    public R run();

    public interface Result extends ActionResult, NotAProxy, Serializable {
    }
}
