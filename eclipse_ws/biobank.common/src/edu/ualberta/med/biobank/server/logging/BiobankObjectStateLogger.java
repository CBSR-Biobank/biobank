package edu.ualberta.med.biobank.server.logging;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import edu.ualberta.med.biobank.common.wrappers.loggers.SpecimenLogProvider;
import edu.ualberta.med.biobank.common.wrappers.loggers.WrapperLogProvider;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.SpecimenPosition;

/**
 * This class logs the object state information.
 * 
 * Copy from CLM
 */

public abstract class BiobankObjectStateLogger {
    private static Logger logger = null;
    public static final SimpleDateFormat dateTimeFormatter = new SimpleDateFormat(
        "yyyy-MM-dd HH:mm"); //$NON-NLS-1$

    private static HashMap<Class<?>, WrapperLogProvider<?>> loggersMap = new HashMap<Class<?>, WrapperLogProvider<?>>();

    static {
        logger = Logger.getLogger("Biobank.Activity"); //$NON-NLS-1$
    }

    /**
     * This method logs the message for update operation
     * 
     * @param <T>
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
    public static void logMessage(
        WrapperLogProvider<? extends Object> logProvider, Object obj,
        String action) {
        Log log = logProvider.getObjectLog(obj);
        if (log != null) {
            String message = MessageGenerator.generateStringMessage(action,
                log.getCenter(), log.getPatientNumber(), log.getInventoryId(),
                log.getLocationLabel(), log.getDetails(), logProvider
                    .getClass().getSimpleName().replace("LogProvider", "")); //$NON-NLS-1$//$NON-NLS-2$
            LocalInfo userInfo = BiobankThreadVariable.get();
            if (null == userInfo)
                userInfo = new LocalInfo();
            if (userInfo.getIsIntransaction() == true) {
                logToBuffer(message);
            } else {
                log(message);
            }
        }
    }

    /**
     * This method saves the message to the buffer for later use
     * 
     * @param msg -- message to be logged
     */
    public static void logToBuffer(String msg) {
        LocalInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new LocalInfo();
        ArrayList<String> logs = userInfo.getTransactionLogs();
        if (logs == null) {
            logs = new ArrayList<String>();
        }
        logs.add(msg);
        userInfo.setTransactionLogs(logs);
        BiobankThreadVariable.set(userInfo);
    }

    protected abstract Log getLogObject(Object obj,
        Map<String, Object> statesMap);

    /**
     * This method logs the message
     * 
     * @param message -- message to be logged
     */
    public static void log(String message) {
        Level level = Level.toLevel("INFO"); //$NON-NLS-1$
        logger.log(level, message);
    }

    @SuppressWarnings("rawtypes")
    public static Map<Class<?>, Class<? extends WrapperLogProvider>> specialLogProvidersMap = new HashMap<Class<?>, Class<? extends WrapperLogProvider>>();

    static {
        specialLogProvidersMap.put(SpecimenPosition.class,
            SpecimenLogProvider.class);
    }

    // FIXME should get something nicer. Especially is need special mappings
    // like with SpecimenPosition to Specimen
    @SuppressWarnings("unchecked")
    public static WrapperLogProvider<?> getLogProvider(Class<?> entityClass)
        throws InstantiationException, IllegalAccessException {
        WrapperLogProvider<?> logProvider = loggersMap.get(entityClass);
        if (logProvider == null) {
            Class<? extends WrapperLogProvider<?>> loggerClass = (Class<? extends WrapperLogProvider<?>>) specialLogProvidersMap
                .get(entityClass);
            if (loggerClass == null) {
                try {
                    String className = WrapperLogProvider.class.getPackage()
                        .getName() + "." //$NON-NLS-1$
                        + entityClass.getSimpleName() + "LogProvider"; //$NON-NLS-1$
                    loggerClass = (Class<? extends WrapperLogProvider<?>>) Class
                        .forName(className);
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
            logProvider = loggerClass.newInstance();
            loggersMap.put(entityClass, logProvider);
        }
        return logProvider;
    }
}
