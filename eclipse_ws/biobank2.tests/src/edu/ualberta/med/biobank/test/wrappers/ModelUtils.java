package edu.ualberta.med.biobank.test.wrappers;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.List;

import org.springframework.util.Assert;

@Deprecated
public class ModelUtils {

    public static <E> E getObjectWithId(WritableApplicationService appService,
        Class<E> classType, Integer id) throws Exception {
        Constructor<?> constructor = classType.getConstructor();
        Object instance = constructor.newInstance();
        Method setIdMethod = classType.getMethod("setId", Integer.class);
        setIdMethod.invoke(instance, id);

        List<E> list = appService.search(classType, instance);
        if (list.size() == 0)
            return null;
        Assert.isTrue(list.size() == 1);
        return list.get(0);
    }

}
