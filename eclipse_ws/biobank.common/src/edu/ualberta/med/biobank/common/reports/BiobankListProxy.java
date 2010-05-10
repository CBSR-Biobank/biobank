package edu.ualberta.med.biobank.common.reports;

import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.util.Assert;

//ListProxy with transparent paging
//Read only
//Non-searchable
//Non-iterable
public class BiobankListProxy implements List<Object> {

    private List<Object> listChunk;
    private int pageSize;
    private int offset;
    private ApplicationService appService;
    private HQLCriteria criteria;

    public BiobankListProxy(ApplicationService appService, HQLCriteria criteria) {
        this.appService = appService;
        this.offset = 0;
        this.pageSize = appService.getMaxRecordsCount();
        this.criteria = criteria;
        this.listChunk = updateListChunk(-1);
    }

    @Override
    public boolean add(Object e) {
        return false;
    }

    @Override
    public void add(int index, Object element) {
    }

    @Override
    public boolean addAll(Collection<? extends Object> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends Object> c) {
        return false;
    }

    @Override
    public void clear() {
    }

    @Override
    public boolean contains(Object o) {
        return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    @Override
    public Object get(int index) {
        Assert.isTrue(index >= 0);
        listChunk = updateListChunk(index);
        if (listChunk.size() > 0 && listChunk.size() > index - offset)
            return listChunk.get(index - offset);
        else
            return null;
    }

    private List<Object> updateListChunk(int index) {
        if (index - offset >= pageSize || index < offset) {
            offset = (index / pageSize) * pageSize;
            try {
                return appService.query(criteria, offset, Site.class.getName());
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
        }
        return listChunk;
    }

    @Override
    public int indexOf(Object o) {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return listChunk.isEmpty();
    }

    @Override
    public Iterator<Object> iterator() {
        return new BiobankListProxyIterator(this);
    }

    @Override
    public int lastIndexOf(Object o) {
        return -1;
    }

    @Override
    public ListIterator<Object> listIterator() {
        return null;
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
        return null;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public Object remove(int index) {
        return null;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public Object set(int index, Object element) {
        return null;
    }

    @Override
    public int size() {
        // bsearch was still too slow, hqlcounts too complicated
        // going to have to operate without size
        return -1;
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        Assert.isTrue(fromIndex >= 0 && toIndex > 0);
        Assert.isTrue(fromIndex < toIndex);
        listChunk = updateListChunk(fromIndex);
        List<Object> subList = new ArrayList<Object>();
        subList.addAll(listChunk.subList(fromIndex - offset, Math.min(listChunk
            .size(), Math.min(toIndex, offset + pageSize) - offset)));
        while (offset + pageSize < toIndex && listChunk.size() == pageSize) {
            listChunk = updateListChunk(offset + pageSize);
            subList.addAll(listChunk.subList(0, Math.min(listChunk.size(), Math
                .min(toIndex, offset + pageSize)
                - offset)));
        }
        return subList;
    }

    @Override
    public java.lang.Object[] toArray() {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

}