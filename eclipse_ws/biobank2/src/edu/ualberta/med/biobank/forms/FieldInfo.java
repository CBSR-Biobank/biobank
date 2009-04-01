package edu.ualberta.med.biobank.forms;

public class FieldInfo {
	public String label;
	public Class<?> widgetClass;
	public String [] widgetValues;
	public Class<?> validatorClass;
	public String errMsg;
	
	public FieldInfo(String l, Class<?> w, String [] values, Class<?> v, String msg) {
		label = l;
		widgetClass = w;
		widgetValues = values;
		validatorClass = v; 
		errMsg = msg;
	}
}
