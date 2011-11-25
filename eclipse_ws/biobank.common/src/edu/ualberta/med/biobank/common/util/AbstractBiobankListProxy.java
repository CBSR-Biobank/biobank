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

    private static enum Shown {
        BUSY,
        DONE
    };

    private Shown lastShown = Shown.DONE;

    private static final long serialVersionUID = 1L;
    private static final int REAL_SIZE_UNKNOWN = -1;

    protected final int pageSize;
    private final Semaphore loadingNextPage = new Semaphore(1);

    protected transient ApplicationService appService;
    private Page<Object> page;
    private Page<Object> nextPage;
    private int realSize;
    private IBusyListener listener;

    public AbstractBiobankListProxy(ApplicationService appService) {
        page = new Page<Object>();
        nextPage = new Page<Object>();

        pageSize = appService.getMaxRecordsCount();
        this.appService = appService;

        this.realSize = REAL_SIZE_UNKNOWN;
    }

    protected abstract List<Object> getChunk(Integer firstRow)
        throws ApplicationException;

    @Override
    public boolean add(Object e) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void add(int index, Object element) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean contains(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E get(int index) {
        init();
        Assert.isTrue(index >= 0);
        updateListChunk(index);

        Object element = page.get(index);
        if (element != null) {
            @SuppressWarnings("unchecked")
            E tmp = (E) element;
            return getRowObject(tmp);
        }
        return null;
    }

    private void swapPages() {
        Page<Object> tmp = nextPage;
        nextPage = page;
        page = tmp;
    }

    private void updateRealSize(Page<?> page) {
        if (page.list.size() < pageSize && realSize == REAL_SIZE_UNKNOWN) {
            realSize = page.offset + page.list.size();
        }
    }

    /**
     * Loads a chunk into the given page, starting from the given offset.
     * 
     * @param offset
     * @param page
     */
    private void loadChunk(int offset, Page<Object> page)
        throws ApplicationException {
        page.list = getChunk(offset);
        page.offset = offset;

        updateRealSize(page);
    }

    private void showBusy() {
        if (lastShown != Shown.BUSY && listener != null) {
            listener.showBusy();
            lastShown = Shown.BUSY;
        }
    }

    private void showDone() {
        if (lastShown != Shown.DONE && listener != null) {
            listener.done();
            lastShown = Shown.DONE;
        }
    }

    private void updateListChunk(int index) {
        if (!page.hasElement(index)) {
            boolean acquired = loadingNextPage.tryAcquire();
            if (!acquired || !nextPage.hasElement(index)) {
                showBusy();
            }

            try {
                if (!acquired)
                    loadingNextPage.acquire();
                if (!nextPage.hasElement(index)) {
                    int offset = (index / pageSize) * pageSize;
                    loadChunk(offset, page);
                } else {
                    swapPages();
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            } finally {
                showDone();
                loadingNextPage.release();
            }

        } else
            preLoadList(index);
    }

    private void preLoadList(final int i) {
        if (!loadingNextPage.tryAcquire()) {
            return;
        }

        int nextOffset;
        if ((i - page.offset) > (pageSize / 2)) {
            nextOffset = page.offset + pageSize;
        } else
            nextOffset = page.offset - pageSize;

        boolean alreadyLoaded = nextPage.offset != null
            && nextPage.offset.equals(nextOffset);
        boolean afterStart = nextOffset >= 0;
        boolean beforeEnd =
            (realSize == REAL_SIZE_UNKNOWN || nextOffset < realSize);

        if (!alreadyLoaded && afterStart && beforeEnd) {
            final int finalNextOffset = nextOffset;
            Thread t = new Thread() {
                @Override
                public void run() {
                    try {
                        loadChunk(finalNextOffset, nextPage);
                    } catch (ApplicationException e) {
                        throw new RuntimeException(e);
                    } finally {
                        loadingNextPage.release();
                    }
                }
            };
            t.start();
        } else {
            loadingNextPage.release();
        }
    }

    @Override
    public int indexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isEmpty() {
        init();
        return page.list.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        return new AbstractBiobankListProxyIterator<E>(this);
    }

    @Override
    public int lastIndexOf(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E remove(int index) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    @Override
    public E set(int index, Object element) {
        throw new UnsupportedOperationException();
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
        List<E> subList = new ArrayList<E>(toIndex - fromIndex);

        // for (int i = fromIndex; i < toIndex; i++) {
        // subList.add(get(i));
        // }

        for (Object o : page.list.subList(fromIndex - page.offset,
            Math.min(page.list.size(), toIndex - page.offset))) {
            subList.add(getRowObject((E) o));
        }
        if (page.offset + pageSize < toIndex && page.list.size() == pageSize) {
            subList.addAll(subList(page.offset + pageSize, toIndex));
        }

        return subList;
    }

    protected E getRowObject(E object) {
        return object;
    }

    @Override
    public java.lang.Object[] toArray() {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        throw new UnsupportedOperationException();
    }

    public void setAppService(ApplicationService as) {
        this.appService = as;
    }

    // TODO: rename to "setBusyListener"?
    public void addBusyListener(IBusyListener l) {
        this.listener = l;
    }

    public AbstractBiobankListProxy<?> init() {
        if (!page.isInitialized()) {
            updateListChunk(0);
        }

        return this;
    }

    private class Page<V> implements Serializable, NotAProxy {

        private static final long serialVersionUID = 7230323011529770077L;
        public Integer offset;
        public List<V> list;

        /**
         * 
         * @param index
         * @return true if this page could hold the given index, otherwise
         *         false.
         */
        public boolean hasElement(int index) {
            return isInitialized() && index - offset < pageSize
                && index >= offset;
        }

        /**
         * 
         * @param index
         * @return the element at the given index, otherwise null.
         */
        public V get(int index) {
            V result = null;

            if (offset != null && list != null) {
                if (list.size() > 0 && list.size() > index - offset) {
                    result = list.get(index - offset);
                }
            }

            return result;
        }

        public boolean isInitialized() {
            return offset != null && list != null;
        }
    }
}