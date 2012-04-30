package edu.ualberta.med.biobank.test.wrappers;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.util.Assert;

@Deprecated
public class ModelUtils {

    public static <E> E getObjectWithId(WritableApplicationService appService,
        Class<E> classType, Integer id) throws Exception {

        DetachedCriteria criteria = DetachedCriteria.forClass(classType)
            .add(Restrictions.idEq(id));

        List<E> list = appService.query(criteria);
        if (list.size() == 0)
            return null;
        Assert.isTrue(list.size() == 1);
        return list.get(0);
    }

}
