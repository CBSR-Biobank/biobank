package edu.ualberta.med.biobank.mvp.presenter.validation;

import com.pietschy.gwt.pectin.client.condition.Condition;

public class ConditionUtil {
    public static boolean isTrue(Condition condition) {
        return Boolean.TRUE.equals(condition.getValue());
    }
}
