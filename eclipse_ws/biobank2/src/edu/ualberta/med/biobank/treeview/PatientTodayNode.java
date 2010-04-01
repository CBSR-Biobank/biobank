package edu.ualberta.med.biobank.treeview;

import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.views.PatientAdministrationView;

public class PatientTodayNode extends AbstractTodayNode {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(PatientTodayNode.class.getName());

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
    public void performExpand() {
        removeAll();
        if (!SessionManager.getInstance().isAllSitesSelected()) {
            try {
                List<PatientWrapper> todayPatients = PatientWrapper
                    .getPatientsInTodayShipments(
                        SessionManager.getAppService(), SessionManager
                            .getInstance().getCurrentSite());
                for (PatientWrapper patient : todayPatients) {
                    PatientAdministrationView.addPatientToNode(this, patient);
                }
            } catch (final RemoteAccessException exp) {
                BioBankPlugin.openRemoteAccessErrorMessage();
            } catch (Exception e) {
                logger.error("Error while getting today's patients", e);
            }
        }
    }
}
