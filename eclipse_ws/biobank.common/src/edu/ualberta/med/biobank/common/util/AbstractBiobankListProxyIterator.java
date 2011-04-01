package edu.ualberta.med.biobank.common.util;

import java.util.Iterator;

public class AbstractBiobankListProxyIterator implements Iterator<Object> {
    AbstractBiobankListProxy<?> proxy;
    int index;

    public AbstractBiobankListProxyIterator(
        AbstractBiobankListProxy<?> abstractBiobankListProxy) {
        this.proxy = abstractBiobankListProxy;
        this.index = -1;
    }

    @Override
    public boolean hasNext() {
        return proxy.get(index + 1) != null;
    }

    @Override
    public Object next() {
        return proxy.get(++index);
    }

    @Override
    public void remove() {
    }

}
