
package edu.ualberta.med.biobank.importer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class SiteContainerTypes {

    private static Map<String, SampleTypeWrapper> sampleTypeMap;

    private static Map<String, ContainerTypeWrapper> containerTypeMap = new HashMap<String, ContainerTypeWrapper>();

    private static String [] biopsyPallet96SampleTypes = new String [] {
        "Descending Colon", "Stomach, Body", "Stomach, Antrum", "Duodenum",
        "Jejunum", "Ileum", "Ascending Colon", "Transverse Colon", };

    private static String [] box81SampleTypes = new String [] {
        "Plasma", "Paxgene", "DNA (Blood)", "Cells", "Urine", "Hemodialysate",
        "Peritoneal Dialysate", "Serum", "Finger Nails", "Buffy coat",
        "Toe Nails", "Whole Blood EDTA", "RNAlater Biopsies", "Heparin Blood",
        "Filtered Urine", "Serum (Beige top)", "Centrifuged Urine",
        "Sodium Azide Urine", "Source Water", "Meconium - BABY", "WB DMSO" };

    private static String [] cellPallet96SampleTypes = new String [] { "Cells" };

    private static String [] ftaBinSampleTypes = new String [] { "DNA(Blood)" };

    private static String [] hairBinSampleTypes = new String [] { "Hair" };

    private static String [] pallet96SampleTypes = new String [] {
        "Plasma", "Paxgene", "DNA (Blood)", "Cells", "Urine", "Hemodialysate",
        "Peritoneal Dialysate", "Serum", "Finger Nails", "Buffy coat",
        "Toe Nails", "Whole Blood EDTA", "RNAlater Biopsies", "Heparin Blood",
        "Filtered Urine", "Serum (Beige top)", "Centrifuged Urine",
        "Sodium Azide Urine", "Source Water", "Plasma (Na Heparin) - DAD",
        "WB Serum - BABY", "SerumPellet - BABY", "WB - BABY", "WB RNA - BABY",
        "WB Plasma - BABY", "Cord Blood Mononuclear Cells", "RNA CBMC",
        "Meconium - BABY", "WB DMSO" };

    private static void getSampleTypeMap() throws Exception {
        if (sampleTypeMap != null) return;

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper.getGlobalSampleTypes(
            Importer.getAppService(), true);
        if ((allSampleTypes == null) || (allSampleTypes.size() == 0)) {
            throw new Exception("no global sample types found in the database");
        }

        sampleTypeMap = new HashMap<String, SampleTypeWrapper>();
        for (SampleTypeWrapper sampleType : allSampleTypes) {
            sampleTypeMap.put(sampleType.getName(), sampleType);
        }
    }

    public static ContainerTypeWrapper getContainerType(String name)
        throws Exception {
        ContainerTypeWrapper type = containerTypeMap.get(name);
        if (type == null) {
            throw new Exception("container type " + name + " not in database");
        }
        return type;
    }

    public static void createContainerTypes(SiteWrapper site) throws Exception {
        getSampleTypeMap();
        System.out.println("adding container types ...");

        createFreezerTypes(site);
        createCabinetTypes(site);
    }

    private static void createFreezerTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper biopsyPallet96 = assignContainerType(site,
            "Biopsy Pallet 96", "BP96", false, 1, 8, 12, null,
            biopsyPallet96SampleTypes);
        containerTypeMap.put("Biopsy Pallet 96", biopsyPallet96);

        ContainerTypeWrapper box81 = assignContainerType(site, "Box 81", "B81",
            false, 1, 9, 9, null, box81SampleTypes);
        containerTypeMap.put("Box 81", box81);

        ContainerTypeWrapper cellPallet96 = assignContainerType(site,
            "Cell Pallet 96", "CP96", false, 1, 9, 9, null,
            cellPallet96SampleTypes);
        containerTypeMap.put("Cell Pallet 96", cellPallet96);

        ContainerTypeWrapper pallet96 = assignContainerType(site, "Pallet 96",
            "P96", false, 1, 8, 12, null, pallet96SampleTypes);
        containerTypeMap.put("Pallet 96", pallet96);

        ContainerTypeWrapper hotel13 = assignContainerType(site, "Hotel 13",
            "H13", false, 3, 13, 1,
            Arrays.asList(new ContainerTypeWrapper [] { pallet96 }));
        containerTypeMap.put("Hotel 13", hotel13);

        ContainerTypeWrapper hotel17 = assignContainerType(site, "Hotel 17",
            "H17", false, 3, 17, 1,
            Arrays.asList(new ContainerTypeWrapper [] { box81 }));
        containerTypeMap.put("Hotel 17", hotel17);

        ContainerTypeWrapper hotel18 = assignContainerType(site, "Hotel 18",
            "H18", false, 3, 18, 1,
            Arrays.asList(new ContainerTypeWrapper [] { box81 }));
        containerTypeMap.put("Hotel 18", hotel17);

        ContainerTypeWrapper hotel19 = assignContainerType(site, "Hotel 19",
            "H19", false, 3, 19, 1, Arrays.asList(new ContainerTypeWrapper [] {
                biopsyPallet96, cellPallet96, pallet96 }));
        containerTypeMap.put("Hotel 19", hotel19);

        containerTypeMap.put("Freezer 3x10", assignContainerType(site,
            "Freezer 3x10", "F3x10", true, 2, 3, 10,
            Arrays.asList(new ContainerTypeWrapper [] { hotel17 })));

        containerTypeMap.put("Freezer 4x12", assignContainerType(site,
            "Freezer 4x12", "F4x12", true, 2, 4, 12,
            Arrays.asList(new ContainerTypeWrapper [] { hotel18 })));

        containerTypeMap.put("Freezer 5x9", assignContainerType(site,
            "Freezer 5x9", "F5x9", true, 2, 5, 9,
            Arrays.asList(new ContainerTypeWrapper [] { hotel13, hotel19 })));

        containerTypeMap.put("Freezer 6x12", assignContainerType(site,
            "Freezer 6x12", "F6x12", true, 2, 6, 12,
            Arrays.asList(new ContainerTypeWrapper [] { hotel13, hotel19 })));
    }

    private static void createCabinetTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper ftaBin = assignContainerType(site, "FTA Bin",
            "FBin", false, 2, 120, 1, null, ftaBinSampleTypes);
        containerTypeMap.put("FTA Bin", ftaBin);

        ContainerTypeWrapper hairBin = assignContainerType(site, "Hair Bin",
            "HBin", false, 2, 120, 1, null, hairBinSampleTypes);
        containerTypeMap.put("Hair Bin", ftaBin);

        ContainerTypeWrapper drawer36 = assignContainerType(site, "Drawer36",
            "D36", false, 3, 36, 1, Arrays.asList(new ContainerTypeWrapper [] {
                ftaBin, hairBin }));
        containerTypeMap.put("Drawer36", drawer36);
        containerTypeMap.put("Cabinet 4 drawer", assignContainerType(site,
            "Cabinet 4 drawer", "Cabinet 4", true, 2, 4, 1,
            Arrays.asList(new ContainerTypeWrapper [] { drawer36 })));
    }

    private static ContainerTypeWrapper assignContainerType(SiteWrapper site,
        String name, String nameShort, boolean topLevel,
        int childLabelingScheme, int rows, int cols,
        List<ContainerTypeWrapper> children, String... sampleTypeNames)
        throws Exception {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(site.getAppService());
        ct.setName(name);
        ct.setName(nameShort);
        ct.setTopLevel(topLevel);
        ct.setSite(site);
        ct.setRowCapacity(rows);
        ct.setColCapacity(cols);
        ct.setActivityStatus("Active");
        ct.setChildLabelingScheme(childLabelingScheme);
        ct.setChildContainerTypeCollection(children);
        List<SampleTypeWrapper> list = new ArrayList<SampleTypeWrapper>();
        for (String sampleTypeName : sampleTypeNames) {
            SampleTypeWrapper sampleType = sampleTypeMap.get(sampleTypeName);
            if (sampleType == null) {
                throw new Exception("sample type name is invalid: "
                    + sampleTypeName);
            }
            list.add(sampleType);
        }
        ct.setSampleTypeCollection(list);
        ct.persist();
        ct.reload();
        return ct;
    }
}
