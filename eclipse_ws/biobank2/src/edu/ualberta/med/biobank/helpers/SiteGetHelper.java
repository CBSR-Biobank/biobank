package edu.ualberta.med.biobank.helpers;

import java.util.List;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.model.Site;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SiteGetHelper implements Runnable {

    public static final int LOAD_STUDIES = 1 << 0;
    public static final int LOAD_CLINICS = 1 << 1;
    public static final int LOAD_STORAGE_CONTAINERS = 1 << 2;
    public static final int LOAD_ALL = LOAD_STUDIES & LOAD_CLINICS & LOAD_STORAGE_CONTAINERS;
    
    private WritableApplicationService appService;
    
    private int id;
    private int flags;
    private Site site;
    
    
    public SiteGetHelper(WritableApplicationService appService, int id, int flags) {
        this.appService = appService;
        this.id = id;
    }

    @Override
    public void run() {
        try {
            site = new Site();
            site.setId(id);
            List<Site> result = appService.search(Site.class, site);
            Assert.isTrue(result.size() == 1);
            
            site = result.get(0);
            site.getAddress();
            
            if ((flags & LOAD_STUDIES) != 0) { 
                site.getStudyCollection();
            }

            if ((flags & LOAD_CLINICS) != 0) { 
                site.getClinicCollection();
            }

            if ((flags & LOAD_STORAGE_CONTAINERS) != 0) { 
                site.getStorageTypeCollection();
            }
        }
        catch (final RemoteConnectFailureException exp) {
            Display.getDefault().asyncExec(new Runnable() {
                public void run() {
                    MessageDialog.openError(
                            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), 
                            "Connection Attempt Failed", 
                    "Could not connect to server. Make sure server is running.");
                }
            });
        }
        catch (Exception exp) {
            exp.printStackTrace();
        }
    }
    
    public Site getResult() {
        return site;
    }

}
