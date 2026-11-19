package net.interlecto.geoutils.gup

import android.content.Context
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.net.toUri
import kotlin.reflect.full.createType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.io.File
import java.io.InputStream
import java.nio.file.Files
import java.util.zip.*

class GupFileManager(private val gupFile: File) {

    @Serializable
    data class Resource(
        val name: String,
        val datatype: String,
        val uri: String
    )

    @Serializable
    data class ProjectJson(
        val resources: List<Resource>,
        val actions: List<Action> // Define Action class based on your needs
    )

    @Serializable
    data class Action(
        val type: String,
        val params: Map<String, String>
    )

    private val json = Json { prettyPrint = true }

    private var isExtracted: Boolean = false
    private var extractedFolder: File? = null

    val gupFileUri = gupFile.toUri()

    fun setExtractedMode(extracted: Boolean, folder: File? = null) {
        isExtracted = extracted
        extractedFolder = if (extracted) folder ?: createTempDir() else null
    }

    private fun createTempDir(): File {
        val tempDir = Files.createTempDirectory("geoutils_project_").toFile()
        Runtime.getRuntime().addShutdownHook(
            Thread {
                val gupFileManager = GupFileManager(gupFile)
                gupFileManager.createGup()
            }
        )
        tempDir.deleteOnExit()
        return tempDir
    }

    fun listFiles(): List<String> {
        return if (isExtracted) {
            extractedFolder?.list()?.toList() ?: emptyList()
        } else {
            GZIPInputStream(gupFile.inputStream()).use { gzipInputStream ->
                ZipInputStream(gzipInputStream).use { zipStream ->
                    generateSequence { zipStream.nextEntry?.name }.toList()
                }
            }
        }
    }

    fun readFile(fileName: String): ByteArray? {
        return if (isExtracted) {
            val file = File(extractedFolder, fileName)
            if (file.exists()) file.readBytes() else null
        } else {
            GZIPInputStream(gupFile.inputStream()).use { gzipInputStream ->
                ZipInputStream(gzipInputStream).use { zipStream ->
                    var entry: ZipEntry?
                    while (zipStream.nextEntry.also { entry = it } != null) {
                        if (entry!!.name == fileName) {
                            return zipStream.readBytes()
                        }
                    }
                }
            }
            null
        }
    }

    fun writeFile(fileName: String, content: ByteArray) {
        if (isExtracted) {
            val file = File(extractedFolder, fileName)
            file.parentFile?.mkdirs()
            file.writeBytes(content)
        } else {
            val tempFile = File.createTempFile("temp", ".gup")
            GZIPInputStream(gupFile.inputStream()).use { gzipInputStream ->
                GZIPOutputStream(tempFile.outputStream()).use { gzipOutputStream ->
                    ZipInputStream(gzipInputStream).use { zipInputStream ->
                        ZipOutputStream(gzipOutputStream).use { zipOutputStream ->
                            val entries = mutableSetOf<String>()
                            var entry: ZipEntry?
                            while (zipInputStream.nextEntry.also { entry = it } != null) {
                                if (entry!!.name != fileName) {
                                    zipOutputStream.putNextEntry(ZipEntry(entry!!.name))
                                    zipInputStream.copyTo(zipOutputStream)
                                    zipOutputStream.closeEntry()
                                    entries.add(entry!!.name)
                                }
                            }
                            zipOutputStream.putNextEntry(ZipEntry(fileName))
                            zipOutputStream.write(content)
                            zipOutputStream.closeEntry()
                        }
                    }
                }
            }
            tempFile.renameTo(gupFile)
        }
    }

    fun extractGup(): File {
        extractedFolder?.mkdirs()
        GZIPInputStream(gupFile.inputStream()).use { gzipInputStream ->
            ZipInputStream(gzipInputStream).use { zipStream ->
                var entry: ZipEntry?
                while (zipStream.nextEntry.also { entry = it } != null) {
                    val outFile = File(extractedFolder, entry!!.name)
                    outFile.parentFile?.mkdirs()
                    outFile.outputStream().use { zipStream.copyTo(it) }
                }
            }
        }
        return extractedFolder!!
    }

    fun createGup(): File {
        if (isExtracted) {
            GZIPOutputStream(gupFile.outputStream()).use { gzipStream ->
                ZipOutputStream(gzipStream).use { zipStream ->
                    extractedFolder?.walkTopDown()?.forEach { file ->
                        if (file.isFile) {
                            val relativePath =
                                extractedFolder!!.toPath().relativize(file.toPath()).toString()
                            zipStream.putNextEntry(ZipEntry(relativePath))
                            file.inputStream().use { it.copyTo(zipStream) }
                            zipStream.closeEntry()
                        }
                    }
                }
            }
        }
        return gupFile
    }

    fun readProjectJson(): ProjectJson {
        val jsonBytes = readFile(".project.json")
            ?: error(".project.json not found in gup file")
        return json.decodeFromString<ProjectJson>(jsonBytes.decodeToString())
    }

    fun updateProjectJson(modifyJson: (ProjectJson) -> ProjectJson) {
        val projectJson = readProjectJson()
        val updatedJson = modifyJson(projectJson)
        val serializer = findSerializer<ProjectJson>() // Use reflection to find serializer
        val jsonBytes = json.encodeToString(serializer, updatedJson).toByteArray()
        writeFile(".project.json", jsonBytes)
    }

    inline fun <reified T> findSerializer(): KSerializer<T> {
        val kType = T::class.createType()
        val serializer = try {
            Class.forName("kotlinx.serialization.internal.GeneratedSerializer").getDeclaredMethod("serializer", kotlin.reflect.KType::class.java)
                .invoke(null, kType) as KSerializer<T>
        } catch (e: Exception) {
            object : KSerializer<T> {
                override val descriptor: SerialDescriptor
                    get() = TODO("Not yet implemented")

                override fun deserialize(decoder: Decoder): T {
                    TODO("Not yet implemented")
                }

                override fun serialize(encoder: Encoder, value: T) {
                    TODO("Not yet implemented")
                }
            }
        }
        return serializer
    }

    fun readThumbnail(): ByteArray {
        val thumbnailBytes = readFile(".thumbnail")
            ?: error(".thumbnail not found in gup file")
        return thumbnailBytes
    }

    fun updateThumbnail(newThumbnail: InputStream) {
        val thumbnailBytes = newThumbnail.readBytes()
        writeFile(".thumbnail", thumbnailBytes)
    }

    companion object {
        fun createWithWorkingDir(workingDir: File, gupFilename: String, context: Context?=null): GupFileManager {
            val filename = if (gupFilename.endsWith(".gup")) gupFilename else "$gupFilename.gup"
            val gupFile = if (filename.contains(File.separator)) {
                File(filename)
            } else {
                val appHomeDir = context?.filesDir
                File(appHomeDir, filename)
            }
            // You might want to add logic here to check if the file exists
            // and handle it accordingly (e.g., overwrite, throw an exception)
            val gupFileManager = GupFileManager(gupFile)
            gupFileManager.setExtractedMode(true, workingDir)
            gupFileManager.createGup()
            return GupFileManager(gupFile)
        }
    }

}
