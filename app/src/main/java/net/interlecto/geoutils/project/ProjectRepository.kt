package net.interlecto.geoutils.project

import android.net.Uri
import android.os.Environment
import android.util.Log
import java.io.File

class ProjectRepository(
    private val defaultDirectory: File = Environment
        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
) {
    // Methods to fetch projects from internal gallery, home directory, web server, andads
    fun getInternalProjects( ) :  List<ProjectItem> {
        // TODO("implement")
        return emptyList()
    }

    fun getDirProjects(path: String? = null): List<ProjectItem> {
        val targetDir = if (path == null) defaultDirectory else File(path)

        if (!targetDir.exists() || !targetDir.canRead()) {
            Log.e("ProjectRepository", "Cannot access directory: ${targetDir.absolutePath}")
            return emptyList()
        }

        return targetDir.listFiles()
            ?.filter {
                it.isDirectory || it.extension.lowercase() in listOf("geo", "kml", "svg", "png", "jpg", "jpeg", "tif", "tiff")
            }
            ?.sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
            ?.map { file ->
                val isDirectory = file.isDirectory
                var hasGeoConfig = false
                var isRelevantEmpty = false

                if (isDirectory) {
                    val filesInDir = file.listFiles()?.toList() ?: emptyList()
                    hasGeoConfig = filesInDir.any { it.name == ".geo_config" }
                    val hasRelevantFile = filesInDir.any {
                        it.isFile && it.extension.lowercase() in listOf("geo", "kml", "svg", "png", "jpg", "jpeg", "tif", "tiff")
                    }
                    isRelevantEmpty = !hasRelevantFile
                }

                ProjectItem(
                    name = file.nameWithoutExtension,
                    path = file.absolutePath,
                    type = when {
                        file.isDirectory -> ProjectItemType.FOLDER
                        file.extension.lowercase() == "geo" -> ProjectItemType.PROJECT
                        file.extension.lowercase() == "kml" -> ProjectItemType.KML
                        file.extension.lowercase() == "svg" -> ProjectItemType.SVG
                        file.extension.lowercase() in listOf("png", "jpg", "jpeg", "tif", "tiff") -> ProjectItemType.IMAGE
                        else -> ProjectItemType.UNKNOWN
                    },
                    source = ProjectSource.HOME,
                    thumbnailUri = null,
                    fileUri = if (file.extension.lowercase() == "geo") Uri.fromFile(file) else null,
                    hasGeoConfig = hasGeoConfig,
                    isRelevantEmpty = isRelevantEmpty
                )
            }
            ?: emptyList()
    }

    fun getWebProjects(): List<ProjectItem> {
        // TODO("implement")
        return emptyList()
    }

    fun getAdProjects(): List<ProjectItem> {
        // TODO("implement")
        return emptyList()
    }
}