package edu.ualberta.med.biobank.server.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.reports2.ReportOption;

public class CabinetCAliquotsImpl extends CAliquots {

    public CabinetCAliquotsImpl(List<Object> parameters,
        List<ReportOption> options) {
        super("%Cabinet%", parameters, options);
    }

}