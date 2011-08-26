package edu.ualberta.med.biobank.common.wrappers.util;

import java.util.List;

import org.springframework.aop.framework.Advised;

public class ProxyUtil {
    public static <E> E convertProxyToObject(E obj) {
        if (obj == null) {
            return null;
        }
        while (obj != null && obj instanceof Advised) {
            Advised proxy = (Advised) obj;
            try {
                @SuppressWarnings("unchecked")
                E target = (E) proxy.getTargetSource().getTarget();
                obj = target;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return obj;
    }

    public static <E> void unproxy(List<E> list) {
        for (int i = 0, n = list.size(); i < n; i++) {
            E orig = list.get(i);
            E unproxied = convertProxyToObject(orig);
            list.set(i, unproxied);
        }
    }
}
