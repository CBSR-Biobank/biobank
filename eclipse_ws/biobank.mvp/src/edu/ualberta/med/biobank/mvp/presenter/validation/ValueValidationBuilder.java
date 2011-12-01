package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.google.gwt.user.client.ui.HasValue;
import com.pietschy.gwt.pectin.client.condition.DelegatingCondition;
import com.pietschy.gwt.pectin.client.form.validation.Validator;

public class ValueValidationBuilder<T> {
    private final ValidationTree validationTree;
    private final HasValue<T> source;

    ValueValidationBuilder(ValidationTree validationTree, HasValue<T> source) {
        this.validationTree = validationTree;
        this.source = source;
    }

    public DelegatingConditionBuilder using(Validator<? super T> validator) {
        ValueValidation<T> validation =
            validationTree.getValueValidation(source);

        DelegatingCondition condition = new DelegatingCondition(true);
        validation.add(validator, condition);

        return new DelegatingConditionBuilder(condition);
    }
}
