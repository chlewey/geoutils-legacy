package net.interlecto.geoutils.lib.fix
/*
class MapItem(val name: String) {
    val key: String = str2key(name)
    var description: String? = null

    open fun move(from: Any, to: Any): Boolean = false

    open fun reline(line: Any): Boolean = false

    open fun xml(node: String = "Placemark"): String {
        return if (description != null) {
            """
            <$node>
                <name>$name</name>
                <description><![CDATA[$description]]></description>
            </$node>
            """
        } else {
            """
            <$node>
                <name>$name</name>
            </$node>
            """
        }
    }
}

*/