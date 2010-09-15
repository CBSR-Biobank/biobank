package edu.ualberta.med.biobank.common.util;

import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.springframework.util.Assert;

/**
 * ListProxy with transparent paging: <br>
 * <ul>
 * <li>Read only</li><br>
 * <li>Non-searchable</li><br>
 * </ul>
 */
public class BiobankListProxy implements List<Object>, Serializable {

    private static final long serialVersionUID = 1L;

    protected List<Object> listChunk;
    protected List<Object> nextListChunk;
    protected int pageSize;
    protected int offset;
    protected int nextOffset;
    protected int realSize;
    protected transient ApplicationService appService;
    protected HQLCriteria criteria;

    protected int loadedOffset;

    protected boolean loading;

    private IBusyListener listener;

    public BiobankListProxy(ApplicationService appService, HQLCriteria criteria) {
        this.appService = appService;
        this.offset = 0;
        this.nextOffset = 1000;
        this.loadedOffset = -2000;
        this.pageSize = appService.getMaxRecordsCount();
        this.criteria = criteria;
        this.realSize = -1;
        updateListChunk(-1);
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
        updateListChunk(index);
        if (listChunk.size() > 0 && listChunk.size() > index - offset)
            return getRowObject(listChunk.get(index - offset));
        else
            return null;
    }

    private void updateListChunk(int index) {
        if (index - offset >= pageSize || index < offset) {
            if (index < loadedOffset + pageSize) {
                // swap
                if (loading) {
                    if (listener != null)
                        listener.showBusy();
                    while (loading) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            break;
                        }
                    }
                    if (listener != null)
                        listener.done();
                }
                System.out.println(loading);
                System.out.println(listener);
                if (nextListChunk != null) {
                    List<Object> temp = listChunk;
                    listChunk = nextListChunk;
                    nextListChunk = temp;
                    int tempOffset = loadedOffset;
                    loadedOffset = offset;
                    offset = tempOffset;
                } else
                    System.out.println(" null swap avoided");
            } else {
                // user loading out of order, do a query on demand
                try {
                    offset = (index / pageSize) * pageSize;
                    listChunk = appService.query(criteria, offset,
                        Site.class.getName());
                    if (listChunk.size() != 1000 && realSize == -1)
                        realSize = offset + listChunk.size();
                } catch (ApplicationException e) {
                    throw new RuntimeException(e);
                }
            }
        } else
            preLoadList(index);
    }

    private void preLoadList(final int i) {
        if ((i - offset) > (pageSize / 2)) {
            nextOffset = offset + pageSize;
        } else
            nextOffset = offset - pageSize;
        if (loadedOffset != nextOffset && nextOffset >= 0) {
            loadedOffset = nextOffset;
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        loading = true;
                        nextListChunk = appService.query(criteria, nextOffset,
                            Site.class.getName());
                        if (nextListChunk.size() != 1000 && realSize == -1)
                            realSize = nextOffset + nextListChunk.size();
                        loading = false;
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            t.start();
        }
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
        return -1;
    }

    public int getRealSize() {
        return realSize;
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
        Assert.isTrue(fromIndex >= 0 && toIndex >= 0);
        Assert.isTrue(fromIndex <= toIndex);
        updateListChunk(fromIndex);
        List<Object> subList = new ArrayList<Object>();
        for (Object o : listChunk.subList(fromIndex - offset,
            Math.min(listChunk.size(), toIndex - offset))) {
            subList.add(getRowObject(o));
        }
        if (offset + pageSize < toIndex && listChunk.size() == pageSize) {
            subList.addAll(subList(offset + pageSize, toIndex));
        }
        return subList;
    }

    protected Object getRowObject(Object object) {
        return object;
    }

    @Override
    public java.lang.Object[] toArray() {
        return null;
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return null;
    }

    public void setAppService(ApplicationService as) {
        this.appService = as;
    }

    /**
     * Used in BiobankProxyHelperImpl
     */
    public List<Object> getListChunk() {
        return listChunk;
    }

    /**
     * Used in BiobankProxyHelperImpl
     */
    public void setListChunk(List<Object> listChunk) {
        this.listChunk = listChunk;
    }

    public void addBusyListener(IBusyListener l) {
        this.listener = l;
    }
}