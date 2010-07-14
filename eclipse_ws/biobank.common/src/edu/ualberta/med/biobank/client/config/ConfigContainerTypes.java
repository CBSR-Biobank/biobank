package edu.ualberta.med.biobank.client.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.client.config.calgary.CalgarySite;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class ConfigContainerTypes {

    protected static Map<String, ContainerTypeWrapper> containerTypeMap = new HashMap<String, ContainerTypeWrapper>();

    protected static String[] biopsyPallet96SampleTypes = new String[] {
        "Colon, D", "Stomach, B", "Stomach, A", "Duodenum", "Jejunum", "Ileum",
        "Colon, A", "Colon, T", };

    protected static String[] box81SampleTypes = new String[] { "BC",
        "C Urine", "CBMC RNA", "CBMC", "CDPA Plasma", "Cells", "Dialysate",
        "Effluent", "F Nails", "F Urine", "HB", "Lith Hep Plasma", "Meconium",
        "PF Plasma", "Paxgene", "Plasma SH", "Plasma", "RNA Biopsy", "S Water",
        "Serum B", "Serum Pel", "Serum", "T Nails", "T Water", "Urine",
        "WB DMSO", "WB Plasma", "WB RNA", "WB Serum", "WBE", "WBlood",
        "Z Urine" };

    protected static String[] cellPallet96SampleTypes = new String[] { "Cells" };

    protected static String[] ftaBinSampleTypes = new String[] { "DNA(Blood)",
        "DNA (WBC)" };

    protected static String[] hairBinSampleTypes = new String[] { "Hair" };

    protected static String[] pallet96SampleTypes = new String[] { "BC",
        "C Urine", "CBMC RNA", "CBMC", "CDPA Plasma", "Cells", "Colon, A",
        "Colon, D", "Colon, T", "Dialysate", "Duodenum", "Effluent", "F Nails",
        "F Urine", "HB", "Ileum", "Jejunum", "Lith Hep Plasma", "Meconium",
        "PF Plasma", "Paxgene", "Plasma SH", "Plasma", "R-ColonA", "R-ColonA",
        "R-ColonD", "R-ColonD", "R-ColonT", "R-ColonT", "R-Duodenum",
        "R-Duodenum", "R-Ilieum", "R-Ilieum", "R-Jejunum", "R-StomachA",
        "R-StomachA", "R-StomachB", "R-StomachB", "RNA Biopsy", "RNA Biopsy",
        "S Water", "Serum B", "Serum Pel", "Serum", "Stomach, A", "Stomach, B",
        "T Nails", "T Water", "Urine", "WB DMSO", "WB Plasma", "WB RNA",
        "WB Serum", "WBE", "WBlood", "Z Urine",

        "Colon, D", "Stomach, B", "Stomach, A", "Duodenum", "Jejunum", "Ileum",
        "Colon, A", "Colon, T",

        "LH PFP 200", "UrineC900", "PlasmaE800", "P100 500", "PlasmaL500",
        "LH PFP 500", "PlasmaE200", "DNA L 1000", "SerumG500", "PlasmaL200",
        "DNA E 1000", "PlasmaE500", "UrineSA900", "PlasmaE250", "UrineSA700" };

    protected ConfigContainerTypes(SiteWrapper site) throws Exception {
        if (site == null) {
            throw new Exception("site is null");
        }
        site.reload();
    }

    public ContainerTypeWrapper getContainerType(String name) throws Exception {
        ContainerTypeWrapper type = containerTypeMap.get(name);
        if (type == null) {
            throw new Exception("container type " + name + " not in database");
        }
        return type;
    }

    protected static ContainerTypeWrapper addContainerType(SiteWrapper site,
        String name, String nameShort, Double temperature,
        String activityStatusName, boolean topLevel, int childLabelingScheme,
        int rows, int cols, List<ContainerTypeWrapper> children,
        String... sampleTypeNames) throws Exception {
        ContainerTypeWrapper ct = new ContainerTypeWrapper(site.getAppService());
        ct.setName(name);
        ct.setNameShort(nameShort);
        ct.setDefaultTemperature(temperature);
        ct.setActivityStatus(CalgarySite.getActivityStatus(activityStatusName));
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
            SampleTypeWrapper sampleType = CalgarySite
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
