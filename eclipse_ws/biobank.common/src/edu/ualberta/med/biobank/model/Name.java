package edu.ualberta.med.biobank.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.i18n.LocalizedString;

@SuppressWarnings("nls")
public class Name {
    private static final Map<Class<?>, LocalizedString> names;
    private static final LocalizedString UNKNOWN = LocalizedString.tr("Unknown Object");

    static {
        Map<Class<?>, LocalizedString> tmp = new HashMap<Class<?>, LocalizedString>();

        // @formatter:off
        tmp.put(ActivityStatus.class,           LocalizedString.tr("Activity Status"));
        tmp.put(Address.class,                  LocalizedString.tr("Address"));
        tmp.put(AliquotedSpecimen.class,        LocalizedString.tr("Aliquoted Specimen"));
        tmp.put(Capacity.class,                 LocalizedString.tr("Capacity"));
        tmp.put(Center.class,                   LocalizedString.tr("Center"));
        tmp.put(Clinic.class,                   LocalizedString.tr("Clinic"));
        tmp.put(CollectionEvent.class,          LocalizedString.tr("Collection Event"));
        tmp.put(Comment.class,                  LocalizedString.tr("Comment"));
        tmp.put(Contact.class,                  LocalizedString.tr("Contact"));
        tmp.put(Container.class,                LocalizedString.tr("Container"));
        tmp.put(ContainerLabelingScheme.class,  LocalizedString.tr("Container Labeling Scheme"));
        tmp.put(ContainerPosition.class,        LocalizedString.tr("Container Position"));
        tmp.put(ContainerType.class,            LocalizedString.tr("Container Type"));
        tmp.put(Dispatch.class,                 LocalizedString.tr("Dispatch"));
        tmp.put(DispatchSpecimen.class,         LocalizedString.tr("Dispatch Specimen"));
        tmp.put(Domain.class,                   LocalizedString.tr("Domain"));
        tmp.put(EventAttr.class,                LocalizedString.tr("Event Attribute"));
        tmp.put(EventAttrType.class,            LocalizedString.tr("Event Attribute Type"));
        tmp.put(GlobalEventAttr.class,          LocalizedString.tr("Global Event Attribute"));
        tmp.put(Group.class,                    LocalizedString.tr("Group"));
        tmp.put(Membership.class,               LocalizedString.tr("Membership"));
        tmp.put(OriginInfo.class,               LocalizedString.tr("Origin Information"));
        tmp.put(Patient.class,                  LocalizedString.tr("Patient"));
        tmp.put(PermissionEnum.class,           LocalizedString.tr("Permission"));
        tmp.put(Principal.class,                LocalizedString.tr("Principal"));
        tmp.put(ProcessingEvent.class,          LocalizedString.tr("Processing Event"));
        tmp.put(Request.class,                  LocalizedString.tr("Request"));
        tmp.put(RequestSpecimen.class,          LocalizedString.tr("Request Specimen"));
        tmp.put(ResearchGroup.class,            LocalizedString.tr("Research Group"));
        tmp.put(Role.class,                     LocalizedString.tr("Role"));
        tmp.put(ShipmentInfo.class,             LocalizedString.tr("Shipment Information"));
        tmp.put(ShippingMethod.class,           LocalizedString.tr("Shipping Method"));
        tmp.put(Site.class,                     LocalizedString.tr("Site"));
        tmp.put(SourceSpecimen.class,           LocalizedString.tr("Source Specimen"));
        tmp.put(Specimen.class,                 LocalizedString.tr("Specimen"));
        tmp.put(SpecimenPosition.class,         LocalizedString.tr("Specimen Position"));
        tmp.put(SpecimenType.class,             LocalizedString.tr("Specimen Type"));
        tmp.put(Study.class,                    LocalizedString.tr("Study"));
        tmp.put(StudyEventAttr.class,           LocalizedString.tr("Study Event Attribute"));
        tmp.put(User.class,                     LocalizedString.tr("User"));
        // @formatter:on

        names = Collections.unmodifiableMap(tmp);
    }

    public static LocalizedString of(Class<?> klazz) {
        LocalizedString name = names.get(klazz);
        if (name == null) name = UNKNOWN;
        return name;
    }
}
