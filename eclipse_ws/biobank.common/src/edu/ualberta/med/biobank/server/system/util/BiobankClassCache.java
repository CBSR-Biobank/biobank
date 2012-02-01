package edu.ualberta.med.biobank.server.system.util;

import java.lang.reflect.Field;
import java.util.Collection;

import gov.nih.nci.system.dao.QueryException;
import gov.nih.nci.system.util.ClassCache;

public class BiobankClassCache extends ClassCache {
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
                    if (Collection.class.isAssignableFrom(type))
                    {
                        return true;
                    }
                    else
                    { 
                        return false;
                    }
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
