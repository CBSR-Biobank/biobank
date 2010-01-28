package edu.ualberta.med.biobank.server;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.security.SecurityConstants;
import gov.nih.nci.system.util.WritableApiApplicationServiceMethodHelper;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.aopalliance.intercept.MethodInvocation;

/**
 * Security class called when methods of the ApplicationService instance are
 * called. See extension.sdk.security.writable.applicationservice.method.impl
 * entry on the build.properties file of the sdk generator.
 */
public class BiobankApiApplicationServiceMethodHelper extends
    WritableApiApplicationServiceMethodHelper {

    @Override
    public Map<String, Collection<String>> getDomainObjectName(
        MethodInvocation invocation) throws ApplicationException {
        Method method = invocation.getMethod();
        Object[] arguments = invocation.getArguments();
        if ("newSite".equals(method.getName())
            && (arguments[0] instanceof Integer)
            && (arguments[1] instanceof String)) {
            Map<String, Collection<String>> securityMap = new HashMap<String, Collection<String>>();
            securityMap.put(BiobankApplicationServiceImpl.SITE_CLASS_NAME,
                Arrays.asList(SecurityConstants.CREATE));
            return securityMap;
        } else {
            return super.getDomainObjectName(invocation);
        }

    }
}
