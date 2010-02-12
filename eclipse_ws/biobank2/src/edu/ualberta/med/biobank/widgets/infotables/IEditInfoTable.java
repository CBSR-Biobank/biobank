package edu.ualberta.med.biobank.widgets.infotables;

public interface IEditInfoTable<T> {
    void editItem(T item);

    void deleteItem(T item);
}
