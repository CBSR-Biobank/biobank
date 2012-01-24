package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class ReportFilterValue  implements IBiobankModel
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
	* 
	**/
	
	public String value;
	/**
	* Retrieves the value of the value attribute
	* @return value
	**/

	public String getValue(){
		return value;
	}

	/**
	* Sets the value of value attribute
	**/

	public void setValue(String value){
		this.value = value;
	}
	
	/**
	* 
	**/
	
	public String secondValue;
	/**
	* Retrieves the value of the secondValue attribute
	* @return secondValue
	**/

	public String getSecondValue(){
		return secondValue;
	}

	/**
	* Sets the value of secondValue attribute
	**/

	public void setSecondValue(String secondValue){
		this.secondValue = secondValue;
	}
	
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof ReportFilterValue) 
		{
			ReportFilterValue c =(ReportFilterValue)obj; 			 
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
