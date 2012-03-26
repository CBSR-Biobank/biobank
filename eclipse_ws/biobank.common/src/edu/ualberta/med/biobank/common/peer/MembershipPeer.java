package edu.ualberta.med.biobank.common.peer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.util.TypeReference;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.model.Membership;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Principal;
import edu.ualberta.med.biobank.model.Role;

public class MembershipPeer {
    public static final Property<Integer, Membership> ID = Property.create("id" //$NON-NLS-1$
        , Membership.class
        , new TypeReference<Integer>() {
        }
        , new Property.Accessor<Integer, Membership>() {
            private static final long serialVersionUID = 1L;

            @Override
            public Integer get(Membership model) {
                return model.getId();
            }

            @Override
            public void set(Membership model, Integer value) {
                model.setId(value);
            }
        });

    public static final Property<Set<PermissionEnum>, Membership> PERMISSIONS =
        Property.create("permissions" //$NON-NLS-1$
            , Membership.class
            , new TypeReference<Set<PermissionEnum>>() {
            }
            , new Property.Accessor<Set<PermissionEnum>, Membership>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Set<PermissionEnum> get(Membership model) {
                    return model.getPermissions();
                }

                @Override
                public void set(Membership model, Set<PermissionEnum> value) {
                    model.setPermissions(value);
                }
            });

    public static final Property<Principal, Membership> PRINCIPAL = Property
        .create("principal" //$NON-NLS-1$
            , Membership.class
            , new TypeReference<Principal>() {
            }
            , new Property.Accessor<Principal, Membership>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Principal get(Membership model) {
                    return model.getPrincipal();
                }

                @Override
                public void set(Membership model, Principal value) {
                    model.setPrincipal(value);
                }
            });

    public static final Property<Collection<Role>, Membership> ROLES = Property
        .create("roles" //$NON-NLS-1$
            , Membership.class
            , new TypeReference<Collection<Role>>() {
            }
            , new Property.Accessor<Collection<Role>, Membership>() {
                private static final long serialVersionUID = 1L;

                @Override
                public Collection<Role> get(Membership model) {
                    return model.getRoles();
                }

                @Override
                public void set(Membership model, Collection<Role> value) {
                    model.getRoles().clear();
                    model.getRoles().addAll(value);
                }
            });

    public static final List<Property<?, ? super Membership>> PROPERTIES;
    static {
        List<Property<?, ? super Membership>> aList =
            new ArrayList<Property<?, ? super Membership>>();
        aList.add(ID);
        aList.add(PERMISSIONS);
        aList.add(PRINCIPAL);
        aList.add(ROLES);
        PROPERTIES = Collections.unmodifiableList(aList);
    };
}
