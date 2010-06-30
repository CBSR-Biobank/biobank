package edu.ualberta.med.biobank.test.speed;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import edu.ualberta.med.biobank.client.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class AppServiceSpeedTest {

    private static final Logger logger = Logger
        .getLogger(AppServiceSpeedTest.class);

    private WritableApplicationService appService;

    private SiteWrapper cbsrSite = null;

    public static void main(String[] args) {
        try {
            new AppServiceSpeedTest();
        } catch (Exception e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }

    public AppServiceSpeedTest() throws Exception {
        PropertyConfigurator.configure("conf/log4j.properties");

        File file = new File("conf/all.keystore");

        String defaultServerUrl = "http://localhost:8080";
        // String defaultServerUrl = "https://136.159.173.90:9443";
        // String defaultServerUrl = "https://cbsr.med.ualberta.ca:8443";

        appService = ServiceConnection.getAppService(System.getProperty(
            "server.url", defaultServerUrl)
            + "/biobank2", file.toURI().toURL(), "testuser", "test");

        cbsrSite = getCbsrSite();

        performTests();
    }

    private void performTests() throws Exception {
        Class<?>[] classes = getClasses("edu.ualberta.med.biobank.test.speed");

        // Execute test methods for each class starting with "Test"
        for (Class<?> klass : classes) {
            if (!klass.getName().startsWith(
                "edu.ualberta.med.biobank.test.speed.Test"))
                continue;

            Class<?>[] args = new Class[] { WritableApplicationService.class,
                SiteWrapper.class };
            Constructor<?> constructor = klass.getConstructor(args);
            Object testObj = constructor.newInstance(new Object[] { appService,
                cbsrSite });

            for (Method method : klass.getMethods()) {
                if (!method.getName().startsWith("test"))
                    continue;

                logger.info("starting test " + klass.getName() + ":"
                    + method.getName());
                long tsStart = (new Date()).getTime();
                method.invoke(testObj, new Object[] {});
                long tsEnd = (new Date()).getTime();
                logger.info("test " + klass.getName() + ":" + method.getName()
                    + " execution time is: " + (tsEnd - tsStart) + " ms");
            }
        }
    }

    private SiteWrapper getCbsrSite() throws Exception {
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getName().equals("Canadian BioSample Repository")) {
                return site;
            }
        }
        return null;
    }

    /*
     * Scans all classes accessible from the context class loader which belong
     * to the given package and subpackages.
     * 
     * @param packageName The base package
     * 
     * @return The classes
     * 
     * @throws ClassNotFoundException
     * 
     * @throws IOException
     */
    private static Class<?>[] getClasses(String packageName)
        throws ClassNotFoundException, IOException {
        ClassLoader classLoader = Thread.currentThread()
            .getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> dirs = new ArrayList<File>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            dirs.add(new File(resource.getFile()));
        }
        ArrayList<Class<?>> classes = new ArrayList<Class<?>>();
        for (File directory : dirs) {
            classes.addAll(findClasses(directory, packageName));
        }
        return classes.toArray(new Class[classes.size()]);
    }

    /*
     * Recursive method used to find all classes in a given directory and
     * subdirs.
     * 
     * @param directory The base directory
     * 
     * @param packageName The package name for classes found inside the base
     * directory
     * 
     * @return The classes
     * 
     * @throws ClassNotFoundException
     */
    private static List<Class<?>> findClasses(File directory, String packageName)
        throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "."
                    + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes
                    .add(Class.forName(packageName
                        + '.'
                        + file.getName().substring(0,
                            file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
