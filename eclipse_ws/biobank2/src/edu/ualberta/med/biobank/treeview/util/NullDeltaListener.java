package edu.ualberta.med.biobank.treeview.util;

/**
 * default listener that does nothing.
 */
public class NullDeltaListener implements IDeltaListener {
    protected static NullDeltaListener soleInstance = new NullDeltaListener();

    public static NullDeltaListener getSoleInstance() {
        return soleInstance;
    }

    /*
     * @see IDeltaListener#add(DeltaEvent)
     */
    @Override
    public void add(DeltaEvent event) {
    }

    /*
     * @see IDeltaListener#remove(DeltaEvent)
     */
    @Override
    public void remove(DeltaEvent event) {
    }

}
