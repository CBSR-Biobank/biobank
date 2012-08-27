package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@Entity
@Table(name = "DISPATCH_SPECIMEN")
public class DispatchSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Dispatched Specimen",
        "Dispatched Specimens");

    @SuppressWarnings("nls")
    public static class Property {
        public static final LString STATE = bundle.trc(
            "model",
            "State").format();
    }

    private Integer state;
    private Dispatch dispatch;
    private Specimen specimen;
    private Set<Comment> comments = new HashSet<Comment>(0);

    // TODO: convert to enum
    @NotNull(message = "{edu.ualberta.med.biobank.model.DispatchSpecimen.state.NotNull}")
    @Column(name = "STATE")
    public Integer getState() {
        return this.state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.DispatchSpecimen.dispatch.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DISPATCH_ID", nullable = false)
    public Dispatch getDispatch() {
        return this.dispatch;
    }

    public void setDispatch(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    @NotNull(message = "{edu.ualberta.med.biobank.model.DispatchSpecimen.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return this.specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @ManyToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinTable(name = "DISPATCH_SPECIMEN_COMMENT",
        joinColumns = { @JoinColumn(name = "DISPATCH_SPECIMEN_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "COMMENT_ID", unique = true, nullable = false, updatable = false) })
    public Set<Comment> getComments() {
        return this.comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }
}
