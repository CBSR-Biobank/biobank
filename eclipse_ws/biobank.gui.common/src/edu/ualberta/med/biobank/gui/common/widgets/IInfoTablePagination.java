package edu.ualberta.med.biobank.gui.common.widgets;

// TODO: this should be changed so that implementors only have methods to determine the number of rows per page and to show a page starting at a specified index.
public interface IInfoTablePagination {

    public void firstPage();

    public void prevPage();

    public void nextPage();

    public void lastPage();

}
