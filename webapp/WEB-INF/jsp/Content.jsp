<%@taglib prefix="s" uri="/struts-tags" %>
<%@ page import="gov.nih.nci.system.web.util.JSPUtils"%>
<%
	JSPUtils jspUtils= JSPUtils.getJSPUtils(config.getServletContext());
	boolean isSecurityEnabled = jspUtils.isSecurityEnabled();
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<head>
<title>Content</title>
<s:head theme="ajax" debug="true" />
<link rel="stylesheet" type="text/css" href="styleSheet.css" />
<script src="script.js" type="text/javascript"></script>
<script>
	<!-- transport: "XMLHTTPTransport" -->
    function treeNodeSelected(nodeId) {
        dojo.io.bind({
            url: "<s:url value='Criteria.action' />?nodeId="+nodeId,
            load: function(type, data, evt) {
                var displayDiv = dojo.byId("displayId");
                displayDiv.innerHTML = data;
		    	setFocus('firstInputField')
            },
            mimeType: "text/html"
        });
    };

    dojo.event.topic.subscribe("treeSelected", this, "treeNodeSelected"); 
</script>
<script>
	function setFocus(fieldName)
	{
		try {
			document.getElementById( fieldName ).focus();
		} catch(e) {
			return true;
		}
	}// setFocus()    
</script>
</head>
<body>
<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="500px">

<%@ include file="include/header.inc" %>	
	
  <tr>
    <td height="500px" align="center" valign="top">
      <table summary="" cellpadding="0" cellspacing="0" border="0" height="500px" width="771">

<%@ include file="include/applicationHeader.inc" %>

        <tr>
          <td valign="top">
            <table summary="" cellpadding="0" cellspacing="0" border="0" height="500px" width="100%">
              <tr>
                <td height="20" class="mainMenu">
                
					<!-- main menu begins -->
					<table summary="" cellpadding="0" cellspacing="0" border="0" height="20">
					  <tr>
					    <td width="1"><!-- anchor to skip main menu --><a href="#content"><img src="images/shim.gif" alt="Skip Menu" width="1" height="1" border="0" /></a></td>
						<!-- link 1 begins -->
					    <td height="20" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()" onclick="document.location.href='Home.action'">
					      <a class="mainMenuLink" href="Home.action">HOME</a>
					    </td>
					    <!-- link 1 ends -->
					    <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt="" /></td>
					    <!-- link 2 begins -->
					    <td height="20" class="mainMenuItemOver" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItemOver'),hideCursor()" onclick="document.location.href='ShowDynamicTree.action'">
					      <a class="mainMenuLink" href="ShowDynamicTree.action">CRITERIA</a>
					    </td>
					    <!-- link 2 ends -->
					    <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt=""/></td>
					    <td height="20" width="100%">&nbsp;</td> 
					    <!-- link 3 begins -->
					    <%if(isSecurityEnabled){ %>
					    <td><img src="images/mainMenuSeparator.gif" width="1" height="16" alt=""/></td>
					    <td height="20" width="100%" align="right" class="mainMenuItem" onmouseover="changeMenuStyle(this,'mainMenuItemOver'),showCursor()" onmouseout="changeMenuStyle(this,'mainMenuItem'),hideCursor()">
					      <a class="mainMenuLink" href="j_acegi_logout">Logout</a>
					    </td> 
					    <%}%>
					    <!-- link 3 ends -->
					    
					    
					    
					  </tr>
					</table>
					<!-- main menu ends -->
                  
                </td>
              </tr>
              
<!--_____ main content begins _____-->
              <tr>
                <td valign="top">
                  <!-- target of anchor to skip menus --><a name="content" />
                  <table summary="" cellpadding="0" cellspacing="0" border="0" class="contentPage" width="100%" height="500px">
										<tr>
											<td valign="top">
												<table cellpadding="0" cellspacing="0" border="1" bordercolor="white" class="contentBegins">
													<tr>
														<td colspan="2">
															<h2>Domain Class Browser</h2>
															<h3>Please click on any of the tree nodes.</h3>
															<p>To view the search criteria for a class, expand a package listed below and select a class.  To search for records, provide valid search criteria and click the Submit button.  For any date attributes, please use the syntax: mm-dd-yyyy.</p> 
														</td>
													</tr>
													<tr>
														<td valign="top" style="border:0px; border-right:1px; border-style:solid; border-color:black;">
															<div style="overflow:auto; height:350px; float:left; margin: 7px;">
															<s:tree 
															    theme="ajax"
															    rootNode="%{classTreeRootNode}" 
															    childCollectionProperty="children" 
															    nodeIdProperty="id"
															    nodeTitleProperty="name"
															    treeSelectedTopic="treeSelected">
															</s:tree> 
															</div>														
															<img width="400" height="1"/>														
														</td>
														<td valign="top" id="displayId" >
														</td>														
													</tr>														
												</table>
											</td>
										</tr>
									</table>
                </td>
              </tr>
<!--_____ main content ends _____-->
              
              <tr>
                <td height="20" width="100%" class="footerMenu">
                
                  <!-- application ftr begins -->
<%@ include file="include/applicationFooter.inc" %>
                  <!-- application ftr ends -->
                  
                </td>
              </tr>
            </table>
          </td>
        </tr>
      </table>
    </td>
  </tr>
  <tr>
    <td>
    
<%@ include file="include/footer.inc" %>
    
    </td>
  </tr>
</table>
</body>
</html>
