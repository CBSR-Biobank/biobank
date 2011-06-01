package edu.ualberta.med.biobank.widgets.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class VetoListenerSupport<T, V> {
    private Map<T, Collection<VetoListener<T, V>>> listenersMap = new HashMap<T, Collection<VetoListener<T, V>>>();

    public void addListener(T type, VetoListener<T, V> listener) {
        Collection<VetoListener<T, V>> listeners = listenersMap.get(type);

        if (listeners == null) {
            listeners = new ArrayList<VetoListener<T, V>>();
            listenersMap.put(type, listeners);
        }

        listeners.add(listener);
    }

    public void removeListener(T type, VetoListener<T, V> listener) {
        Collection<VetoListener<T, V>> listeners = listenersMap.get(type);

        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    public void notifyListeners(Event<T, V> event) throws VetoException {
        T type = event.getType();
        Collection<VetoListener<T, V>> listeners = listenersMap.get(type);

        if (listeners != null) {
            for (VetoListener<T, V> listener : listeners) {
                listener.handleEvent(event);
            }
        }
    }

    public static class Event<T, V> {
        private final T type;
        private final V object;
        public boolean doit = true;

        public Event(T type, V object) {
            this.type = type;
            this.object = object;
        }

        public T getType() {
            return type;
        }

        public V getObject() {
            return object;
        }

        public static <T, V> Event<T, V> newEvent(T type, V object) {
            return new Event<T, V>(type, object);
        }
    }

    public interface VetoListener<T, V> {
        public void handleEvent(Event<T, V> event) throws VetoException;
    }

    public static class VetoException extends Exception {
        private static final long serialVersionUID = 1L;

        public VetoException(String message) {
            super(message);
        }
    }
}
