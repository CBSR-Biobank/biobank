package edu.ualberta.med.biobank.treeview.patient;

import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.patient.PatientSearchAction.SearchedPatientInfo;
import edu.ualberta.med.biobank.common.permission.study.StudyReadPermission;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.admin.NewStudyAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyWithPatientAdapter extends NewStudyAdapter {

    private StudyNodeInfo spInfo;

    public StudyWithPatientAdapter(AbstractAdapterBase parent,
        StudyNodeInfo spInfo) {
        super(parent, spInfo.study);
        this.spInfo = spInfo;

        init();
    }

    @Override
    public void init() {
        try {
            Integer id = spInfo.study.getId();
            this.isReadable =
                SessionManager.getAppService().isAllowed(
                    new StudyReadPermission(id));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Permission Error",
                "Unable to retrieve user permissions");
        }
    }

    @Override
    public List<AbstractAdapterBase> search(Class<?> searchedClass,
        Integer objectId) {
        return findChildFromClass(searchedClass, objectId, Patient.class);
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        return spInfo.patients;
    }

    @Override
    protected PatientAdapter createChildNode() {
        return new PatientAdapter(this, null);
    }

    @Override
    protected PatientAdapter createChildNode(Object child) {
        return new PatientAdapter(this, (SearchedPatientInfo) child);
    }

    @Override
    protected void removeChildInternal(Integer id) {
        spInfo.patients.remove(id);
    }
}
