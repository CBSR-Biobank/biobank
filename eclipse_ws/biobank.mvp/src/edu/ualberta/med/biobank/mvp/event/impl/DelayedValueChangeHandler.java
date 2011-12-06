package edu.ualberta.med.biobank.mvp.event.impl;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

public abstract class DelayedValueChangeHandler<T> implements
    ValueChangeHandler<T> {
    private static final ScheduledExecutorService SERVICE = Executors
        .newScheduledThreadPool(10);

    private final ScheduledExecutorService service;
    private final int delay;
    private final TimeUnit unit;
    private ScheduledFuture<?> future;

    public DelayedValueChangeHandler(int delay) {
        this(SERVICE, delay, TimeUnit.MILLISECONDS);
    }

    public DelayedValueChangeHandler(ScheduledExecutorService service, int delay) {
        this(service, delay, TimeUnit.MILLISECONDS);
    }

    public DelayedValueChangeHandler(ScheduledExecutorService service,
        int delay, TimeUnit unit) {
        this.service = service;
        this.delay = delay;
        this.unit = unit;
    }

    @Override
    public synchronized void onValueChange(ValueChangeEvent<T> event) {
        if (future != null) {
            future.cancel(false);
        }

        future = service.schedule(new DelayedEventRunnable(event), delay, unit);
    }

    /**
     * Will only be called with the last {@link ValueChangeEvent} fired in the
     * last n milliseconds, where n is the delay. For example, if n = 500ms and
     * 3 events are fired no more than 500ms apart, then only the third event
     * will be called.
     * <p>
     * This method should be executed quickly as it may cause other delayed
     * events to be handled late.
     * 
     * @param event
     */
    protected abstract void onDelayedValueChange(ValueChangeEvent<T> event);

    public abstract static class Delayed500MsValueChangeHandler<T> extends
        DelayedValueChangeHandler<T> {
        public Delayed500MsValueChangeHandler() {
            super(500);
        }
    }

    private class DelayedEventRunnable implements Runnable {
        private ValueChangeEvent<T> event;

        public DelayedEventRunnable(ValueChangeEvent<T> event) {
            this.event = event;
        }

        @Override
        public void run() {
            onDelayedValueChange(event);
        }
    }
}
