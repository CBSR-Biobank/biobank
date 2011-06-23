package edu.ualberta.med.biobank.common.wrappers.util;

import org.springframework.aop.framework.Advised;

public class ProxyUtil {
    public static Object convertProxyToObject(Object obj) {
        if (obj == null) {
            return null;
        }
        while (obj != null && obj instanceof Advised) {
            Advised proxy = (Advised) obj;
            try {
                obj = proxy.getTargetSource().getTarget();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }
}
