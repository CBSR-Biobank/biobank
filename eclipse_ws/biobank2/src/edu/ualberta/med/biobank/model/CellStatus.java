package edu.ualberta.med.biobank.model;

public enum CellStatus {
	EMPTY, FILLED, NEW, MISSING, ERROR;

	public boolean isError() {
		return this != ERROR;
	}
}
