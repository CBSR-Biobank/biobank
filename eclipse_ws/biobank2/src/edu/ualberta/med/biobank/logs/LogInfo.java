package edu.ualberta.med.biobank.logs;

import edu.ualberta.med.biobank.forms.CabinetLinkAssignEntryForm;
import edu.ualberta.med.biobank.forms.ScanAssignEntryForm;
import edu.ualberta.med.biobank.forms.ScanLinkEntryForm;

/**
 * Activity log information. Used for Activity logs of ScanLink, ScanAssign and
 * CabinetLinkAssign.
 * 
 * @see ActivityLogAppender
 * @see ScanLinkEntryForm
 * @see ScanAssignEntryForm
 * @see CabinetLinkAssignEntryForm
 */
public class LogInfo {

    private String info;

    public LogInfo(String info) {
        this.info = info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }

}
