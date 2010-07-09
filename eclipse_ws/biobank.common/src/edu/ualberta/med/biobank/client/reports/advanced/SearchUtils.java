package edu.ualberta.med.biobank.client.reports.advanced;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class SearchUtils {

    public static HashMap<String, String> getColumnInfo(Class<?> type) {
        HashMap<String, String> properties = new HashMap<String, String>();
        if (type == Aliquot.class) {
            properties.put("Inventory Id", "aliquot.inventoryId");
            properties.put("Link Date", "aliquot.linkDate");
            properties.put("Sample Type", "aliquot.sampleType.name");
        } else if (type == Container.class) {
            properties.put("Label", "container.label");
            properties.put("Container Type", "container.containerType.name");
        } else if (type == Site.class) {
            properties.put("Site", "site.name");
            properties.put("Status", "site.activityStatus.name");
        } else if (type == Clinic.class) {
            properties.put("Clinic", "clinic.name");
            properties.put("Clinic Status", "clinic.activityStatus.name");
        } else if (type == Study.class) {
            properties.put("Study", "study.name");
            properties.put("Short Name", "study.nameShort");
        } else if (type == Patient.class) {
            properties.put("Patient Number", "patient.pnumber");
            properties.put("Study", "patient.study.nameShort");
        } else if (type == PatientVisit.class) {
            properties.put("Patient", "patientVisit.patient.pnumber");
            properties.put("Date Processed", "patientVisit.dateProcessed");
        } else if (type == Contact.class) {
            properties.put("Contact", "contact.name");
            properties.put("Title", "contact.title");
            properties.put("Mobile", "contact.mobileNumber");
            properties.put("Pager", "contact.pagerNumber");
            properties.put("Office", "contact.officeNumber");
            properties.put("Email", "contact.emailAddress");
        }
        return properties;
    }

    public static List<String> getOperatorSet(Class<?> type) {
        List<String> opList = new ArrayList<String>();
        if (type == String.class) {
            opList.add("=");
            opList.add("!=");
            opList.add("contains");
            opList.add("doesn't contain");
            opList.add("starts with");
            opList.add("doesn't start with");
            opList.add("ends with");
            opList.add("doesn't end with");
        } else if (type == Integer.class || type == Double.class) {
            opList.add("=");
            opList.add("<=");
            opList.add(">=");
            opList.add("<");
            opList.add(">");
        } else if (type == Date.class) {
            opList.add("<");
            opList.add(">");
        }
        return opList;
    }

    public static List<Class<?>> getSearchableObjs() {
        ArrayList<Class<?>> objList = new ArrayList<Class<?>>();
        objList.add(Aliquot.class);
        objList.add(Container.class);
        objList.add(Site.class);
        objList.add(Clinic.class);
        objList.add(Study.class);
        objList.add(Patient.class);
        objList.add(PatientVisit.class);
        objList.add(Contact.class);
        return objList;
    }

    public static List<HQLField> getSimpleFields(Class<?> c, String path,
        boolean collection) {
        ArrayList<HQLField> searchableFields = new ArrayList<HQLField>();
        if (c == Site.class) {
            if (collection)
                path = path.replace('.', '_') + "siteCollection.";
            else
                path = path + "site.";
            add(searchableFields, path, "name", String.class);
            add(searchableFields, path, "activityStatus.name", String.class);
        } else if (c == Clinic.class) {
            if (collection)
                path = path.replace('.', '_') + "clinicCollection.";
            else
                path = path + "clinic.";
            add(searchableFields, path, "name", String.class);
            add(searchableFields, path, "activityStatus.name", String.class);
        } else if (c == Address.class) {
            path = path + "address.";
            add(searchableFields, path, "street1", String.class);
            add(searchableFields, path, "street2", String.class);
            add(searchableFields, path, "city", String.class);
            add(searchableFields, path, "province", String.class);
            add(searchableFields, path, "postalCode", String.class);
        } else if (c == Aliquot.class) {
            if (collection)
                path = path.replace('.', '_') + "aliquotCollection.";
            else
                path = path + "aliquot.";
            add(searchableFields, path, "inventoryId", String.class);
            add(searchableFields, path, "position", String.class);
            add(searchableFields, path, "linkDate", Date.class);
            add(searchableFields, path, "quantity", Integer.class);
            add(searchableFields, path, "comment", String.class);
            add(searchableFields, path, "sampleType.name", String.class);
        } else if (c == Patient.class) {
            if (collection)
                path = path.replace('.', '_') + "patientCollection.";
            else
                path = path + "patient.";
            add(searchableFields, path, "pnumber", String.class);
        } else if (c == Study.class) {
            if (collection)
                path = path.replace('.', '_') + "studyCollection.";
            else
                path = path + "study.";
            add(searchableFields, path, "name", String.class);
            add(searchableFields, path, "nameShort", String.class);
            add(searchableFields, path, "comment", String.class);
            add(searchableFields, path, "activityStatus.name", String.class);
        } else if (c == PatientVisit.class) {
            if (collection)
                path = path.replace('.', '_') + "patientVisitCollection.";
            else
                path = path + "patientVisit.";
            add(searchableFields, path, "dateProcessed", Date.class);
            add(searchableFields, path, "comment", String.class);
        } else if (c == Contact.class) {
            if (collection)
                path = path.replace('.', '_') + "contactCollection.";
            else
                path = path + "contact.";
            add(searchableFields, path, "name", String.class);
            add(searchableFields, path, "title", String.class);
            add(searchableFields, path, "mobileNumber", String.class);
            add(searchableFields, path, "pagerNumber", String.class);
            add(searchableFields, path, "officeNumber", String.class);
            add(searchableFields, path, "faxNumber", String.class);
            add(searchableFields, path, "emailAddress", String.class);
        } else if (c == Shipment.class) {
            if (collection)
                path = path.replace('.', '_') + "shipmentCollection.";
            else
                path = path + "shipment.";
            add(searchableFields, path, "dateShipped", Date.class);
            add(searchableFields, path, "dateReceived", Date.class);
            add(searchableFields, path, "comment", String.class);
            add(searchableFields, path, "waybill", String.class);
            add(searchableFields, path, "boxNumber", String.class);
            add(searchableFields, path, "shippingCompany", String.class);
        } else if (c == Container.class) {
            if (collection)
                path = path.replace('.', '_') + "containerCollection.";
            else
                path = path + "container.";
            add(searchableFields, path, "productBarcode", String.class);
            add(searchableFields, path, "position", String.class);
            add(searchableFields, path, "activityStatus.name", String.class);
            add(searchableFields, path, "label", String.class);
            add(searchableFields, path, "temperature", Double.class);
            add(searchableFields, path, "comment", String.class);
            add(searchableFields, path, "containerType.name", String.class);
        } else if (c == SampleStorage.class) {
            if (collection)
                path = path.replace('.', '_') + "sampleStorageCollection.";
            else
                path = path + "sampleStorage.";
            add(searchableFields, path, "sampleType.name", String.class);
            add(searchableFields, path, "quantity", Integer.class);
            add(searchableFields, path, "volume", String.class);
            add(searchableFields, path, "activityStatus.name", String.class);
        }

        return searchableFields;
    }

    public static List<HQLField> getComplexFields(Class<?> c, String path,
        boolean collection) {
        ArrayList<HQLField> searchableFields = new ArrayList<HQLField>();
        if (c == Site.class) {
            if (collection)
                path = path.replace('.', '_') + "siteCollection.";
            else
                path = path + "site.";
            add(searchableFields, path, "address", Address.class);
            add(searchableFields, path, "clinicCollection", Clinic.class);
            add(searchableFields, path, "containerCollection", Container.class);
        } else if (c == Clinic.class) {
            if (collection)
                path = path.replace('.', '_') + "clinicCollection.";
            else
                path = path + "clinic.";
            add(searchableFields, path, "address", Address.class);
            add(searchableFields, path, "site", Site.class);
            add(searchableFields, path, "contactCollection", Contact.class);
            add(searchableFields, path, "shipmentCollection", Shipment.class);
        } else if (c == Aliquot.class) {
            if (collection)
                path = path.replace('.', '_') + "aliquotCollection.";
            else
                path = path + "aliquot.";
            add(searchableFields, path, "patientVisit", PatientVisit.class);
        } else if (c == Patient.class) {
            if (collection)
                path = path.replace('.', '_') + "patientCollection.";
            else
                path = path + "patient.";
            add(searchableFields, path, "study", Study.class);
            add(searchableFields, path, "patientVisitCollection",
                PatientVisit.class);
            add(searchableFields, path, "shipmentCollection", Shipment.class);
        } else if (c == Study.class) {
            if (collection)
                path = path.replace('.', '_') + "studyCollection.";
            else
                path = path + "study.";
            add(searchableFields, path, "site", Site.class);
            add(searchableFields, path, "contactCollection", Contact.class);
            add(searchableFields, path, "sampleStorageCollection",
                SampleStorage.class);
            add(searchableFields, path, "patientCollection", Patient.class);
        } else if (c == PatientVisit.class) {
            if (collection)
                path = path.replace('.', '_') + "patientVisitCollection.";
            else
                path = path + "patientVisit.";
            add(searchableFields, path, "patient", Patient.class);
            add(searchableFields, path, "aliquotCollection", Aliquot.class);
            add(searchableFields, path, "shipment", Shipment.class);
        } else if (c == Contact.class) {
            if (collection)
                path = path.replace('.', '_') + "contactCollection.";
            else
                path = path + "contact.";
            add(searchableFields, path, "clinic", Clinic.class);
            add(searchableFields, path, "studyCollection", Study.class);
        } else if (c == Shipment.class) {
            if (collection)
                path = path.replace('.', '_') + "shipmentCollection.";
            else
                path = path + "shipment.";
            add(searchableFields, path, "clinic", Clinic.class);
            add(searchableFields, path, "patientCollection", Patient.class);
            add(searchableFields, path, "patientVisitCollection",
                PatientVisit.class);
        } else if (c == Container.class) {
            if (collection)
                path = path.replace('.', '_') + "containerCollection.";
            else
                path = path + "container.";
            add(searchableFields, path, "site", Site.class);
        } else if (c == SampleStorage.class) {
            if (collection)
                path = path.replace('.', '_') + "sampleStorageCollection.";
            else
                path = path + "sampleStorage.";
            add(searchableFields, path, "study", Study.class);
        }

        return searchableFields;
    }

    private static void add(List<HQLField> searchableFields, String path,
        String name, Class<?> type) {
        if (!cycleDetected(path, name))
            searchableFields.add(new HQLField(path, name, type));
    }

    public static boolean cycleDetected(String path, String name) {
        if (path.contains(name))
            return true;
        return false;
    }

    public static Map<Class<?>, int[]> getColumnWidths() {
        HashMap<Class<?>, int[]> columnWidths = new HashMap<Class<?>, int[]>();
        columnWidths.put(Clinic.class, new int[] { 100, 100 });
        columnWidths.put(Site.class, new int[] { 100, 100 });
        columnWidths.put(Aliquot.class, new int[] { 100, 100, 100 });
        columnWidths.put(Container.class, new int[] { 100, 100 });
        columnWidths.put(Study.class, new int[] { 100, 100 });
        columnWidths.put(Patient.class, new int[] { 100, 100 });
        columnWidths.put(PatientVisit.class, new int[] { 100, 100 });
        columnWidths.put(Contact.class, new int[] { 100, 100, 100, 100 });
        return columnWidths;
    }

}
