package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotEmpty;

import java.io.Serializable;
/**
	* 
	**/
	

public class GlobalEventAttr  implements IBiobankModel
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

        private Integer version;

	
	/**
	* 
	**/
	
	private Integer id;
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
	
	@NotEmpty
	private String label;
	/**
	* Retrieves the value of the label attribute
	* @return label
	**/

	public String getLabel(){
		return label;
	}

	/**
	* Sets the value of label attribute
	**/

	public void setLabel(String label){
		this.label = label;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.EventAttrType object
	**/
			
	private EventAttrType eventAttrType;
	/**
	* Retrieves the value of the eventAttrType attribute
	* @return eventAttrType
	**/
	
	public EventAttrType getEventAttrType(){
		return eventAttrType;
	}
	/**
	* Sets the value of eventAttrType attribute
	**/

	public void setEventAttrType(EventAttrType eventAttrType){
		this.eventAttrType = eventAttrType;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof GlobalEventAttr) 
		{
			GlobalEventAttr c =(GlobalEventAttr)obj; 			 
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
