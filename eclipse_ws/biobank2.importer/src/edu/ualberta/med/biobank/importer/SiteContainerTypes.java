
package edu.ualberta.med.biobank.importer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;

public class SiteContainerTypes {

    private static SiteContainerTypes instance = null;

    private HashMap<String, ContainerType> containerTypeMap;

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
        SampleType sampleType = new SampleType();
        Collection<SampleType> allSampleTypes = BioBank2Db.getInstance().getAppService().search(
            SampleType.class, sampleType);

        Collection<SampleType> freezerSampleTypes = new HashSet<SampleType>();
        Collection<SampleType> cabinetSampleTypes = new HashSet<SampleType>();

        for (SampleType st : allSampleTypes) {
            if (st.getNameShort().startsWith("DNA")
                || st.getNameShort().startsWith("Hair")) {
                cabinetSampleTypes.add(st);
            }
            else {
                freezerSampleTypes.add(st);
            }
        }

        // Freezer Types
        ContainerType palette = assignContainerType(site, "Palette", 8, 12,
            null, freezerSampleTypes);
        containerTypeMap.put("Palette", palette);

        ContainerType hotel13 = assignContainerType(site, "Hotel-13", 13, 1,
            Arrays.asList(new ContainerType [] { palette }), null);
        containerTypeMap.put("Hotel-13", hotel13);

        ContainerType hotel19 = assignContainerType(site, "Hotel-19", 19, 1,
            Arrays.asList(new ContainerType [] { palette }), null);
        containerTypeMap.put("Hotel-19", hotel19);

        containerTypeMap.put("Freezer-3x10", assignContainerType(site,
            "Freezer", 3, 10, Arrays.asList(new ContainerType [] {
                hotel13, hotel19 }), null));
        containerTypeMap.put("Freezer-4x12", assignContainerType(site,
            "Freezer", 4, 12, Arrays.asList(new ContainerType [] {
                hotel13, hotel19 }), null));
        containerTypeMap.put("Freezer-5x9", assignContainerType(site,
            "Freezer", 4, 12, Arrays.asList(new ContainerType [] {
                hotel13, hotel19 }), null));

        // Cabinet Types
        ContainerType bin = assignContainerType(site, "Bin", 120, 1, null,
            cabinetSampleTypes);
        containerTypeMap.put("Bin", bin);
        ContainerType drawer = assignContainerType(site, "Drawer", 36, 1,
            Arrays.asList(new ContainerType [] { bin }), null);
        containerTypeMap.put("Drawer", drawer);
        containerTypeMap.put("Cabinet", assignContainerType(site, "Cabinet", 4,
            1, Arrays.asList(new ContainerType [] { drawer }), null));
    }

    private ContainerType assignContainerType(Site site, String name, int dim1,
        int dim2, List<ContainerType> children,
        Collection<SampleType> sampleTypes) throws Exception {
        ContainerType st = new ContainerType();
        st.setName(name);
        st.setSite(site);
        Capacity capacity = new Capacity();
        capacity.setDimensionOneCapacity(dim1);
        capacity.setDimensionTwoCapacity(dim2);
        st.setCapacity(capacity);
        st.setActivityStatus("Active");
        if (children != null) {
            st.setChildContainerTypeCollection(new HashSet<ContainerType>(
                children));
        }
        if (sampleTypes != null) {
            st.setSampleTypeCollection(sampleTypes);
        }

        return (ContainerType) BioBank2Db.getInstance().setObject(st);
    }
}
