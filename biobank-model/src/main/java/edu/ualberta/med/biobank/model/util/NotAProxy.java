package edu.ualberta.med.biobank.model.util;

/**
 * Implement this interface for any <code>Object</code> you do <em>not</em> want
 * to be converted into a proxy or converted out of a proxy.
 * 
 * <code>Object</code>-s passed as either arguments to or received as return
 * values from any method on an <code>Object</code> that inherits from
 * <code>ApplicationService</code> are "in danger" of being wrapped or
 * unwrapped, respecitvely, with a proxy.
 * 
 * @author jferland
 * 
 */
public interface NotAProxy {
}
