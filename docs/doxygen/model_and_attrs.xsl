<?xml version="1.0" ?>
<!--
  Use the following command to generate HTML documentation:

  xsltproc model_and_attrs.xsl xml/all.xml > FILE

  TO INSERT NEW LINE: <xsl:text>&#10;</xsl:text>
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:output indent="no" method="html" />

  <xsl:variable name="classCount" select="1"/>

  <xsl:template name="compoundname">
    <xsl:param name="classNum" />
    <xsl:param name="className" />
    <h2>
      <xsl:value-of select="$classNum" />. Class: <xsl:value-of select="substring-after($className,'edu::ualberta::med::biobank::model::')" />
    </h2>
    <xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match='briefdescription | detaileddescription'>
    <xsl:if test="normalize-space(para)!=''">
        <p><xsl:apply-templates /></p><xsl:text>&#10;</xsl:text>
    </xsl:if>
  </xsl:template>

  <xsl:template match="name">
      <p><xsl:number level="multiple" format="1.1. " value="position()" />Attribute: <xsl:value-of select="substring-after(../name,'get')"/>, Type: <xsl:value-of select="../type"/></p><xsl:text>&#10;</xsl:text>
      <xsl:apply-templates select="../briefdescription"/>
      <xsl:apply-templates select="../detaileddescription"/>
  </xsl:template>

  <xsl:template match="sectiondef[@kind='public-func']">
    <xsl:param name="classNum" />
    <xsl:for-each select="memberdef[@kind='function']/name[starts-with(.,'get')]" >
      <xsl:sort select="../name" data-type="text" />
      <xsl:apply-templates select="../name" />
    </xsl:for-each>
  </xsl:template>

  <xsl:template match="/*">
    <xsl:for-each select="/doxygen/compounddef[@kind='class']">
      <div>
        <xsl:call-template name="compoundname">
          <xsl:with-param name="classNum" select="position()"/>
          <xsl:with-param name="className" select="compoundname"/>
        </xsl:call-template>
        <xsl:apply-templates select="briefdescription" />
        <xsl:apply-templates select="detaileddescription" />
        <xsl:apply-templates select="sectiondef[@kind='public-func']" />
      </div><xsl:text>&#10;</xsl:text>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
