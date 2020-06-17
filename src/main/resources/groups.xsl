<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:template match="/">
        <html>
            <body>
                <h2>Groups list</h2>
                <table border="1">
                    <tr bgcolor="#9acd32">
                        <th>Group name</th>
                    </tr>
                    <xsl:for-each select="/*[name()='Payload']/*[name()='Groups']/*[name()='Group'][@project='mj']/.">
                        <tr>
                            <td><xsl:value-of select="@id"/></td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>

</xsl:stylesheet>