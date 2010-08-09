package edu.ualberta.med.biobank.tools.calgaryconfig;

import edu.ualberta.med.biobank.client.config.calgary.CalgaryClinics;
import edu.ualberta.med.biobank.client.config.calgary.CalgaryContainerTypes;
import edu.ualberta.med.biobank.client.config.calgary.CalgaryContainers;
import edu.ualberta.med.biobank.client.config.calgary.CalgarySite;
import edu.ualberta.med.biobank.client.config.calgary.CalgaryStudies;
import edu.ualberta.med.biobank.client.util.ServiceConnection;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class CalgaryConfig {

    private static String USAGE = "Usage: calgarysite [options]\n\n"
        + "Options\n" + "  -v, --verbose    Shows verbose output";

    @SuppressWarnings("unused")
    private GenericAppArgs appArgs = null;

    private static WritableApplicationService appService;

    protected SiteWrapper calgarySite;

    protected CalgaryClinics configClinics;

    public CalgaryConfig(GenericAppArgs appArgs) throws Exception {
        this.appArgs = appArgs;

        String prefix = "https://";
        if (appArgs.port == 8080)
            prefix = "http://";

        String serverUrl = prefix + appArgs.hostname + ":" + appArgs.port
            + "/biobank2";

        appService = ServiceConnection.getAppService(serverUrl,
            appArgs.username, appArgs.password);

        CalgarySite.deleteConfiguration(appService);
        calgarySite = CalgarySite.addSite(appService);
        configClinics = new CalgaryClinics(calgarySite);
        new CalgaryStudies(calgarySite);
        new CalgaryContainerTypes(calgarySite);
        new CalgaryContainers(calgarySite);

    }

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new CalgaryConfig(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
