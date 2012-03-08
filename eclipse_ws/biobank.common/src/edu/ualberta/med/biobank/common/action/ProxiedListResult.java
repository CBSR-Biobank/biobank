package edu.ualberta.med.biobank.common.action;

import edu.ualberta.med.biobank.common.util.AbstractBiobankListProxy;

public class ProxiedListResult<E> implements ActionResult {
    private static final long serialVersionUID = 1L;
    private AbstractBiobankListProxy<E> list;

    public ProxiedListResult(AbstractBiobankListProxy<E> list) {
        this.list = list;
    }

    public AbstractBiobankListProxy<E> getList() {
        return list;
    }
}
