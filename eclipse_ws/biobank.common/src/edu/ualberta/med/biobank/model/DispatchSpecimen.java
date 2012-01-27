package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class DispatchSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer state;
    private Dispatch dispatch;
    private Specimen specimen;
    private Collection<Comment> commentCollection = new HashSet<Comment>();

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public Dispatch getDispatch() {
        return dispatch;
    }

    public void setDispatch(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public Collection<Comment> getCommentCollection() {
        return commentCollection;
    }

    public void setCommentCollection(Collection<Comment> commentCollection) {
        this.commentCollection = commentCollection;
    }
}
