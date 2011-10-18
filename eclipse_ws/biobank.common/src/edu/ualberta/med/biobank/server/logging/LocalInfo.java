package edu.ualberta.med.biobank.server.logging;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * local information keep on the local thread.
 */
public class LocalInfo implements Serializable {
    private static final long serialVersionUID = 7526471155622776147L;

    private String userName;

    private boolean isIntransaction;
    private ArrayList<String> transactionLogs;
    private String objectIDKey;

    public String getUsername() {
        return userName;
    }

    public void setUsername(String userName) {
        this.userName = userName;
    }

    public boolean getIsIntransaction() {
        return isIntransaction;
    }

    public void setIsIntransaction(boolean isIntransaction) {

        this.isIntransaction = isIntransaction;
    }

    public ArrayList<String> getTransactionLogs() {
        return transactionLogs;
    }

    public void setTransactionLogs(ArrayList<String> transactionLogs) {
        this.transactionLogs = transactionLogs;
    }

    /**
     * @return Returns the objectID.
     */
    public String getObjectIDKey() {
        return objectIDKey;
    }

    /**
     * @param objectID The objectID to set.
     */
    public void setObjectIDKey(String objectIDKey) {
        this.objectIDKey = objectIDKey;
    }

}
