package edu.ualberta.med.biobank.wsclient;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.xml.namespace.QName;
import javax.xml.rpc.ParameterMode;
import javax.xml.soap.SOAPElement;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.message.SOAPHeaderElement;

public class TestClient {
    public static void main(String args[]) {
        TestClient client = new TestClient();
        try {
            client.testSearch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void testSearch() throws Exception {
        Collection<Class<?>> classList = getClasses();

        String url = "http://localhost:8080/biobank/services/biobankService";
        Service service = new Service();
        Call call = null;

        for (Class<?> klass : classList) {
            try {
                call = (Call) service.createCall();

                for (Class<?> klassToMap : classList) {
                    QName searchClassQNameToMap = new QName("urn:"
                        + getInversePackageName(klassToMap),
                        klassToMap.getSimpleName());
                    call.registerTypeMapping(
                        klassToMap,
                        searchClassQNameToMap,
                        new org.apache.axis.encoding.ser.BeanSerializerFactory(
                            klassToMap, searchClassQNameToMap),
                        new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                            klassToMap, searchClassQNameToMap));
                }
                QName searchClassQNameToMap = new QName(
                    "urn:Character.lang.java", "Character");
                call.registerTypeMapping(Character.class,
                    searchClassQNameToMap,
                    new org.apache.axis.encoding.ser.BeanSerializerFactory(
                        Character.class, searchClassQNameToMap),
                    new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                        Character.class, searchClassQNameToMap));

                QName searchClassQName = new QName("urn:"
                    + getInversePackageName(klass), klass.getSimpleName());

                call.setTargetEndpointAddress(new java.net.URL(url));
                call.setOperationName(new QName("biobankService", "queryObject"));
                call.addParameter("arg1",
                    org.apache.axis.encoding.XMLType.XSD_STRING,
                    ParameterMode.IN);
                call.addParameter("arg2", searchClassQName, ParameterMode.IN);
                call.setReturnType(org.apache.axis.encoding.XMLType.SOAP_ARRAY);

                // This block inserts the security headers in the service call
                SOAPHeaderElement headerElement = new SOAPHeaderElement(call
                    .getOperationName().getNamespaceURI(), "SecurityHeader");
                headerElement.setPrefix("security");
                headerElement.setMustUnderstand(false);
                SOAPElement usernameElement = headerElement
                    .addChildElement("testuser");
                usernameElement.addTextNode("userId");
                SOAPElement passwordElement = headerElement
                    .addChildElement("test");
                passwordElement.addTextNode("password");
                call.addHeader(headerElement);

                Object o = klass.newInstance();

                System.out.println("Searching for " + klass.getName());
                Object[] results = (Object[]) call.invoke(new Object[] {
                    klass.getName(), o });

                if ((results != null) && (results.length > 0)) {
                    for (Object obj : results) {
                        printObject(obj, klass);

                        for (Method method : obj.getClass().getMethods()) {

                            if (method.getName().startsWith("get")
                                && !method.getName().equals("getClass")) {
                                if (!(method.getReturnType().getName()
                                    .startsWith("java")
                                    || method.getReturnType().isPrimitive() || (method
                                    .getReturnType().getName()
                                    .indexOf("Collection") > 0))) {
                                    System.out
                                        .println("Testing getAssociation() call for Class: "
                                            + klass.getName()
                                            + ", Method: "
                                            + method.getName()
                                            + ", of type: "
                                            + method.getReturnType().getName());

                                    String rolename = String.valueOf(
                                        method.getName().charAt(3))
                                        .toLowerCase()
                                        + method.getName().substring(4);
                                    Field field = getField(obj, rolename);

                                    if (field == null) {
                                        rolename = Character
                                            .toUpperCase(rolename.charAt(0))
                                            + rolename.substring(1);
                                        field = getField(obj, rolename);
                                    }
                                    rolename = field.getName();
                                    testGetAssociation(url, service, obj,
                                        method.getReturnType(), rolename);
                                }
                            }
                        }

                        break;
                    }
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
            // break
        }
    }

    private void testGetAssociation(String url, Service service,
        Object containingObj, Class<?> associationClass, String rolename)
        throws Exception {
        // Sample Scenario:
        // http://localhost:8080/biobank/GetHTML?query=Bank&Credit[@id=3]&roleName=issuingBank

        Call call = (Call) service.createCall();

        for (Class<?> klassToMap : getClasses()) {
            QName searchClassQNameToMap = new QName("urn:"
                + getInversePackageName(klassToMap), klassToMap.getSimpleName());
            call.registerTypeMapping(klassToMap, searchClassQNameToMap,
                new org.apache.axis.encoding.ser.BeanSerializerFactory(
                    klassToMap, searchClassQNameToMap),
                new org.apache.axis.encoding.ser.BeanDeserializerFactory(
                    klassToMap, searchClassQNameToMap));
        }

        QName searchClassQName = new QName("urn:"
            + getInversePackageName(associationClass),
            associationClass.getSimpleName());

        call.setTargetEndpointAddress(new java.net.URL(url));
        call.setOperationName(new QName("biobankService", "getAssociation"));
        call.addParameter("arg1", searchClassQName, ParameterMode.IN);
        call.addParameter("arg2", org.apache.axis.encoding.XMLType.XSD_STRING,
            ParameterMode.IN);
        call.addParameter("arg3", searchClassQName, ParameterMode.IN);
        call.setReturnType(org.apache.axis.encoding.XMLType.SOAP_ARRAY);

        // This block inserts the security headers in the service call
        SOAPHeaderElement headerElement = new SOAPHeaderElement(call
            .getOperationName().getNamespaceURI(), "CSMSecurityHeader");
        headerElement.setPrefix("csm");
        headerElement.setMustUnderstand(false);
        SOAPElement usernameElement = headerElement.addChildElement("username");
        usernameElement.addTextNode("userId");
        SOAPElement passwordElement = headerElement.addChildElement("password");
        passwordElement.addTextNode("password");
        call.addHeader(headerElement);

        System.out.println("Searching for association: "
            + containingObj.getClass().getName() + "." + rolename);
        Object[] results = (Object[]) call.invoke(new Object[] { containingObj,
            rolename, 0 });

        if ((results != null) && (results.length > 0)) {
            for (Object obj : results) {
                printObject(obj, associationClass);
                break;
            }
        }
    }

    private String getInversePackageName(Class<?> klass) {
        String name1 = klass.getPackage().getName();
        String[] tokens = name1.split("[.]");
        String newName = "";
        for (int i = 0; i < tokens.length; i++)
            newName += "." + tokens[tokens.length - i - 1];
        newName = newName.substring(1);
        return newName;
    }

    private void printObject(Object obj, Class<?> klass) throws Exception {
        System.out.println("Printing " + klass.getName());
        Method[] methods = klass.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("get")
                && !method.getName().equals("getClass")) {
                System.out.print("\t" + method.getName().substring(3) + ":");
                Object val = method.invoke(obj, (Object[]) null);
                if (val instanceof java.util.Set)
                    System.out.println("size=" + ((Collection<?>) val).size());
                else
                    System.out.println(val);
            }
        }
    }

    protected Collection<Class<?>> getClasses() throws Exception {
        Collection<Class<?>> list = new ArrayList<Class<?>>();
        JarFile file = null;
        int count = 0;
        for (File f : new File("lib").listFiles()) {
            if (f.getName().endsWith("-beans.jar")) {
                file = new JarFile(f);
                count++;
            }
        }
        if (file == null)
            throw new Exception("Could not locate the bean jar");
        if (count > 1)
            throw new Exception("Found more than one bean jar");

        Enumeration<JarEntry> e = file.entries();
        while (e.hasMoreElements()) {
            JarEntry o = e.nextElement();
            if (!o.isDirectory()) {
                String name = o.getName();
                if (name.endsWith(".class")) {
                    String klassName = name.replace('/', '.').substring(0,
                        name.lastIndexOf('.'));
                    list.add(Class.forName(klassName));
                }
            }
        }
        return list;
    }

    protected Field getField(Object bean, String fieldName) {
        Field field = null;
        if (bean == null)
            return null;

        Class<?> klass = bean.getClass();
        while ((klass != null) && (klass != Object.class)) {
            try {
                field = klass.getDeclaredField(fieldName);
            } catch (SecurityException e) {
            } catch (NoSuchFieldException e) {
            } catch (Exception e) {
            }
            if (field != null)
                break;
            ;
            klass = klass.getSuperclass();
        }
        if (field == null)
            System.out.println("Error: field not found for fieldName: "
                + fieldName);
        return field;
    }

}
