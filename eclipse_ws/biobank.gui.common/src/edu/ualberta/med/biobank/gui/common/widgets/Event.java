package edu.ualberta.med.biobank.gui.common.widgets;

import org.eclipse.swt.widgets.Widget;

/**
 * Instances of this class provide a description of a particular event which occurred within a widet
 * used by this plugin.
 */
public class Event {

    /**
     * the widget that issued the event
     */
    public Widget widget;

    /**
     * the type of event, as defined by the event type constants in class <code>SWT</code>
     * 
     * @see org.eclipse.swt.SWT
     */
    public int type;

    /**
     * the event specific detail field, as defined by the detail constants in class <code>SWT</code>
     * 
     * @see org.eclipse.swt.SWT
     */
    public int detail;

    /**
     * a field for application use
     */
    public Object data;

}
