package edu.ualberta.med.biobank.server.logging;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.server.logging.logger.BiobankObjectStateLogger;
import edu.ualberta.med.biobank.server.logging.user.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.logging.user.UserInfo;

/**
 * 
 * This class intercepts all the related events when the client application
 * performs the persistence such as save, update and delete. Also it generate
 * the audit information about the states of the entity object in different
 * stages.
 * 
 * See use in application-config.xml
 * 
 * Copy from the CLM API
 * 
 */
public class BiobankObjectStateInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    private void log(Object entity, Serializable id, Object[] state,
        Object[] previousState, String[] propertyNames, Type[] types,
        String action) {
        String name = entity.getClass().getSimpleName();
        BiobankObjectStateLogger logger = BiobankObjectStateLogger
            .getlogger(name);
        if (logger != null) {
            try {
                Map<String, Object> statesMaps = new HashMap<String, Object>();
                for (int i = 0; i < state.length; i++) {
                    statesMaps.put(propertyNames[i], state[i]);
                }
                logger.logMessage(entity, action, statesMaps);
            } catch (Exception ex) {
                ex.printStackTrace();
                ExceptionUtils.writeMsgToTmpFile(name
                    + "_biobankstateinterceptor", ex);
            }
        }
    }

    /**
     * This method gets called before an object is saved.
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
        String[] propertyNames, Type[] types) throws CallbackException {
        log(entity, id, state, null, propertyNames, types, "insert");
        return false;
    }

    /**
     * 
     * This method gets Called before an object is saved
     */
    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
        String[] propertyNames, Type[] types) throws CallbackException {
        log(entity, id, state, null, propertyNames, types, "delete");
    }

    /**
     * 
     * This method gets Called before an object is updated
     */
    @Override
    public boolean onFlushDirty(Object entity, Serializable id,
        Object[] currentState, Object[] previousState, String[] propertyNames,
        Type[] types) throws CallbackException {
        log(entity, id, currentState, previousState, propertyNames, types,
            "update");
        return false;
    }

    /**
     * Really write logs registered in the buffer.
     */
    @Override
    public void afterTransactionCompletion(Transaction arg0) {
        UserInfo user = BiobankThreadVariable.get();
        if (arg0.wasCommitted() && user.getTransactionLogs() != null) {
            Iterator<String> it = user.getTransactionLogs().iterator();
            while (it.hasNext()) {
                String str = it.next();
                BiobankObjectStateLogger.log(str);
                it.remove();
            }
        } else {
            // clear the logs Buffer
            clearTransactionLogs();
        }
        user.setIsIntransaction(false);
        // set back the local thread variable
        BiobankThreadVariable.set(user);
    }

    @Override
    public void afterTransactionBegin(Transaction tx) {
        UserInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new UserInfo();
        userInfo.setIsIntransaction(true);
        BiobankThreadVariable.set(userInfo);
    }

    private void clearTransactionLogs() {
        UserInfo user = BiobankThreadVariable.get();
        if (user.getTransactionLogs() != null) {
            user.getTransactionLogs().clear();
            BiobankThreadVariable.set(user);
        }
    }

}
