package edu.ualberta.med.biobank.treeview.request;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.request.RequestRetrievalAction;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.RequestWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class ReceivingRequestGroup extends AbstractRequestGroup {
    private static final I18n i18n = I18nFactory
        .getI18n(ReceivingRequestGroup.class);

    @SuppressWarnings("nls")
    public ReceivingRequestGroup(AdapterBase parent, int id) {
        super(parent, id, i18n.tr("Pending Requests"));
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ModelWrapper.wrapModelCollection(
            SessionManager.getAppService(),
            SessionManager.getAppService().doAction(
                new RequestRetrievalAction(SessionManager.getUser()
                    .getCurrentWorkingCenter().getWrappedObject())).getList(),
            RequestWrapper.class);
    }

}
