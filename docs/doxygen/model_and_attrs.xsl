<?xml version="1.0" ?>
<!--
  Use the following command to generate HTML documentation:

  xsltproc model_and_attrs.xsl xml/all.xml > FILE

  TO INSERT NEW LINE: <xsl:text>&#10;</xsl:text>

  USAGE:
     cd ~/proj/cbsr/biobank2/docs/doxygen && java -jar ~/apps/saxon9he.jar -xsl:model_and_attrs.xsl -s:xml/all.xml

-->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output indent="no" method="html" />

  <xsl:template match='ref'>
    <a><xsl:attribute name="href">#<xsl:value-of select="@refid" /></xsl:attribute><xsl:value-of select="." /></a>
  </xsl:template>

  <xsl:template match='para'>
    <xsl:if test="normalize-space(.)!=''">
      <p><xsl:apply-templates /></p>
    </xsl:if>
  </xsl:template>

  <xsl:template match='briefdescription | detaileddescription'>
    <xsl:apply-templates />
  </xsl:template>

  <xsl:template match="name">
    <tr>
      <td><xsl:value-of select="lower-case(substring(substring-after(../name,'get'),1,1))"/><xsl:value-of select="substring(substring-after(../name,'get'),2)"/></td>
      <td>
        <xsl:choose>
          <xsl:when test="../type = 'ActivityStatus'">
            one of: ACTIVE,<br/>FLAGGED,<br/>CLOSED
          </xsl:when>
        <xsl:otherwise>
          <xsl:value-of select="../type" />
        </xsl:otherwise>
        </xsl:choose>
      </td>
      <td>
        <xsl:apply-templates select="../briefdescription"/>
        <xsl:apply-templates select="../detaileddescription"/>
      </td>
    </tr>
  </xsl:template>

  <xsl:template match="sectiondef[@kind='public-func']">
    <xsl:param name="classNum" />
    <table>
      <thead>
        <tr>
          <th>Name</th>
          <th>Type</th>
          <th>Description</th>
        </tr>
      </thead>
      <tbody>
    <xsl:for-each select="memberdef[@kind='function']/name[starts-with(.,'get')]" >
      <xsl:sort select="../name" data-type="text" />
      <xsl:apply-templates select="../name" />
    </xsl:for-each>
      </tbody>
    </table>
  </xsl:template>

  <xsl:template match="compoundname">
      <h2>Class: <a><xsl:attribute name="name"><xsl:value-of select="../@id" /></xsl:attribute><xsl:value-of select="substring-after(.,'edu::ualberta::med::biobank::model::')" /></a></h2>
  </xsl:template>

  <xsl:template match="/*">
    <html>
      <head>
        <style type="text/css">
          body {font-family: Arial, Helvetica, sans-serif;}
          table th {font-size:0.85em;font-weight: bold;text-align: left;}
          table td {font-size:0.85em;vertical-align: top;}
        </style>
      </head>
      <body>
        <!-- do not show class AbstractBiobankModel in the output -->
        <xsl:for-each select="compounddef[@kind='class'][not(contains(@id,'AbstractBiobankModel'))]">
          <div>
            <xsl:apply-templates select="compoundname" />
            <xsl:apply-templates select="briefdescription" />
            <xsl:apply-templates select="detaileddescription" />
            <div><h3>Attributes</h3>
            <xsl:apply-templates select="sectiondef[@kind='public-func']" />
            </div>
          </div>
        </xsl:for-each>
      </body>
    </html>
  </xsl:template>

</xsl:stylesheet>
