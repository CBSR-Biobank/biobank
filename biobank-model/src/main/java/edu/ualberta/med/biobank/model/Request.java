package edu.ualberta.med.biobank.model;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;
import edu.ualberta.med.biobank.validator.group.PreDelete;

/**
 * caTissue Term - Specimen Distribution: An event that results in transfer of a
 * specimen from a Repository to a Laboratory
 * 
 */
@Audited
@Entity
@Table(name = "REQUEST")
public class Request extends AbstractModel
    implements HasCreatedAt, HasAddress {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Request",
        "Requests");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString CREATED = bundle.trc(
            "model",
            "Time Created").format();
        public static final LString SUBMITTED = bundle.trc(
            "model",
            "Time Submitted").format();
    }

    private Date submitted;
    private Date created;
    private Set<Dispatch> dispatches = new HashSet<Dispatch>(0);
    private Address address = new Address();
    private Study study;

    @Null(groups = PreDelete.class, message = "{Request.submitted.Null}")
    @Column(name = "SUBMITTED")
    public Date getSubmitted() {
        return this.submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    @Override
    @NotNull(message = "{Request.created.NotNull}")
    @Column(name = "CREATED", nullable = false)
    // TODO: rename column to CREATED_AT?
    public Date getCreatedAt() {
        return this.created;
    }

    @Override
    public void setCreatedAt(Date created) {
        this.created = created;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "REQUEST_DISPATCH",
        joinColumns = { @JoinColumn(name = "REQUEST_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "DISPATCH_ID", unique = true, nullable = false, updatable = false) })
    public Set<Dispatch> getDispatches() {
        return this.dispatches;
    }

    public void setDispatches(Set<Dispatch> dispatches) {
        this.dispatches = dispatches;
    }

    @Override
    @NotNull(message = "{Request.address.NotNull}")
    @Embedded
    public Address getAddress() {
        return this.address;
    }

    @Override
    public void setAddress(Address address) {
        this.address = address;
    }

    @NotNull(message = "{Request.study.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "STUDY_ID", nullable = false)
    public Study getStudy() {
        return this.study;
    }

    public void setStudy(Study study) {
        this.study = study;
    }
}
