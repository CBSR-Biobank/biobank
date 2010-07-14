package edu.ualberta.med.biobank.client.reports;

import java.util.List;

import edu.ualberta.med.biobank.common.util.ReportOption;

public interface IReport {

    public String[] getColumnNames();

    public String getDescription();

    public List<ReportOption> getOptions();

    public String getName();

}
