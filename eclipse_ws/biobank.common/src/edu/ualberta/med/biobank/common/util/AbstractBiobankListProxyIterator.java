package edu.ualberta.med.biobank.common.util;

import java.util.Iterator;

public class AbstractBiobankListProxyIterator<E> implements Iterator<E> {
    AbstractBiobankListProxy<E> proxy;
    int index;

    public AbstractBiobankListProxyIterator(
        AbstractBiobankListProxy<E> abstractBiobankListProxy) {
        this.proxy = abstractBiobankListProxy;
        this.index = -1;
    }

    @Override
    public boolean hasNext() {
        return proxy.get(index + 1) != null;
    }

    @Override
    public E next() {
        return proxy.get(++index);
    }

    @Override
    public void remove() {
    }

}
