package edu.ualberta.med.biobank.server.interceptor;

import java.io.Serializable;
import java.util.Iterator;

import org.hibernate.CallbackException;
import org.hibernate.EmptyInterceptor;
import org.hibernate.Transaction;
import org.hibernate.type.Type;

import edu.ualberta.med.biobank.common.wrappers.loggers.WrapperLogProvider;
import edu.ualberta.med.biobank.server.logging.BiobankObjectStateLogger;
import edu.ualberta.med.biobank.server.logging.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.logging.ExceptionUtils;
import edu.ualberta.med.biobank.server.logging.LocalInfo;

/**
 * 
 * This class intercepts all the related events when the client application
 * performs the persistence such as save, update and delete. stages.
 * 
 * See use in application-config.xml
 * 
 */
public class SessionInterceptor extends EmptyInterceptor {

    private static final long serialVersionUID = 1L;

    private void log(Object entity, String action) {
        try {
            WrapperLogProvider<?> logProvider = BiobankObjectStateLogger
                .getLogProvider(entity.getClass());
            if (logProvider != null) {
                BiobankObjectStateLogger
                    .logMessage(logProvider, entity, action);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            ExceptionUtils.writeMsgToTmpFile(entity.getClass().getSimpleName()
                + "_sessioninterceptor", //$NON-NLS-1$
                ex);
        }
    }

    /**
     * This method gets called before an object is saved.
     */
    @Override
    public boolean onSave(Object entity, Serializable id, Object[] state,
        String[] propertyNames, org.hibernate.type.Type[] types)
        throws CallbackException {
        log(entity, "insert"); //$NON-NLS-1$
        return false;
    }

    @Override
    public boolean onFlushDirty(Object entity, Serializable id,
        Object[] currentState, Object[] previousState, String[] propertyNames,
        Type[] types) {
        log(entity, "update"); //$NON-NLS-1$
        return false;
    }

    @Override
    public void onDelete(Object entity, Serializable id, Object[] state,
        String[] propertyNames, org.hibernate.type.Type[] types) {
        log(entity, "delete"); //$NON-NLS-1$
    }

    /**
     * Really write logs registered in the buffer.
     */
    @Override
    public void afterTransactionCompletion(Transaction arg0) {
        LocalInfo user = BiobankThreadVariable.get();
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
        LocalInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new LocalInfo();
        userInfo.setIsIntransaction(true);
        BiobankThreadVariable.set(userInfo);
    }

    private void clearTransactionLogs() {
        LocalInfo user = BiobankThreadVariable.get();
        if (user.getTransactionLogs() != null) {
            user.getTransactionLogs().clear();
            BiobankThreadVariable.set(user);
        }
    }

}
