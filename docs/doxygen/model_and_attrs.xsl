<?xml version="1.0" ?>
<!--
  Use the following command to generate HTML documentation:

  xsltproc model_and_attrs.xsl xml/all.xml > FILE

  TO INSERT NEW LINE: <xsl:text>&#10;</xsl:text>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output indent="no" method="html" />
  <xsl:param name="XmlPath" />

  <xsl:template match="compoundname">
    <h2>Class: <xsl:value-of select="substring-after(.,'edu::ualberta::med::biobank::model::')" /></h2><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match='briefdescription | detaileddescription'>
    <xsl:if test="normalize-space(para)!=''">
        <p><xsl:apply-templates /></p><xsl:text>&#10;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="sectiondef[@kind='public-func']">
    <xsl:for-each select="memberdef[@kind='function']/name">
      <xsl:if test="starts-with(., 'get')">
        <p>Attribute: <xsl:value-of select="substring-after(../name,'get')"/>, Type: <xsl:value-of select="../type"/></p><xsl:text>&#10;</xsl:text>
        <xsl:apply-templates select="../briefdescription"/>
        <xsl:apply-templates select="../detaileddescription"/>
      </xsl:if>
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="/*">
    <xsl:for-each select="/doxygen/compounddef[@kind='class']">
      <div>
        <xsl:apply-templates select="compoundname" />
        <xsl:apply-templates select="briefdescription" />
        <xsl:apply-templates select="detaileddescription" />
        <xsl:apply-templates select="sectiondef[@kind='public-func']" />
      </div><xsl:text>&#10;</xsl:text>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
