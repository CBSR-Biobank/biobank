package edu.ualberta.med.biobank.model;


import java.io.Serializable;
/**
	* 
	**/

public class Address  implements IBiobankModel
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
	
	private String street1;
	/**
	* Retrieves the value of the street1 attribute
	* @return street1
	**/

	public String getStreet1(){
		return street1;
	}

	/**
	* Sets the value of street1 attribute
	**/

	public void setStreet1(String street1){
		this.street1 = street1;
	}
	
	/**
	* 
	**/
	
	private String street2;
	/**
	* Retrieves the value of the street2 attribute
	* @return street2
	**/

	public String getStreet2(){
		return street2;
	}

	/**
	* Sets the value of street2 attribute
	**/

	public void setStreet2(String street2){
		this.street2 = street2;
	}
	
	/**
	* 
	**/
	
	private String city;
	/**
	* Retrieves the value of the city attribute
	* @return city
	**/

	public String getCity(){
		return city;
	}

	/**
	* Sets the value of city attribute
	**/

	public void setCity(String city){
		this.city = city;
	}
	
	/**
	* 
	**/
	
	private String province;
	/**
	* Retrieves the value of the province attribute
	* @return province
	**/

	public String getProvince(){
		return province;
	}

	/**
	* Sets the value of province attribute
	**/

	public void setProvince(String province){
		this.province = province;
	}
	
	/**
	* 
	**/
	
	private String postalCode;
	/**
	* Retrieves the value of the postalCode attribute
	* @return postalCode
	**/

	public String getPostalCode(){
		return postalCode;
	}

	/**
	* Sets the value of postalCode attribute
	**/

	public void setPostalCode(String postalCode){
		this.postalCode = postalCode;
	}
	
	/**
	* 
	**/
	
	public String emailAddress;
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
	
	public String phoneNumber;
	/**
	* Retrieves the value of the phoneNumber attribute
	* @return phoneNumber
	**/

	public String getPhoneNumber(){
		return phoneNumber;
	}

	/**
	* Sets the value of phoneNumber attribute
	**/

	public void setPhoneNumber(String phoneNumber){
		this.phoneNumber = phoneNumber;
	}
	
	/**
	* 
	**/
	
	public String faxNumber;
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
	
	public String country;
	/**
	* Retrieves the value of the country attribute
	* @return country
	**/

	public String getCountry(){
		return country;
	}

	/**
	* Sets the value of country attribute
	**/

	public void setCountry(String country){
		this.country = country;
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
	* Compares <code>obj</code> to it self and returns true if they both are same
	*
	* @param obj
	**/
	public boolean equals(Object obj)
	{
		if(obj instanceof Address) 
		{
			Address c =(Address)obj; 			 
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
