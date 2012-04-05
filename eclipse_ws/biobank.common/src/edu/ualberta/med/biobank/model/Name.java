package edu.ualberta.med.biobank.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.i18n.SS;

@SuppressWarnings("nls")
public class Name {
    private static final Map<Class<?>, SS> names;
    private static final SS UNKNOWN = SS.tr("Unknown Object");

    static {
        Map<Class<?>, SS> tmp = new HashMap<Class<?>, SS>();

        // @formatter:off
        tmp.put(ActivityStatus.class,           SS.tr("Activity Status"));
        tmp.put(Address.class,                  SS.tr("Address"));
        tmp.put(AliquotedSpecimen.class,        SS.tr("Aliquoted Specimen"));
        tmp.put(Capacity.class,                 SS.tr("Capacity"));
        tmp.put(Center.class,                   SS.tr("Center"));
        tmp.put(Clinic.class,                   SS.tr("Clinic"));
        tmp.put(CollectionEvent.class,          SS.tr("Collection Event"));
        tmp.put(Comment.class,                  SS.tr("Comment"));
        tmp.put(Contact.class,                  SS.tr("Contact"));
        tmp.put(Container.class,                SS.tr("Container"));
        tmp.put(ContainerLabelingScheme.class,  SS.tr("Container Labeling Scheme"));
        tmp.put(ContainerPosition.class,        SS.tr("Container Position"));
        tmp.put(ContainerType.class,            SS.tr("Container Type"));
        tmp.put(Dispatch.class,                 SS.tr("Dispatch"));
        tmp.put(DispatchSpecimen.class,         SS.tr("Dispatch Specimen"));
        tmp.put(Domain.class,                   SS.tr("Domain"));
        tmp.put(EventAttr.class,                SS.tr("Event Attribute"));
        tmp.put(EventAttrType.class,            SS.tr("Event Attribute Type"));
        tmp.put(GlobalEventAttr.class,          SS.tr("Global Event Attribute"));
        tmp.put(Group.class,                    SS.tr("Group"));
        tmp.put(Membership.class,               SS.tr("Membership"));
        tmp.put(OriginInfo.class,               SS.tr("Origin Information"));
        tmp.put(Patient.class,                  SS.tr("Patient"));
        tmp.put(PermissionEnum.class,           SS.tr("Permission"));
        tmp.put(Principal.class,                SS.tr("Principal"));
        tmp.put(ProcessingEvent.class,          SS.tr("Processing Event"));
        tmp.put(Request.class,                  SS.tr("Request"));
        tmp.put(RequestSpecimen.class,          SS.tr("Request Specimen"));
        tmp.put(ResearchGroup.class,            SS.tr("Research Group"));
        tmp.put(Role.class,                     SS.tr("Role"));
        tmp.put(ShipmentInfo.class,             SS.tr("Shipment Information"));
        tmp.put(ShippingMethod.class,           SS.tr("Shipping Method"));
        tmp.put(Site.class,                     SS.tr("Site"));
        tmp.put(SourceSpecimen.class,           SS.tr("Source Specimen"));
        tmp.put(Specimen.class,                 SS.tr("Specimen"));
        tmp.put(SpecimenPosition.class,         SS.tr("Specimen Position"));
        tmp.put(SpecimenType.class,             SS.tr("Specimen Type"));
        tmp.put(Study.class,                    SS.tr("Study"));
        tmp.put(StudyEventAttr.class,           SS.tr("Study Event Attribute"));
        tmp.put(User.class,                     SS.tr("User"));
        // @formatter:on

        names = Collections.unmodifiableMap(tmp);
    }

    public static SS of(Class<?> klazz) {
        SS name = names.get(klazz);
        if (name == null) name = UNKNOWN;
        return name;
    }
}
