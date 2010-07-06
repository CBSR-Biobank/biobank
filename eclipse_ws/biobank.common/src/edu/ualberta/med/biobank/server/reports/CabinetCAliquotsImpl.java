package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;

public class CabinetCAliquotsImpl extends CAliquots {

    public CabinetCAliquotsImpl(List<Object> parameters,
        List<ReportOption> options) {
        super("%Cabinet%", parameters, options);
    }

}