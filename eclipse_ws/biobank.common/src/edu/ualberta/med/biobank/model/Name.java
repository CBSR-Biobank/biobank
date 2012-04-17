package edu.ualberta.med.biobank.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.i18n.LString;

@SuppressWarnings("nls")
public class Name {
    private static final Map<Class<?>, LString> names;
    private static final LString UNKNOWN = LString.tr("Unknown Object");

    static {
        Map<Class<?>, LString> tmp = new HashMap<Class<?>, LString>();

        // @formatter:off
        tmp.put(ActivityStatus.class,           LString.tr("Activity Status"));
        tmp.put(Address.class,                  LString.tr("Address"));
        tmp.put(AliquotedSpecimen.class,        LString.tr("Aliquoted Specimen"));
        tmp.put(Capacity.class,                 LString.tr("Capacity"));
        tmp.put(Center.class,                   LString.tr("Center"));
        tmp.put(Clinic.class,                   LString.tr("Clinic"));
        tmp.put(CollectionEvent.class,          LString.tr("Collection Event"));
        tmp.put(Comment.class,                  LString.tr("Comment"));
        tmp.put(Contact.class,                  LString.tr("Contact"));
        tmp.put(Container.class,                LString.tr("Container"));
        tmp.put(ContainerLabelingScheme.class,  LString.tr("Container Labeling Scheme"));
        tmp.put(ContainerPosition.class,        LString.tr("Container Position"));
        tmp.put(ContainerType.class,            LString.tr("Container Type"));
        tmp.put(Dispatch.class,                 LString.tr("Dispatch"));
        tmp.put(DispatchSpecimen.class,         LString.tr("Dispatch Specimen"));
        tmp.put(Domain.class,                   LString.tr("Domain"));
        tmp.put(EventAttr.class,                LString.tr("Event Attribute"));
        tmp.put(EventAttrType.class,            LString.tr("Event Attribute Type"));
        tmp.put(GlobalEventAttr.class,          LString.tr("Global Event Attribute"));
        tmp.put(Group.class,                    LString.tr("Group"));
        tmp.put(Membership.class,               LString.tr("Membership"));
        tmp.put(OriginInfo.class,               LString.tr("Origin Information"));
        tmp.put(Patient.class,                  LString.tr("Patient"));
        tmp.put(PermissionEnum.class,           LString.tr("Permission"));
        tmp.put(Principal.class,                LString.tr("Principal"));
        tmp.put(ProcessingEvent.class,          LString.tr("Processing Event"));
        tmp.put(Request.class,                  LString.tr("Request"));
        tmp.put(RequestSpecimen.class,          LString.tr("Request Specimen"));
        tmp.put(ResearchGroup.class,            LString.tr("Research Group"));
        tmp.put(Role.class,                     LString.tr("Role"));
        tmp.put(ShipmentInfo.class,             LString.tr("Shipment Information"));
        tmp.put(ShippingMethod.class,           LString.tr("Shipping Method"));
        tmp.put(Site.class,                     LString.tr("Site"));
        tmp.put(SourceSpecimen.class,           LString.tr("Source Specimen"));
        tmp.put(Specimen.class,                 LString.tr("Specimen"));
        tmp.put(SpecimenPosition.class,         LString.tr("Specimen Position"));
        tmp.put(SpecimenType.class,             LString.tr("Specimen Type"));
        tmp.put(Study.class,                    LString.tr("Study"));
        tmp.put(StudyEventAttr.class,           LString.tr("Study Event Attribute"));
        tmp.put(User.class,                     LString.tr("User"));
        // @formatter:on

        names = Collections.unmodifiableMap(tmp);
    }

    public static LString of(Class<?> klazz) {
        LString name = names.get(klazz);
        if (name == null) name = UNKNOWN;
        return name;
    }
}
