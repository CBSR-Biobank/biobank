package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class PrinterLabelTemplate  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	public Integer id;
	/**
	* Retrieves the value of the id attribute
	* @return id
	**/

	public Integer getId(){
		return id;
	}

	/**
	* Sets the value of id attribute
	**/

	public void setId(Integer id){
		this.id = id;
	}
	
	/**
	* 
	**/
	
	public String name;
	/**
	* Retrieves the value of the name attribute
	* @return name
	**/

	public String getName(){
		return name;
	}

	/**
	* Sets the value of name attribute
	**/

	public void setName(String name){
		this.name = name;
	}
	
	/**
	* 
	**/
	
	public String printerName;
	/**
	* Retrieves the value of the printerName attribute
	* @return printerName
	**/

	public String getPrinterName(){
		return printerName;
	}

	/**
	* Sets the value of printerName attribute
	**/

	public void setPrinterName(String printerName){
		this.printerName = printerName;
	}
	
	/**
	* 
	**/
	
	public String configData;
	/**
	* Retrieves the value of the configData attribute
	* @return configData
	**/

	public String getConfigData(){
		return configData;
	}

	/**
	* Sets the value of configData attribute
	**/

	public void setConfigData(String configData){
		this.configData = configData;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.JasperTemplate object
	**/
			
	private JasperTemplate jasperTemplate;
	/**
	* Retrieves the value of the jasperTemplate attribute
	* @return jasperTemplate
	**/
	
	public JasperTemplate getJasperTemplate(){
		return jasperTemplate;
	}
	/**
	* Sets the value of jasperTemplate attribute
	**/

	public void setJasperTemplate(JasperTemplate jasperTemplate){
		this.jasperTemplate = jasperTemplate;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof PrinterLabelTemplate) 
		{
			PrinterLabelTemplate c =(PrinterLabelTemplate)obj; 			 
			if(getId() != null && getId().equals(c.getId()))
				return true;
		}
		return false;
	}
		
	/**
	* Returns hash code for the primary key of the object
	**/
	public int hashCode()
	{
		if(getId() != null)
			return getId().hashCode();
		return 0;
	}
	
}
