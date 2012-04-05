package edu.ualberta.med.biobank.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.i18n.Msg;

@SuppressWarnings("nls")
public class Name {
    private static final Map<Class<?>, Msg> names;
    private static final Msg UNKNOWN = Msg.tr("Unknown Object");

    static {
        Map<Class<?>, Msg> tmp = new HashMap<Class<?>, Msg>();

        // @formatter:off
        tmp.put(ActivityStatus.class,           Msg.tr("Activity Status"));
        tmp.put(Address.class,                  Msg.tr("Address"));
        tmp.put(AliquotedSpecimen.class,        Msg.tr("Aliquoted Specimen"));
        tmp.put(Capacity.class,                 Msg.tr("Capacity"));
        tmp.put(Center.class,                   Msg.tr("Center"));
        tmp.put(Clinic.class,                   Msg.tr("Clinic"));
        tmp.put(CollectionEvent.class,          Msg.tr("Collection Event"));
        tmp.put(Comment.class,                  Msg.tr("Comment"));
        tmp.put(Contact.class,                  Msg.tr("Contact"));
        tmp.put(Container.class,                Msg.tr("Container"));
        tmp.put(ContainerLabelingScheme.class,  Msg.tr("Container Labeling Scheme"));
        tmp.put(ContainerPosition.class,        Msg.tr("Container Position"));
        tmp.put(ContainerType.class,            Msg.tr("Container Type"));
        tmp.put(Dispatch.class,                 Msg.tr("Dispatch"));
        tmp.put(DispatchSpecimen.class,         Msg.tr("Dispatch Specimen"));
        tmp.put(Domain.class,                   Msg.tr("Domain"));
        tmp.put(EventAttr.class,                Msg.tr("Event Attribute"));
        tmp.put(EventAttrType.class,            Msg.tr("Event Attribute Type"));
        tmp.put(GlobalEventAttr.class,          Msg.tr("Global Event Attribute"));
        tmp.put(Group.class,                    Msg.tr("Group"));
        tmp.put(Membership.class,               Msg.tr("Membership"));
        tmp.put(OriginInfo.class,               Msg.tr("Origin Information"));
        tmp.put(Patient.class,                  Msg.tr("Patient"));
        tmp.put(PermissionEnum.class,           Msg.tr("Permission"));
        tmp.put(Principal.class,                Msg.tr("Principal"));
        tmp.put(ProcessingEvent.class,          Msg.tr("Processing Event"));
        tmp.put(Request.class,                  Msg.tr("Request"));
        tmp.put(RequestSpecimen.class,          Msg.tr("Request Specimen"));
        tmp.put(ResearchGroup.class,            Msg.tr("Research Group"));
        tmp.put(Role.class,                     Msg.tr("Role"));
        tmp.put(ShipmentInfo.class,             Msg.tr("Shipment Information"));
        tmp.put(ShippingMethod.class,           Msg.tr("Shipping Method"));
        tmp.put(Site.class,                     Msg.tr("Site"));
        tmp.put(SourceSpecimen.class,           Msg.tr("Source Specimen"));
        tmp.put(Specimen.class,                 Msg.tr("Specimen"));
        tmp.put(SpecimenPosition.class,         Msg.tr("Specimen Position"));
        tmp.put(SpecimenType.class,             Msg.tr("Specimen Type"));
        tmp.put(Study.class,                    Msg.tr("Study"));
        tmp.put(StudyEventAttr.class,           Msg.tr("Study Event Attribute"));
        tmp.put(User.class,                     Msg.tr("User"));
        // @formatter:on

        names = Collections.unmodifiableMap(tmp);
    }

    public static Msg of(Class<?> klazz) {
        Msg name = names.get(klazz);
        if (name == null) name = UNKNOWN;
        return name;
    }
}
