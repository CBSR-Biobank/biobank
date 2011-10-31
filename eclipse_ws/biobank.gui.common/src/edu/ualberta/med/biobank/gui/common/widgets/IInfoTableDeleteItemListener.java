package edu.ualberta.med.biobank.gui.common.widgets;

public interface IInfoTableDeleteItemListener<T> {
    void deleteItem(InfoTableEvent<T> event);
}
