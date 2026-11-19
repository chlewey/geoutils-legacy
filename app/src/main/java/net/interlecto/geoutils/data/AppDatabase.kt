package net.interlecto.geoutils.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [NonProjectFile::class, ProjectFile::class, FileProjectCrossRef::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun fileDictionaryDao(): FileDictionaryDao
}