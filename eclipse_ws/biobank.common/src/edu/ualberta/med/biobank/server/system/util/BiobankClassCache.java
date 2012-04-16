package edu.ualberta.med.biobank.server.system.util;

import gov.nih.nci.system.dao.QueryException;
import gov.nih.nci.system.util.ClassCache;

import java.lang.reflect.Field;
import java.util.Collection;

public class BiobankClassCache extends ClassCache {
    @SuppressWarnings("nls")
    @Override
    public boolean isCollection(String className, String attribName)
        throws QueryException
    {
        Field[] classFields;
        try
        {
            classFields = getFields(getClassFromCache(className));
            for (int i = 0; i < classFields.length; i++)
            {
                if (classFields[i].getName().equals(attribName))
                {
                    Class<?> type = classFields[i].getType();
                    if (Collection.class.isAssignableFrom(type)) {
                        return true;
                    }
                    return false;
                }
            }
            return false;
        } catch (ClassNotFoundException e)
        {
            throw new QueryException("Could not determine type of attribute "
                + attribName + " in class " + className, e);
        }
    }
}
