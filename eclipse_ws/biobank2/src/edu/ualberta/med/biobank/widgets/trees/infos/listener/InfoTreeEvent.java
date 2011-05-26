package edu.ualberta.med.biobank.widgets.trees.infos.listener;

import java.util.EventObject;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ISelection;

import edu.ualberta.med.biobank.widgets.infotables.InfoTableWidget;
import edu.ualberta.med.biobank.widgets.trees.infos.InfoTreeWidget;

public class InfoTreeEvent extends EventObject {

    /**
     * Generated serial version UID for this class.
     */
    private static final long serialVersionUID = -8960426037179772430L;

    protected transient ISelection selection;

    public InfoTreeEvent(InfoTreeWidget<?> source, ISelection selection) {
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