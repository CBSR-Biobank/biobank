package edu.ualberta.med.biobank.treeview.dispatch;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.treeview.dispatch.messages"; //$NON-NLS-1$
    public static String DispatchAdapter_check_error_msg;
    public static String DispatchAdapter_delete_confirm_msg;
    public static String DispatchAdapter_dispatch_label;
    public static String DispatchAdapter_lost_label;
    public static String DispatchAdapter_move_creation_label;
    public static String DispatchAdapter_receive_label;
    public static String DispatchAdapter_receive_process_label;
    public static String DispatchAdapter_save_error_title;
    public static String IncomingNode_incoming_node_label;
    public static String InCreationDispatchGroup_add_label;
    public static String InCreationDispatchGroup_creation_node_label;
    public static String OutgoingNode_add_label;
    public static String OutgoingNode_outgoing_node_label;
    public static String ReceivingInTransitDispatchGroup_transit_node_label;
    public static String ReceivingNoErrorsDispatchGroup_receiving_node_label;
    public static String ReceivingWithErrorsDispatchGroup_error_node_label;
    public static String SentInTransitDispatchGroup_sent_node_label;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
