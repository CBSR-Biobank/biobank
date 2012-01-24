package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class PrintedSsInvItem  implements IBiobankModel
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
	
	public String txt;
	/**
	* Retrieves the value of the txt attribute
	* @return txt
	**/

	public String getTxt(){
		return txt;
	}

	/**
	* Sets the value of txt attribute
	**/

	public void setTxt(String txt){
		this.txt = txt;
	}
	
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof PrintedSsInvItem) 
		{
			PrintedSsInvItem c =(PrintedSsInvItem)obj; 			 
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
