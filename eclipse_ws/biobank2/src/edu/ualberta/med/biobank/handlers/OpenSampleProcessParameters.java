package edu.ualberta.med.biobank.handlers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.commands.IParameterValues;

public class OpenSampleProcessParameters implements IParameterValues {

    @Override
    public Map<String, String> getParameterValues() {
        Map<String, String> params = new HashMap<String, String>();
        params.put("Scan Link",
            "edu.ualberta.med.biobank.forms.ScanLinkEntryForm");
        params.put("Scan Assign",
            "edu.ualberta.med.biobank.forms.ScanAssignEntryForm");
        params.put("Cabinet Link Assign",
            "edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm");
        return params;
    }

}
