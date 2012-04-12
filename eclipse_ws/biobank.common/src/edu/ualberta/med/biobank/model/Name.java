package edu.ualberta.med.biobank.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.i18n.S;

@SuppressWarnings("nls")
public class Name {
    private static final Map<Class<?>, S> names;
    private static final S UNKNOWN = S.tr("Unknown Object");

    static {
        Map<Class<?>, S> tmp = new HashMap<Class<?>, S>();

        // @formatter:off
        tmp.put(ActivityStatus.class,           S.tr("Activity Status"));
        tmp.put(Address.class,                  S.tr("Address"));
        tmp.put(AliquotedSpecimen.class,        S.tr("Aliquoted Specimen"));
        tmp.put(Capacity.class,                 S.tr("Capacity"));
        tmp.put(Center.class,                   S.tr("Center"));
        tmp.put(Clinic.class,                   S.tr("Clinic"));
        tmp.put(CollectionEvent.class,          S.tr("Collection Event"));
        tmp.put(Comment.class,                  S.tr("Comment"));
        tmp.put(Contact.class,                  S.tr("Contact"));
        tmp.put(Container.class,                S.tr("Container"));
        tmp.put(ContainerLabelingScheme.class,  S.tr("Container Labeling Scheme"));
        tmp.put(ContainerPosition.class,        S.tr("Container Position"));
        tmp.put(ContainerType.class,            S.tr("Container Type"));
        tmp.put(Dispatch.class,                 S.tr("Dispatch"));
        tmp.put(DispatchSpecimen.class,         S.tr("Dispatch Specimen"));
        tmp.put(Domain.class,                   S.tr("Domain"));
        tmp.put(EventAttr.class,                S.tr("Event Attribute"));
        tmp.put(EventAttrType.class,            S.tr("Event Attribute Type"));
        tmp.put(GlobalEventAttr.class,          S.tr("Global Event Attribute"));
        tmp.put(Group.class,                    S.tr("Group"));
        tmp.put(Membership.class,               S.tr("Membership"));
        tmp.put(OriginInfo.class,               S.tr("Origin Information"));
        tmp.put(Patient.class,                  S.tr("Patient"));
        tmp.put(PermissionEnum.class,           S.tr("Permission"));
        tmp.put(Principal.class,                S.tr("Principal"));
        tmp.put(ProcessingEvent.class,          S.tr("Processing Event"));
        tmp.put(Request.class,                  S.tr("Request"));
        tmp.put(RequestSpecimen.class,          S.tr("Request Specimen"));
        tmp.put(ResearchGroup.class,            S.tr("Research Group"));
        tmp.put(Role.class,                     S.tr("Role"));
        tmp.put(ShipmentInfo.class,             S.tr("Shipment Information"));
        tmp.put(ShippingMethod.class,           S.tr("Shipping Method"));
        tmp.put(Site.class,                     S.tr("Site"));
        tmp.put(SourceSpecimen.class,           S.tr("Source Specimen"));
        tmp.put(Specimen.class,                 S.tr("Specimen"));
        tmp.put(SpecimenPosition.class,         S.tr("Specimen Position"));
        tmp.put(SpecimenType.class,             S.tr("Specimen Type"));
        tmp.put(Study.class,                    S.tr("Study"));
        tmp.put(StudyEventAttr.class,           S.tr("Study Event Attribute"));
        tmp.put(User.class,                     S.tr("User"));
        // @formatter:on

        names = Collections.unmodifiableMap(tmp);
    }

    public static S of(Class<?> klazz) {
        S name = names.get(klazz);
        if (name == null) name = UNKNOWN;
        return name;
    }
}
