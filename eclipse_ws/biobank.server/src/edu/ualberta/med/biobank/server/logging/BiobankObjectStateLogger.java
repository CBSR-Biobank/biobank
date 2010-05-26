package edu.ualberta.med.biobank.server.logging;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.server.logging.user.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.logging.user.UserInfo;

/**
 * This class logs the object state information.
 * 
 * Copy from CLM
 */

public class BiobankObjectStateLogger {
    private static Logger logger = null;
    private static BiobankObjectStateLogger myInstance = null;

    private BiobankObjectStateLogger() {
        logger = Logger.getLogger("Biobank.Activity");
    }

    /**
     * This method logs the message for update operation
     * 
     * @param id -- Serializable id of the object
     * @param currentState -- current states of the object after the operation
     * @param previousState -- previous states of the object before the
     *            operation
     * @param propertyNames --names of the object states
     * @param types -- Hibernate types of the object states
     * @param action -- the name of the operation being performed
     * 
     */
    public void logMessage(Object obj, Serializable id, Object[] currentState,
        Object[] prevState, String[] propertyNames, Type[] types, String action) {

        String message = MessageGenerator.generateStringMessage(action, null,
            null, null, null);
        UserInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new UserInfo();
        if (userInfo.getIsIntransaction() == true) {
            logToBuffer(message);
        } else {
            log(message);
        }

    }

    /**
     * This method saves the message to the buffer for later use
     * 
     * @param msg -- message to be logged
     */
    public void logToBuffer(String msg) {
        UserInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new UserInfo();
        ArrayList<String> logs = userInfo.getTransactionLogs();
        if (logs == null) {
            logs = new ArrayList<String>();
        }
        logs.add(msg);
        userInfo.setTransactionLogs(logs);
        BiobankThreadVariable.set(userInfo);

    }

    /**
     * This method logs the message
     * 
     * @param message -- message to be logged
     */
    public void log(String message) {
        Level level = Level.toLevel("INFO");
        logger.log(level, message);
    }

    /**
     * @return -- Returns the singleton of this class
     */
    public static BiobankObjectStateLogger getInstance() {
        if (myInstance == null) {
            myInstance = new BiobankObjectStateLogger();
        }
        return myInstance;
    }

}
