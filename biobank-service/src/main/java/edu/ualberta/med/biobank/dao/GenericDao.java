package edu.ualberta.med.biobank.dao;

import java.util.List;

import edu.ualberta.med.biobank.model.VersionedLongIdModel;

public interface GenericDao<T extends VersionedLongIdModel> {

    public T get(Long id);

    public List<T> getAll();

    public void save(T object);

    public void delete(T object);

    public void indexEntity(T object);

    public void indexAllItems();

}