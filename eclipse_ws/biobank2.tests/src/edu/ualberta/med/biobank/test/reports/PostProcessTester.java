package edu.ualberta.med.biobank.test.reports;

import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.Collection;
import java.util.List;

public interface PostProcessTester {
    public List<Object> postProcess(WritableApplicationService appService,
        Collection<Object> results);
}
