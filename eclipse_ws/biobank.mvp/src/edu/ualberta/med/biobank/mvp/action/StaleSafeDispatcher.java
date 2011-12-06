package edu.ualberta.med.biobank.mvp.action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.gwt.user.client.History;
import com.google.inject.Inject;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.Dispatcher;

/**
 * Thread-safe delegating {@link Dispatcher} that will not execute the callback
 * methods of stale {@link ActionCallback}-s.
 * <p>
 * This is most easily explained with an example. Say an action is
 * asynchronously invoked twice (via
 * {@link Dispatcher#asyncExec(Action, ActionCallback)}, but the second
 * invocation returns before the first. This {@link StaleSafeDispatcher}
 * implementation will invoke the callback of the second {@link Action}, but not
 * the first, if and when it does finally return.
 * <p>
 * TODO: explain default realm and optional realm.
 * 
 * @author jferland
 * 
 */
public class StaleSafeDispatcher implements Dispatcher {
    private final Dispatcher dispatcher;
    private final Map<Object, History> histories =
        new HashMap<Object, History>();

    @Inject
    public StaleSafeDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public <T extends ActionResult> T exec(Action<T> action) {
        return dispatcher.exec(action);
    }

    @Override
    public <T extends ActionResult> boolean exec(Action<T> action,
        ActionCallback<T> cb) {
        return dispatcher.exec(action, cb);
    }

    @Override
    public synchronized <T extends ActionResult> void asyncExec(
        Action<T> action, ActionCallback<T> cb) {
        // asyncExec(action.getClass(), action, cb);
    }

    public synchronized <T extends ActionResult> void asyncExec(
        StaleContext context, Action<T> action, ActionCallback<T> cb) {
        // context.start
        dispatcher.asyncExec(action, new StaleSafeActionCallback<T>(cb));
    }

    private synchronized History getHistory(Object object) {
        History history = histories.get(object);
        if (history == null) {
            history = new History();
            histories.put(object, history);
        }
        return history;
    }

    /**
     * 
     * @author jferland
     * 
     */
    public static class StaleContext {
        private AtomicInteger numberProvider = new AtomicInteger(0);
        private Call lastCompleted;

        public Call start() {
            return null;
        }

        public class Call {
            private final Integer number;

            private Call() {
                this.number = numberProvider.getAndIncrement();
            }

            /**
             * Returns true if the {@link Call} should be allowed to finish.
             * 
             * @param point
             * @return
             */
            public synchronized boolean finish() {
                return false;
            }
        }
    }
}
