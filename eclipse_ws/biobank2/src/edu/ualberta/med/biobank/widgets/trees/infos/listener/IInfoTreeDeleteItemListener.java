package edu.ualberta.med.biobank.widgets.trees.infos.listener;

public interface IInfoTreeDeleteItemListener<T> {
    void deleteItem(InfoTreeEvent<T> event);
}
