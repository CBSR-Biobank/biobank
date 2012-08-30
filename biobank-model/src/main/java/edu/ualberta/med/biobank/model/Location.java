package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.envers.Audited;
import org.hibernate.validator.constraints.Length;

/**
 * Represents physical or logical location that does not necessarily use this
 * software. Shared by {@link Center}s and {@link Study}s.
 * <p>
 * This is only really beneficial assuming that all {@link Center}s have at
 * least one {@link Location}, but not all {@link Location}s have a
 * {@link Center}.
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "LOCATION")
public class Location
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private String gLN;
    private Address address;

    @NotNull(message = "{Location.gLN.NotNull}")
    @Length(min = 13, max = 13, message = "{Location.gLN.Length}")
    @Column(name = "GLN", nullable = false, length = 13, unique = true)
    public String getGLN() {
        return gLN;
    }

    public void setGLN(String gLN) {
        this.gLN = gLN;
    }

    @Valid
    @Embedded
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }
}
