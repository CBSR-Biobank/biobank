package edu.ualberta.med.biobank.mvp.view.item;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.mvp.event.ui.ListChangeEvent;
import edu.ualberta.med.biobank.mvp.event.ui.ListChangeHandler;
import edu.ualberta.med.biobank.mvp.user.ui.HasListField;

public class AdaptedListField<T, U> extends AbstractListField<T> {
    private final AdapteeMonitor adapteeMonitor = new AdapteeMonitor();
    private final HasListField<U> adaptee;
    private final Adapter<T, U> adapter;

    public AdaptedListField(HasListField<U> adaptee, Adapter<T, U> adapter) {
        this.adaptee = adaptee;
        this.adapter = adapter;

        adaptee.addListChangeHandler(adapteeMonitor);
    }

    @Override
    protected void update() {
        List<U> unadaptedElements = unadaptList(asUnmodifiableList());
        adaptee.setElements(unadaptedElements, false);
    }

    private List<T> adaptList(List<U> unadaptedList) {
        List<T> adaptedList = new ArrayList<T>();
        for (U unadapted : unadaptedList) {
            T adapted = adapter.adapt(unadapted);
            adaptedList.add(adapted);
        }
        return adaptedList;
    }

    private List<U> unadaptList(List<T> adaptedList) {
        List<U> unadaptedList = new ArrayList<U>();
        for (T adapted : adaptedList) {
            U unadapted = adapter.unadapt(adapted);
            unadaptedList.add(unadapted);
        }
        return unadaptedList;
    }

    private class AdapteeMonitor implements ListChangeHandler<U> {
        @Override
        public void onListChange(ListChangeEvent<U> event) {
            List<T> adaptedElements = adaptList(adaptee.asUnmodifiableList());
            setElements(adaptedElements, true);
        }
    }
}
