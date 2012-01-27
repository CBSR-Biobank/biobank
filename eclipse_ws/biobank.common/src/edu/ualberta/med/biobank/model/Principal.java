package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

public class Principal extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Collection<Membership> membershipCollection =
        new HashSet<Membership>();

    public Collection<Membership> getMembershipCollection() {
        return membershipCollection;
    }

    public void setMembershipCollection(
        Collection<Membership> membershipCollection) {
        this.membershipCollection = membershipCollection;
    }
}
