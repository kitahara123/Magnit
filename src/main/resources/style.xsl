<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
<xsl:output method="xml" indent="yes" />
	<xsl:template match="/">
	<entries>
	<xsl:apply-templates />
	</entries>
	</xsl:template>
	
	<xsl:template match="entry">
	<xsl:element name="entry">
	<xsl:attribute name="field">
	<xsl:value-of select="field"/>
	</xsl:attribute>
	</xsl:element>
	</xsl:template>
	
</xsl:stylesheet>