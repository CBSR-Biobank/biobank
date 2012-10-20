package edu.ualberta.med.biobank.model.study;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import edu.ualberta.med.biobank.model.LongIdModel;
import edu.ualberta.med.biobank.model.security.User;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;

@Audited
@Entity
@Table(name = "REQUEST_SPECIMEN")
public class RequestSpecimen extends LongIdModel {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private Request request;
    private RequestSpecimenState state;
    private User claimedBy;

    @NotNull(message = "{RequestSpecimen.specimen.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SPECIMEN_ID", nullable = false)
    public Specimen getSpecimen() {
        return this.specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @NotNull(message = "{RequestSpecimen.request.NotNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUEST_ID", nullable = false)
    public Request getRequest() {
        return this.request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }

    @NotNull(message = "{RequestSpecimen.state.NotNull}")
    @Column(name = "STATE", nullable = false)
    @Type(type = "requestSpecimenState")
    public RequestSpecimenState getState() {
        return this.state;
    }

    public void setState(RequestSpecimenState state) {
        this.state = state;
    }

    @NotAudited
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CLAIMED_BY_USER_ID")
    public User getClaimedBy() {
        return this.claimedBy;
    }

    public void setClaimedBy(User claimedBy) {
        this.claimedBy = claimedBy;
    }
}
