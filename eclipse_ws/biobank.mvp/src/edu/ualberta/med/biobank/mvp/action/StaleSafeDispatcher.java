package edu.ualberta.med.biobank.mvp.action;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Inject;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionCallback;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.Dispatcher;
import edu.ualberta.med.biobank.mvp.action.StaleSafeDispatcher.AsyncContext.Call;

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
 * Staleness is determined according to an {@link Action}'s class.
 * 
 * @author jferland
 * 
 */
@SuppressWarnings("unused")
public class StaleSafeDispatcher implements Dispatcher {
    private final Dispatcher dispatcher;

    private final Map<Object, AsyncContext> contexts =
        new HashMap<Object, AsyncContext>();

    @Inject
    public StaleSafeDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public <T extends ActionResult> T exec(Action<T> action) {
        return dispatcher.exec(action);
    }

    @Override
    public synchronized <T extends ActionResult> Future<T> asyncExec(
        Action<T> action, ActionCallback<T> callback) {
        return asyncExec(action.getClass(), action, callback);
    }

    private synchronized <T extends ActionResult> Future<T> asyncExec(
        Object contextKey, Action<T> action, ActionCallback<T> callback) {

        AsyncContext context = new AsyncContext();
        Call call = context.start();

        return dispatcher.asyncExec(action, new StaleSafeActionCallback<T>(
            call, callback));
    }

    public static class AsyncContext {
        private AtomicInteger placeProvider = new AtomicInteger(0);
        private Integer lastFinished = -1;

        public Call start() {
            return new Call();
        }

        public class Call {
            private final Integer place;

            private Call() {
                this.place = placeProvider.getAndIncrement();
            }

            public synchronized boolean finish() {
                if (place > lastFinished) {
                    lastFinished = place;
                    return true;
                }
                return false;
            }
        }
    }
}
