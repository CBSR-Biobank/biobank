package edu.ualberta.med.biobank.widgets.report;

import java.util.Map;

public interface SelectableFilterValueWidget extends FilterValueWidget {
    public void setOptions(Map<String, String> options);
}
