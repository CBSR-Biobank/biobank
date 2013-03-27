package edu.ualberta.med.biobank;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class OutputSchema {

    public static void main(String[] args) {
        Configuration config = new Configuration();

        Properties properties = new Properties();

        properties.put("hibernate.dialect", "org.hibernate.dialect.HSQLDialect");
        properties.put("hibernate.show_sql", "true");
        config.setProperties(properties);

        config.addPackage("edu.ualberta.med.biobank.model");

        List<Class<?>> modelClasses = getClasses("edu.ualberta.med.biobank.model");
        for (Class<?> modelClass : modelClasses) {
            config.addAnnotatedClass(modelClass);
        }

        SchemaExport schemaExport = new EnversSchemaGenerator(config).export();
        schemaExport.setOutputFile("schema.sql");
        // schemaExport.drop(false, false);
        schemaExport.create(true, false);
    }

    /**
     * Scans all classes accessible from the context class loader which belong to the given package
     * and subpackages.
     * 
     * @param packageName The base package
     * @return The classes
     */
    private static List<Class<?>> getClasses(String packageName) {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        assert classLoader != null;
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources;
        List<Class<?>> classes = new ArrayList<Class<?>>();
        try {
            resources = classLoader.getResources(path);
            List<File> dirs = new ArrayList<File>();
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                dirs.add(new File(resource.getFile()));
            }
            for (File directory : dirs) {
                classes.addAll(findClasses(directory, packageName));
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     * 
     * @param directory The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
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
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.'
                    + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}
