package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.VarCharLengths;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.CheckFieldLimitsException;
import edu.ualberta.med.biobank.common.peer.UserPeer;
import edu.ualberta.med.biobank.common.wrappers.tasks.PreQueryTask;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;
import gov.nih.nci.system.applicationservice.ApplicationException;

// FIXME not very nice. Should be better is we remove CSM completely and use our User table for authentication as well
public class CSMUserIdPreQueryTask implements PreQueryTask {
    private final UserWrapper user;

    public CSMUserIdPreQueryTask(UserWrapper user) {
        this.user = user;
    }

    @Override
    public void beforeExecute() throws BiobankException {
        Long csmId;
        try {
            Integer max = VarCharLengths.getMaxSize(User.class,
                UserPeer.LOGIN.getName());
            if (max != null && user.getLogin().length() > max) {
                throw new CheckFieldLimitsException(UserPeer.LOGIN.getName(),
                    max, user.getLogin());
            }
            csmId = ((BiobankApplicationService) user.getAppService())
                .persistUser(user.getWrappedObject(), user.getPassword());
            if (user.isNew())
                user.setCsmUserId(csmId);
        } catch (ApplicationException e) {
            throw new BiobankException(e);
        }
    }
}