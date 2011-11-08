package edu.ualberta.med.biobank.mvp.view.item;

import java.util.Collection;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.site.SiteGetStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.mvp.event.SimpleValueChangeEvent;

// TODO: describe this class!
public class TranslatedItem<T, U> extends ValidationItem<T>
    implements HasValue<T> {
    private final DelegateMonitor delegateMonitor = new DelegateMonitor();
    private final ValidationItem<U> delegate;
    private final Translator<T, U> translator;

    public static void main() {
        // TODO: static constructor so smaller.
        TranslatedItem<Collection<StudyInfo>, Collection<StudyWrapper>> studies =
            new TranslatedItem<Collection<StudyInfo>, Collection<StudyWrapper>>(
                new TableItem<StudyWrapper>(), null);
    }

    public interface Translator<T, U> {
        T fromDelegate(U delegate);

        U toDelegate(T foreign);
    }

    public static <T, U> TranslatedItem<T, U> from(ValidationItem<U> delegate,
        Translator<T, U> using) {
        return new TranslatedItem<T, U>(delegate, using);
    }

    public TranslatedItem(ValidationItem<U> delegate,
        Translator<T, U> translator) {
        this.delegate = delegate;
        this.translator = translator;

        delegate.addValueChangeHandler(delegateMonitor);
    }

    @Override
    public T getValue() {
        U delegateValue = delegate.getValue();
        T translatedValue = translator.fromDelegate(delegateValue);
        return translatedValue;
    }

    @Override
    public void setValue(T value, boolean fireEvents) {
        U translatedValue = translator.toDelegate(value);
        delegateMonitor.setFireEvents(fireEvents);
        delegate.setValue(translatedValue, fireEvents);
        delegateMonitor.setFireEvents(true);
    }

    private class DelegateMonitor implements ValueChangeHandler<U> {
        private boolean fireEvents;

        public void setFireEvents(boolean fireEvents) {
            this.fireEvents = fireEvents;
        }

        @Override
        public void onValueChange(ValueChangeEvent<U> event) {
            if (fireEvents) {
                T value = getValue();
                fireEvent(new SimpleValueChangeEvent<T>(value));
            }
        }
    }
}
