package edu.ualberta.med.biobank.common.wrappers.actions;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * Loads a new instance of the model object from persistence and calls {@code
 * onLoaded({@link E} attachedObject)}.
 * 
 * @author jferland
 * 
 * @param <E>
 */
public abstract class LoadAction<E> extends WrapperAction<E> {
    private static final long serialVersionUID = 1L;

    protected LoadAction(ModelWrapper<E> wrapper) {
        super(wrapper);
    }

    @Override
    public final Object doAction(Session session)
        throws BiobankSessionException {
        // TODO: write a test that persists a model object, then does a
        // CheckLoad on it, then persists, etc. to check if hib throws
        // exceptions

        E freshObject = loadModel(session);
        onLoad(session, freshObject);

        return null;
    }

    protected E loadModel(Session session) {
        @SuppressWarnings("unchecked")
        E loaded = (E) session.load(getModelClass(), getModelId());
        return loaded;
    }

    public abstract void onLoad(Session session, E freshObject)
        throws BiobankSessionException;
}
