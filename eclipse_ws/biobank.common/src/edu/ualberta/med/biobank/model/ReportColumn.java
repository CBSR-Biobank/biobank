package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class ReportColumn  implements IBiobankModel
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
	
	public Integer position;
	/**
	* Retrieves the value of the position attribute
	* @return position
	**/

	public Integer getPosition(){
		return position;
	}

	/**
	* Sets the value of position attribute
	**/

	public void setPosition(Integer position){
		this.position = position;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.PropertyModifier object
	**/
			
	private PropertyModifier propertyModifier;
	/**
	* Retrieves the value of the propertyModifier attribute
	* @return propertyModifier
	**/
	
	public PropertyModifier getPropertyModifier(){
		return propertyModifier;
	}
	/**
	* Sets the value of propertyModifier attribute
	**/

	public void setPropertyModifier(PropertyModifier propertyModifier){
		this.propertyModifier = propertyModifier;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.EntityColumn object
	**/
			
	private EntityColumn entityColumn;
	/**
	* Retrieves the value of the entityColumn attribute
	* @return entityColumn
	**/
	
	public EntityColumn getEntityColumn(){
		return entityColumn;
	}
	/**
	* Sets the value of entityColumn attribute
	**/

	public void setEntityColumn(EntityColumn entityColumn){
		this.entityColumn = entityColumn;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ReportColumn) 
		{
			ReportColumn c =(ReportColumn)obj; 			 
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
