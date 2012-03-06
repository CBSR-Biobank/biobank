package edu.ualberta.med.biobank.common;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("nls")
public class VarCharLengths {

    // this map gives the maximum length for class attributes that are of
    // type VARCHAR in the data model.
    //
    // if an attribute is not in the map then it does not have a maximum length
    private static Map<String, Integer> attrMaxLen;
    static {
        Map<String, Integer> aMap = new HashMap<String, Integer>();
        aMap.put("edu.ualberta.med.biobank.model.User.email", 100);
        aMap.put("edu.ualberta.med.biobank.model.User.login", 500);
        aMap.put("edu.ualberta.med.biobank.model.User.fullName", 100);
        aMap.put(
            "edu.ualberta.med.biobank.model.AbstractPosition.positionString",
            50);
        aMap.put(
            "edu.ualberta.med.biobank.model.ContainerPosition.positionString",
            50);
        aMap.put("edu.ualberta.med.biobank.model.PropertyType.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.ContainerLabelingScheme.name",
            50);
        aMap.put("edu.ualberta.med.biobank.model.ContainerType.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.ContainerType.nameShort", 50);
        aMap.put("edu.ualberta.med.biobank.model.Center.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.Center.nameShort", 50);
        aMap.put("edu.ualberta.med.biobank.model.Clinic.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.Clinic.nameShort", 50);
        aMap.put("edu.ualberta.med.biobank.model.Group.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.EntityFilter.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.JasperTemplate.name", 50);
        aMap.put("edu.ualberta.med.biobank.model.Site.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.Site.nameShort", 50);
        aMap.put("edu.ualberta.med.biobank.model.EventAttr.value", 255);
        aMap.put("edu.ualberta.med.biobank.model.EventAttrType.name", 50);
        aMap.put("edu.ualberta.med.biobank.model.RequestSpecimen.claimedBy", 50);
        aMap.put("edu.ualberta.med.biobank.model.PrintedSsInvItem.txt", 15);
        aMap.put("edu.ualberta.med.biobank.model.EntityColumn.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.GlobalEventAttr.label", 50);
        aMap.put("edu.ualberta.med.biobank.model.Entity.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.Entity.className", 255);
        aMap.put(
            "edu.ualberta.med.biobank.model.SpecimenPosition.positionString",
            50);
        aMap.put("edu.ualberta.med.biobank.model.Report.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.Study.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.Study.nameShort", 50);
        aMap.put("edu.ualberta.med.biobank.model.Log.center", 50);
        aMap.put("edu.ualberta.med.biobank.model.Log.username", 100);
        aMap.put("edu.ualberta.med.biobank.model.Log.action", 100);
        aMap.put("edu.ualberta.med.biobank.model.Log.patientNumber", 100);
        aMap.put("edu.ualberta.med.biobank.model.Log.locationLabel", 255);
        aMap.put("edu.ualberta.med.biobank.model.Log.inventoryId", 100);
        aMap.put("edu.ualberta.med.biobank.model.Log.type", 100);
        aMap.put("edu.ualberta.med.biobank.model.SpecimenType.name", 100);
        aMap.put("edu.ualberta.med.biobank.model.SpecimenType.nameShort", 50);
        aMap.put(
            "edu.ualberta.med.biobank.model.PrinterLabelTemplate.printerName",
            50);
        aMap.put("edu.ualberta.med.biobank.model.PrinterLabelTemplate.name", 50);
        aMap.put("edu.ualberta.med.biobank.model.Container.productBarcode", 255);
        aMap.put("edu.ualberta.med.biobank.model.Container.path", 255);
        aMap.put("edu.ualberta.med.biobank.model.Container.label", 255);
        aMap.put("edu.ualberta.med.biobank.model.EntityProperty.property", 255);
        aMap.put("edu.ualberta.med.biobank.model.Permission.className", 255);
        aMap.put("edu.ualberta.med.biobank.model.ShippingMethod.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.Contact.title", 100);
        aMap.put("edu.ualberta.med.biobank.model.Contact.faxNumber", 50);
        aMap.put("edu.ualberta.med.biobank.model.Contact.name", 100);
        aMap.put("edu.ualberta.med.biobank.model.Contact.pagerNumber", 50);
        aMap.put("edu.ualberta.med.biobank.model.Contact.officeNumber", 50);
        aMap.put("edu.ualberta.med.biobank.model.Contact.emailAddress", 50);
        aMap.put("edu.ualberta.med.biobank.model.Contact.mobileNumber", 50);
        aMap.put("edu.ualberta.med.biobank.model.ResearchGroup.name", 255);
        aMap.put("edu.ualberta.med.biobank.model.ResearchGroup.nameShort", 50);
        aMap.put("edu.ualberta.med.biobank.model.ProcessingEvent.worksheet",
            100);
        aMap.put("edu.ualberta.med.biobank.model.Specimen.inventoryId", 100);
        aMap.put("edu.ualberta.med.biobank.model.ShipmentInfo.waybill", 255);
        aMap.put("edu.ualberta.med.biobank.model.ShipmentInfo.boxNumber", 255);
        aMap.put("edu.ualberta.med.biobank.model.Address.street2", 255);
        aMap.put("edu.ualberta.med.biobank.model.Address.street1", 255);
        aMap.put("edu.ualberta.med.biobank.model.Address.postalCode", 50);
        aMap.put("edu.ualberta.med.biobank.model.Address.faxNumber", 50);
        aMap.put("edu.ualberta.med.biobank.model.Address.phoneNumber", 50);
        aMap.put("edu.ualberta.med.biobank.model.Address.name", 50);
        aMap.put("edu.ualberta.med.biobank.model.Address.province", 50);
        aMap.put("edu.ualberta.med.biobank.model.Address.emailAddress", 100);
        aMap.put("edu.ualberta.med.biobank.model.Address.city", 50);
        aMap.put("edu.ualberta.med.biobank.model.Address.country", 50);
        aMap.put("edu.ualberta.med.biobank.model.Patient.pnumber", 100);
        aMap.put("edu.ualberta.med.biobank.model.Role.name", 255);

        attrMaxLen = Collections.unmodifiableMap(aMap);
    };

    // if an attribute is not in the map then it does not have a maximum length
    public static Integer getMaxSize(Class<?> klass, String attributeName) {
        return attrMaxLen.get(klass.getName() + "." + attributeName);
    }
}
