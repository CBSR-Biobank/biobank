package edu.ualberta.med.biobank.mvp.view.item;

public interface Adapter<T, U> {
    T adapt(U unadapted);

    U unadapt(T adapted);
}
