package edu.ualberta.med.biobank.model;

import java.util.Set;

public interface HasComments {
    public Set<Comment> getComments();

    public void setComments(Set<Comment> comments);
}
