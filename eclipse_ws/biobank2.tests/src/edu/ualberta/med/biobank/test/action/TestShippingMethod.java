package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodDeleteAction;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodGetInfoAction;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodGetInfoAction.ShippingMethodInfo;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodSaveAction;
import edu.ualberta.med.biobank.common.action.shipment.ShippingMethodsGetAllAction;
import edu.ualberta.med.biobank.model.ShippingMethod;

public class TestShippingMethod extends TestAction {

    @SuppressWarnings("unchecked")
    @Test
    public void save() throws Exception {
        session.beginTransaction();
        List<ShippingMethod> shippingMethods = session.createCriteria(ShippingMethod.class).list();
        int preAddCount = shippingMethods.size();
        session.getTransaction().commit();

        String name = getMethodNameR();

        exec(new ShippingMethodSaveAction(null, name));

        session.clear();
        shippingMethods = session.createCriteria(ShippingMethod.class).list();
        Assert.assertEquals(preAddCount + 1, shippingMethods.size());

        Set<String> names = new HashSet<String>();
        for (ShippingMethod shippingMethod : shippingMethods) {
            names. add(shippingMethod.getName());
        }

        Assert.assertTrue(names.contains(name));
    }

    @Test
    public void get() throws Exception {
        session.beginTransaction();
        ShippingMethod shippingMethod = factory.createShippingMethod();
        session.getTransaction().commit();

        ShippingMethodInfo info = exec(new ShippingMethodGetInfoAction(shippingMethod.getName()));

        Assert.assertEquals(shippingMethod.getId(), info.getShippingMethod().getId());
        Assert.assertEquals(shippingMethod.getName(), info.getShippingMethod().getName());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void getAll() throws Exception {
        session.beginTransaction();
        ShippingMethod shippingMethod = factory.createShippingMethod();
        session.getTransaction().commit();

        List<ShippingMethod> shippingMethods = session.createCriteria(ShippingMethod.class).list();
        List<ShippingMethod> actionShippingMethods = exec(new ShippingMethodsGetAllAction()).getList();
        Assert.assertEquals(shippingMethods, actionShippingMethods);
        Assert.assertTrue(actionShippingMethods.contains(shippingMethod));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void delete() throws Exception {
        session.beginTransaction();
        ShippingMethod shippingMethod = factory.createShippingMethod();
        session.getTransaction().commit();

        exec(new ShippingMethodDeleteAction(shippingMethod.getId()));

        session.clear();
        List<ShippingMethod> shippingMethods = session.createCriteria(ShippingMethod.class).list();

        Assert.assertFalse(shippingMethods.contains(shippingMethod));
    }

}
