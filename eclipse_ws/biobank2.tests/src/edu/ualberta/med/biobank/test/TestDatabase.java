package edu.ualberta.med.biobank.test;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import edu.ualberta.med.biobank.common.VarCharLengths;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.CheckFieldLimitsException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.StringValueLengthServerException;
import edu.ualberta.med.biobank.test.internal.ClinicHelper;
import edu.ualberta.med.biobank.test.internal.DispatchHelper;
import edu.ualberta.med.biobank.test.internal.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.internal.ShippingMethodHelper;
import edu.ualberta.med.biobank.test.internal.SiteHelper;
import edu.ualberta.med.biobank.test.internal.SpecimenTypeHelper;
import edu.ualberta.med.biobank.test.internal.StudyHelper;
import edu.ualberta.med.biobank.test.internal.UserHelper;

public class TestDatabase {
    protected static BiobankApplicationService appService;

    protected Random r;

    private static final List<Class<?>> IGNORE_RETURN_TYPES = new ArrayList<Class<?>>() {
        private static final long serialVersionUID = 1L;
        {
            add(java.lang.Class.class);
            add(java.lang.Object.class);
        }
    };

    private class GetterInfo {
        Method getMethod;
        Method setMethod;
    }

    @Before
    public void setUp() throws Exception {
        r = new Random();
        appService = AllTests.appService;
        if (appService == null) {
            AllTests.setUp();
            appService = AllTests.appService;
            Assert.assertNotNull("setUp: appService is null", appService);
        }
    }

    @After
    public void tearDown() throws Exception {
        Assert.assertNotNull("appService is null", appService);
        try {
            DispatchHelper.deleteCreatedDispatches();
            SiteHelper.deleteCreatedSites();
            StudyHelper.deleteCreatedStudies();
            ShipmentInfoHelper.deleteCreatedShipInfos();
            ClinicHelper.deleteCreatedClinics();
            SpecimenTypeHelper.deleteCreatedSpecimenTypes();
            ShippingMethodHelper.deleteCreateShippingMethods();
            UserHelper.deleteCreatedUsers();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            Assert.fail();
        }
    }

    public Collection<GetterInfo> getGettersAndSetters(ModelWrapper<?> w) {
        HashMap<String, GetterInfo> map = new HashMap<String, GetterInfo>();
        Method[] methods = w.getClass().getMethods();

        for (Method method : methods) {
            if (method.getName().startsWith("get")
                && !method.getName().equals("getClass")
                && !method.getName().equals("getId")
                && !IGNORE_RETURN_TYPES.contains(method.getReturnType())
                && !Collection.class.isAssignableFrom(method.getReturnType())
                && !Map.class.isAssignableFrom(method.getReturnType())
                && !method.getReturnType().isArray()
                && !method.getReturnType().getName()
                    .startsWith("edu.ualberta.med.biobank.common")
                && !method.getReturnType().getName()
                    .startsWith("edu.ualberta.med.biobank.util")
                && (method.getParameterTypes().length == 0)) {
                GetterInfo getterInfo = new GetterInfo();
                getterInfo.getMethod = method;
                map.put(method.getName(), getterInfo);
            }
        }

        for (Method method : methods) {
            if (method.getName().startsWith("set")
                && !method.getName().equals("setClass")) {
                String setterName = method.getName();
                String getterName = "g"
                    + setterName.substring(1, setterName.length());
                GetterInfo getterInfo = map.get(getterName);
                if (getterInfo == null) {
                    // System.out.println("no getter found for "
                    // + w.getClass().getName() + "." + setterName + "()");
                    continue;
                }
                getterInfo.setMethod = method;
            }
        }
        return map.values();
    }

    public void testGettersAndSetters(ModelWrapper<?> w)
        throws BiobankCheckException, Exception {
        testGettersAndSetters(w, null);
    }

    public void testGettersAndSetters(ModelWrapper<?> w,
        List<String> skipMethods) throws BiobankCheckException, Exception {
        Collection<GetterInfo> gettersInfoList = getGettersAndSetters(w);

        if (skipMethods != null) {
            List<String> methodNames = new ArrayList<String>();
            for (GetterInfo getterInfo : gettersInfoList) {
                methodNames.add(getterInfo.getMethod.getName());
            }

            for (String methodName : skipMethods) {
                if (!methodNames.contains(methodName)) {
                    throw new Exception("method to skip does not exist: "
                        + methodName);
                }
            }
        }

        for (GetterInfo getterInfo : gettersInfoList) {
            if ((skipMethods != null)
                && skipMethods.contains(getterInfo.getMethod.getName())) {
                continue;
            }

            if (getterInfo.setMethod == null) {
                // System.out.println("no setter found for "
                // + w.getClass().getName() + "."
                // + getterInfo.getMethod.getName() + "()");
                continue;
            }

            Class<?> returnType = getterInfo.getMethod.getReturnType();

            for (int i = 0; i < 5; ++i) {
                Object parameter = null;

                if (returnType.equals(java.lang.Boolean.class)) {
                    parameter = new Boolean(r.nextBoolean());
                } else if (returnType.equals(java.lang.Integer.class)) {
                    parameter = new Integer(r.nextInt(Integer.MAX_VALUE));
                } else if (returnType.equals(java.lang.Long.class)) {
                    parameter = new Long(r.nextLong());
                } else if (returnType.equals(java.lang.Double.class)) {
                    parameter = new Double(r.nextDouble());
                } else if (returnType.equals(java.lang.String.class)) {
                    parameter = Utils.getRandomString(32);
                } else if (returnType.equals(java.util.Date.class)) {
                    parameter = Utils.getRandomDate();
                } else {
                    throw new Exception("return type "
                        + getterInfo.getMethod.getReturnType().getName()
                        + " for method " + getterInfo.getMethod.getName()
                        + " for class " + w.getClass().getName()
                        + " not implemented");
                }

                // System.out.println("invoking " + w.getClass().getName() + "."
                // + getterInfo.getMethod.getName());

                getterInfo.setMethod.invoke(w, parameter);
                w.persist();
                w.reload();
                Object getResult = getterInfo.getMethod.invoke(w);
                String msg = new StringBuffer(w.getClass().getName())
                    .append(".").append(getterInfo.getMethod.getName())
                    .append("()").toString();

                if (returnType.equals(java.lang.Double.class)) {
                    // FIXME: temporary fix for caCORE not supporting DECIMAL
                    // MySQL type
                    //
                    // our wish is to convert all Doubles in the model to
                    // DECIMALs
                    Assert
                        .assertTrue(
                            msg,
                            Math.abs((Double) parameter - (Double) getResult) < 0.0001);

                } else {
                    Assert.assertEquals(msg, parameter, getResult);
                }
            }

            // maxlength for varchar test
            if (returnType.equals(java.lang.String.class)) {
                String attrName = getterInfo.getMethod.getName().substring(3);
                attrName = attrName.substring(0, 1).toLowerCase()
                    + attrName.substring(1);
                // 512 char string
                String longString = Utils.getRandomString(511, 512);
                try {
                    getterInfo.setMethod.invoke(w, longString);
                    w.persist();
                    // no specific error thrown
                    Assert.assertTrue(true);
                } catch (StringValueLengthServerException e) {
                    if (VarCharLengths
                        .getMaxSize(w.getWrappedClass(), attrName) == null) {
                        System.out
                            .println("StringValueLengthServerException thrown but "
                                + "no corresponding key in VarCharLengths map for "
                                + w.getWrappedClass() + "." + attrName);
                        continue;
                    }
                    // FIXME VarCharLength map does not contain information for
                    // internal properties like street1 for centers or sub
                    // classes like clinic
                    Assert
                        .fail("VARCHAR limits not checked with 'checkFieldLimits method' on field: "
                            + w.getWrappedClass().getName() + "." + attrName);
                } catch (CheckFieldLimitsException cfle) {
                    if (VarCharLengths
                        .getMaxSize(w.getWrappedClass(), attrName) == null)
                        Assert
                            .fail("CheckFieldLimitsException should not be thrown");
                } finally {
                    w.reload();
                }
            }
        }

    }
}
