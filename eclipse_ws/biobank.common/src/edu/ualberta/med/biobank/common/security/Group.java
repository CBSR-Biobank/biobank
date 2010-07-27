package edu.ualberta.med.biobank.common.security;

import java.io.Serializable;

public class Group implements Serializable {

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

}
