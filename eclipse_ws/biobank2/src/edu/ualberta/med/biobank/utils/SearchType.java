package edu.ualberta.med.biobank.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.PeListViewForm;
import edu.ualberta.med.biobank.forms.SpecimenListViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.util.AdapterFactory;

public enum SearchType {
    INVENTORY_ID("Inventory ID") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            List<SpecimenWrapper> res = new ArrayList<SpecimenWrapper>();
            SpecimenWrapper specimen = SpecimenWrapper.getSpecimen(
                SessionManager.getAppService(), searchString);
            if (specimen != null) {
                res.add(specimen);
            }
            return res;
        }

    },

    SPECIMEN_POSITION("Specimen position") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper)
                return SpecimenWrapper.getSpecimensInSiteWithPositionLabel(
                    SessionManager.getAppService(), (SiteWrapper) center,
                    searchString);
            return Collections.emptyList();
        }
    },

    SPECIMEN_NON_ACTIVE("Specimens - non active") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            List<SpecimenWrapper> specimens = SpecimenWrapper
                .getSpecimensNonActiveInCenter(SessionManager.getAppService(),
                    center);
            return specimens;
        }

        @Override
        public void processResults(List<? extends ModelWrapper<?>> res) {
            Assert.isNotNull(res);
            FormInput input = new FormInput(res, "Specimen List");
            try {
                PlatformUI.getWorkbench().getActiveWorkbenchWindow()
                    .getActivePage()
                    .openEditor(input, SpecimenListViewForm.ID, false);
            } catch (PartInitException e) {
                logger.error("Can't open form with id "
                    + SpecimenListViewForm.ID, e);
            }
        }

        @Override
        protected void openResult(ModelWrapper<?> wrapper) {
        }
    },

    CONTAINER_LABEL("Container label") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper)
                return ContainerWrapper.getContainersInSite(
                    SessionManager.getAppService(), (SiteWrapper) center,
                    searchString);
            return Collections.emptyList();
        }
    },

    CONTAINER_PRODUCT_BARCODE("Container product barcode") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            if (center instanceof SiteWrapper) {
                ContainerWrapper container = ContainerWrapper
                    .getContainerWithProductBarcodeInSite(
                        SessionManager.getAppService(), (SiteWrapper) center,
                        searchString);
                if (container != null) {
                    return Arrays.asList(container);
                }
            }
            return null;
        }
    },

    WORKSHEET("Worksheet") {
        @Override
        public List<? extends ModelWrapper<?>> search(String searchString,
            CenterWrapper<?> center) throws Exception {
            List<ProcessingEventWrapper> pvs = ProcessingEventWrapper
                .getProcessingEventsWithWorksheet(
                    SessionManager.getAppService(), searchString);

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
                    .openEditor(input, PeListViewForm.ID, false);
            } catch (PartInitException e) {
                logger.error("Can't open form with id " + PeListViewForm.ID, e);
            }
        }
    };

    private static BgcLogger logger = BgcLogger
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
        CenterWrapper<?> center) throws Exception;

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