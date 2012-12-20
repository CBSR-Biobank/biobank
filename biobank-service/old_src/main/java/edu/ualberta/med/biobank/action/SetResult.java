package edu.ualberta.med.biobank.action;

import java.util.Set;

public class SetResult<E> implements ActionResult {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Set<E> set;

    public SetResult(Set<E> set) {
        this.set = set;
    }

    public Set<E> getSet() {
        return set;
    }

}
