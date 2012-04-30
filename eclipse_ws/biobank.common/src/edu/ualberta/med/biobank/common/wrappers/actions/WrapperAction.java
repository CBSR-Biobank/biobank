package edu.ualberta.med.biobank.common.wrappers.actions;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.util.ProxyUtil;
import gov.nih.nci.system.query.example.SearchExampleQuery;

/**
 * Extends {@link SearchExampleQuery} so it can use/ pass through the existing
 * security features. Implements {@link BiobankSessionAction} so this class is
 * executed as a different type of query.
 * 
 * @author jferland
 * 
 */
public abstract class WrapperAction<E> extends SearchExampleQuery implements
    BiobankSessionAction {
    private static final long serialVersionUID = 1L;

    private final Class<E> modelClass;
    private final Property<? extends Integer, ? super E> idProperty;

    protected WrapperAction(ModelWrapper<E> wrapper) {
        super(ProxyUtil.convertProxyToObject(wrapper.getWrappedObject()));
        this.modelClass = wrapper.getWrappedClass();
        this.idProperty = wrapper.getIdProperty();
    }

    protected WrapperAction(WrapperAction<E> action) {
        this(action.getModel(), action.modelClass, action.idProperty);
    }

    protected WrapperAction(E model, Class<E> modelClass,
        Property<? extends Integer, ? super E> idProperty) {
        super(model);
        this.modelClass = modelClass;
        this.idProperty = idProperty;
    }

    /**
     * This method should not be called anymore. It was used to unwrap model
     * objects (from their CaCORE proxy) but we just do this upon instantiation.
     */
    @Override
    @Deprecated
    public void setExample(Object example) {
        throw new UnsupportedOperationException();
    }

    protected E getModel() {
        @SuppressWarnings("unchecked")
        E example = (E) getExample();
        return example;
    }

    protected Class<E> getModelClass() {
        return modelClass;
    }

    protected Integer getModelId() {
        return idProperty.get(getModel());
    }

    protected Property<? extends Integer, ? super E> getIdProperty() {
        return idProperty;
    }
}
