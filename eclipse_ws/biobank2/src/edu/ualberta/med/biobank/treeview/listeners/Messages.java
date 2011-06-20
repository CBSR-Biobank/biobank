package edu.ualberta.med.biobank.treeview.listeners;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.listeners.messages"; //$NON-NLS-1$
    public static String ContainerDragDropListener_drag_error_title;
    public static String ContainerDragDropListener_drop_error_title;
    public static String ContainerDragDropListener_move_multiple_error_msg;
    public static String ContainerDragDropListener_move_multiple_error_title;
    public static String ContainerDragDropListener_state_error_msg;
    public static String ContainerDragDropListener_state_error_title;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
