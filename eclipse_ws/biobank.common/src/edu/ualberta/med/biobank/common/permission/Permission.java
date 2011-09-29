package edu.ualberta.med.biobank.common.permission;

import java.io.Serializable;

import edu.ualberta.med.biobank.model.User;

public interface Permission extends Serializable {
    public boolean isAllowed(User user);
}
