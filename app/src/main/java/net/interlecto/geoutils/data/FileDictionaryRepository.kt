package net.interlecto.geoutils.data

import java.io.File

class FileDictionaryRepository(private val fileDictionaryDao: FileDictionaryDao) {
    suspend fun addFileToProject(filePath: String, gupFilePath: String) {
        fileDictionaryDao.insertNonProjectFile(NonProjectFile(filePath, File(filePath).lastModified()))
        fileDictionaryDao.insertProjectFile(ProjectFile(gupFilePath))
        fileDictionaryDao.insertFileProjectCrossRef(FileProjectCrossRef(filePath, gupFilePath))
    }

    suspend fun getProjectsForFile(filePath: String): List<String> {
        return fileDictionaryDao.getProjectsForFile(filePath)
    }

    suspend fun getFilesForProject(gupFilePath: String): List<String> {
        return fileDictionaryDao.getFilesForProject(gupFilePath)
    }
}