package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Control;

import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;

import edu.ualberta.med.biobank.mvp.user.ui.HasControl;

public abstract class AbstractControlField
    implements HasControl, ValidationDisplay {
    private final EnabledForwarder enabledForwarder = new EnabledForwarder();
    private final VisibleForwarder visibleForwarder = new VisibleForwarder();

    public void forwardEnabled(Control control) {
        enabledForwarder.add(control);
    }

    public void unforwardEnabled(Control control) {
        enabledForwarder.remove(control);
    }

    public void forwardVisible(Control control) {
        visibleForwarder.add(control);
    }

    public void unforwardVisible(Control control) {
        visibleForwarder.remove(control);
    }

    @Override
    public void setEnabled(boolean enabled) {
        enabledForwarder.setState(enabled);
    }

    @Override
    public boolean isEnabled() {
        return enabledForwarder.getState();
    }

    @Override
    public void setVisible(boolean visible) {
        visibleForwarder.setState(visible);
    }

    @Override
    public boolean isVisible() {
        return visibleForwarder.getState();
    }

    /**
     * Synchronises a state between a list of objects.
     * 
     * @author jferland
     * 
     * @param <T> object
     * @param <S> state
     */
    protected abstract static class StateForwarder<T, S> {
        private final List<T> objects = new ArrayList<T>();
        private S state;

        /**
         * @param state initial state.
         */
        public StateForwarder(S state) {
            this.state = state;
        }

        public void add(T object) {
            setState(object, state);
            objects.add(object);
        }

        public void remove(T object) {
            objects.remove(object); // TODO: revert state before removing?
        }

        public void clear() {
            objects.clear(); // TODO: revert state before clearing?
        }

        public void setState(S state) {
            this.state = state;
            update();
        }

        public S getState() {
            return state;
        }

        public void update() {
            for (T object : objects) {
                setState(object, state);
            }
        }

        protected abstract void setState(T object, S state);
    }

    private static class EnabledForwarder extends
        StateForwarder<Control, Boolean> {

        public EnabledForwarder() {
            super(true);
        }

        @Override
        protected void setState(Control object, Boolean state) {
            object.setEnabled(Boolean.TRUE.equals(state));
        }
    }

    private static class VisibleForwarder extends
        StateForwarder<Control, Boolean> {

        public VisibleForwarder() {
            super(true);
        }

        @Override
        protected void setState(Control object, Boolean state) {
            object.setVisible(Boolean.TRUE.equals(state));
        }
    }
}
