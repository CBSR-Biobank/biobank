package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class SpecimenPosition extends AbstractPosition implements Serializable
{
	/**
	* An attribute to allow serialization of the domain objects
	*/
	private static final long serialVersionUID = 1234567890L;

	
	/**
	* An associated edu.ualberta.med.biobank.model.Container object
	**/
			
	private Container container;
	/**
	* Retrieves the value of the container attribute
	* @return container
	**/
	
	public Container getContainer(){
		return container;
	}
	/**
	* Sets the value of container attribute
	**/

	public void setContainer(Container container){
		this.container = container;
	}
			
	/**
	* An associated edu.ualberta.med.biobank.model.Specimen object
	**/
			
	private Specimen specimen;
	/**
	* Retrieves the value of the specimen attribute
	* @return specimen
	**/
	
	public Specimen getSpecimen(){
		return specimen;
	}
	/**
	* Sets the value of specimen attribute
	**/

	public void setSpecimen(Specimen specimen){
		this.specimen = specimen;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof SpecimenPosition) 
		{
			SpecimenPosition c =(SpecimenPosition)obj; 			 
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