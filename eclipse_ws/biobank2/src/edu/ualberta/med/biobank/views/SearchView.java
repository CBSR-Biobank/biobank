package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.exception.MultipleSearchResultException;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SearchView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.SearchView";
    private Composite top = null;
    private Button button = null;
    private Text searchField = null;
    private Combo combo = null;
    private ComboViewer comboViewer = null;

    public SearchView() {
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(4, false);
        layout.horizontalSpacing = 10;
        top.setLayout(layout);

        createCombo();

        searchField = new Text(top, SWT.BORDER);
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 13) {
                    search();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

        });
        GridData gd = new GridData();
        gd.widthHint = 100;
        searchField.setLayoutData(gd);

        button = new Button(top, SWT.NONE);
        button.setText("Search");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                search();

            }
        });
    }

    @Override
    public void setFocus() {

    }

    /**
     * This method initializes combo
     * 
     */
    private void createCombo() {
        combo = new Combo(top, SWT.READ_ONLY);
        comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setInput(SearchType.values());
    }

    private void search() {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                try {
                    WritableApplicationService appService = null;
                    AdapterBase node;
                    IStructuredSelection treeSelection = (IStructuredSelection) SessionManager
                        .getInstance().getTreeViewer().getSelection();
                    if (treeSelection != null && treeSelection.size() > 0) {
                        node = (AdapterBase) treeSelection.getFirstElement();
                        appService = node.getAppService();
                    } else {
                        BioBankPlugin.openMessage("Search",
                            "No selection available for search");
                        return;
                    }
                    if (appService == null) {
                        BioBankPlugin.openError("Search",
                            "No connection available");
                        return;
                    } else {
                        IStructuredSelection selection = (IStructuredSelection) comboViewer
                            .getSelection();
                        if (selection != null && selection.size() > 0) {
                            SearchType searchType = (SearchType) selection
                                .getFirstElement();
                            String id = searchField.getText();
                            AdapterBase nodeFound = searchType.search(appService, id,
                                node);
                            if (nodeFound != null) {
                                SessionManager.getInstance().getTreeViewer()
                                    .setSelection(
                                        new StructuredSelection(nodeFound));
                                nodeFound.performDoubleClick();
                            }
                        }
                    }
                } catch (MultipleSearchResultException msre) {
                    BioBankPlugin
                        .openError("Search result",
                            "More than one result found : select a more appropriate node in the tree");
                } catch (Exception ex) {
                    SessionManager.getLogger().error("Error for search", ex);
                }
            }
        });
    }
}
