<!DOCTYPE xsl:stylesheet [<!ENTITY nbsp " ">]>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:xlink="http://www.w3.org/1999/xlink" exclude-result-prefixes="xlink" version="2.0">
	<xsl:output method="html"/>
	<xsl:variable name="role">simple</xsl:variable>
	<xsl:variable name="recordCounter" select="/xlink:httpQuery/queryResponse/recordCounter"/>
	<xsl:variable name="start" select="/xlink:httpQuery/queryResponse/start"/>
	<xsl:variable name="end" select="/xlink:httpQuery/queryResponse/end"/>
	<xsl:template match="/">
<html>
<head>
<title>Result Data Table</title>
<link rel="stylesheet" type="text/css" href="styleSheet.css" />
</head>
<body>
<table summary="" cellpadding="0" cellspacing="0" border="0" width="100%" height="100%">

	<!-- nci hdr begins -->
	<tr>
	  <td>
	    <table width="100%" border="0" cellspacing="0" cellpadding="0" class="hdrBG">
	      <tr>
	        <td width="283" height="37" align="left"><a href="http://www.cancer.gov"><img alt="National Cancer Institute" src="images/logotype.gif" width="283" height="37" border="0"/></a></td>
	        <td>&nbsp;</td>
	        <td width="295" height="37" align="right"><a href="http://www.cancer.gov"><img alt="U.S. National Institutes of Health | www.cancer.gov" src="images/tagline.gif" width="295" height="37" border="0"/></a></td>
	      </tr>
	    </table>
	  </td>
	</tr>
	<!-- nci hdr ends -->
	
  <tr>
    <td height="100%" align="center" valign="top">
      <table summary="" cellpadding="0" cellspacing="0" border="0" height="100%" width="771">
		<!-- application hdr begins -->
		<tr>
			<td height="50">
				<table width="100%" height="50" border="0" cellspacing="0" cellpadding="0" class="subhdrBG">
					<tr>
						<td height="50" align="left"><a href="#"><img src="images/sdkLogoSmall.gif" alt="Application Logo" hspace="10" border="0"/></a></td>
					</tr>
				</table>
			</td>
		</tr>
		<!-- application hdr ends -->
        <tr>
          <td valign="top">
            <table summary="" cellpadding="0" cellspacing="0" border="0" bordercolor="red" height="100%" width="100%" class="contentPage">
              
<!--_____ main content begins _____-->
		
		        <tr>
		          <td valign="top">

					<table cellpadding="0" cellspacing="0" border="0" bordercolor="blue" class="contentBegins" height="100%" width="100%">
						<tr>
							<td valign="top">
								<table border="0" bordercolor="orange" summary="" cellpadding="0" cellspacing="0">
									<tr>
										<td class="dataTablePrimaryLabel" height="20" align="left">
											Criteria: <xsl:value-of select="/xlink:httpQuery/queryRequest/criteria"/>
											<br/>
											Result Class: <xsl:value-of select="/xlink:httpQuery/queryRequest/query/class"/> &nbsp;&nbsp;
										</td>
									</tr>
									
									<!-- paging begins -->
									<tr>
										<td align="right" class="dataPagingSection" height="20">
											<table border="0" bordercolor="purple" cellpadding="0" cellspacing="0" width="100%">
												<tr>
												
											<xsl:choose>
												<xsl:when test="$recordCounter > 0">
													<td class="dataPagingText" align="right">
														<xsl:for-each select="/xlink:httpQuery/queryResponse/previous">
															<a class="dataPagingLink" href="{@xlink:href}">&lt;&lt;Previous</a>
														</xsl:for-each>
														| <xsl:value-of select="$start"/>-<xsl:value-of select="$end"/> of <xsl:value-of select="$recordCounter"/> |
														<xsl:for-each select="/xlink:httpQuery/queryResponse/next">
															<a class="dataPagingLink" href="{@xlink:href}">Next &gt;&gt;</a>
														</xsl:for-each>
													</td>																	
												</xsl:when>
												<xsl:otherwise>	
													<td class="dataPagingText" align="left" style="border:0px; border-bottom:1px; border-style:solid; border-color:#5C5C5C;">
														<br/>Results: Zero records found<br/><br/>
													</td>	
												</xsl:otherwise>
											</xsl:choose>												

												</tr>
											</table>
										</td>
									</tr>
									<!-- paging ends -->
									
									<tr>
										<td>
											<table summary="Data Summary" cellpadding="3" cellspacing="0" border="0" class="dataTable" width="100%">										
												<xsl:apply-templates select="/xlink:httpQuery/queryResponse/class" mode="res"/>
											</table>																			
										</td>
									</tr>
<!-- Insert details block here if needed -->
								</table>
							</td>
						</tr>
					</table>

                </td>
              </tr> 
<!--_____ main content ends _____-->
              
              <tr>
                <td height="20" width="100%" class="footerMenu">
                	&nbsp;                 
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
    
      <!-- footer begins -->
      <table width="100%" border="0" cellspacing="0" cellpadding="0" class="ftrTable">
        <tr>
          <td valign="top">
            <div align="center">
              <a href="http://www.cancer.gov/"><img src="images/footer_nci.gif" width="63" height="31" alt="National Cancer Institute" border="0"/></a>
              <a href="http://www.dhhs.gov/"><img src="images/footer_hhs.gif" width="39" height="31" alt="Department of Health and Human Services" border="0"/></a>
              <a href="http://www.nih.gov/"><img src="images/footer_nih.gif" width="46" height="31" alt="National Institutes of Health" border="0"/></a>
              <a href="http://www.firstgov.gov/"><img src="images/footer_firstgov.gif" width="91" height="31" alt="FirstGov.gov" border="0"/></a>
            </div>
          </td>
        </tr>
      </table>
      <!-- footer ends -->
    
    </td>
  </tr>
</table>
</body>
</html>
</xsl:template>

<xsl:template name="request" match="/xlink:httpQuery/queryRequest" mode="req">
		<!--  -#003333 #99C68E #FDEEF4 -->
		<font color="#737CA1" size="4">
			<xsl:for-each select="/xlink:httpQuery/queryRequest">
				<table bgcolor="#98B7B7">
					<tr>
						<td>
							<font color="#003333" size="4">
		Criteria: <xsl:value-of select="/xlink:httpQuery/queryRequest/criteria"/>
								<br/>
							</font>
						</td>
					</tr>
					<tr>
						<td>
							<font color="#003333" size="4">
		Result class name: <xsl:value-of select="/xlink:httpQuery/queryRequest/query/class"/>
								<br/>
							</font>
						</td>
					</tr>
				</table>
				<hr size="2"/>
				<xsl:choose>
					<xsl:when test="$recordCounter > 0">
						<b>
							<u>Results:  <xsl:value-of select="$recordCounter"/> records found</u>
						</b>
						<b> (Displaying record(s) #<xsl:value-of select="$start"/>-<xsl:value-of select="$end"/>)</b>
					</xsl:when>
					<xsl:otherwise>
						<b>
							<u>Results:  zero records found</u>
						</b>
					</xsl:otherwise>
				</xsl:choose>
				<br/>
				<br/>
			</xsl:for-each>
		</font>
	</xsl:template>

	<xsl:template name="response" match="/xlink:httpQuery/queryResponse/class" mode="res">

		<xsl:call-template name="cacore">
			<xsl:with-param name="counter" select="@recordNumber"/>
			<xsl:with-param name="rec" select="@recordNumber"/>
		</xsl:call-template>

	</xsl:template>

	<xsl:template name="cacore">
		<xsl:param name="counter"/>
		<xsl:param name="rec"/>
		<xsl:choose>
			<xsl:when test="$counter = $rec">
			
				<xsl:if test="$counter = @recordNumber">  
				
					<xsl:if test="@recordNumber = $start"> <!--  Print Header -->
						<tr>
							<th class="dataTableHeader" scope="col" align="left" colspan="20">
								<xsl:value-of select="@name"/>
							</th>							
						</tr>	
						<tr>
							<xsl:for-each select="field">
								<th class="dataTableHeader" scope="col" align="center">
									<xsl:value-of select="@name"/>
								</th>
							</xsl:for-each>
						</tr>
					</xsl:if>
					
					<tr class="dataRowLight">
						<xsl:for-each select="field">
							<xsl:choose>
								<xsl:when test="$role = @xlink:type">
									<td class="dataCellText" nowrap="off">
										<a href="{@xlink:href}">
											<xsl:value-of select="."/>
										</a>
									</td>
								</xsl:when>
								<xsl:otherwise>
									<td class="dataCellText" nowrap="off">
										<xsl:value-of select="."/>
									</td>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</tr>
					
				</xsl:if>
				
				<xsl:choose>
					<xsl:when test="$counter > @recordNumber"></xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="cacore">
							<xsl:with-param name="counter" select="$counter + 1"/>
							<xsl:with-param name="rec" select="following::node()/@recordNumber"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
				
			</xsl:when> <!-- $counter = $rec --> 
		</xsl:choose>
	</xsl:template>

	<xsl:template name="previous" match="/xlink:httpQuery/queryResponse/previous" mode="previous">
		<xsl:for-each select="/xlink:httpQuery/queryResponse/previous">
			<td bgcolor="#E0FFFF">
				<font color="#25587E">
					<a href="{@xlink:href}">
						<xsl:value-of select="."/>
					</a>
				</font>
			</td>
		</xsl:for-each>
	</xsl:template>
	<xsl:template name="page" match="/xlink:httpQuery/queryResponse/pages" mode="page">
		<xsl:choose>
			<xsl:when test="@count > 1">
				<xsl:for-each select="/xlink:httpQuery/queryResponse/pages/page">
					<td bgcolor="#E0FFFF">
						<font color="#25587E">
							<a href="{@xlink:href}">
								<xsl:value-of select="."/>
							</a>
						</font>
					</td>
				</xsl:for-each>
			</xsl:when>
		</xsl:choose>
	</xsl:template>
	<xsl:template name="next" match="/xlink:httpQuery/queryResponse/next" mode="next">
		<xsl:for-each select="/xlink:httpQuery/queryResponse/next">
			<td bgcolor="#E0FFFF">
				<font color="#25587E">
					<a href="{@xlink:href}">
						<xsl:value-of select="."/>
					</a>
				</font>
			</td>
		</xsl:for-each>
	</xsl:template>
	
</xsl:stylesheet>
