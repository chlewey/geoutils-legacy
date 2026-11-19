package net.interlecto.geoutils.lib.fix
/*
import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

class MapArea(name: String) : MapItem(name) {
    private val polys = mutableListOf<MapPoly>()

    fun addArea(placemark: Placemark) {
        val name = this.name.trim()
        when {
            placemark is MapPoly -> polys.add(MapPoly(placemark.outer, name))
            placemark.hasMultiGeometry() -> placemark.multiGeometryPolygons.forEach {
                polys.add(MapPoly(it, name))
            }
            placemark.hasPolygon() -> polys.add(MapPoly(placemark.polygon, name))
        }
    }

    fun addHole(poly: MapPoly) {
        val origin = poly.outer.first()
        val count = polys.count { shape -> shape.outer.contains(origin) }
        if (count == 0) {
            val sub = poly.sub ?: 0
            val index = findGreater(sub - 1)
            polys[index].inner.add(poly.outer)
        }
    }

    fun areaVector(): Vector {
        return polys.fold(Vector(0.0, 0.0, 0.0)) { acc, poly -> acc + poly.areaVector() }
    }

    fun areaNorm(): Double {
        return polys.sumOf { it.areaNorm() }
    }

    fun midPoint(): LatLong {
        return areaVector().toLatLong()
    }

    fun move(from: Coordinate, to: Coordinate) {
        polys.forEach { it.move(from, to) }
    }

    fun reline(line: MapLine) {
        polys.forEach { it.reline(line) }
    }

    fun delete(line: MapLine): Boolean {
        val coord = line.coordinates.first()
        for ((i, poly) in polys.withIndex()) {
            if (poly.hasPoint(coord)) {
                polys.removeAt(i)
                return true
            }
            poly.findInner(coord)?.let { idx ->
                poly.deleteInner(idx)
                return true
            }
        }
        return false
    }

    fun subline(start: Coordinate, end: Coordinate): List<Coordinate>? {
        polys.forEach {
            it.subline(start, end)?.let { return it }
        }
        return null
    }

    private fun findGreater(order: Int = 0): Int {
        val ordered = polys.mapIndexed { index, poly -> index to poly.outer.size }
            .sortedByDescending { it.second }
        return ordered.getOrNull(order)?.first ?: 0
    }

    override fun toXml(node: String): Element {
        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument()
        val element = doc.createElement(node)

        polys.forEach { poly ->
            val polyElement = poly.toXml("Polygon")
            element.appendChild(doc.adoptNode(polyElement))
        }

        return element
    }
}

 */