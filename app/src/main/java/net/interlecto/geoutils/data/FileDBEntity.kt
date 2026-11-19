package net.interlecto.geoutils.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "non_project_files")
data class NonProjectFile(
    @PrimaryKey val filePath: String,
    val lastModified: Long
)

@Entity(tableName = "project_files")
data class ProjectFile(
    @PrimaryKey val gupFilePath: String
)

@Entity(
    tableName = "file_project_cross_ref",
    primaryKeys = ["filePath", "gupFilePath"]
)
data class FileProjectCrossRef(
    val filePath: String,
    val gupFilePath: String
)