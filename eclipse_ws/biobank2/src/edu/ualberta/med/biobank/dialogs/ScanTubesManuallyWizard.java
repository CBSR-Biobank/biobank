package edu.ualberta.med.biobank.dialogs;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.collections.BidiMap;
import org.apache.commons.collections.bidimap.DualHashBidiMap;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Specimen;

/**
 * This dialog prompts the user to enter one or more inventory IDs decoded from
 * aliquot tubes. It is assumed that the user will use a hand scanner to decode
 * the 2D barcode at the bottom of each tube.
 * 
 * A set of inavlid inventory IDs should be passed to the constructor. These are
 * inventory IDs that the user should not be allowed to enter.
 */
public class ScanTubesManuallyWizard extends Wizard {
    private static final I18n i18n = I18nFactory.getI18n(ScanTubesManuallyWizard.class);

    protected static BgcLogger log = BgcLogger.getLogger(ScanTubesManuallyWizard.class.getName());

    private final Set<String> labels;
    private final BidiMap existingInventoryIds = new DualHashBidiMap();
    private final Map<String, String> resultIventoryIdsByLabel = new HashMap<String, String>();

    public static Map<String, String> getInventoryIds(Shell parentShell, Set<String> labels,
        Map<String, String> invalidInventoryIdsByLabel) {
        ScanTubesManuallyWizard wizard = new ScanTubesManuallyWizard(labels,
            invalidInventoryIdsByLabel);
        WizardDialog dialog = new WizardDialog(parentShell, wizard);
        dialog.create();
        dialog.open();
        return wizard.getInventoryIdsByLabel();
    }

    private class ScanSingleTubePage extends WizardPage {

        private final String labelToScan;
        private Text text;
        private Composite area;

        @SuppressWarnings("nls")
        protected ScanSingleTubePage(String labelToScan) {
            super(labelToScan);
            this.labelToScan = labelToScan;
            setTitle(labelToScan);

            // TR: wizard dialog dialog page description message
            setDescription(i18n.tr("Scan the tube at position {0}", labelToScan));
            setControl(text);
        }

        @SuppressWarnings("nls")
        @Override
        public void createControl(Composite parent) {
            area = new Composite(parent, SWT.NONE);
            GridLayout layout = new GridLayout(2, false);
            layout.horizontalSpacing = 10;
            area.setLayout(layout);
            area.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

            Label label = new Label(area, SWT.LEFT);
            label.setText(Specimen.PropertyName.INVENTORY_ID.toString() + ":");

            text = new Text(area, SWT.BORDER | SWT.SINGLE);
            text.setText("");
            text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            text.addKeyListener(new KeyListener() {

                @Override
                public void keyPressed(KeyEvent e) {
                    // do nothing
                }

                @Override
                public void keyReleased(KeyEvent e) {
                    String inventoryId = text.getText();

                    // TODO: use status messages here instead
                    // see http://www.eclipse.org/articles/article.php?file=Article-JFaceWizards/index.html

                    // check if this value already exists
                    String label = (String) existingInventoryIds.getKey(inventoryId);
                    log.debug("keyReleased: label: " + label + ", inventoryId: " + inventoryId);

                    if ((label != null) && !label.equals(labelToScan)) {
                        BgcPlugin.openAsyncError(
                            // TR: dialog title
                            i18n.tr("Tube Scan Error"),
                            // TR: dialog message
                            i18n.tr("The value entered already exists at position {0}", label));
                        text.setFocus();
                        text.setSelection(0, inventoryId.length());
                        setPageComplete(false);
                        return;
                    }

                    setPageComplete(!inventoryId.isEmpty());
                }

            });

            setControl(area);
            text.setFocus();
            setPageComplete(false);
        }

        @SuppressWarnings("nls")
        @Override
        public IWizardPage getNextPage() {
            log.debug("getNextPage: labelToScan: " + labelToScan + ", value: " + text.getText());
            existingInventoryIds.put(labelToScan, text.getText());
            return super.getNextPage();
        }

    };

    /**
     * 
     * @param parentShell the parent SWT shell
     * @param labels the labels that the user should be prompted for. The order
     *            is important.
     * @param existingInventoryIdsByLabel a map of inventory IDs that the user should not enter.
     *            The key for the map is the position label where this inventory ID is present.
     */
    @SuppressWarnings("nls")
    public ScanTubesManuallyWizard(Set<String> labels,
        Map<String, String> existingInventoryIdsByLabel) {
        super();

        // convert to a BidiMap
        for (Entry<String, String> entry : existingInventoryIdsByLabel.entrySet()) {
            existingInventoryIds.put(entry.getKey(), entry.getValue());
        }

        if (labels.isEmpty()) {
            throw new RuntimeException("labels is empty");
        }

        this.labels = labels;
    }

    @Override
    public void addPages() {
        for (String label : labels) {
            addPage(new ScanSingleTubePage(label));
        }
    }

    @Override
    public void createPageControls(Composite parent) {
    }

    @Override
    public boolean canFinish() {
        return true;
    }

    @Override
    public boolean needsPreviousAndNextButtons() {
        return true;
    }

    @Override
    public boolean needsProgressMonitor() {
        return true;
    }

    @Override
    public boolean isHelpAvailable() {
        return false;
    }

    @SuppressWarnings("nls")
    @Override
    public String getWindowTitle() {
        // TR: dialog title area title
        return i18n.tr("Scan tubes manually");
    }

    @Override
    public boolean performFinish() {
        return true;
    }

    /**
     * Returns the inventory IDs entered by the user. This is a map of labels to
     * inventory IDs.
     */
    public Map<String, String> getInventoryIdsByLabel() {
        return resultIventoryIdsByLabel;
    }
}
