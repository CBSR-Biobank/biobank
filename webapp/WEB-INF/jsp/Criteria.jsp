<%@ page contentType="text/html; charset=UTF-8" %>
<%@taglib prefix="s" uri="/struts-tags" %>
<%@ page import="gov.nih.nci.system.web.util.JSPUtils,
				 java.lang.reflect.*,
				 java.util.*" %> 
			 
<link href="styleSheet.css" type="text/css" rel="stylesheet" />
<% 
JSPUtils jspUtils= null;
List fieldNames=new ArrayList();
List domainNames=new ArrayList();
String message = null, selectedSearchDomain=null;
String className = (String)request.getAttribute("klassName");

//out.println("className: " + className);
//session.setAttribute("selectedDomain", className);

if(className != null)
{
	try
	{	
		jspUtils = JSPUtils.getJSPUtils(config.getServletContext());
		fieldNames = jspUtils.getSearchableFields(className);
		domainNames = jspUtils.getAssociations(className);
		
	}
	catch(Exception ex){
		message=ex.getMessage();
	}
	
	if(fieldNames != null && fieldNames.size() > 0)
	{ 	
%>
<s:form method="post" action="Result.action" target="_blank" name="form1" theme="simple">
	<table summary="" cellpadding="3" cellspacing="0" border="0" align="center">
		<tr>
			<td class="formTitle" height="20" colspan="3"><s:property value="fullyQualClassName" /></td>
		</tr>
		
<!--  Sample Form Fields		
		<tr>
			<td class="formRequiredNotice" width="5">*</td>
			<td class="formRequiredLabel"><label for="field1">Text Field</label></td>
			<td class="formField"><input class="formFieldSized" type="text" name="field1" id="field1" size="30" /></td>
		</tr>
		<tr>
			<td class="formRequiredNotice" width="5">&nbsp;</td>
			<td class="formLabel"><label for="field2">Textarea Field</label></td>
			<td class="formField"><textarea class="formFieldSized" name="field2" id="field2" cols="32" rows="2"></textarea></td>
		</tr>
		<tr>
			<td class="formRequiredNotice" width="5">&nbsp;</td>
			<td class="formLabel"><label for="field3">Select Field</label></td>
			<td class="formField">
				<select class="formFieldSized" name="field3" id="field3" size="1">
					<option value="option1">Option1</option>
					<option value="option2">Option2</option>
				</select>
			</td>
		</tr>
		<tr>
			<td class="formRequiredNotice" width="5">&nbsp;</td>
			<td class="formLabel">Checkbox Fields</td>
			<td class="formField">
				<input type="checkbox" name="box1" id="box1" checked="checked" /> <label for="box1">Box1</label>
				<br />
				<input type="checkbox" name="box2" id="box2" /> <label for="box2">Box2</label>
			</td>
		</tr>
		<tr>
			<td class="formRequiredNotice" width="5">&nbsp;</td>
			<td class="formLabel">Radio Fields</td>
			<td class="formField">
				<input type="radio" id="radio1" name="radio5" checked="checked" /> <label for="field5">Radio1</label>
				<br>
				<input type="radio" id="radio2" name="radio5" /> <label for="field5">Radio2</label>
			</td>
		</tr>
-->		
		<% 
		
		String focusAttributes;
		if(fieldNames != null && fieldNames.size() > 0)
		{  
			String attrName;
		   	String attrType;
		   	
		   
		   	for(int i=0; i < fieldNames.size(); i++)
		   	{	attrName = ((Field)fieldNames.get(i)).getName();
			   	attrType = ((Field)fieldNames.get(i)).getType().getName(); 
			   	
			   	if (i==0) {
			   		focusAttributes = "id=\"firstInputField\" tabindex=\"1\"";
			   	} else {
			   		focusAttributes = "tabindex=\"" + i+1 + "\"";
			   	}
			   	
		%>
			   	
		<tr align="left" valign="top">
			<td class="formRequiredNotice" width="5px">&nbsp;</td>
			<td class="formLabel" align="right"><%=attrName%>:</td>
		<% if ( attrType.equalsIgnoreCase("java.Lang.Boolean") ) {%>
			<td class="formField" width="90%"><SELECT <%=focusAttributes%> class="formFieldSized" NAME=<%=attrName%> > 
			   		<OPTION SELECTED></OPTION>
			   		<OPTION >True</OPTION>
			   		<OPTION >False</OPTION>
			</SELECT></td>
		<%} else {%>
			<td class="formField"><input type="text" name="<%=attrName%>" <%=focusAttributes%> class="formField" size="14" theme="simple" /></td>
		<%}%>
		</tr>
		  <%}%>
		<tr align="left" valign="top">
			<td class="formRequiredNotice" width="5px">&nbsp;</td>
			<td class="formLabel" align="right">Search Object: </td>
			<td class="formField" width="90%"><SELECT tabIndex="100" class="formFieldSized" size="1" NAME=searchObj STYLE="width:90%">
			<% if(domainNames != null)
			   { if(!((String)domainNames.get(0)).equals("Please choose")) domainNames.add(0, "Please choose");
			   %>
			   		<%for(int i=0; i<domainNames.size(); i++)
			   		{%>
			   		<OPTION<% selectedSearchDomain = request.getParameter("searchObj");
			   				   if((selectedSearchDomain != null) && (domainNames.get(i).equals(selectedSearchDomain))) 
			   					{%> SELECTED <% } %> ><%=domainNames.get(i)%></OPTION>
			   		<%}%>
			   <%}%></SELECT></td>
			<%}// end if(domainNames != null) statement%>			   
		</tr>
		<tr>
			<td align="left" colspan="3">
				<!-- action buttons begins -->
				<table cellpadding="4" cellspacing="0" border="0">
					<tr align="left">
						<td align="left"><input type="submit" name="BtnSearch" class="actionButton" value="Submit" ></td>
						<td align="left"><input class="actionButton" type="reset" value="Reset"></td> 
					</tr>
				</table>
				<!-- action buttons end -->
			</td>	
		</tr>
	</table>
	<s:hidden name="selectedDomain" />
</s:form>

<%		}
	}%> 
