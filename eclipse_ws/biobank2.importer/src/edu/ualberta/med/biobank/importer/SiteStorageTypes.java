
package edu.ualberta.med.biobank.importer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.SampleType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.StorageType;

public class SiteStorageTypes {

    private static SiteStorageTypes instance = null;

    private HashMap<String, StorageType> storageTypeMap;

    private SiteStorageTypes() {
        storageTypeMap = new HashMap<String, StorageType>();
    }

    public static SiteStorageTypes getInstance() {
        if (instance != null) return instance;

        instance = new SiteStorageTypes();
        return instance;
    }

    public StorageType getStorageType(String name) throws Exception {
        StorageType st = storageTypeMap.get(name);
        if (st == null) {
            throw new Exception("Storage type " + name + " not in database");
        }
        return st;
    }

    public void insertStorageTypes(Site site) throws Exception {
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
        StorageType palette = assignStorageType(site, "Palette", "rows",
            "columns", 8, 12, null, freezerSampleTypes);
        storageTypeMap.put("Palette", palette);

        StorageType hotel13 = assignStorageType(site, "Hotel-13", "row", "",
            13, 1, Arrays.asList(new StorageType [] { palette }), null);
        storageTypeMap.put("Hotel-13", hotel13);

        StorageType hotel19 = assignStorageType(site, "Hotel-19", "row", "",
            19, 1, Arrays.asList(new StorageType [] { palette }), null);
        storageTypeMap.put("Hotel-19", hotel19);

        storageTypeMap.put("Freezer-3x10", assignStorageType(site, "Freezer",
            "row", "column", 3, 10, Arrays.asList(new StorageType [] {
                hotel13, hotel19 }), null));
        storageTypeMap.put("Freezer-4x12", assignStorageType(site, "Freezer",
            "row", "column", 4, 12, Arrays.asList(new StorageType [] {
                hotel13, hotel19 }), null));
        storageTypeMap.put("Freezer-5x9", assignStorageType(site, "Freezer",
            "row", "column", 4, 12, Arrays.asList(new StorageType [] {
                hotel13, hotel19 }), null));

        // Cabinet Types
        StorageType bin = assignStorageType(site, "Bin", "", "row", 1, 120,
            null, cabinetSampleTypes);
        storageTypeMap.put("Bin", bin);
        StorageType drawer = assignStorageType(site, "Drawer", "", "row", 1,
            36, Arrays.asList(new StorageType [] { bin }), null);
        storageTypeMap.put("Drawer", drawer);
        storageTypeMap.put("Cabinet", assignStorageType(site, "Cabinet", "",
            "row", 1, 4, Arrays.asList(new StorageType [] { drawer }), null));
    }

    private StorageType assignStorageType(Site site, String name,
        String dim1Label, String dim2Label, int dim1, int dim2,
        List<StorageType> children, Collection<SampleType> sampleTypes)
        throws Exception {
        StorageType st = new StorageType();
        st.setName(name);
        st.setSite(site);
        Capacity capacity = new Capacity();
        capacity.setDimensionOneCapacity(dim1);
        capacity.setDimensionTwoCapacity(dim2);
        st.setCapacity(capacity);
        st.setDimensionOneLabel(dim1Label);
        st.setDimensionTwoLabel(dim2Label);
        if (children != null) {
            st.setChildStorageTypeCollection(new HashSet<StorageType>(children));
        }
        if (sampleTypes != null) {
            st.setSampleTypeCollection(sampleTypes);
        }

        return (StorageType) BioBank2Db.getInstance().setObject(st);
    }
}
