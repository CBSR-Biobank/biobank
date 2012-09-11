package edu.ualberta.med.biobank.action;

import java.io.Serializable;

/**
 * Why {@link Action}s? So that work can be queued up and executed in a batch in
 * a single or multiple transactions. This class-per-action style can always be
 * wrapped by a method-per-action style.
 * 
 * @author Jonathan Ferland
 * 
 * @param <R> type of result ({@link ActionResult}) returned by the
 *            {@link Action}.
 */
public interface Action<R extends ActionResult>
    extends Serializable {
}