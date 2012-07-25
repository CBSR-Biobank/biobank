package edu.ualberta.med.biobank.action;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ListResult<E> implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final ArrayList<E> list;

    public ListResult(List<E> list) {
        this.list = new ArrayList<E>(list);
    }

    public ListResult(Collection<E> list) {
        this.list = new ArrayList<E>(list);
    }

    public ArrayList<E> getList() {
        return list;
    }
}
