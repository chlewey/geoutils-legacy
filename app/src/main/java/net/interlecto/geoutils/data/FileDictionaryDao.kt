package net.interlecto.geoutils.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FileDictionaryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNonProjectFile(file: NonProjectFile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProjectFile(file: ProjectFile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFileProjectCrossRef(crossRef: FileProjectCrossRef)

    @Query("SELECT gupFilePath FROM file_project_cross_ref WHERE filePath = :filePath")
    suspend fun getProjectsForFile(filePath: String): List<String>

    @Query("SELECT filePath FROM file_project_cross_ref WHERE gupFilePath = :gupFilePath")
    suspend fun getFilesForProject(gupFilePath: String): List<String>
}