<?xml version="1.0" ?>
<!--
  Use the following command to generate HTML documentation:

  xsltproc model_and_attrs.xsl xml/all.xml > FILE
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output indent="yes" method="text"/>
  <xsl:param name="XmlPath" />

  <xsl:template match="compoundname">
    <h2>Class: <xsl:value-of select="substring-after(.,'edu::ualberta::med::biobank::model::')" /></h2><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="briefdescription">
    <p><xsl:value-of select="para"/></p><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="detaileddescription">
    <p><xsl:value-of select="para"/></p><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="memberdef[@kind='function']">
  </xsl:template>

  <xsl:template match="memberdef[@kind='function']">
    <xsl:if
    <p>Attribute: <xsl:value-of select="name"/>, Type: <xsl:value-of select="type"/></p><xsl:text>&#10;</xsl:text>
    <p><xsl:value-of select="briefdescription"/></p><xsl:text>&#10;</xsl:text>
    <p><xsl:value-of select="detaileddescription"/></p><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="/*">
    <xsl:for-each select="/doxygen/compounddef[@kind='class']">
      <div>
        <xsl:apply-templates select="compoundname" />
        <xsl:apply-templates select="briefdescription" />
        <xsl:apply-templates select="detaileddescription" />
        <xsl:apply-templates select="sectiondef[@kind='public-func']" />
      </div>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
