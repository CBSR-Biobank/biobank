package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.VarCharLengths;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.CheckFieldLimitsException;
import edu.ualberta.med.biobank.common.wrappers.tasks.PreQueryTask;

public class CheckStringLengthsPreQueryTask<E> implements PreQueryTask {
    private final ModelWrapper<E> modelWrapper;

    public CheckStringLengthsPreQueryTask(ModelWrapper<E> modelWrapper) {
        this.modelWrapper = modelWrapper;
    }

    @Override
    public void beforeExecute() throws BiobankException {
        E model = modelWrapper.getWrappedObject();
        Class<E> modelClass = modelWrapper.getWrappedClass();

        for (Property<?, ? super E> property : modelWrapper.getProperties()) {
            String field = property.getName();

            Integer max = VarCharLengths.getMaxSize(modelClass, field);
            if (max == null)
                continue;

            if (property.getElementClass().equals(String.class)) {
                String value = (String) property.get(model);
                if ((value != null) && (value.length() > max)) {
                    throw new CheckFieldLimitsException(field, max, value);
                }
            }
        }
    }
}