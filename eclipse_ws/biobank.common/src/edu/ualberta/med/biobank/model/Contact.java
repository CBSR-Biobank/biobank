package edu.ualberta.med.biobank.model;

import java.util.Collection;
import java.util.HashSet;

import java.io.Serializable;
/**
	* 
	**/

public class Contact  implements IBiobankModel
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
	
	private String name;
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
	
	private String title;
	/**
	* Retrieves the value of the title attribute
	* @return title
	**/

	public String getTitle(){
		return title;
	}

	/**
	* Sets the value of title attribute
	**/

	public void setTitle(String title){
		this.title = title;
	}
	
	/**
	* 
	**/
	
	private String mobileNumber;
	/**
	* Retrieves the value of the mobileNumber attribute
	* @return mobileNumber
	**/

	public String getMobileNumber(){
		return mobileNumber;
	}

	/**
	* Sets the value of mobileNumber attribute
	**/

	public void setMobileNumber(String mobileNumber){
		this.mobileNumber = mobileNumber;
	}
	
	/**
	* 
	**/
	
	private String faxNumber;
	/**
	* Retrieves the value of the faxNumber attribute
	* @return faxNumber
	**/

	public String getFaxNumber(){
		return faxNumber;
	}

	/**
	* Sets the value of faxNumber attribute
	**/

	public void setFaxNumber(String faxNumber){
		this.faxNumber = faxNumber;
	}
	
	/**
	* 
	**/
	
	private String emailAddress;
	/**
	* Retrieves the value of the emailAddress attribute
	* @return emailAddress
	**/

	public String getEmailAddress(){
		return emailAddress;
	}

	/**
	* Sets the value of emailAddress attribute
	**/

	public void setEmailAddress(String emailAddress){
		this.emailAddress = emailAddress;
	}
	
	/**
	* 
	**/
	
	public String pagerNumber;
	/**
	* Retrieves the value of the pagerNumber attribute
	* @return pagerNumber
	**/

	public String getPagerNumber(){
		return pagerNumber;
	}

	/**
	* Sets the value of pagerNumber attribute
	**/

	public void setPagerNumber(String pagerNumber){
		this.pagerNumber = pagerNumber;
	}
	
	/**
	* 
	**/
	
	public String officeNumber;
	/**
	* Retrieves the value of the officeNumber attribute
	* @return officeNumber
	**/

	public String getOfficeNumber(){
		return officeNumber;
	}

	/**
	* Sets the value of officeNumber attribute
	**/

	public void setOfficeNumber(String officeNumber){
		this.officeNumber = officeNumber;
	}
	
	/**
	* An associated edu.ualberta.med.biobank.model.Study object's collection 
	**/
			
	private Collection<Study> studyCollection = new HashSet<Study>();

	/**
	* Retrieves the value of the studyCollection attribute
	* @return studyCollection
	**/

	public Collection<Study> getStudyCollection(){
		return studyCollection;
	}

	/**
	* Sets the value of studyCollection attribute
	**/

	public void setStudyCollection(Collection<Study> studyCollection){
		this.studyCollection = studyCollection;
	}
		
	/**
	* An associated edu.ualberta.med.biobank.model.Clinic object
	**/
			
	private Clinic clinic;
	/**
	* Retrieves the value of the clinic attribute
	* @return clinic
	**/
	
	public Clinic getClinic(){
		return clinic;
	}
	/**
	* Sets the value of clinic attribute
	**/

	public void setClinic(Clinic clinic){
		this.clinic = clinic;
	}
			
	/**
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Contact) 
		{
			Contact c =(Contact)obj; 			 
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
