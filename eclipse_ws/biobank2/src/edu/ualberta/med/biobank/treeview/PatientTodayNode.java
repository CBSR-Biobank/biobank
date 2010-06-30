package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientTodayNode extends AbstractTodayNode {

    public PatientTodayNode(AdapterBase parent, int id) {
        super(parent, id);
        setName("From today's shipments");
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof StudyWrapper);
        return new StudyAdapter(this, (StudyWrapper) child);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new StudyAdapter(this, null);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    protected List<? extends ModelWrapper<?>> getTodayElements()
        throws ApplicationException {
        return PatientWrapper.getPatientsInTodayShipments(SessionManager
            .getAppService(), SessionManager.getInstance().getCurrentSite());
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        if (child instanceof PatientWrapper) {
            return parent.equals(((PatientWrapper) child).getStudy());
        }
        return false;
    }

}
