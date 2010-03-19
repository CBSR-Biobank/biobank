package edu.ualberta.med.biobank.common.reports.advanced;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Shipment;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;

public class SearchUtils {

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
        } else if (type == Integer.class) {
            opList.add("=");
            opList.add("<=");
            opList.add(">=");
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
            add(searchableFields, path, "activityStatus", String.class);
        } else if (c == Clinic.class) {
            if (collection)
                path = path.replace('.', '_') + "clinicCollection.";
            else
                path = path + "clinic.";
            add(searchableFields, path, "name", String.class);
        } else if (c == Address.class) {
            path = path + "address.";
            add(searchableFields, path, "street1", String.class);
            add(searchableFields, path, "street2", String.class);
            add(searchableFields, path, "city", String.class);
            add(searchableFields, path, "province", String.class);
            add(searchableFields, path, "postalCode", String.class);
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
            add(searchableFields, path, "sampleTypeCollection",
                SampleType.class);
        } else if (c == Clinic.class) {
            if (collection)
                path = path.replace('.', '_') + "clinicCollection.";
            else
                path = path + "clinic.";
            add(searchableFields, path, "address", Address.class);
            add(searchableFields, path, "site", Site.class);
            add(searchableFields, path, "contactCollection", Contact.class);
            add(searchableFields, path, "shipmentCollection", Shipment.class);
            add(searchableFields, path, "patientVisitCollection",
                PatientVisit.class);
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

}
