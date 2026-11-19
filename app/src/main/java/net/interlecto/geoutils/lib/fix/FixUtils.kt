package net.interlecto.geoutils.lib.fix
/*
import kotlin.math.*

// Configuration map
val config: MutableMap<String, Double> = mutableMapOf("lon" to 0.0)

fun String.fixSing(): String {
    if (this.isEmpty()) return ""
    val lon = config["lon"] ?: 0.0
    val t = this.split(",").map { it.toDouble() }.toMutableList()
    t[1] = t[1].roundTo(3)
    val x = (1000 * cos(Math.toRadians(t[1]))).roundToInt()
    val l = ((t[0] - lon) * x).roundToInt()
    t[0] = (l.toDouble() / x + lon).roundTo(5)
    t.add(0.0) // Ensures the list has three elements.
    return t.joinToString(",")
}

fun String.fixCoor(defReverse: Boolean = false): List<String> {
    val r = this.trim().split(Regex("\\s+")).toMutableList()
    if (defReverse) r.reverse()
    return r.map { it.fixSing() }
}

fun Double.degMinSec(precision: Int = 0): Pair<String, Boolean> {
    val isNegative = this < 0
    val absVal = if (isNegative) -this else this
    val degrees = absVal.toInt()
    var result = "$degrees°"
    if (absVal > degrees && precision > -2) {
        val minutes = 60 * (absVal - degrees)
        val intMinutes = minutes.toInt()
        result += "$intMinutes'"
        if (minutes > intMinutes && precision > -1) {
            val seconds = 60 * (minutes - intMinutes)
            result += String.format("%.${precision}f\"", seconds)
        }
    }
    return result to isNegative
}

fun String.coorName(): String {
    val co = this.fixSing()
    val parts = co.split(",").map { it.toDouble() }
    val lon = parts[0].degMinSec()
    val lat = parts[1].degMinSec(1)
    return "${lat.first}${if (lat.second) 'S' else 'N'} ${lon.first}${if (lon.second) 'W' else 'E'}"
}

fun String.vector(): List<Double> {
    if (this.isEmpty()) return emptyList()
    val lon = config["lon"] ?: 0.0
    val c = this.split(",").map { it.toDouble() }
    val theta = Math.toRadians(c[0] - lon)
    val phi = Math.toRadians(c[1])
    return listOf(
        sin(theta) * cos(phi),
        sin(phi),
        cos(theta) * cos(phi)
    )
}

// Utility Functions
fun List<Double>.vprod(other: List<Double>) = listOf(
    this[1] * other[2] - this[2] * other[1],
    this[2] * other[0] - this[0] * other[2],
    this[0] * other[1] - this[1] * other[0]
)

fun List<Double>.eprod(other: List<Double>) =
    this.zip(other).sumOf { it.first * it.second }

fun List<Double>.vadd(other: List<Double>) =
    this.zip(other).map { it.first + it.second }

fun List<Double>.vdif(other: List<Double>) =
    this.zip(other).map { it.first - it.second }

fun List<Double>.ptrvec() = String.format("[%.5f, %.5f, %.5f]", this[0], this[1], this[2])

fun List<List<Double>>.varea(): List<Double> {
    var c = listOf(0.0, 0.0, 0.0)
    for (i in 1 until this.size) {
        c = c.vadd(this[i - 1].vprod(this[i]))
    }
    return c.map { it / 2 }
}

fun Double.roundTo(places: Int) = "%.${places}f".format(this).toDouble()

import kotlin.math.*

data class Config(val lon: Double = 0.0)
val config = Config()

fun latlong(v: Vector): String {
    val lon = config.lon
    val ln = atan2(v.x, v.z)
    val rn = sqrt(v.x * v.x + v.z * v.z)
    val lt = atan2(v.y, rn)
    return fixSing("${Math.toDegrees(ln) + lon},${Math.toDegrees(lt)}")
}

fun form(coo: String): List<Vector> {
    val lon = config.lon
    val coords = coo.split(',').map { it.toDouble() }
    val sln = sin(Math.toRadians(coords[0] - lon))
    val cln = cos(Math.toRadians(coords[0] - lon))
    val slt = sin(Math.toRadians(coords[1]))
    val clt = cos(Math.toRadians(coords[1]))
    return listOf(
        Vector(-sln, cln, 0.0),
        Vector(-cln * slt, -sln * slt, clt)
    )
}

fun acim(co1: String, co2: Vector): Double {
    val M = form(co1)
    val y = eprod(co2, M[0])
    val z = eprod(co2, M[1])
    return Math.toDegrees(atan2(y, z))
}

const val ARG_NORMAL = 0
const val ARG_DIVZERO = 1
const val ARG_INLINE = 2
const val ARG_NOPOLY = 3

var argExep = ARG_NORMAL

fun argument(v0: Vector, v1: Vector, v2: Vector): Double? {
    val X01 = vprod(v0, v1)
    val X02 = vprod(v0, v2)
    val num = vprod(X01, X02)
    val de0 = eprod(v0, v0)
    val de1 = eprod(X01, X01)
    val de2 = eprod(X02, X02)

    if (de0 == 0.0 || de1 == 0.0 || de2 == 0.0) {
        argExep = ARG_DIVZERO
        return null
    }

    val sin = eprod(v0, num) / sqrt(de0 * de1 * de2)
    val arg = asin(sin)
    val chk = eprod(X01, X02)

    if (chk >= 0) return arg

    return when {
        arg == 0.0 -> {
            argExep = ARG_INLINE
            PI
        }
        arg > 0 -> PI - arg
        else -> -PI - arg
    }
}

// Placeholder utility functions

fun fixSing(input: String): String {
    // Implement any singularity corrections as needed
    return input
}

data class Vector(val x: Double, val y: Double, val z: Double)

fun vprod(v1: Vector, v2: Vector): Vector {
    return Vector(
        v1.y * v2.z - v1.z * v2.y,
        v1.z * v2.x - v1.x * v2.z,
        v1.x * v2.y - v1.y * v2.x
    )
}

fun eprod(v1: Vector, v2: Vector): Double {
    return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z
}
*/