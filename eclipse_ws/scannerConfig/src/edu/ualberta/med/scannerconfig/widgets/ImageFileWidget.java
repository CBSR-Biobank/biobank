package edu.ualberta.med.scannerconfig.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.PlatformUI;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.gui.common.widgets.Event;
import edu.ualberta.med.biobank.gui.common.widgets.IBgcFileBrowserListener;

public class ImageFileWidget extends Composite
    implements IBgcFileBrowserListener, SelectionListener {

    private static final I18n i18n = I18nFactory.getI18n(ImageFileWidget.class);

    // private static Logger log = LoggerFactory.getLogger(ImageFileWidget.class.getName());

    @SuppressWarnings("nls")
    private static final String OPEN_FILE_DIALOG_BOX_TITLE = i18n.tr("Open");

    @SuppressWarnings("nls")
    private static final String[] FILTER_EXTENSIONS = new String[] { "*.jpg;*.jpeg;*.png;*.bmp" };

    private final GridData gridData;

    private edu.ualberta.med.biobank.gui.common.events.SelectionListener selectionListener;

    private String filename;

    public ImageFileWidget(Composite parent) {
        super(parent, SWT.NONE);

        GridLayout layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        layout.horizontalSpacing = 1;
        setLayout(layout);

        gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
        setLayoutData(gridData);

        createLoadFileButton();
    }

    @SuppressWarnings("nls")
    private Button createLoadFileButton() {
        Button button = new Button(this, SWT.PUSH);
        button.setText(i18n.tr("Open File"));
        button.addSelectionListener(this);
        return button;
    }

    @Override
    public void widgetSelected(SelectionEvent e) {
        String filename = getFromFileDialog();
        if (filename != null) {
            Event newEvent = new Event();
            newEvent.widget = this;
            newEvent.type = SWT.Selection;
            newEvent.data = filename;
            newEvent.detail = ImageSourceAction.FILENAME.getId();
            this.selectionListener.widgetSelected(newEvent);

            this.filename = filename;
        }
    }

    private String getFromFileDialog() {
        FileDialog fileDialog = new FileDialog(PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getShell(), SWT.OPEN);
        fileDialog.setText(OPEN_FILE_DIALOG_BOX_TITLE);
        fileDialog.setFilterExtensions(FILTER_EXTENSIONS);
        final String path = fileDialog.open();
        return path;
    }

    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        widgetSelected(e);
    }

    @Override
    public void fileSelected(String filename) {
        Event newEvent = new Event();
        newEvent.widget = this;
        newEvent.type = SWT.Selection;
        newEvent.data = filename;
        newEvent.detail = ImageSourceAction.FILENAME.getId();
        this.selectionListener.widgetSelected(newEvent);

    }

    public void addSelectionListener(
        edu.ualberta.med.biobank.gui.common.events.SelectionListener listener) {
        this.selectionListener = listener;
    }

    @Override
    public void setVisible(boolean visible) {
        gridData.exclude = !visible;
        super.setVisible(visible);
    }

    public String getFilename() {
        return filename;
    }
}
