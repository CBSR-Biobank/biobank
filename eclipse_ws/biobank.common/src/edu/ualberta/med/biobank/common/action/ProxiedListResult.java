package edu.ualberta.med.biobank.common.action;

import java.util.List;

public class ProxiedListResult<E> implements ActionResult {
    private static final long serialVersionUID = 1L;
    private List<E> list;

    public ProxiedListResult(List<E> list) {
        this.list = list;
    }

    public List<E> getList() {
        return list;
    }
}
