package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class HQLAccessor {
    private static final String GET_COLLECTION_HQL = "SELECT e FROM {0} e WHERE e.{1} = ?"; //$NON-NLS-1$

    /**
     * Get a {@link List} of {@link ModelWrapper}-s that wrap a collection of
     * 
     * @param <W1>
     * @param <M1>
     * @param <W2>
     * @param <M2>
     * @param wrapper
     * @param property
     * @param elementClass
     * @param elementWrapperClass
     * @param sort
     * @return
     * @throws ApplicationException
     */
    public static <W1 extends ModelWrapper<? extends M1>, M1, W2 extends ModelWrapper<? extends M2>, M2> List<W2> getCachedCollection(
        W1 wrapper, Property<? extends M1, M2> property,
        Class<M2> elementClass, Class<W2> elementWrapperClass, boolean sort)
        throws ApplicationException {

        @SuppressWarnings("unchecked")
        List<W2> list = (List<W2>) wrapper.cache.get(property);

        if (list == null) {
            WritableApplicationService service = wrapper.appService;

            String hqlString = MessageFormat.format(GET_COLLECTION_HQL,
                elementClass.getName(), property.getName());

            HQLCriteria hqlCriteria = new HQLCriteria(hqlString,
                Arrays.asList((Object) wrapper.wrappedObject));

            List<M2> models = service.query(hqlCriteria);

            list = ModelWrapper.wrapModelCollection(service, models,
                elementWrapperClass);
        }

        if (sort) {
            Collections.sort(list);
        }

        return list;
    }
}
