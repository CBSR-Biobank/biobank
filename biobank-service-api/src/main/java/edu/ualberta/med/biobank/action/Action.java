package edu.ualberta.med.biobank.action;

import java.io.Serializable;

/**
 * Why {@link Action}s? Flexibility!
 * <ol>
 * <li>Batching or queueing work is easier.</li>
 * <li>Transactions can span multiple or single units of work.</li>
 * <li>Method and class-based service structure can still be achieved by
 * wrapping commands.</li>
 * <li>Testing and configuration are easier as handlers can be plugged in.</li>
 * </ol>
 * 
 * Why a service layer?
 * <ol>
 * <li>Testing is easier and more direct.</li>
 * <li>Multiple interfaces to the service can be created: web, cli, etc.</li>
 * <li>Presentation layers only need to worry about presentation.</li>
 * <li>Services should be use-cases.</li>
 * <li>Transaction demarcation and authorisation can exist in the service layer.
 * </li>
 * </ol>
 * 
 * @author Jonathan Ferland
 * 
 * @param <R> type of result ({@link ActionResult}) returned by the
 *            {@link Action}.
 */
public interface Action<R extends ActionResult>
    extends Serializable {
}