package edu.ualberta.med.biobank.model;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Trnc;

@Entity
@Table(name = "DOMAIN")
public class Domain extends LongIdModel {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final Trnc NAME = bundle.trnc(
        "model",
        "Domain",
        "Domains");

    @SuppressWarnings("nls")
    public static class PropertyName {
        public static final LString ALL_CENTERS = bundle.trc(
            "model",
            "All Centers").format();
        public static final LString ALL_STUDIES = bundle.trc(
            "model",
            "All Studies").format();
    }

    private Set<Center> centers = new HashSet<Center>(0);
    private Set<Study> studies = new HashSet<Study>(0);
    private Boolean allCenters;
    private Boolean allStudies;

    public Domain() {
    }

    public Domain(Domain domain) {
        getCenters().addAll(domain.getCenters());
        getStudies().addAll(domain.getStudies());

        setAllCenters(domain.isAllCenters());
        setAllStudies(domain.isAllStudies());
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "DOMAIN_CENTER",
        joinColumns = { @JoinColumn(name = "DOMAIN_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CENTER_ID", nullable = false, updatable = false) })
    public Set<Center> getCenters() {
        return centers;
    }

    public void setCenters(Set<Center> centers) {
        this.centers = centers;
    }

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "DOMAIN_STUDY",
        joinColumns = { @JoinColumn(name = "DOMAIN_ID", nullable = false, updatable = false) },
        inverseJoinColumns = { @JoinColumn(name = "CENTER_ID", nullable = false, updatable = false) })
    public Set<Study> getStudies() {
        return studies;
    }

    public void setStudies(Set<Study> studies) {
        this.studies = studies;
    }

    @Column(name = "ALL_CENTERS")
    public Boolean isAllCenters() {
        return allCenters;
    }

    public void setAllCenters(Boolean allCenters) {
        this.allCenters = allCenters;
        if (allCenters) getCenters().clear();
    }

    @Column(name = "ALL_STUDIES")
    public Boolean isAllStudies() {
        return allStudies;
    }

    public void setAllStudies(Boolean allStudies) {
        this.allStudies = allStudies;
        if (allStudies) getStudies().clear();
    }

    @Transient
    public boolean isGlobal() {
        return Boolean.TRUE.equals(isAllCenters())
            && Boolean.TRUE.equals(isAllStudies());
    }

    @Transient
    public boolean isSuperset(Domain that) {
        boolean allCenters = containsAllCenters(that);
        boolean allStudies = containsAllStudies(that);
        return allCenters && allStudies;
    }

    @Transient
    public boolean contains(Center center) {
        return Boolean.TRUE.equals(isAllCenters())
            || getCenters().contains(center);
    }

    /**
     * Done on a {@link Domain} instead of a set of {@link Center}-s because if
     * the given {@link Domain} returns true for {@link #isAllCenters()} but has
     * an empty set from {@link #getCenters()}, then that is very misleading.
     * 
     * @param domain
     * @return
     */
    @Transient
    public boolean containsAllCenters(Domain that) {
        if (Boolean.TRUE.equals(isAllCenters())) return true;
        return !Boolean.TRUE.equals(that.isAllCenters())
            && getCenters().containsAll(that.getCenters());
    }

    @Transient
    public boolean contains(Study study) {
        return Boolean.TRUE.equals(isAllStudies())
            || getStudies().contains(study);
    }

    @Transient
    public boolean containsAllStudies(Domain that) {
        if (Boolean.TRUE.equals(isAllStudies())) return true;
        return !Boolean.TRUE.equals(that.isAllStudies())
            && getStudies().containsAll(that.getStudies());
    }

    @Transient
    public boolean isEquivalent(Domain that) {
        boolean equivalent = true;
        equivalent &= isAllCenters() == that.isAllCenters();
        equivalent &= isAllStudies() == that.isAllStudies();
        equivalent &= getCenters().equals(that.getCenters());
        equivalent &= getStudies().equals(that.getStudies());
        return equivalent;
    }
}
