package edu.ualberta.med.biobank.common.action.security;

import java.io.Serializable;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionInput;
import edu.ualberta.med.biobank.common.action.security.Action2p0.ActionOutput;
import edu.ualberta.med.biobank.common.util.NotAProxy;

/**
 * Intended to replace the {@link edu.ualberta.med.biobank.common.action.Action}
 * interface.
 * 
 * @author Jonathan Ferland
 * 
 * @param <I>
 * @param <O>
 */
public interface Action2p0<I extends ActionInput, O extends ActionOutput> {
    public boolean isAllowed(I input);

    public O run(I input);

    public interface ActionInput extends Serializable, NotAProxy {
    }

    public interface ActionOutput extends ActionResult, Serializable, NotAProxy {
    }
}
