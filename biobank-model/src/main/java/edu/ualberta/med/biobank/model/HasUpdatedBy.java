package edu.ualberta.med.biobank.model;

import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.model.security.User;

public interface HasUpdatedBy {
    /**
     * @return the {@link User} that last persisted the implementing instance.
     */
    @NotNull(message = "{HasUpdatedBy.updatedBy.NotNull}")
    public User getUpdatedBy();

    public void setUpdatedBy(User updatedBy);
}
