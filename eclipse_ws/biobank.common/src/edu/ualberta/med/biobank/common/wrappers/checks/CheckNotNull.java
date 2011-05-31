package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.BiobankSearchAction;
import edu.ualberta.med.biobank.common.wrappers.BiobankSessionActionException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;

class CheckNotNull<E> extends BiobankSearchAction<E> {
    private static final long serialVersionUID = 1L;

    private static final String ERR_STR = "Property {0} of {1} cannot be null.";

    private final Property<?, E> property;
    private final String description;

    protected CheckNotNull(ModelWrapper<E> wrapper, Property<?, E> property) {
        super(wrapper);
        this.description = wrapper.toString();
        this.property = property;
    }

    @Override
    public Object doAction(Session session)
        throws BiobankSessionActionException {

        E model = getModel();
        if (property.get(model) == null) {
            String name = property.getName();
            String msg = MessageFormat.format(ERR_STR, name, description);

            throw new BiobankSessionActionException(msg);
        }

        return null;
    }
}
