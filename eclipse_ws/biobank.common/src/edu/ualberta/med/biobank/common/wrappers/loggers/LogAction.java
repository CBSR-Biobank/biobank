package edu.ualberta.med.biobank.common.wrappers.loggers;

import java.util.Date;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadModelAction;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

/**
 * 
 * @author jferland
 * 
 */
public class LogAction<E> extends LoadModelAction<E> {
    private static final long serialVersionUID = 1L;

    private final Type type;
    private final WrapperLogProvider<E> logProvider;
    private final LogGroup logGroup;

    public enum Type {
        PERSIST, DELETE;
    }

    public LogAction(Type type, ModelWrapper<E> wrapper,
        WrapperLogProvider<E> logProvider, LogGroup logGroup) {
        super(wrapper);
        this.type = type;
        this.logProvider = logProvider;
        this.logGroup = logGroup;
    }

    @Override
    public void doLoadModelAction(Session session, E loadedModel)
        throws BiobankSessionException {

        Log log = logProvider.getLog(loadedModel);

        if (log == null) {
            return;
        }

        log.setType(getModelClass().getSimpleName());
        log.setCreatedAt(new Date());
        log.setAction(getAction());

        // TODO: currently the information is pulled from the LogGroup and put
        // into the Log, in the future the LogGroup should have already been
        // saved and the Log should have it's LogGroup set then saved
        log.setUsername(getUsername());
        // log.setLogGroup(logGroup);

        session.save(log);
    }

    private String getAction() {
        String action = null;

        if (Type.PERSIST.equals(type)) {
            if (getIdProperty().get(getModel()) == null) {
                action = "insert";
            } else {
                action = "update";
            }
        } else if (Type.DELETE.equals(type)) {
            action = "delete";
        }

        return action;
    }

    private String getUsername() {
        String username = null;

        Authentication auth = SecurityContextHolder.getContext()
            .getAuthentication();

        if (auth != null) {
            username = auth.getName();
        }

        return username;
    }
}