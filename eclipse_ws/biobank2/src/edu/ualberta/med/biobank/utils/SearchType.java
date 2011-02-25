package edu.ualberta.med.biobank.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.AliquotListViewForm;
import edu.ualberta.med.biobank.forms.CeListViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;

public enum SearchType {
    INVENTORY_ID("Inventory ID") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            SiteWrapper site) throws Exception {
            List<AliquotWrapper> res = new ArrayList<AliquotWrapper>();
            AliquotWrapper aliquot = AliquotWrapper.getAliquot(
                SessionManager.getAppService(), searchString,
                SessionManager.getUser());
            if (aliquot != null) {
                res.add(aliquot);
            }
            return res;
        }

    },

    ALIQUOT_POSITION("Aliquot position") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            SiteWrapper site) throws Exception {
            return AliquotWrapper.getAliquotsInSiteWithPositionLabel(
                SessionManager.getAppService(), site, searchString);
        }
    },

    ALIQUOT_NON_ACTIVE("Aliquots - non active") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            SiteWrapper site) throws Exception {
            List<AliquotWrapper> aliquots = AliquotWrapper
                .getAliquotsNonActiveInSite(SessionManager.getAppService(),
                    site);
            return aliquots;
        }

        @Override
        public void processResults(List<? extends ModelWrapper<?>> res) {
            Assert.isNotNull(res);
            FormInput input = new FormInput(res, "Aliquot List");
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, AliquotListViewForm.ID, false);
            } catch (PartInitException e) {
                logger.error("Can't open form with id "
                    + AliquotListViewForm.ID, e);
            }
        }

        @Override
        protected void openResult(ModelWrapper<?> wrapper) {
        }
    },

    CONTAINER_LABEL("Container label") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            SiteWrapper site) throws Exception {
            return ContainerWrapper.getContainersInSite(
                SessionManager.getAppService(), site, searchString);
        }
    },

    CONTAINER_PRODUCT_BARCODE("Container product barcode") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            SiteWrapper site) throws Exception {
            ContainerWrapper container = ContainerWrapper
                .getContainerWithProductBarcodeInSite(
                    SessionManager.getAppService(), site, searchString);
            if (container != null) {
                return Arrays.asList(container);
            }
            return null;
        }
    },

    WORKSHEET("Worksheet") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            SiteWrapper site) throws Exception {

            List<ProcessingEventWrapper> pvs = ProcessingEventWrapper
                .getPatientVisitsWithWorksheet(SessionManager.getAppService(),
                    searchString);

            if (pvs == null)
                return null;
            return pvs;

        }

        @Override
        public void processResults(List<? extends ModelWrapper<?>> res) {
            Assert.isNotNull(res);
            FormInput input = new FormInput(res, "Patient Visit List");
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, CeListViewForm.ID, false);
            } catch (PartInitException e) {
                logger.error("Can't open form with id "
                    + AliquotListViewForm.ID, e);
            }
        }
    };

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SearchType.class.getName());

    private String label;

    private SearchType(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }

    public abstract List<? extends ModelWrapper<?>> search(String searchString,
        SiteWrapper site) throws Exception;

    public void processResults(List<? extends ModelWrapper<?>> res) {
        Assert.isNotNull(res);
        int size = res.size();
        if (size == 1) {
            openResult(res.get(0));
        } else {
            boolean open = MessageDialog
                .openQuestion(PlatformUI.getWorkbench()
                    .getActiveWorkbenchWindow().getShell(), "Search Result",
                    "Found " + size
                        + " results. Do you want to open all of them ?");
            if (open) {
                for (ModelWrapper<?> wrapper : res) {
                    openResult(wrapper);
                }
            }
        }
    }

    protected void openResult(ModelWrapper<?> wrapper) {
        AdapterBase adapter = AdapterFactory.getAdapter(wrapper);
        if (adapter != null) {
            adapter.performDoubleClick();
        }
    }

}