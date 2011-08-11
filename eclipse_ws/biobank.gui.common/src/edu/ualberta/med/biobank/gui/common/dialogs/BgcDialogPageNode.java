package edu.ualberta.med.biobank.gui.common.dialogs;

public class BgcDialogPageNode {

    private String title;

    private BgcDialogPage page;

    public BgcDialogPageNode(BgcDialogPage page) {
        this(null, page);
    }

    public BgcDialogPageNode(String title, BgcDialogPage page) {
        this.title = title;
        this.page = page;
    }

    public String getTitle() {
        if ((title == null) && (page != null))
            return page.getTitle();
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public BgcDialogPage getPage() {
        return page;
    }

    public void setPage(BgcDialogPage page) {
        this.page = page;
    }

}
