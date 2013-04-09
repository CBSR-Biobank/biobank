package edu.ualberta.med.biobank;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.hibernate.cfg.Configuration;
import org.hibernate.mapping.PersistentClass;

public class DaoGenerator {

    public static void main(String[] args) {
        Configuration config = new Configuration();

        // config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5InnoDBDialect");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.hbm2ddl.auto", "create-drop");

        config.addPackage("edu.ualberta.med.biobank.model");
        List<Class<?>> modelClasses = FindUtils.getClasses("edu.ualberta.med.biobank.model");
        for (Class<?> modelClass : modelClasses) {
            config.addAnnotatedClass(modelClass);
        }

        config.buildMappings();

        Set<String> classes = new TreeSet<String>();

        for (Iterator<PersistentClass> it = config.getClassMappings(); it.hasNext();) {
            PersistentClass pc = it.next();
            classes.add(pc.getEntityName());
        }

        for (String className : classes) {
            System.out.println(className);
        }

    }

}
