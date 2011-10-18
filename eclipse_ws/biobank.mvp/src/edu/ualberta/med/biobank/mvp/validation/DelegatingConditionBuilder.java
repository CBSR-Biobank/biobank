package edu.ualberta.med.biobank.mvp.validation;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.user.ui.impl.DelegatingHasValue;

public class DelegatingConditionBuilder {
    private final DelegatingHasValue<Boolean> delegatingCondition;

    public DelegatingConditionBuilder(
        DelegatingHasValue<Boolean> delegatingCondition) {
        this.delegatingCondition = delegatingCondition;
    }

    public void when(HasValue<Boolean> value) {
        delegatingCondition.setDelegate(value);
    }
}
