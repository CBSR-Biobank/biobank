package edu.ualberta.med.biobank.forms;

public class FieldInfo {
	public String label;
	public Class<?> widgetClass;
	public int widgetOptions;
	public String [] widgetValues;
	public Class<?> validatorClass;
	public String errMsg;
	
	public FieldInfo(String l, Class<?> w, int options, String [] values, 
	    Class<?> v, String msg) {
		label = l;
		widgetClass = w;
		widgetOptions = options;
		widgetValues = values;
		validatorClass = v; 
		errMsg = msg;
	}
}
