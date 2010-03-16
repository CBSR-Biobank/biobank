package edu.ualberta.med.biobank.server.applicationservice.helper;

import gov.nih.nci.system.util.WritableApiApplicationServiceMethodHelper;

/**
 * Security class called when methods of the ApplicationService instance are
 * called. See extension.sdk.security.writable.applicationservice.method.impl
 * entry on the build.properties file of the sdk generator.
 */
public class BiobankApiApplicationServiceMethodHelper extends
    WritableApiApplicationServiceMethodHelper {

    // @Override
    // public Map<String, Collection<String>> getDomainObjectName(
    // MethodInvocation invocation) throws ApplicationException {
    // Method method = invocation.getMethod();
    // Object[] arguments = invocation.getArguments();
    // if ("newSite".equals(method.getName())
    // && (arguments[0] instanceof Integer)
    // && (arguments[1] instanceof String)) {
    // Map<String, Collection<String>> securityMap = new HashMap<String,
    // Collection<String>>();
    // securityMap.put(BiobankApplicationServiceImpl.SITE_CLASS_NAME,
    // Arrays.asList(SecurityConstants.CREATE));
    // return securityMap;
    // } else {
    // return super.getDomainObjectName(invocation);
    // }
    // }
}
