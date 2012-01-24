package edu.ualberta.med.biobank.model;

import org.hibernate.validator.NotNull;
import org.hibernate.validator.Min;

import java.io.Serializable;
/**
	* 
	**/
	

public abstract class AbstractPosition  implements IBiobankModel
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
	private Integer row;
	/**
	* Retrieves the value of the row attribute
	* @return row
	**/

	public Integer getRow(){
		return row;
	}

	/**
	* Sets the value of row attribute
	**/

	public void setRow(Integer row){
		this.row = row;
	}
	
	/**
	* 
	**/
	
	@NotNull
	@Min(value=0)
	private Integer col;
	/**
	* Retrieves the value of the col attribute
	* @return col
	**/

	public Integer getCol(){
		return col;
	}

	/**
	* Sets the value of col attribute
	**/

	public void setCol(Integer col){
		this.col = col;
	}
	
	/**
	* 
	**/
	
	public String positionString;
	/**
	* Retrieves the value of the positionString attribute
	* @return positionString
	**/

	public String getPositionString(){
		return positionString;
	}

	/**
	* Sets the value of positionString attribute
	**/

	public void setPositionString(String positionString){
		this.positionString = positionString;
	}
	
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof AbstractPosition) 
		{
			AbstractPosition c =(AbstractPosition)obj; 			 
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
