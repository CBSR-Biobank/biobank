package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.pietschy.gwt.pectin.client.form.validation.ConditionBuilder;
import com.pietschy.gwt.pectin.client.value.DelegatingValueModel;
import com.pietschy.gwt.pectin.client.value.ValueModel;

public class DelegatingConditionBuilder implements ConditionBuilder {
    private DelegatingValueModel<Boolean> conditionDelegate;

    DelegatingConditionBuilder(DelegatingValueModel<Boolean> conditionDelegate) {
        this.conditionDelegate = conditionDelegate;
    }

    @Override
    public void when(ValueModel<Boolean> condition) {
        conditionDelegate.setDelegate(condition);
    }
}
