package edu.ualberta.med.biobank.common.scanprocess.data;

import java.util.Locale;

import edu.ualberta.med.biobank.common.security.User;
import edu.ualberta.med.biobank.server.scanprocess.LinkProcess;
import edu.ualberta.med.biobank.server.scanprocess.ServerProcess;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class LinkProcessData implements ProcessData {

    private static final long serialVersionUID = 1L;

    @Override
    public ServerProcess getProcessInstance(
        WritableApplicationService appService, User user, Locale locale) {
        return new LinkProcess(appService, this, user, locale);
    }

}
