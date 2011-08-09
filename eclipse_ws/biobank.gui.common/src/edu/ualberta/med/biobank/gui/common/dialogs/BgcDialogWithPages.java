package edu.ualberta.med.biobank.gui.common.dialogs;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.jface.dialogs.DialogMessageArea;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.util.SafeRunnable;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;

import edu.ualberta.med.biobank.gui.common.BgcPlugin;

/**
 * Took same structure than PreferenceDialog. Difference is that the left panel
 * is a list and not a tree. The list is composed of BgcDialogPage objects (see
 * createPages method).
 * 
 * @see org.eclipse.jface.preference.PreferenceDialog
 * @author delphine
 * 
 */
public abstract class BgcDialogWithPages extends BgcBaseDialog {
    private ScrolledComposite scrolled;
    private Composite pageContainer;

    private BgcDialogPage currentPage;

    private List<BgcDialogPage> pages;

    private Point lastShellSize;

    private Composite titleArea;
    private DialogMessageArea messageArea;
    private Composite formTitleComposite;
    private boolean showingError;
    private ListViewer listViewer;

    public BgcDialogWithPages(Shell parentShell) {
        super(parentShell);
    }

    protected List<BgcDialogPage> getPages() {
        if (pages == null)
            pages = createPages();
        return pages;
    }

    /**
     * @return Pages that are listed inside the left list.
     */
    protected abstract List<BgcDialogPage> createPages();

    @Override
    protected Control createContents(final Composite parent) {
        Control control = super.createContents(parent);
        BgcDialogPage defaultSelection = getDefaultSelection();
        if (defaultSelection != null)
            // select first node when dialog open
            listViewer.setSelection(new StructuredSelection(defaultSelection));
        return control;
    }

    /**
     * @return selection when dialog opens
     */
    protected abstract BgcDialogPage getDefaultSelection();

    @Override
    protected void createDialogAreaInternal(Composite parent) throws Exception {
        // remove default spaces from parent
        GridLayout l = (GridLayout) parent.getLayout();
        l.marginHeight = 0;
        l.marginWidth = 0;
        l.horizontalSpacing = 0;
        l.verticalSpacing = 0;

        Composite content = new Composite(parent, SWT.NONE);
        GridLayout gl = new GridLayout(3, false);
        gl.horizontalSpacing = 0;
        gl.verticalSpacing = 0;
        gl.marginWidth = 0;
        gl.marginHeight = 0;
        content.setLayout(gl);
        content.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        // left list
        createListWidget(content);

        // show a separator between the list and the right container
        Label versep = new Label(content, SWT.SEPARATOR | SWT.VERTICAL);
        GridData verGd = new GridData(GridData.FILL_VERTICAL
            | GridData.GRAB_VERTICAL);
        versep.setLayoutData(verGd);
        versep.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, true));

        // right container. Display the current page.
        Composite pageAreaComposite = new Composite(content, SWT.NONE);
        pageAreaComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridLayout layout = new GridLayout(1, true);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.verticalSpacing = 0;
        pageAreaComposite.setLayout(layout);

        formTitleComposite = new Composite(pageAreaComposite, SWT.NONE);
        FormLayout titleLayout = new FormLayout();
        titleLayout.marginWidth = 0;
        titleLayout.marginHeight = 0;
        formTitleComposite.setLayout(titleLayout);
        GridData titleGridData = new GridData(GridData.FILL_HORIZONTAL);
        titleGridData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
        formTitleComposite.setLayoutData(titleGridData);

        // Build the title area
        Composite titleComposite = new Composite(formTitleComposite, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.marginBottom = 5;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.horizontalSpacing = 0;
        titleComposite.setLayout(layout);
        FormData titleFormData = new FormData();
        titleFormData.top = new FormAttachment(0, 0);
        titleFormData.left = new FormAttachment(0, 0);
        titleFormData.right = new FormAttachment(100, 0);
        titleFormData.bottom = new FormAttachment(100, 0);
        titleComposite.setLayoutData(titleFormData);
        createTitleArea(titleComposite);

        ToolBar tbar = new ToolBar(titleComposite, SWT.FLAT | SWT.HORIZONTAL);
        ToolItem titem = new ToolItem(tbar, SWT.NULL);
        titem.setImage(BgcPlugin.getDefault().getImageRegistry()
            .get(BgcPlugin.IMG_ADD));
        titem.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getSelection().runAddAction();
            }
        });

        // separator between title and page container
        Label separator2 = new Label(pageAreaComposite, SWT.HORIZONTAL
            | SWT.SEPARATOR);
        separator2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
            | GridData.GRAB_HORIZONTAL));

        // Build the Page container
        pageContainer = createPageContainer(pageAreaComposite);
        GridData pageContainerData = new GridData(GridData.FILL_BOTH);
        pageContainerData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
        pageContainer.setLayoutData(pageContainerData);
    }

    protected BgcDialogPage getSelection() {
        return (BgcDialogPage) ((IStructuredSelection) listViewer
            .getSelection()).getFirstElement();
    }

    private void createListWidget(Composite content) {
        listViewer = new ListViewer(content);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, false, true);
        gd.verticalIndent = 0;
        gd.horizontalIndent = 0;
        gd.widthHint = 150;
        listViewer.getControl().setLayoutData(gd);
        listViewer.setContentProvider(new ArrayContentProvider());
        listViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                if ((element != null) && (element instanceof BgcDialogPage))
                    return ((BgcDialogPage) element).getTitle();
                return ""; //$NON-NLS-1$
            }
        });
        listViewer.setInput(getPages());
        listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                showPage(getSelection());
            }
        });
    }

    /**
     * Create the title area which will contain a title, message, and image.
     */
    protected Composite createTitleArea(Composite parent) {
        int margins = 2;
        titleArea = new Composite(parent, SWT.NONE);
        FormLayout layout = new FormLayout();
        layout.marginHeight = 0;
        layout.marginWidth = margins;
        titleArea.setLayout(layout);
        GridData layoutData = new GridData(GridData.FILL_HORIZONTAL);
        layoutData.verticalAlignment = SWT.TOP;
        titleArea.setLayoutData(layoutData);

        // Message label
        messageArea = new DialogMessageArea();
        messageArea.createContents(titleArea);

        titleArea.addControlListener(new ControlAdapter() {
            @Override
            public void controlResized(ControlEvent e) {
                updateMessage();
            }
        });

        final IPropertyChangeListener fontListener = new IPropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent event) {
                if (JFaceResources.BANNER_FONT.equals(event.getProperty())) {
                    updateMessage();
                }
                if (JFaceResources.DIALOG_FONT.equals(event.getProperty())) {
                    updateMessage();
                    Font dialogFont = JFaceResources.getDialogFont();
                    // updateTreeFont(dialogFont);
                    Control[] children = ((Composite) buttonBar).getChildren();
                    for (int i = 0; i < children.length; i++) {
                        children[i].setFont(dialogFont);
                    }
                }
            }
        };

        titleArea.addDisposeListener(new DisposeListener() {
            @Override
            public void widgetDisposed(DisposeEvent event) {
                JFaceResources.getFontRegistry().removeListener(fontListener);
            }
        });
        JFaceResources.getFontRegistry().addListener(fontListener);
        messageArea.setTitleLayoutData(createMessageAreaData());
        messageArea.setMessageLayoutData(createMessageAreaData());
        return titleArea;
    }

    private FormData createMessageAreaData() {
        FormData messageData = new FormData();
        messageData.top = new FormAttachment(0);
        messageData.bottom = new FormAttachment(100);
        messageData.right = new FormAttachment(100);
        messageData.left = new FormAttachment(0);
        return messageData;
    }

    /**
     * Create main page container
     */
    protected Composite createPageContainer(Composite parent) {
        Composite outer = new Composite(parent, SWT.NONE);
        GridData outerData = new GridData(GridData.FILL_BOTH
            | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        outerData.horizontalIndent = IDialogConstants.HORIZONTAL_MARGIN;
        GridLayout outerLayout = new GridLayout();
        outerLayout = new GridLayout();
        outerLayout.marginBottom = 0;
        outerLayout.marginHeight = 0;
        outerLayout.marginWidth = 0;
        outerLayout.horizontalSpacing = 0;
        outer.setLayout(outerLayout);
        outer.setLayoutData(outerData);

        // Create an outer composite for spacing
        scrolled = new ScrolledComposite(outer, SWT.V_SCROLL | SWT.H_SCROLL);
        // always show the focus control
        scrolled.setShowFocusedControl(true);
        scrolled.setExpandHorizontal(true);
        scrolled.setExpandVertical(true);
        GridData scrolledData = new GridData(GridData.FILL_BOTH
            | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        scrolled.setLayoutData(scrolledData);

        Composite result = new Composite(scrolled, SWT.NONE);
        GridLayout resLayout = new GridLayout(1, false);
        resLayout = new GridLayout();
        resLayout.marginBottom = 0;
        resLayout.marginHeight = 0;
        resLayout.marginWidth = 0;
        resLayout.horizontalSpacing = 0;
        result.setLayout(resLayout);
        result.setLayoutData(new GridData(GridData.FILL_BOTH
            | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL));

        scrolled.setContent(result);

        return result;
    }

    /**
     * Display the selection
     */
    protected boolean showPage(BgcDialogPage newPage) {
        if (newPage == currentPage) {
            return true;
        }
        BgcDialogPage oldPage = currentPage;
        currentPage = newPage;
        // Ensure that the page control has been created
        // (this allows lazy page control creation)
        if (currentPage.getControl() == null) {
            final boolean[] failed = { false };
            SafeRunnable.run(new ISafeRunnable() {
                @Override
                public void handleException(Throwable e) {
                    failed[0] = true;
                }

                @Override
                public void run() {
                    currentPage.createControl(pageContainer);
                }
            });
            if (failed[0]) {
                return false;
            }
            // the page is responsible for ensuring the created control is
            // accessible via getControl.
            Assert.isNotNull(currentPage.getControl());
        }
        // Force calculation of the page's description label because
        // label can be wrapped.
        final Point[] size = new Point[1];
        final Point failed = new Point(-1, -1);
        SafeRunnable.run(new ISafeRunnable() {
            @Override
            public void handleException(Throwable e) {
                size[0] = failed;
            }

            @Override
            public void run() {
                size[0] = currentPage.computeSize();
            }
        });
        if (size[0].equals(failed)) {
            return false;
        }
        Point contentSize = size[0];
        // Do we need resizing. Computation not needed if the
        // first page is inserted since computing the dialog's
        // size is done by calling dialog.open().
        // Also prevent auto resize if the user has manually resized
        Shell shell = getShell();
        Point shellSize = shell.getSize();
        if (oldPage != null) {
            Rectangle rect = pageContainer.getClientArea();
            Point containerSize = new Point(rect.width, rect.height);
            int hdiff = contentSize.x - containerSize.x;
            int vdiff = contentSize.y - containerSize.y;
            if (((hdiff > 0) || (vdiff > 0)) && shellSize.equals(lastShellSize)) {
                hdiff = Math.max(0, hdiff);
                vdiff = Math.max(0, vdiff);
                setShellSize(shellSize.x + hdiff, shellSize.y + vdiff);
                lastShellSize = shell.getSize();
                if (currentPage.getControl().getSize().x == 0) {
                    currentPage.getControl().setSize(containerSize);
                }

            } else {
                currentPage.setSize(containerSize);
            }
        }

        scrolled.setMinSize(contentSize);
        // Ensure that all other pages are invisible
        // (including ones that triggered an exception during
        // their creation).
        Control[] children = pageContainer.getChildren();
        Control currentControl = currentPage.getControl();
        for (int i = 0; i < children.length; i++) {
            if (children[i] != currentControl) {
                children[i].setVisible(false);
            }
        }
        // Make the new page visible
        currentPage.setVisible(true);
        if (oldPage != null) {
            oldPage.setVisible(false);
        }
        // update the dialog controls
        update();
        return true;
    }

    private void setShellSize(int width, int height) {
        Rectangle preferred = getShell().getBounds();
        preferred.width = width;
        preferred.height = height;
        getShell().setBounds(getConstrainedShellBounds(preferred));
    }

    /**
     * Updates this dialog's controls to reflect the current page.
     */
    protected void update() {
        // Update the title bar
        updateTitle();
        // Update the message line
        updateMessage();
    }

    public void updateTitle() {
        if (currentPage == null) {
            return;
        }
        messageArea.showTitle(currentPage.getTitle(), currentPage.getImage());
    }

    public void updateMessage() {
        String message = null;
        String errorMessage = null;
        if (currentPage != null) {
            message = currentPage.getMessage();
            errorMessage = currentPage.getErrorMessage();
        }
        int messageType = IMessageProvider.NONE;
        if ((message != null) && (currentPage != null)) {
            messageType = ((IMessageProvider) currentPage).getMessageType();
        }

        if (errorMessage == null) {
            if (showingError) {
                // we were previously showing an error
                showingError = false;
            }
        } else {
            message = errorMessage;
            messageType = IMessageProvider.ERROR;
            if (!showingError) {
                // we were not previously showing an error
                showingError = true;
            }
        }
        messageArea.updateText(message, messageType);
    }

    @Override
    protected boolean isResizable() {
        // DD: couldn't find out why the display was wrong after the dialog
        // was resized, so forbid it for now.
        return false;
    }

}
