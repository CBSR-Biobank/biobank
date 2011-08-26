package edu.ualberta.med.biobank.common.wrappers.property;

import edu.ualberta.med.biobank.common.wrappers.Property;

public interface GetterInterceptor {
    public <P, M> P get(Property<P, M> subProperty, M model);
}
