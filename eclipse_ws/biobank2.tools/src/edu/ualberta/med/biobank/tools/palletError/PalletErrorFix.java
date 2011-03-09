package edu.ualberta.med.biobank.tools.palletError;

import java.util.List;

import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

/**
 * Fix for: - Issue 735: Pallet 05BL12 on CBSR server has no parent - Issue 736:
 * Pallet SSAD11 appears twice in container table on CBSR server
 * 
 */
public class PalletErrorFix {

    private static String USAGE = "Usage: calgarysite [options]\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    @SuppressWarnings("unused")
    private GenericAppArgs appArgs = null;

    private static WritableApplicationService appService;

    private SiteWrapper cbsrSite;

    public PalletErrorFix(GenericAppArgs appArgs) throws Exception {
        this.appArgs = appArgs;

        String prefix = "https://";
        if (appArgs.port == 8080)
            prefix = "http://";

        String serverUrl = prefix + appArgs.hostname + ":" + appArgs.port
            + "/biobank2";

        appService = ServiceConnection.getAppService(serverUrl,
            appArgs.username, appArgs.password);

        cbsrSite = null;
        for (SiteWrapper site : SiteWrapper.getSites(appService)) {
            if (site.getNameShort().equals("CBSR")) {
                cbsrSite = site;
            }
        }

        if (cbsrSite == null) {
            throw new Exception("CBSR site not found");
        }

        fix735();
        fix736();
    }

    private void fix735() throws Exception {
        List<ContainerWrapper> containers = ContainerWrapper
            .getContainersInSite(appService, cbsrSite, "05BL12");
        if (containers.size() != 1) {
            throw new Exception("Container 05BL12 not found");
        }

        ContainerWrapper container = containers.get(0);

        containers = ContainerWrapper.getContainersInSite(appService, cbsrSite,
            "05BL");
        if (containers.size() != 1) {
            throw new Exception("Container 05BL not found");
        }

        ContainerWrapper parent = containers.get(0);

        parent.addChild("12", container);
        parent.persist();
        System.out
            .println("Container 05BL12 assigned correct position in parent");
    }

    private void fix736() throws Exception {
        ContainerWrapper sourceContainer = null, destContainer = null;

        for (ContainerWrapper container : ContainerWrapper.getContainersInSite(
            appService, cbsrSite, "SSAD11")) {
            if ((container.getParentContainer() == null)
                && (container.getSpecimens().size() > 0)) {
                sourceContainer = container;
            } else if (container.getParentContainer() != null) {
                destContainer = container;
            }
        }

        if (sourceContainer == null) {
            throw new Exception("source container not found");
        }

        if (destContainer == null) {
            throw new Exception("dest container not found");
        }

        sourceContainer.moveAliquots(destContainer);
        sourceContainer.reload();
        sourceContainer.delete();
        destContainer.persist();
        System.out.println("aliquots moved and SSAD11 with no parent deleted");
    }

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new PalletErrorFix(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
