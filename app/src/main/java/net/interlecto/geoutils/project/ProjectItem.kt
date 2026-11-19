package net.interlecto.geoutils.project

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable
import net.interlecto.geoutils.R
import java.io.File
import java.util.UUID

enum class ProjectItemType {
    PROJECT, FILE, FOLDER, REMOTE, DEMO, AD, UNKNOWN
}

enum class ProjectSource {
    HOME, LOCAL, WEB, EMBEDDED, AD, CLIPBOARD, OTHER
}

enum class PreviewType {
    IMAGE, MAP, TEXT, NONE
}

enum class FileType {
    GEO, KML, SVG, PNG, JPEG, TIFF, UNKNOWN;

    companion object {
        fun fromExtension(extension: String): FileType = when (extension.lowercase()) {
            "geo" -> GEO
            "kml" -> KML
            "svg" -> SVG
            "png" -> PNG
            "jpg" -> JPEG
            "jpeg" -> JPEG
            "tif" -> TIFF
            "tiff" -> TIFF
            else -> UNKNOWN
        }

        val fileExtensions = listOf("geo", "kml", "svg", "png", "jpg", "jpeg", "tif", "tiff")
    }

    fun isBitmap(): Boolean = this in listOf(PNG, JPEG, TIFF)
    fun isVector(): Boolean = this in listOf(SVG, KML)
}

@Parcelize
data class ProjectItem(
    val id: String = UUID.randomUUID().toString(),

    val name: String,
    val description: String? = null,

    val source: ProjectSource,
    val type: ProjectItemType,

    val file: File? = null,          // Used if local file
    val url: String? = null,         // Used if remote or ad
    val embeddedId: String? = null,  // Used for demo projects, tutorials

    val thumbnailUri: Uri? = null,
    val iconResId: Int? = null,      // Used if no thumbnail
    val fileType: FileType? = null,  // Used if file

    val hasGeoConfig: Boolean = false,
    val geoMetadata: Map<String, String> = emptyMap(),

    val isDirectory: Boolean = false,
    val isRelevantEmpty: Boolean = false,

    val previewType: Long? = PreviewType.NONE.ordinal.toLong(),
    val lastOpened: Long? = null     // Timestamp for recents

) : Parcelable {
    companion object {
        fun fromLocalFile(file: File): ProjectItem {
            val name = file.name
            val isFolder = file.isDirectory
            val hasGeoConfig = File(file, ".geo_config").exists()
            val type = when {
                hasGeoConfig -> ProjectItemType.PROJECT
                isFolder -> ProjectItemType.FOLDER
                file.extension.lowercase() in FileType.fileExtensions -> ProjectItemType.FILE
                else -> ProjectItemType.UNKNOWN
            }

            return ProjectItem(
                name = name,
                source = ProjectSource.LOCAL,
                type = type,
                file = file,
                isDirectory = isFolder,
                hasGeoConfig = hasGeoConfig,
                fileType = FileType.fromExtension(file.extension)
            )
        }
    }

    fun getDefaultIcon(): Int = when (type) {
        ProjectItemType.PROJECT -> R.drawable.ic_unknown_format // TODO: create and replace
        ProjectItemType.FILE -> when (fileType) {
            FileType.GEO -> R.drawable.ic_fileformat_geo
            FileType.KML -> R.drawable.ic_fileformat_kml
            FileType.SVG -> R.drawable.ic_fileformat_svg
            FileType.PNG -> R.drawable.ic_fileformat_png
            FileType.JPEG -> R.drawable.ic_fileformat_jpeg
            FileType.TIFF -> R.drawable.ic_fileformat_tiff
            else -> R.drawable.ic_unknown_format
        }
        ProjectItemType.FOLDER ->
            if (hasGeoConfig) R.drawable.ic_foldertype_geo else R.drawable.ic_foldertype

        ProjectItemType.AD, ProjectItemType.REMOTE, ProjectItemType.DEMO -> R.drawable.ic_unknown_format
        else -> R.drawable.ic_unknown_format
    }
}