
package edu.ualberta.med.biobank.importer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;

public class SiteContainerTypes {

    private static SiteContainerTypes instance = null;

    private HashMap<String, ContainerType> containerTypeMap;

    private HashMap<String, ContainerLabelingScheme> labelingSchemeMap;

    private Collection<SampleType> freezerSampleTypes;

    private Collection<SampleType> cabinetSampleTypes;

    private SiteContainerTypes() {
        containerTypeMap = new HashMap<String, ContainerType>();
    }

    public static SiteContainerTypes getInstance() {
        if (instance != null) return instance;

        instance = new SiteContainerTypes();
        return instance;
    }

    public ContainerType getContainerType(String name) throws Exception {
        ContainerType st = containerTypeMap.get(name);
        if (st == null) {
            throw new Exception("Storage type " + name + " not in database");
        }
        return st;
    }

    public void insertContainerTypes(Site site) throws Exception {
        System.out.println("adding container types ...");
        freezerSampleTypes = new HashSet<SampleType>();
        cabinetSampleTypes = new HashSet<SampleType>();

        labelingSchemeMap = new HashMap<String, ContainerLabelingScheme>();
        Collection<ContainerLabelingScheme> allLabelingSchemes = BioBank2Db.getInstance().getAppService().search(
            ContainerLabelingScheme.class, new ContainerLabelingScheme());
        for (ContainerLabelingScheme scheme : allLabelingSchemes) {
            labelingSchemeMap.put(scheme.getName(), scheme);
        }

        Collection<SampleType> allSampleTypes = BioBank2Db.getInstance().getAppService().search(
            SampleType.class, new SampleType());
        for (SampleType st : allSampleTypes) {
            if (st.getNameShort().startsWith("DNA")
                || st.getNameShort().startsWith("Hair")) {
                cabinetSampleTypes.add(st);
            }
            else {
                freezerSampleTypes.add(st);
            }
        }

        createFreezerTypes(site);
        createCabinetTypes(site);
    }

    private void createFreezerTypes(Site site) throws Exception {
        ContainerType box81 = assignContainerType(site, "Box-81",
            labelingSchemeMap.get("SBS Standard"), 8, 12, null,
            freezerSampleTypes);
        containerTypeMap.put("Box-81", box81);

        ContainerType pallet96 = assignContainerType(site, "Pallet-96",
            labelingSchemeMap.get("SBS Standard"), 8, 12, null,
            freezerSampleTypes);
        containerTypeMap.put("Pallet-96", pallet96);

        ContainerType pallet384 = assignContainerType(site, "Pallet-384",
            labelingSchemeMap.get("SBS Standard"), 16, 24, null,
            freezerSampleTypes);
        containerTypeMap.put("Pallet-384", pallet384);

        ContainerType hotel17 = assignContainerType(site, "Hotel-17",
            labelingSchemeMap.get("2 char numeric"), 17, 1,
            Arrays.asList(new ContainerType [] { box81 }), null);
        containerTypeMap.put("Hotel-17", hotel17);

        ContainerType hotel18 = assignContainerType(site, "Hotel-18",
            labelingSchemeMap.get("2 char numeric"), 18, 1,
            Arrays.asList(new ContainerType [] { box81 }), null);
        containerTypeMap.put("Hotel-18", hotel17);

        ContainerType hotel13 = assignContainerType(site, "Hotel-13",
            labelingSchemeMap.get("2 char numeric"), 13, 1,
            Arrays.asList(new ContainerType [] { pallet96 }), null);
        containerTypeMap.put("Hotel-13", hotel13);

        ContainerType hotel19 = assignContainerType(site, "Hotel-19",
            labelingSchemeMap.get("2 char numeric"), 19, 1,
            Arrays.asList(new ContainerType [] { pallet96 }), null);
        containerTypeMap.put("Hotel-19", hotel19);

        containerTypeMap.put("Freezer-3x10", assignContainerType(site,
            "Freezer-3x10", labelingSchemeMap.get("CBSR 2 char alphabetic"), 3,
            10, Arrays.asList(new ContainerType [] { hotel17 }), null));

        containerTypeMap.put("Freezer-4x12", assignContainerType(site,
            "Freezer-4x12", labelingSchemeMap.get("CBSR 2 char alphabetic"), 4,
            12, Arrays.asList(new ContainerType [] { hotel18 }), null));

        containerTypeMap.put("Freezer-5x9", assignContainerType(site,
            "Freezer-5x9", labelingSchemeMap.get("CBSR 2 char alphabetic"), 5,
            9, Arrays.asList(new ContainerType [] { hotel13, hotel19 }), null));

        containerTypeMap.put("Freezer-6x12", assignContainerType(site,
            "Freezer-6x12", labelingSchemeMap.get("CBSR 2 char alphabetic"), 6,
            12, Arrays.asList(new ContainerType [] { hotel13, hotel19 }), null));
    }

    private void createCabinetTypes(Site site) throws Exception {
        ContainerType bin = assignContainerType(site, "Bin",
            labelingSchemeMap.get("CBSR 2 char alphabetic"), 120, 1, null,
            cabinetSampleTypes);
        containerTypeMap.put("Bin", bin);
        ContainerType drawer = assignContainerType(site, "Drawer",
            labelingSchemeMap.get("2 char numeric"), 36, 1,
            Arrays.asList(new ContainerType [] { bin }), null);
        containerTypeMap.put("Drawer", drawer);
        containerTypeMap.put("Cabinet", assignContainerType(site, "Cabinet",
            labelingSchemeMap.get("CBSR 2 char alphabetic"), 4, 1,
            Arrays.asList(new ContainerType [] { drawer }), null));
    }

    private ContainerType assignContainerType(Site site, String name,
        ContainerLabelingScheme childLabelingScheme, int dim1, int dim2,
        List<ContainerType> children, Collection<SampleType> sampleTypes)
        throws Exception {
        ContainerType ct = new ContainerType();
        ct.setName(name);
        ct.setSite(site);
        Capacity capacity = new Capacity();
        capacity.setRowCapacity(dim1);
        capacity.setColCapacity(dim2);
        ct.setCapacity(capacity);
        ct.setActivityStatus("Active");
        ct.setChildLabelingScheme(childLabelingScheme);
        if (children != null) {
            ct.setChildContainerTypeCollection(new HashSet<ContainerType>(
                children));
        }
        if (sampleTypes != null) {
            ct.setSampleTypeCollection(sampleTypes);
        }

        return (ContainerType) BioBank2Db.getInstance().setObject(ct);
    }
}
