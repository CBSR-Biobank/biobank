package edu.ualberta.med.biobank.tools.cli;

import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import edu.ualberta.med.biobank.tools.SessionProvider;

public interface CliProvider {

    public BiobankApplicationService getAppService();

    public SessionProvider getSessionProvider();

}
