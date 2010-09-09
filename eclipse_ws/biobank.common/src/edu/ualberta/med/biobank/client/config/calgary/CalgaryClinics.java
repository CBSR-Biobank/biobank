package edu.ualberta.med.biobank.client.config.calgary;

import java.util.HashMap;

import edu.ualberta.med.biobank.client.config.ConfigClinics;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;

public class CalgaryClinics extends ConfigClinics {

    public CalgaryClinics(SiteWrapper site) throws Exception {
        super(site);
        clinicsMap = new HashMap<String, ClinicWrapper>();
        contactsMap = new HashMap<String, ContactWrapper>();

        addClinic("CL1-Foothills", "CL1-Foothills", false,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, null,
            "Foothills Medical Centre", "1403 29 Street", "Calgary", "Alberta",
            "t2n2t9");

        addContact("CL1-Foothills", "Andrijana Lawton", "Research Coordinator",
            "403-210-7356", "", "Andrijana.Lawton@albertahealthservices.ca");
    }

}
