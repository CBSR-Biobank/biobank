<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
  this file is used to change the version number in the biobank2.product
  to whatever is passed in the parameter "version"
  -->

  <xsl:output omit-xml-declaration="no" indent="yes"/>

  <xsl:param name="version" select="'unknown'"/>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="product/@version">
    <xsl:attribute name="version">
      <xsl:value-of select="$version"/>
    </xsl:attribute>
  </xsl:template>

</xsl:stylesheet>
