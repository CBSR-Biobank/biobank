package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.BiobankWrapperAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.InvalidOptionException;

public class PostCheckLegalOption<E> extends BiobankWrapperAction<E> {
    private static final long serialVersionUID = 1L;
    private static final String COUNT_HQL = "SELECT COUNT(option) FROM {0} m JOIN m.{1} option WHERE m = ? AND m.{2} = option";

    private final Property<?, ? super E> legalOptions;
    private final Property<?, ? super E> selectedOption;
    private final LazyMessage exceptionMessage;

    /**
     * Checks that the {@code ModelWrapper}'s wrapped object has a valid value,
     * i.e. that the value selected is in the set of legal options.
     * 
     * NOTE: this check should be performed after the object being checked is
     * persisted. It is easier to do this since the check can be done with HQL.
     * 
     * @param <T>
     * @param wrapper
     * @param legalOptions property path to the legal options/ values, from the
     *            wrapped object
     * @param selectedOption property path to the actual selected value, from
     *            the wrapped object
     * @param exceptionMessage message to display if the option is invalid
     */
    public <T> PostCheckLegalOption(ModelWrapper<E> wrapper,
        Property<? extends Collection<? extends T>, ? super E> legalOptions,
        Property<? extends T, ? super E> selectedOption,
        LazyMessage exceptionMessage) {
        super(wrapper);
        this.legalOptions = legalOptions;
        this.selectedOption = selectedOption;
        this.exceptionMessage = exceptionMessage;
    }

    @Override
    public Object doAction(Session session) throws BiobankSessionException {
        String hql = MessageFormat.format(COUNT_HQL, getModelClass().getName(),
            legalOptions.getName(), selectedOption.getName());
        Query query = session.createQuery(hql);
        query.setParameter(0, getModel());

        List<?> results = query.list();
        Long count = CheckUtil.getCountFromResult(results);

        if (count == null || count <= 0) {
            String message = exceptionMessage.format(session);
            throw new InvalidOptionException(message);
        }

        return null;
    }
}