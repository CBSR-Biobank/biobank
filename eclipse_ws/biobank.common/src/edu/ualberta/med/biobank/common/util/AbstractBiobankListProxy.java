package edu.ualberta.med.biobank.common.util;

import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.ApplicationService;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Semaphore;

import org.springframework.util.Assert;

/**
 * ListProxy with transparent paging: <br>
 * <ul>
 * <li>Read only</li><br>
 * <li>Non-searchable</li><br>
 * </ul>
 */
public abstract class AbstractBiobankListProxy<E> implements List<E>,
    Serializable {

    private static final long serialVersionUID = 1L;

    protected List<Object> listChunk;
    protected List<Object> nextListChunk;
    protected int pageSize;
    protected int offset;
    protected int nextOffset;
    protected int realSize;
    protected transient ApplicationService appService;

    protected int loadedOffset;

    private IBusyListener listener;
    private Semaphore semaphore = new Semaphore(1);

    public AbstractBiobankListProxy(ApplicationService appService) {
        this.offset = 0;
        this.nextOffset = 1000;
        this.loadedOffset = -2000;
        this.pageSize = appService.getMaxRecordsCount();
        this.appService = appService;
        this.realSize = -1;
    }

    protected abstract List<Object> getChunk(Integer firstRow)
        throws ApplicationException;

    @Override
    public boolean add(Object e) {
        return false;
    }

    @Override
    public void add(int index, Object element) {
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        return false;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
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

    @SuppressWarnings("unchecked")
    @Override
    public E get(int index) {
        init();
        Assert.isTrue(index >= 0);
        updateListChunk(index);
        if (listChunk.size() > 0 && listChunk.size() > index - offset)
            return getRowObject((E) listChunk.get(index - offset));
        else
            return null;
    }

    private void updateListChunk(int index) {
        if (index - offset >= pageSize || index < offset) {
            if (index < loadedOffset + pageSize && index >= loadedOffset) {
                // swap
                if (semaphore.availablePermits() == 0) {
                    if (listener != null)
                        listener.showBusy();
                    try {
                        semaphore.acquire();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    if (listener != null)
                        listener.done();
                    semaphore.release();
                }
                if (nextListChunk != null) {
                    List<Object> temp = listChunk;
                    listChunk = nextListChunk;
                    nextListChunk = temp;
                    int tempOffset = loadedOffset;
                    loadedOffset = offset;
                    offset = tempOffset;
                }
            } else {
                // user loading out of order, do a query on demand
                try {
                    offset = (index / pageSize) * pageSize;
                    listChunk = getChunk(offset);
                    // add cancel support
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
        if (!semaphore.tryAcquire()) {
            return;
        }

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
                        nextListChunk = getChunk(nextOffset);
                        if (nextListChunk.size() != 1000 && realSize == -1)
                            realSize = nextOffset + nextListChunk.size();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    semaphore.release();
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
        init();
        return listChunk.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return new AbstractBiobankListProxyIterator<E>(this);
    }

    @Override
    public int lastIndexOf(Object o) {
        return -1;
    }

    @Override
    public ListIterator<E> listIterator() {
        return null;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        return null;
    }

    @Override
    public boolean remove(Object o) {
        return false;
    }

    @Override
    public E remove(int index) {
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
    public E set(int index, Object element) {
        return null;
    }

    @Override
    public int size() {
        return -1;
    }

    public int getRealSize() {
        return realSize;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        init();
        assert (fromIndex >= 0 && toIndex >= 0);
        assert (fromIndex <= toIndex);
        updateListChunk(fromIndex);
        List<E> subList = new ArrayList<E>();
        for (Object o : listChunk.subList(fromIndex - offset,
            Math.min(listChunk.size(), toIndex - offset))) {
            subList.add(getRowObject((E) o));
        }
        if (offset + pageSize < toIndex && listChunk.size() == pageSize) {
            subList.addAll(subList(offset + pageSize, toIndex));
        }
        return subList;
    }

    protected E getRowObject(E object) {
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
        init();
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

    public AbstractBiobankListProxy<?> init() {
        if (listChunk == null) {
            updateListChunk(-1);
        }

        return this;
    }
}