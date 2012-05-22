package edu.ualberta.med.biobank.treeview.dispatch;

import java.util.List;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchRetrievalAction;
import edu.ualberta.med.biobank.common.wrappers.DispatchWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class SentInTransitDispatchGroup extends AbstractDispatchGroup {
    private static final I18n i18n = I18nFactory
        .getI18n(SentInTransitDispatchGroup.class);

    @SuppressWarnings("nls")
    public SentInTransitDispatchGroup(AdapterBase parent, int id) {
        super(parent, id, i18n.tr("In transit"));
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            SessionManager.getAppService().doAction(
                new DispatchRetrievalAction(DispatchState.IN_TRANSIT,
                    SessionManager.getUser().getCurrentWorkingCenter().getId(),
                    true, true)).getList(), DispatchWrapper.class);
    }
}
