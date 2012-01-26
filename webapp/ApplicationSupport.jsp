<%@taglib prefix="s" uri="/struts-tags" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">

<html>
<s:head theme="ajax" debug="true" />
<title>Disclaimer</title>
<link rel="stylesheet" type="text/css" href="styleSheet.css" />
<script src="script.js" type="text/javascript"></script>
<s:head/>
<body>
<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">

<%@ include file="WEB-INF/jsp/include/header.inc" %>	
	
  <tr>
    <td height="100%" align="center" valign="top">
      <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%" width="771">

<%@ include file="WEB-INF/jsp/include/applicationHeader.inc" %>

        <tr>
          <td valign="top">
            <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%" width="100%">
              <tr>
                <td height="20" class="mainMenu">
                
					<!-- main menu begins -->
<%@ include file="WEB-INF/jsp/include/mainMenu.inc" %>   
					<!-- main menu ends -->
                  
                </td>
              </tr>
              
<!--_____ main content begins _____-->
              <tr>
                <td valign="top">
                  <!-- target of anchor to skip menus --><a name="content" />
                  <table summary="" cellpadding="0" cellspacing="0" border="0" class="contentPage" width="100%" height="100%">
										<tr>
											<td valign="middle" align="center">
												This page is under construction.
											</td>
										</tr>
									</table>
                </td>
              </tr>
<!--_____ main content ends _____-->
              
              <tr>
                <td height="20" width="100%" class="footerMenu">
                
                  <!-- application ftr begins -->
					<%@ include file="WEB-INF/jsp/include/applicationFooter.inc" %>                  
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
    
<%@ include file="WEB-INF/jsp/include/footer.inc" %>
    
    </td>
  </tr>
</table>
</body>
</html>
