package net.interlecto.geoutils.gup

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.GZIPInputStream
import java.util.zip.GZIPOutputStream
import kotlinx.serialization.json.Json
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString

// Data class representing the .gup file structure
data class GupFile(
    val name: String,
    val thumbnail: ByteArray,
    val resources: List<Resource>,
    val actions: String // Assuming actions is a JSON string
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as GupFile

        if (name != other.name) return false
        if (!thumbnail.contentEquals(other.thumbnail)) return false
        if (resources != other.resources) return false
        if (actions != other.actions) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + thumbnail.contentHashCode()
        result = 31 * result + resources.hashCode()
        result = 31 * result + actions.hashCode()
        return result
    }
}

data class Resource(
    val name: String,
    val datatype: String,
    val uri: String,
    val data: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Resource

        if (name != other.name) return false
        if (datatype != other.datatype) return false
        if (uri != other.uri) return false
        if (!data.contentEquals(other.data)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + datatype.hashCode()
        result = 31 * result + uri.hashCode()
        result = 31 * result + data.contentHashCode()
        return result
    }
}

fun readGupFile(filePath: String): GupFile {
    // Decompress the file
    val inputStream = GZIPInputStream(FileInputStream(filePath))
    val data = inputStream.readBytes()
    inputStream.close()

    // Parse the JSON (assuming JSON format)
    return Json.decodeFromString<GupFile>(data.decodeToString())
}

fun writeGupFile(gupFile: GupFile, filePath: String) {
    // Serialize to JSON (assuming JSON format)
    val jsonData = Json.encodeToString(gupFile)

    // Compress the data
    val outputStream = GZIPOutputStream(FileOutputStream(filePath))
    outputStream.write(jsonData.toByteArray())
    outputStream.close()
}