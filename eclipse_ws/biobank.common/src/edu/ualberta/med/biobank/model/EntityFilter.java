package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class EntityFilter  implements IBiobankModel
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
	
	public Integer filterType;
	/**
	* Retrieves the value of the filterType attribute
	* @return filterType
	**/

	public Integer getFilterType(){
		return filterType;
	}

	/**
	* Sets the value of filterType attribute
	**/

	public void setFilterType(Integer filterType){
		this.filterType = filterType;
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
	* An associated edu.ualberta.med.biobank.model.EntityProperty object
	**/
			
	private EntityProperty entityProperty;
	/**
	* Retrieves the value of the entityProperty attribute
	* @return entityProperty
	**/
	
	public EntityProperty getEntityProperty(){
		return entityProperty;
	}
	/**
	* Sets the value of entityProperty attribute
	**/

	public void setEntityProperty(EntityProperty entityProperty){
		this.entityProperty = entityProperty;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof EntityFilter) 
		{
			EntityFilter c =(EntityFilter)obj; 			 
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
