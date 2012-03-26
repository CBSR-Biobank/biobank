package edu.ualberta.med.biobank.model;

import java.util.Collection;
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

@Entity
@Table(name = "DOMAIN")
public class Domain extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Set<Center> centers = new HashSet<Center>(0);
    private Set<Study> studies = new HashSet<Study>(0);
    private boolean allCenters = false;
    private boolean allStudies = false;

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
    public boolean isAllCenters() {
        return allCenters;
    }

    public void setAllCenters(boolean allCenters) {
        this.allCenters = allCenters;
    }

    @Column(name = "ALL_STUDIES")
    public boolean isAllStudies() {
        return allStudies;
    }

    public void setAllStudies(boolean allStudies) {
        this.allStudies = allStudies;
    }

    @Transient
    public boolean isGlobal() {
        return isAllCenters() && isAllStudies();
    }

    @Transient
    public boolean isSuperset(Domain that) {
        boolean allCenters = containsAllCenters(that.getCenters());
        boolean allStudies = containsAllStudies(that.getStudies());
        return allCenters && allStudies;
    }

    @Transient
    public boolean contains(Center center) {
        return isAllCenters() || getCenters().contains(center);
    }

    @Transient
    public boolean containsAllCenters(Collection<Center> centers) {
        return isAllCenters() || getCenters().containsAll(centers);
    }

    @Transient
    public boolean contains(Study study) {
        return isAllStudies() || getStudies().contains(study);
    }

    @Transient
    public boolean containsAllStudies(Collection<Study> studies) {
        return isAllStudies() || getStudies().containsAll(studies);
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
