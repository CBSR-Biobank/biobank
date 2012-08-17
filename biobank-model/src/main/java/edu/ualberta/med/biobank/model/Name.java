package edu.ualberta.med.biobank.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

@SuppressWarnings("nls")
public class Name {
    private static final Map<Class<?>, LString> names;
    private static final Bundle bundle = new CommonBundle();
    private static final LString UNKNOWN = bundle.tr("Unknown Object").format();

    static {
        Map<Class<?>, LString> tmp = new HashMap<Class<?>, LString>();

        // @formatter:off
        tmp.put(Address.class,                  bundle.tr("Address").format());
        tmp.put(Capacity.class,                 bundle.tr("Capacity").format());
        tmp.put(Center.class,                   bundle.tr("Center").format());
        tmp.put(CollectionEvent.class,          bundle.tr("Collection Event").format());
        tmp.put(Comment.class,                  bundle.tr("Comment").format());
        tmp.put(Contact.class,                  bundle.tr("Contact").format());
        tmp.put(Container.class,                bundle.tr("Container").format());
        tmp.put(ContainerLabelingScheme.class,  bundle.tr("Container Labeling Scheme").format());
        tmp.put(ContainerPosition.class,        bundle.tr("Container Position").format());
        tmp.put(ContainerType.class,            bundle.tr("Container Type").format());
        tmp.put(Dispatch.class,                 bundle.tr("Dispatch").format());
        tmp.put(DispatchSpecimen.class,         bundle.tr("Dispatch Specimen").format());
        tmp.put(Domain.class,                   bundle.tr("Domain").format());
        tmp.put(EventAttr.class,                bundle.tr("Event Attribute").format());
        tmp.put(EventAttrType.class,            bundle.tr("Event Attribute Type").format());
        tmp.put(GlobalEventAttr.class,          bundle.tr("Global Event Attribute").format());
        tmp.put(Group.class,                    bundle.tr("Group").format());
        tmp.put(Membership.class,               bundle.tr("Membership").format());
        tmp.put(Patient.class,                  bundle.tr("Patient").format());
        tmp.put(PermissionEnum.class,           bundle.tr("Permission").format());
        tmp.put(Principal.class,                bundle.tr("Principal").format());
        tmp.put(ProcessingEvent.class,          bundle.tr("Processing Event").format());
        tmp.put(Request.class,                  bundle.tr("Request").format());
        tmp.put(RequestSpecimen.class,          bundle.tr("Request Specimen").format());
        tmp.put(Role.class,                     bundle.tr("Role").format());
        tmp.put(ShipmentInfo.class,             bundle.tr("Shipment Information").format());
        tmp.put(ShippingMethod.class,           bundle.tr("Shipping Method").format());
        tmp.put(SourceSpecimen.class,           bundle.tr("Source Specimen").format());
        tmp.put(Specimen.class,                 bundle.tr("Specimen").format());
        tmp.put(SpecimenPosition.class,         bundle.tr("Specimen Position").format());
        tmp.put(SpecimenType.class,             bundle.tr("Specimen Type").format());
        tmp.put(Study.class,                    bundle.tr("Study").format());
        tmp.put(StudyEventAttr.class,           bundle.tr("Study Event Attribute").format());
        tmp.put(User.class,                     bundle.tr("User").format());
        // @formatter:on

        names = Collections.unmodifiableMap(tmp);
    }

    public static LString of(Class<?> klazz) {
        LString name = names.get(klazz);
        if (name == null) name = UNKNOWN;
        return name;
    }
}
