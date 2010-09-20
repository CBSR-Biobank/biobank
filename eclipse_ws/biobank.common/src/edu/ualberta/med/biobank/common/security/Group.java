package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;

public class Group implements Serializable {
    public static final String GROUP_NAME_WEBSITE_ADMINISTRATOR = "Website Administrator";
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    public Group() {

    }

    public Group(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean isWebsiteAdministrator() {
        return name != null && name.equals(GROUP_NAME_WEBSITE_ADMINISTRATOR);
    }

    @Override
    public String toString() {
        return getId() + "/" + getName();
    }

}
