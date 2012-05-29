package edu.ualberta.med.biobank.common.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DelegatingList<E> extends AbstractListChangeSource<E>
    implements List<E> {
    private final ModifierChangeHandler modifierChangeHandler =
        new ModifierChangeHandler();
    private final List<ListChangeSource<E>> modifiers =
        new ArrayList<ListChangeSource<E>>();
    private List<E> delegate;

    public DelegatingList() {
        this(new ArrayList<E>());
    }

    public DelegatingList(List<E> delegate) {
        setDelegate(delegate);
    }

    public void setDelegate(List<E> delegate) {
        if (delegate == null) {
            throw new IllegalArgumentException("delegate is required"); //$NON-NLS-1$
        }

        // removed this code: you cannot be certain your collection hasn't
        // changed, even if it is the same collection
        // if (this == delegate || this.delegate == delegate) {
        // return;
        // }

        clearModifiers();
        this.delegate = delegate;
        fireListChangeEvent();
    }

    @Override
    public boolean add(E e) {
        boolean result = delegate.add(e);
        fireListChangeEvent();
        return result;
    }

    @Override
    public void add(int index, E element) {
        delegate.add(index, element);
        fireListChangeEvent();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean changed = delegate.addAll(c);
        fireListChangeEvent();
        return changed;
    }

    @Override
    public boolean addAll(int index, Collection<? extends E> c) {
        boolean changed = delegate.addAll(index, c);
        fireListChangeEvent();
        return changed;
    }

    @Override
    public void clear() {
        delegate.clear();
        fireListChangeEvent();
    }

    @Override
    public boolean contains(Object o) {
        return delegate.contains(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return delegate.containsAll(c);
    }

    @Override
    public E get(int index) {
        return delegate.get(index);
    }

    @Override
    public int indexOf(Object o) {
        return delegate.indexOf(o);
    }

    @Override
    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    @Override
    public Iterator<E> iterator() {
        ListChangeSourceIterator<E> iterator = new ListChangeSourceIterator<E>(
            delegate.iterator());
        iterator.addListChangeHandler(modifierChangeHandler);
        modifiers.add(iterator);
        return iterator;
    }

    @Override
    public int lastIndexOf(Object o) {
        return delegate.lastIndexOf(o);
    }

    @Override
    public ListIterator<E> listIterator() {
        ListChangeSourceListIterator<E> iterator =
            new ListChangeSourceListIterator<E>(
                delegate.listIterator());
        iterator.addListChangeHandler(modifierChangeHandler);
        modifiers.add(iterator);
        return iterator;
    }

    @Override
    public ListIterator<E> listIterator(int index) {
        ListChangeSourceListIterator<E> iterator =
            new ListChangeSourceListIterator<E>(
                delegate.listIterator(index));
        iterator.addListChangeHandler(modifierChangeHandler);
        modifiers.add(iterator);
        return iterator;
    }

    @Override
    public boolean remove(Object o) {
        boolean changed = delegate.remove(o);
        fireListChangeEvent();
        return changed;
    }

    @Override
    public E remove(int index) {
        E removed = delegate.remove(index);
        fireListChangeEvent();
        return removed;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        boolean changed = delegate.removeAll(c);
        fireListChangeEvent();
        return changed;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        boolean changed = delegate.retainAll(c);
        fireListChangeEvent();
        return changed;
    }

    @Override
    public E set(int index, E element) {
        E previous = delegate.set(index, element);
        fireListChangeEvent();
        return previous;
    }

    @Override
    public int size() {
        return delegate.size();
    }

    @Override
    public List<E> subList(int fromIndex, int toIndex) {
        List<E> view = delegate.subList(fromIndex, toIndex);

        DelegatingList<E> delegatingView = new DelegatingList<E>(view);
        delegatingView.addListChangeHandler(modifierChangeHandler);
        modifiers.add(delegatingView);

        return delegatingView;
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return delegate.toArray(a);
    }

    private static class ListChangeSourceIterator<E> extends
        AbstractListChangeSource<E>
        implements Iterator<E> {
        private final Iterator<E> iterator;

        private ListChangeSourceIterator(Iterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public E next() {
            return iterator.next();
        }

        @Override
        public void remove() {
            iterator.remove();
            fireListChangeEvent();
        }
    }

    private static class ListChangeSourceListIterator<E> extends
        AbstractListChangeSource<E>
        implements ListIterator<E> {
        private final ListIterator<E> iterator;

        private ListChangeSourceListIterator(ListIterator<E> iterator) {
            this.iterator = iterator;
        }

        @Override
        public void add(E e) {
            iterator.add(e);
            fireListChangeEvent();
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public boolean hasPrevious() {
            return iterator.hasPrevious();
        }

        @Override
        public E next() {
            return iterator.next();
        }

        @Override
        public int nextIndex() {
            return iterator.nextIndex();
        }

        @Override
        public E previous() {
            return iterator.previous();
        }

        @Override
        public int previousIndex() {
            return iterator.previousIndex();
        }

        @Override
        public void remove() {
            iterator.remove();
            fireListChangeEvent();
        }

        @Override
        public void set(E e) {
            iterator.set(e);
            fireListChangeEvent();
        }
    }

    private void clearModifiers() {
        for (ListChangeSource<E> modifier : modifiers) {
            modifier.removeListChangeHandler(modifierChangeHandler);
        }

        modifiers.clear();
    }

    private class ModifierChangeHandler implements ListChangeHandler<E> {
        @Override
        public void onListChange(ListChangeEvent<E> event) {
            fireListChangeEvent();
        }
    }
}
