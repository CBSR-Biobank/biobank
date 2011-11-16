package edu.ualberta.med.biobank.common.wrappers.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.cglib.proxy.Enhancer;

import org.hibernate.proxy.HibernateProxy;
import org.springframework.aop.TargetSource;
import org.springframework.aop.framework.Advised;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class WrapperUtil {
    private static final Map<Class<?>, Class<?>> modelClassMap = new HashMap<Class<?>, Class<?>>();

    public static <E> Class<? extends ModelWrapper<E>> getWrapperClass(
        Class<E> modelClass) {
        @SuppressWarnings("unchecked")
        Class<? extends ModelWrapper<E>> wrapperClass = (Class<? extends ModelWrapper<E>>) modelClassMap
            .get(modelClass);

        if (wrapperClass == null && !modelClassMap.containsKey(modelClass)) {
            wrapperClass = findWrapperClass(modelClass);

            modelClassMap.put(modelClass, wrapperClass);
        }

        return wrapperClass;
    }

    public static <E> Class<? extends ModelWrapper<E>> findWrapperClass(
        Class<E> modelClass) {
        Class<? extends ModelWrapper<E>> wrapperClass = null;

        String packageName = ModelWrapper.class.getPackage().getName();
        String internalPackageName = packageName + ".internal"; //$NON-NLS-1$
        String classSimpleName = modelClass.getSimpleName() + "Wrapper"; //$NON-NLS-1$

        try {
            @SuppressWarnings("unchecked")
            Class<? extends ModelWrapper<E>> tmp = (Class<? extends ModelWrapper<E>>) Class
                .forName(packageName + "." + classSimpleName); //$NON-NLS-1$
            wrapperClass = tmp;
        } catch (ClassNotFoundException e) {
            try {
                @SuppressWarnings("unchecked")
                Class<? extends ModelWrapper<E>> tmp = (Class<? extends ModelWrapper<E>>) Class
                    .forName(internalPackageName + "." + classSimpleName); //$NON-NLS-1$
                wrapperClass = tmp;
            } catch (ClassNotFoundException e1) {
            }
        }

        return wrapperClass;
    }

    @SuppressWarnings("unchecked")
    public static <W extends ModelWrapper<? extends M>, M> W wrapModel(
        WritableApplicationService appService, M model, Class<W> wrapperKlazz) {
        if (model == null)
            return null;

        try {

            Class<?> modelKlazz = model.getClass();
            if (Enhancer.isEnhanced(modelKlazz)) {
                // if the given model's Class is 'enhanced' by CGLIB, then the
                // superclass container the real class
                modelKlazz = modelKlazz.getSuperclass();
                if (Modifier.isAbstract(modelKlazz.getModifiers())) {
                    // The super class can be a problem when the class is
                    // abstract,
                    // but it should be an instance of Advised, that contain the
                    // real (non-proxied/non-enhanced) model object.
                    if (model instanceof Advised) { // ok for client side
                        TargetSource ts = ((Advised) model).getTargetSource();
                        modelKlazz = ts.getTarget().getClass();
                    } else if (model instanceof HibernateProxy) {
                        // only on server side (?).
                        Object implementation = ((HibernateProxy) model)
                            .getHibernateLazyInitializer().getImplementation();
                        modelKlazz = implementation.getClass();
                        // Is this bad to do that ? On server side, will get a
                        // proxy
                        // that inherit from Center, not from Site, so won't be
                        // able
                        // to create a SiteWrapper unless is using the direct
                        // implementation
                        model = (M) implementation;
                    }
                }
            }

            if (wrapperKlazz == null
                || Modifier.isAbstract(wrapperKlazz.getModifiers())) {
                Class<W> tmp = (Class<W>) getWrapperClass(modelKlazz);
                wrapperKlazz = tmp;
            }

            Class<?>[] params = new Class[] { WritableApplicationService.class,
                modelKlazz };
            Constructor<W> constructor = wrapperKlazz.getConstructor(params);

            Object[] args = new Object[] { appService, model };

            W wrapper = constructor.newInstance(args);

            return wrapper;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // TODO: can this be replaced with wrapModel?
    public static ModelWrapper<?> wrapObject(
        WritableApplicationService appService, Object nakedObject)
        throws Exception {

        Class<?> nakedKlazz = nakedObject.getClass();
        String wrapperClassName = ModelWrapper.class.getPackage().getName()
            + "." + nakedObject.getClass().getSimpleName() + "Wrapper"; //$NON-NLS-1$//$NON-NLS-2$

        try {
            Class<?> wrapperKlazz = Class.forName(wrapperClassName);

            Class<?>[] params = new Class[] { WritableApplicationService.class,
                nakedKlazz };
            Constructor<?> constructor = wrapperKlazz.getConstructor(params);

            Object[] args = new Object[] { appService, nakedObject };

            return (ModelWrapper<?>) constructor.newInstance(args);
        } catch (Exception e) {
            throw new Exception("cannot find or create expected Wrapper (" //$NON-NLS-1$
                + wrapperClassName + ") for " + nakedKlazz.getName(), e); //$NON-NLS-1$
        }
    }

    // public static <E> Property<Integer, ? super E> getIdProperty() {
    //
    // }

    // TODO: write function to return ID property for a given class!!!!

    public static <E> Collection<E> unwrapCollection(
        Collection<ModelWrapper<E>> wrapperCollection) {
        Collection<E> unwrapped = new ArrayList<E>();

        for (ModelWrapper<E> wrapper : wrapperCollection) {
            unwrapped.add(wrapper.getWrappedObject());
        }

        return unwrapped;
    }
    
    public static Set<Integer> getCollectionIds(Collection<? extends ModelWrapper<?>> wrapperCollection) {
        Set<Integer> ids = new HashSet<Integer>();

        for (ModelWrapper<?> wrapper : wrapperCollection) {
            ids.add(wrapper.getId());
        }
        return ids;
    }
    
}
