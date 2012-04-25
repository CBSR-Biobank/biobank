package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.permission.patient.PatientMergePermission;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.views.CollectionView;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class PatientMergeHandler extends LogoutSensitiveHandler {
    private static final I18n i18n = I18nFactory
        .getI18n(PatientMergeHandler.class);

    private static BgcLogger logger = BgcLogger
        .getLogger(PatientMergeHandler.class.getName());

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        try {
            AdapterBase.openForm(
                new FormInput(CollectionView.getCurrentPatient()),
                "edu.ualberta.med.biobank.forms.PatientMergeForm", true); //$NON-NLS-1$
        } catch (Exception exp) {
            logger.error("Error while opening the patient merge form", exp); //$NON-NLS-1$
        }
        return null;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isEnabled() {
        try {
            allowed =
                SessionManager.getAppService().isAllowed(
                    new PatientMergePermission(CollectionView
                        .getCurrentPatient().getId(), null));
            return SessionManager.getInstance().getSession() != null &&
                allowed;
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError(
                // dialog message
                i18n.tr("Unable to retrieve permissions"));
            return false;
        }
    }
}
