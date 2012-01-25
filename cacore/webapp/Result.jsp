<%@page contentType="text/html"%>
<HTML>
<HEAD>
<title>Result</title>
</HEAD>
<BODY>
<%@ page import="gov.nih.nci.common.util.*,
				 java.lang.reflect.*,
				 java.util.*" %>
<% 
List fieldNames = null, domainNames=null;
String message = null, selectedSearchDomain=null;
String query=null;
String submitValue = request.getParameter("BtnSearch");
String className = (String)session.getAttribute("selectedDomain");

if(submitValue != null && submitValue.equalsIgnoreCase("Submit"))
{
   
    query = "/GetHTML?query=";
   	
   	selectedSearchDomain = request.getParameter("searchObj");
   	//System.out.println("selectedSearchDomain: "+ selectedSearchDomain);
   	   	
   	if(selectedSearchDomain != null && !selectedSearchDomain.equals("Please choose"))
   	{ query +=selectedSearchDomain + "&";
   	
	   	if(className != null && !className.equals("Please choose"))
	   	{   query += className;	
	   		//System.out.println("query with search object = " + query);
	   		Enumeration parameters = request.getParameterNames();
     		while(parameters.hasMoreElements())
     		{
         		String parameterName = (String)parameters.nextElement();
         		//System.out.println("param = " + parameterName);
         		if(!parameterName.equals("klassName") && !parameterName.equals("searchObj") && !parameterName.equals("BtnSearch") && !parameterName.equals("username") && !parameterName.equals("password"))
         		{
         			String parameterValue = (request.getParameter(parameterName)).trim();
         			if(parameterValue.length() > 0)
         			{
         				//System.out.println("parameterValue= " + parameterValue); 
         				query +="[@" + parameterName + "=" + parameterValue + "]";
         			}
         		}    
         	}    	
	   	
	   	 }
   	} 
   	else
   	{
   		if(className != null && !className.equals("Please choose"))
	   	{   query += className + "&" + className;	
	   	    //System.out.println("query no search object = " + query);
	   		Enumeration parameters = request.getParameterNames();
     		while(parameters.hasMoreElements())
     		{
         		String parameterName = (String)parameters.nextElement();
         		//System.out.println("param = " + parameterName);
         		if(!parameterName.equals("klassName") && !parameterName.equals("searchObj") && !parameterName.equals("BtnSearch")&& !parameterName.equals("username") && !parameterName.equals("password"))
         		{
         			String parameterValue = (request.getParameter(parameterName)).trim();
         			if(parameterValue.length() > 0)
         			{
         				//System.out.println("parameterValue= " + parameterValue); 
         				query +="[@" + parameterName + "=" + parameterValue + "]";
         			}
         		}    
         	}     	
	   	
	   	 }
   	
   	}
   	if ((request.getParameter("username") != null) && ((String)request.getParameter("username").trim()).length() > 0)
   		query = query + "&username=" + request.getParameter("username");
   	if ((request.getParameter("password") != null) && ((String)request.getParameter("password").trim()).length() > 0)
   		query = query + "&password=" + request.getParameter("password");
   	//System.out.println("query: " + query);
   	%>  	  	  
   <br>

<%
//System.out.println("contextPath= " + request.getContextPath()); 

response.sendRedirect(request.getContextPath()+"/"+query);

%>
<%
}
%>

</BODY>
</HTML>