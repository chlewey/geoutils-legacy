package net.interlecto.geoutils.lib.fix
/*
class Folder(name: String) : MapItem(name) {
    val areas = mutableMapOf<String, MapArea>()
    val lines = mutableListOf<MapLine>()
    val points = mutableMapOf<String, MapPoint>()

    fun add(obj: Any): Boolean {
        if (obj !is MapItem) return false
        val n = obj.name.trim()
        when (obj) {
            is MapArea -> areas.merge(n, obj) { existing, new -> existing.addArea(new); existing }
            is MapLine -> lines.add(obj)
            is MapPoint -> points[n] = obj
            is MapPoly -> areas.merge(n, MapArea(obj)) { existing, new -> existing.addArea(new); existing }
            else -> return false
        }
        return true
    }

    override fun move(from: Any, to: Any) {
        areas.values.forEach { it.move(from, to) }
        lines.forEach { it.move(from, to) }
        points.values.forEach { it.move(from, to) }
    }

    override fun reline(line: Any) {
        areas.values.forEach { it.reline(line) }
    }

    fun isEmpty(): Boolean = areas.isEmpty() && lines.isEmpty() && points.isEmpty()

    override fun xml(node: String = "Folder"): String {
        val xmlContent = StringBuilder(super.xml(node))
        areas.values.forEach { xmlContent.append(it.xml()) }
        lines.forEach { xmlContent.append(it.xml()) }
        points.values.forEach { xmlContent.append(it.xml()) }
        return xmlContent.toString()
    }
}
*/
