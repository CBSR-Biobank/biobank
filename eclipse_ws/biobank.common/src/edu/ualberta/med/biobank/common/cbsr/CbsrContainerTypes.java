package edu.ualberta.med.biobank.common.cbsr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class CbsrContainerTypes {

    private static Map<String, SampleTypeWrapper> sampleTypeMap;

    private static Map<String, ContainerTypeWrapper> containerTypeMap = new HashMap<String, ContainerTypeWrapper>();

    private static String[] biopsyPallet96SampleTypes = new String[] {
        "Descending Colon", "Stomach, Body", "Stomach, Antrum", "Duodenum",
        "Jejunum", "Ileum", "Ascending Colon", "Transverse Colon", };

    private static String[] box81SampleTypes = new String[] { "Plasma",
        "Paxgene", "DNA (Blood)", "Cells", "Urine", "Hemodialysate",
        "Peritoneal Dialysate", "Serum", "Finger Nails", "Buffy coat",
        "Toe Nails", "Whole Blood EDTA", "RNAlater Biopsies", "Heparin Blood",
        "Filtered Urine", "Serum (Beige top)", "Centrifuged Urine",
        "Sodium Azide Urine", "Source Water", "Meconium - BABY", "WB DMSO" };

    private static String[] cellPallet96SampleTypes = new String[] { "Cells" };

    private static String[] ftaBinSampleTypes = new String[] { "DNA (Blood)" };

    private static String[] hairBinSampleTypes = new String[] { "Hair" };

    private static String[] pallet96SampleTypes = new String[] { "Plasma",
        "Paxgene", "DNA (Blood)", "Cells", "Urine", "Hemodialysate",
        "Peritoneal Dialysate", "Serum", "Finger Nails", "Buffy coat",
        "Toe Nails", "Whole Blood EDTA", "RNAlater Biopsies", "Heparin Blood",
        "Filtered Urine", "Serum (Beige top)", "Centrifuged Urine",
        "Sodium Azide Urine", "Source Water", "Plasma (Na Heparin) - DAD",
        "WB Serum - BABY", "SerumPellet - BABY", "WB - BABY", "WB RNA - BABY",
        "WB Plasma - BABY", "Cord Blood Mononuclear Cells", "RNA CBMC",
        "Meconium - BABY", "WB DMSO" };

    private static void getSampleTypeMap(SiteWrapper site) throws Exception {
        if (sampleTypeMap != null)
            return;

        List<SampleTypeWrapper> allSampleTypes = SampleTypeWrapper
            .getGlobalSampleTypes(site.getAppService(), true);
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
        getSampleTypeMap(site);
        createFreezerTypes(site);
        createCabinetTypes(site);
    }

    private static void createFreezerTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper biopsyPallet96 = addContainerType(site,
            "Biopsy Pallet 96", "BP96", false, 1, 8, 12, null,
            biopsyPallet96SampleTypes);

        ContainerTypeWrapper box81 = addContainerType(site, "Box 81", "B81",
            false, 1, 9, 9, null, box81SampleTypes);

        ContainerTypeWrapper cellPallet96 = addContainerType(site,
            "Cell Pallet 96", "CP96", false, 1, 9, 9, null,
            cellPallet96SampleTypes);

        ContainerTypeWrapper pallet96 = addContainerType(site, "Pallet 96",
            "P96", false, 1, 8, 12, null, pallet96SampleTypes);

        ContainerTypeWrapper hotel13 = addContainerType(site, "Hotel 13",
            "H13", false, 3, 13, 1, Arrays
                .asList(new ContainerTypeWrapper[] { pallet96 }));

        ContainerTypeWrapper hotel17 = addContainerType(site, "Hotel 17",
            "H17", false, 3, 17, 1, Arrays
                .asList(new ContainerTypeWrapper[] { box81 }));

        ContainerTypeWrapper hotel18 = addContainerType(site, "Hotel 18",
            "H18", false, 3, 18, 1, Arrays
                .asList(new ContainerTypeWrapper[] { box81 }));

        ContainerTypeWrapper hotel19 = addContainerType(site, "Hotel 19",
            "H19", false, 3, 19, 1, Arrays.asList(new ContainerTypeWrapper[] {
                biopsyPallet96, cellPallet96, pallet96 }));

        addContainerType(site, "Freezer 3x10", "F3x10", true, 2, 3, 10, Arrays
            .asList(new ContainerTypeWrapper[] { hotel17 }));

        addContainerType(site, "Freezer 4x12", "F4x12", true, 2, 4, 12, Arrays
            .asList(new ContainerTypeWrapper[] { hotel18 }));

        addContainerType(site, "Freezer 5x9", "F5x9", true, 2, 5, 9, Arrays
            .asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));

        addContainerType(site, "Freezer 6x12", "F6x12", true, 2, 6, 12, Arrays
            .asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));
    }

    private static void createCabinetTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper ftaBin = addContainerType(site, "FTA Bin", "FBin",
            false, 2, 120, 1, null, ftaBinSampleTypes);

        ContainerTypeWrapper hairBin = addContainerType(site, "Hair Bin",
            "HBin", false, 2, 120, 1, null, hairBinSampleTypes);

        ContainerTypeWrapper drawer36 = addContainerType(site, "Drawer 36",
            "D36", false, 3, 36, 1, Arrays.asList(new ContainerTypeWrapper[] {
                ftaBin, hairBin }));
        addContainerType(site, "Cabinet 4 drawer", "Cabinet 4", true, 2, 4, 1,
            Arrays.asList(new ContainerTypeWrapper[] { drawer36 }));
    }

    private static ContainerTypeWrapper addContainerType(SiteWrapper site,
        String name, String nameShort, boolean topLevel,
        int childLabelingScheme, int rows, int cols,
        List<ContainerTypeWrapper> children, String... sampleTypeNames)
        throws Exception {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(site.getAppService());
        ct.setName(name);
        ct.setNameShort(nameShort);
        ct.setTopLevel(topLevel);
        ct.setSite(site);
        ct.setRowCapacity(rows);
        ct.setColCapacity(cols);
        ct.setActivityStatus("Active");
        ct.setChildLabelingScheme(childLabelingScheme);

        if (children != null) {
            ct.setChildContainerTypeCollection(children);
        }

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
        containerTypeMap.put(name, ct);
        System.out.println("Added container type " + name + " (" + nameShort
            + ")");
        return ct;
    }
}
