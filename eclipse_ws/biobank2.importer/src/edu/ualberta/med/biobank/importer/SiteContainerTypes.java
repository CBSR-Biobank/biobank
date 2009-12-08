
package edu.ualberta.med.biobank.importer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SiteContainerTypes {

    private static SiteContainerTypes instance = null;

    private HashMap<String, ContainerTypeWrapper> containerTypeMap;

    private Collection<SampleTypeWrapper> freezerSampleTypes;

    private Collection<SampleTypeWrapper> cabinetSampleTypes;

    private SiteContainerTypes() {
        containerTypeMap = new HashMap<String, ContainerTypeWrapper>();
    }

    public static SiteContainerTypes getInstance() {
        if (instance != null) return instance;

        instance = new SiteContainerTypes();
        return instance;
    }

    public ContainerTypeWrapper getContainerType(String name) throws Exception {
        ContainerTypeWrapper st = containerTypeMap.get(name);
        if (st == null) {
            throw new Exception("Storage type " + name + " not in database");
        }
        return st;
    }

    public void insertContainerTypes(SiteWrapper site) throws Exception {
        System.out.println("adding container types ...");
        freezerSampleTypes = new HashSet<SampleTypeWrapper>();
        cabinetSampleTypes = new HashSet<SampleTypeWrapper>();

        Collection<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper.getGlobalSampleTypes(
            site.getAppService(), true);
        for (SampleTypeWrapper st : allSampleTypes) {
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

    private void createFreezerTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper box81 = assignContainerType(site, "Box-81", false,
            1, 9, 9, null, freezerSampleTypes);
        containerTypeMap.put("Box-81", box81);

        ContainerTypeWrapper pallet96 = assignContainerType(site, "Pallet-96",
            false, 1, 8, 12, null, freezerSampleTypes);
        containerTypeMap.put("Pallet-96", pallet96);

        ContainerTypeWrapper pallet384 = assignContainerType(site,
            "Pallet-384", false, 1, 16, 24, null, freezerSampleTypes);
        containerTypeMap.put("Pallet-384", pallet384);

        ContainerTypeWrapper hotel17 = assignContainerType(site, "Hotel-17",
            false, 3, 17, 1,
            Arrays.asList(new ContainerTypeWrapper [] { box81 }), null);
        containerTypeMap.put("Hotel-17", hotel17);

        ContainerTypeWrapper hotel18 = assignContainerType(site, "Hotel-18",
            false, 3, 18, 1,
            Arrays.asList(new ContainerTypeWrapper [] { box81 }), null);
        containerTypeMap.put("Hotel-18", hotel17);

        ContainerTypeWrapper hotel13 = assignContainerType(site, "Hotel-13",
            false, 3, 13, 1,
            Arrays.asList(new ContainerTypeWrapper [] { pallet96 }), null);
        containerTypeMap.put("Hotel-13", hotel13);

        ContainerTypeWrapper hotel19 = assignContainerType(site, "Hotel-19",
            false, 3, 19, 1,
            Arrays.asList(new ContainerTypeWrapper [] { pallet96 }), null);
        containerTypeMap.put("Hotel-19", hotel19);

        containerTypeMap.put("Freezer-3x10", assignContainerType(site,
            "Freezer-3x10", true, 2, 3, 10,
            Arrays.asList(new ContainerTypeWrapper [] { hotel17 }), null));

        containerTypeMap.put("Freezer-4x12", assignContainerType(site,
            "Freezer-4x12", true, 2, 4, 12,
            Arrays.asList(new ContainerTypeWrapper [] { hotel18 }), null));

        containerTypeMap.put("Freezer-5x9", assignContainerType(site,
            "Freezer-5x9", true, 2, 5, 9,
            Arrays.asList(new ContainerTypeWrapper [] { hotel13, hotel19 }),
            null));

        containerTypeMap.put("Freezer-6x12", assignContainerType(site,
            "Freezer-6x12", true, 2, 6, 12,
            Arrays.asList(new ContainerTypeWrapper [] { hotel13, hotel19 }),
            null));
    }

    private void createCabinetTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper bin = assignContainerType(site, "Bin", false, 2,
            120, 1, null, cabinetSampleTypes);
        containerTypeMap.put("Bin", bin);
        ContainerTypeWrapper drawer = assignContainerType(site, "Drawer",
            false, 3, 36, 1,
            Arrays.asList(new ContainerTypeWrapper [] { bin }), null);
        containerTypeMap.put("Drawer", drawer);
        containerTypeMap.put("Cabinet", assignContainerType(site, "Cabinet",
            true, 2, 4, 1,
            Arrays.asList(new ContainerTypeWrapper [] { drawer }), null));
    }

    private ContainerTypeWrapper assignContainerType(SiteWrapper site,
        String name, boolean topLevel, int childLabelingScheme, int dim1,
        int dim2, List<ContainerTypeWrapper> children,
        Collection<SampleTypeWrapper> sampleTypes) throws Exception {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(site.getAppService());
        ct.setName(name);
        ct.setTopLevel(topLevel);
        ct.setSite(site);
        ct.setRowCapacity(dim1);
        ct.setColCapacity(dim2);
        ct.setActivityStatus("Active");
        ct.setChildLabelingScheme(childLabelingScheme);
        ct.setChildContainerTypeCollection(children);
        ct.setSampleTypeCollection(sampleTypes);
        ct.persist();
        ct.reload();
        return ct;
    }
}
