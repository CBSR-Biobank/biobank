package edu.ualberta.med.biobank.server.logging.user;

import org.acegisecurity.context.SecurityContextHolder;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.log4j.Logger;

public class BiobankLogProxy implements MethodInterceptor {

    private static Logger log = Logger.getLogger(BiobankLogProxy.class.getName());

    public Object invoke(MethodInvocation invocation) throws Throwable {

        String userName = null;
        try {
            userName = SecurityContextHolder.getContext().getAuthentication()
                .getName();
        } catch (NullPointerException e) {
            log
                .error("Error:  Unable to retrieve userName from SecurityContext; setting userName to 'dummy'");
            userName = "dummy";
        }

        log.debug("userName has been set to: " + userName);

        UserInfoHelper.setUserName(userName);
        Object value = invocation.proceed();
        UserInfoHelper.setUserName(null);

        return value;
    }
}
