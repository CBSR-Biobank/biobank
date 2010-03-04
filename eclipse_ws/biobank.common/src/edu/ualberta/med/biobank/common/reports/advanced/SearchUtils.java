package edu.ualberta.med.biobank.common.reports.advanced;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.common.wrappers.ShipmentWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.Address;

public class SearchUtils {

    public static List<Class<? extends ModelWrapper<?>>> getSearchableObjs() {
        ArrayList<Class<? extends ModelWrapper<?>>> objList = new ArrayList<Class<? extends ModelWrapper<?>>>();
        objList.add(SampleWrapper.class);
        objList.add(ContainerWrapper.class);
        objList.add(SiteWrapper.class);
        objList.add(ClinicWrapper.class);
        objList.add(StudyWrapper.class);
        objList.add(PatientWrapper.class);
        objList.add(PatientVisitWrapper.class);
        objList.add(ContactWrapper.class);
        return objList;
    }

    public static QueryTreeNode constructTree(HQLField root) {
        QueryTreeNode rootNode = new QueryTreeNode(root);
        expand(rootNode);
        return rootNode;
    }

    public static void expand(QueryTreeNode node) {
        List<HQLField> fields = getSimpleFields(node.getNodeInfo().getType(),
            node.getNodeInfo().getPath());
        for (HQLField field : fields)
            node.addField(field);
        List<HQLField> children = getComplexFields(
            node.getNodeInfo().getType(), node.getNodeInfo().getPath());
        for (HQLField child : children) {
            QueryTreeNode nodeChild = new QueryTreeNode(child);
            nodeChild.setParent(node);
            expand(nodeChild);
            node.addChild(nodeChild);
        }
    }

    public static List<HQLField> getSimpleFields(Class<?> c, String path) {
        ArrayList<HQLField> searchableFields = new ArrayList<HQLField>();
        if (c == SiteWrapper.class) {
            path = path + "site.";
            add(searchableFields, path, "name", String.class);
            add(searchableFields, path, "activityStatus", String.class);
        } else if (c == ClinicWrapper.class) {
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

    public static List<HQLField> getComplexFields(Class<?> c, String path) {
        ArrayList<HQLField> searchableFields = new ArrayList<HQLField>();
        if (c == SiteWrapper.class) {
            path = path + "site.";
            add(searchableFields, path, "address", Address.class);
            add(searchableFields, path, "clinicCollection", ClinicWrapper.class);
            add(searchableFields, path, "containerCollection",
                ContainerWrapper.class);
            add(searchableFields, path, "sampleTypeCollection",
                SampleTypeWrapper.class);
        } else if (c == ClinicWrapper.class) {
            path = path + "clinic.";
            add(searchableFields, path, "address", Address.class);
            add(searchableFields, path, "site", SiteWrapper.class);
            add(searchableFields, path, "contactCollection",
                ContactWrapper.class);
            add(searchableFields, path, "shipmentCollection",
                ShipmentWrapper.class);
            add(searchableFields, path, "patientVisitCollection",
                PatientVisitWrapper.class);
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
