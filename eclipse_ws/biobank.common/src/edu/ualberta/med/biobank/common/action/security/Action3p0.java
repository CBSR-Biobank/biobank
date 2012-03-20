package edu.ualberta.med.biobank.common.action.security;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.security.Action3p0.Result;
import edu.ualberta.med.biobank.common.util.NotAProxy;

public interface Action3p0<R extends Result> {
    public R run();

    public interface Result extends ActionResult, NotAProxy, Serializable {
    }
}
