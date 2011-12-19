package edu.ualberta.med.biobank.test.action.helper;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.junit.Assert;

import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenTypeHelper extends Helper {

    public static List<SpecimenType> getSpecimenTypes(Session session) {
        Query q = session.createQuery("from " + SpecimenType.class.getName());
        @SuppressWarnings("unchecked")
        List<SpecimenType> spcTypes = q.list();
        Assert.assertTrue("specimen types not found in database",
            !spcTypes.isEmpty());
        return spcTypes;
    }

}
