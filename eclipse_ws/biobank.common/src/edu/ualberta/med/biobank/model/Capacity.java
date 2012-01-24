package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Min;

import java.io.Serializable;
/**
	* 
	**/
	

public class Capacity  implements IBiobankModel
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
	
	@NotNull
	@Min(value=0)
	private Integer rowCapacity;
	/**
	* Retrieves the value of the rowCapacity attribute
	* @return rowCapacity
	**/

	public Integer getRowCapacity(){
		return rowCapacity;
	}

	/**
	* Sets the value of rowCapacity attribute
	**/

	public void setRowCapacity(Integer rowCapacity){
		this.rowCapacity = rowCapacity;
	}
	
	/**
	* 
	**/
	
	@NotNull
	@Min(value=0)
	private Integer colCapacity;
	/**
	* Retrieves the value of the colCapacity attribute
	* @return colCapacity
	**/

	public Integer getColCapacity(){
		return colCapacity;
	}

	/**
	* Sets the value of colCapacity attribute
	**/

	public void setColCapacity(Integer colCapacity){
		this.colCapacity = colCapacity;
	}
	
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Capacity) 
		{
			Capacity c =(Capacity)obj; 			 
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
