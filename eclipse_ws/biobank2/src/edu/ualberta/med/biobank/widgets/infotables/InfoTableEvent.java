package edu.ualberta.med.biobank.widgets.infotables;

import java.util.EventObject;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;

public class InfoTableEvent extends EventObject {

    /**
     * Generated serial version UID for this class.
     */
    private static final long serialVersionUID = -8960426037179772430L;

    protected transient ISelection selection;

    public InfoTableEvent(InfoTableWidget<?> source, ISelection selection) {
        super(source);
        Assert.isNotNull(selection);
        this.selection = selection;
    }

    /**
     * Returns the selection.
     * 
     * @return the selection
     */
    public ISelection getSelection() {
        return selection;
    }

    /**
     * Returns the viewer that is the source of this event.
     * 
     * @return the originating viewer
     */
    public InfoTableWidget<?> getInfoTable() {
        return (InfoTableWidget<?>) getSource();
    }

}