package edu.ualberta.med.biobank.common.action;

import org.hibernate.Session;

public class ActionUtil {

    @SuppressWarnings("unchecked")
    public static <T> T sessionGet(Session session, Class<T> clazz, Integer id) {
        return (T) session.get(clazz, id);
    }
}
