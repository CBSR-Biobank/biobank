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

    private static Map<String, ContainerTypeWrapper> containerTypeMap = new HashMap<String, ContainerTypeWrapper>();

    private static String[] biopsyPallet96SampleTypes = new String[] {
        "Colon, D", "Stomach, B", "Stomach, A", "Duodenum", "Jejunum", "Ileum",
        "Colon, A", "Colon, T", };

    private static String[] box81SampleTypes = new String[] { "BC", "C Urine",
        "CBMC RNA", "CBMC", "CDPA Plasma", "Cells", "Dialysate", "Effluent",
        "F Nails", "F Urine", "HB", "Lith Hep Plasma", "Meconium", "PF Plasma",
        "Paxgene", "Plasma SH", "Plasma", "RNA Biopsy", "S Water", "Serum B",
        "Serum Pel", "Serum", "T Nails", "T Water", "Urine", "WB DMSO",
        "WB Plasma", "WB RNA", "WB Serum", "WBE", "WBlood", "Z Urine" };

    private static String[] cellPallet96SampleTypes = new String[] { "Cells" };

    private static String[] ftaBinSampleTypes = new String[] { "DNA(Blood)",
        "DNA (WBC)" };

    private static String[] hairBinSampleTypes = new String[] { "Hair" };

    private static String[] pallet96SampleTypes = new String[] { "BC",
        "C Urine", "CBMC RNA", "CBMC", "CDPA Plasma", "Cells", "Dialysate",
        "Effluent", "F Nails", "F Urine", "HB", "Lith Hep Plasma", "Meconium",
        "PF Plasma", "Paxgene", "Plasma SH", "Plasma", "R-ColonA", "R-ColonD",
        "R-ColonT", "R-Duodenum", "R-Ilieum", "R-Jejunum", "R-StomachA",
        "R-StomachB", "RNA Biopsy", "RNA Biopsy", "S Water", "Serum B",
        "Serum Pel", "Serum", "T Nails", "T Water", "Urine", "WB DMSO",
        "WB Plasma", "WB RNA", "WB Serum", "WBE", "WBlood", "Z Urine" };

    public static ContainerTypeWrapper getContainerType(String name)
        throws Exception {
        ContainerTypeWrapper type = containerTypeMap.get(name);
        if (type == null) {
            throw new Exception("container type " + name + " not in database");
        }
        return type;
    }

    public static void createContainerTypes(SiteWrapper site) throws Exception {
        createFreezerTypes(site);
        createCabinetTypes(site);
    }

    private static void createFreezerTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper biopsyPallet96 = addContainerType(site,
            "Biopsy Pallet 96", "BP96", null, "Active", false, 1, 8, 12, null,
            biopsyPallet96SampleTypes);

        ContainerTypeWrapper box81 = addContainerType(site, "Box 81", "B81",
            null, "Active", false, 1, 9, 9, null, box81SampleTypes);

        ContainerTypeWrapper cellPallet96 = addContainerType(site,
            "Cell Pallet 96", "CP96", null, "Active", false, 1, 9, 9, null,
            cellPallet96SampleTypes);

        ContainerTypeWrapper pallet96 = addContainerType(site, "Pallet 96",
            "P96", null, "Active", false, 1, 8, 12, null, pallet96SampleTypes);

        ContainerTypeWrapper hotel13 = addContainerType(site, "Hotel 13",
            "H13", null, "Active", false, 3, 13, 1, Arrays
                .asList(new ContainerTypeWrapper[] { pallet96 }));

        ContainerTypeWrapper hotel17 = addContainerType(site, "Hotel 17",
            "H17", null, "Active", false, 3, 17, 1, Arrays
                .asList(new ContainerTypeWrapper[] { box81 }));

        ContainerTypeWrapper hotel18 = addContainerType(site, "Hotel 18",
            "H18", null, "Active", false, 3, 18, 1, Arrays
                .asList(new ContainerTypeWrapper[] { box81 }));

        ContainerTypeWrapper hotel19 = addContainerType(site, "Hotel 19",
            "H19", null, "Active", false, 3, 19, 1, Arrays
                .asList(new ContainerTypeWrapper[] { biopsyPallet96,
                    cellPallet96, pallet96 }));

        addContainerType(site, "Freezer 3x10", "F3x10", -80.0, "Active", true,
            2, 3, 10, Arrays.asList(new ContainerTypeWrapper[] { hotel17 }));

        addContainerType(site, "Freezer 4x12", "F4x12", -80.0, "Active", true,
            2, 4, 12, Arrays.asList(new ContainerTypeWrapper[] { hotel18 }));

        addContainerType(site, "Freezer 5x9", "F5x9", -80.0, "Active", true, 2,
            5, 9, Arrays
                .asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));

        addContainerType(site, "Freezer 6x12", "F6x12", -80.0, "Active", true,
            2, 6, 12, Arrays.asList(new ContainerTypeWrapper[] { hotel13,
                hotel19 }));

        addContainerType(site, "Freezer 4x6", "F4x6", -80.0, "Active", true, 2,
            4, 6, Arrays
                .asList(new ContainerTypeWrapper[] { hotel13, hotel19 }));
    }

    private static void createCabinetTypes(SiteWrapper site) throws Exception {
        ContainerTypeWrapper ftaBin = addContainerType(site, "FTA Bin", "FBin",
            null, "Active", false, 2, 120, 1, null, ftaBinSampleTypes);

        ContainerTypeWrapper hairBin = addContainerType(site, "Hair Bin",
            "HBin", null, "Active", false, 2, 120, 1, null, hairBinSampleTypes);

        ContainerTypeWrapper ftaBinLoersch = addContainerType(site,
            "FTA Bin Loersch", "FBinL", null, "Active", false, 2, 68, 1, null,
            ftaBinSampleTypes);

        ContainerTypeWrapper drawer36 = addContainerType(site, "Drawer 36",
            "D36", null, "Active", false, 3, 36, 1, Arrays
                .asList(new ContainerTypeWrapper[] { ftaBin, ftaBinLoersch,
                    hairBin }));
        addContainerType(site, "Cabinet 4 drawer", "Cabinet 4", null, "Active",
            true, 2, 4, 1, Arrays
                .asList(new ContainerTypeWrapper[] { drawer36 }));
    }

    private static ContainerTypeWrapper addContainerType(SiteWrapper site,
        String name, String nameShort, Double temperature,
        String activityStatusName, boolean topLevel, int childLabelingScheme,
        int rows, int cols, List<ContainerTypeWrapper> children,
        String... sampleTypeNames) throws Exception {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(site.getAppService());
        ct.setName(name);
        ct.setNameShort(nameShort);
        ct.setDefaultTemperature(temperature);
        ct.setActivityStatus(CbsrSite.getActivityStatus(activityStatusName));
        ct.setTopLevel(topLevel);
        ct.setSite(site);
        ct.setRowCapacity(rows);
        ct.setColCapacity(cols);
        ct.setChildLabelingScheme(childLabelingScheme);

        if (children != null) {
            ct.addChildContainerTypes(children);
        }

        List<SampleTypeWrapper> list = new ArrayList<SampleTypeWrapper>();
        for (String sampleTypeName : sampleTypeNames) {
            SampleTypeWrapper sampleType = CbsrSite
                .getSampleType(sampleTypeName);
            list.add(sampleType);
        }
        ct.addSampleTypes(list);
        ct.persist();
        ct.reload();
        containerTypeMap.put(name, ct);
        return ct;
    }
}
