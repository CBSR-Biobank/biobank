package edu.ualberta.med.biobank.common.wrappers;

import gov.nih.nci.system.query.example.SearchExampleQuery;

/**
 * Extends {@code SearchExampleQuery} so it can use/ pass through the existing
 * security features. Implements {@code BiobankSessionAction} so this class is
 * executed as a different type of query.
 * 
 * @author jferland
 * 
 */
public abstract class BiobankWrapperAction<E> extends SearchExampleQuery
    implements BiobankSessionAction {
    private static final long serialVersionUID = 1L;

    private final Class<E> modelClass;
    private final Property<? extends Integer, ? super E> idProperty;

    protected BiobankWrapperAction(ModelWrapper<E> wrapper) {
        super(ProxyUtil.convertProxyToObject(wrapper.wrappedObject));
        this.modelClass = wrapper.getWrappedClass();
        this.idProperty = wrapper.getIdProperty();
    }

    protected BiobankWrapperAction(BiobankWrapperAction<E> action) {
        this(action.getModel(), action.modelClass, action.idProperty);
    }

    protected BiobankWrapperAction(E model, Class<E> modelClass,
        Property<? extends Integer, ? super E> idProperty) {
        super(model);
        this.modelClass = modelClass;
        this.idProperty = idProperty;
    }

    /**
     * Overridden to ensure that examples are an instance of {@code E}. The
     * "example" property is used to hold the model object because a public
     * getter and setter are what CaCORE uses to proxy or unproxy properties of
     * Object-s received from or sent to the server, respectively.
     */
    @Override
    public void setExample(Object example) {
        if (!modelClass.isAssignableFrom(example.getClass())) {
            throw new IllegalArgumentException();
        }

        super.setExample(example);
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
