package edu.ualberta.med.biobank.model;

import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.security.User;

public interface HasInsertedBy {
    /**
     * @return the {@link User} that first persisted the implementing instance.
     */
    @NotNull(message = "{HasInsertedBy.insertedBy.NotNull}")
    public User getInsertedBy();

    public void setInsertedBy(User insertedBy);
}
