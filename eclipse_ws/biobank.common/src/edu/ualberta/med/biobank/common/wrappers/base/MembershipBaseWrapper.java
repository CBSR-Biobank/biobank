/*
 * This code is automatically generated. Please do not edit.
 */

package edu.ualberta.med.biobank.common.wrappers.base;

import java.util.List;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.peer.MembershipPeer;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.CenterBaseWrapper;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.PrincipalWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.PrincipalBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.StudyBaseWrapper;
import edu.ualberta.med.biobank.common.wrappers.RoleWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.RoleBaseWrapper;
import java.util.Arrays;

public class MembershipBaseWrapper extends ModelWrapper<Membership> {

    public MembershipBaseWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public MembershipBaseWrapper(WritableApplicationService appService,
        Membership wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    public final Class<Membership> getWrappedClass() {
        return Membership.class;
    }

    @Override
    public Property<Integer, ? super Membership> getIdProperty() {
        return MembershipPeer.ID;
    }

    @Override
    protected List<Property<?, ? super Membership>> getProperties() {
        return MembershipPeer.PROPERTIES;
    }

   @SuppressWarnings("unchecked")
    public CenterWrapper<?> getCenter() {
        boolean notCached = !isPropertyCached(MembershipPeer.CENTER);
        CenterWrapper <?>center = getWrappedProperty(MembershipPeer.CENTER, CenterWrapper.class);
        if (center != null && notCached) ((CenterBaseWrapper<?>) center).addToMembershipCollectionInternal(Arrays.asList(this));
        return center;
    }

    public void setCenter(CenterBaseWrapper<?> center) {
        if (isInitialized(MembershipPeer.CENTER)) {
            CenterBaseWrapper<?> oldCenter = getCenter();
            if (oldCenter != null) oldCenter.removeFromMembershipCollectionInternal(Arrays.asList(this));
        }
        if (center != null) center.addToMembershipCollectionInternal(Arrays.asList(this));
        setWrappedProperty(MembershipPeer.CENTER, center);
    }

    void setCenterInternal(CenterBaseWrapper<?> center) {
        setWrappedProperty(MembershipPeer.CENTER, center);
    }

   @SuppressWarnings("unchecked")
    public PrincipalWrapper<?> getPrincipal() {
        boolean notCached = !isPropertyCached(MembershipPeer.PRINCIPAL);
        PrincipalWrapper <?>principal = getWrappedProperty(MembershipPeer.PRINCIPAL, PrincipalWrapper.class);
        if (principal != null && notCached) ((PrincipalBaseWrapper<?>) principal).addToMembershipCollectionInternal(Arrays.asList(this));
        return principal;
    }

    public void setPrincipal(PrincipalBaseWrapper<?> principal) {
        if (isInitialized(MembershipPeer.PRINCIPAL)) {
            PrincipalBaseWrapper<?> oldPrincipal = getPrincipal();
            if (oldPrincipal != null) oldPrincipal.removeFromMembershipCollectionInternal(Arrays.asList(this));
        }
        if (principal != null) principal.addToMembershipCollectionInternal(Arrays.asList(this));
        setWrappedProperty(MembershipPeer.PRINCIPAL, principal);
    }

    void setPrincipalInternal(PrincipalBaseWrapper<?> principal) {
        setWrappedProperty(MembershipPeer.PRINCIPAL, principal);
    }

    public StudyWrapper getStudy() {
        boolean notCached = !isPropertyCached(MembershipPeer.STUDY);
        StudyWrapper study = getWrappedProperty(MembershipPeer.STUDY, StudyWrapper.class);
        if (study != null && notCached) ((StudyBaseWrapper) study).addToMembershipCollectionInternal(Arrays.asList(this));
        return study;
    }

    public void setStudy(StudyBaseWrapper study) {
        if (isInitialized(MembershipPeer.STUDY)) {
            StudyBaseWrapper oldStudy = getStudy();
            if (oldStudy != null) oldStudy.removeFromMembershipCollectionInternal(Arrays.asList(this));
        }
        if (study != null) study.addToMembershipCollectionInternal(Arrays.asList(this));
        setWrappedProperty(MembershipPeer.STUDY, study);
    }

    void setStudyInternal(StudyBaseWrapper study) {
        setWrappedProperty(MembershipPeer.STUDY, study);
    }

    public List<RoleWrapper> getRoleCollection(boolean sort) {
        List<RoleWrapper> roleCollection = getWrapperCollection(MembershipPeer.ROLES, RoleWrapper.class, sort);
        return roleCollection;
    }

    public void addToRoleCollection(List<? extends RoleBaseWrapper> roleCollection) {
        addToWrapperCollection(MembershipPeer.ROLES, roleCollection);
    }

    void addToRoleCollectionInternal(List<? extends RoleBaseWrapper> roleCollection) {
        if (isInitialized(MembershipPeer.ROLES)) {
            addToWrapperCollection(MembershipPeer.ROLES, roleCollection);
        } else {
            getElementQueue().add(MembershipPeer.ROLES, roleCollection);
        }
    }

    public void removeFromRoleCollection(List<? extends RoleBaseWrapper> roleCollection) {
        removeFromWrapperCollection(MembershipPeer.ROLES, roleCollection);
    }

    void removeFromRoleCollectionInternal(List<? extends RoleBaseWrapper> roleCollection) {
        if (isPropertyCached(MembershipPeer.ROLES)) {
            removeFromWrapperCollection(MembershipPeer.ROLES, roleCollection);
        } else {
            getElementQueue().remove(MembershipPeer.ROLES, roleCollection);
        }
    }

    public void removeFromRoleCollectionWithCheck(List<? extends RoleBaseWrapper> roleCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(MembershipPeer.ROLES, roleCollection);
    }

    void removeFromRoleCollectionWithCheckInternal(List<? extends RoleBaseWrapper> roleCollection) throws BiobankCheckException {
        removeFromWrapperCollectionWithCheck(MembershipPeer.ROLES, roleCollection);
    }

}
