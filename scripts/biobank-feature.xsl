<?xml version="1.0"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
  <!--
  this file is used to change the version number the biobank2.feature.core
  and biobank2.feature.platform to whatever is passed in the parameter "version"
  -->

  <xsl:output omit-xml-declaration="no" indent="yes"/>

  <xsl:param name="version" select="'unknown'"/>

  <xsl:template match="node()|@*">
    <xsl:copy>
      <xsl:apply-templates select="node()|@*"/>
    </xsl:copy>
  </xsl:template>

  <xsl:template match="feature/@version">
    <xsl:attribute name="version">
      <xsl:value-of select="$version"/>
    </xsl:attribute>
  </xsl:template>

</xsl:stylesheet>
