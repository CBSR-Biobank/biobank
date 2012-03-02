<?xml version="1.0" ?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

  <xsl:param name="XmlPath" />

  <xsl:template match="compoundname">
    <h2>Class: <xsl:value-of select="."/></h2><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="briefdescription">
    <p><xsl:value-of select="para"/></p><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="detaileddescription">
    <p><xsl:value-of select="para"/></p><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="*">
    <p>Name: <xsl:value-of select="name"/></p><xsl:text>&#10;</xsl:text>
    <p>Type: <xsl:value-of select="type"/></p><xsl:text>&#10;</xsl:text>
    <p>Brief: <xsl:value-of select="briefdescription"/></p><xsl:text>&#10;</xsl:text>
    <p><xsl:value-of select="detaileddescription"/></p><xsl:text>&#10;</xsl:text>
  </xsl:template>

  <xsl:template match="/*">
    <xsl:for-each select="/doxygen/compounddef[@kind='class']">
      <div>
        <xsl:apply-templates select="compoundname" />
        <xsl:apply-templates select="briefdescription" />
        <xsl:apply-templates select="detaileddescription" />
        <xsl:apply-templates select="sectiondef[@kind='private-attrib']/memberdef[@kind='variable']" />
      </div>
    </xsl:for-each>
  </xsl:template>

</xsl:stylesheet>
